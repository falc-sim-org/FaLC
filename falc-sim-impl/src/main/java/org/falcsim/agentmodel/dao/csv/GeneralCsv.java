package org.falcsim.agentmodel.dao.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.app.FalcPropertyPlaceholderConfigurer;
import org.falcsim.agentmodel.app.UniverseManagerImpl;
import org.falcsim.agentmodel.dao.csv.util.TableReader;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.domain.ICSVRead;
import org.falcsim.agentmodel.util.StringUtil;

/**
 * Encapsulates access to entities CSV tables
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class GeneralCsv {
	
	private static final Logger _log = Logger.getLogger(GeneralCsv.class);	
	
	private static final String DISTANCES_DEFAULT_FILE = "distances";
	private static final String CSV_EXTENSION = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getTableExtension();
	private static final String CSVT_EXTENSION = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getColumnDefinitionExtension();
	private static final String COLUMN_SEPARATOR = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getColumnsSeparator();
	private static final String TYPES_SEPARATOR = ",";
	private static final String SCHEMA_SEPARATOR = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getSchemaDelimiter();
	
	public <T> List<T> loadTableEntity(Class<T> clazz, String where) 
			throws RuntimeException {
		final String DATABASE_PATH = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath();
		TableReader tr = new TableReader();
		
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(clazz);
		String schema = cd.getTableName().substring(0, cd.getTableName().lastIndexOf(SCHEMA_SEPARATOR));
		String tableName = cd.getTableName().substring(cd.getTableName().lastIndexOf(SCHEMA_SEPARATOR) + 1, cd.getTableName().length());
		
		
		String fileName = DATABASE_PATH + cd.getTableName() + GeneralCsv.CSV_EXTENSION;
		String fileNameT = this.checkCSVT(schema, tableName);
		if(fileNameT.equals("")){
			throw new RuntimeException("Types file not found!");
		}
		
		tr.typesRead(fileNameT, GeneralCsv.TYPES_SEPARATOR);
		tr.preReadTable(fileName, GeneralCsv.COLUMN_SEPARATOR);
		
		List<T> ret = new ArrayList<T>();
		tr.prepareStatement(where, tr.getColumnsMap());
		
		String readLine;
		Map<String, Method> prepared = tr.prepareGetters(cd);
		
		try {
			int lineCounter = 0;
			while((readLine = tr.readNextLine()) != null){
				Object entity = clazz.newInstance();
				if(entity instanceof ICSVRead) {
					((ICSVRead)entity).getRowValues(readLine.split(GeneralCsv.COLUMN_SEPARATOR, -1));
					try {
						if(tr.fits(entity, prepared))
							ret.add((T)entity);
					} catch (Exception e) {
						_log.error(e.getMessage(), e);
					}
				} else {
					_log.fatal("Entity read by this table has to implement ICSVRead interface");
					throw new RuntimeException();
				}
				lineCounter++;
			}
			_log.debug("Read lines " + lineCounter);
		} catch (InstantiationException | IllegalAccessException e) {
			_log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (RuntimeException re){
			_log.error(re.getMessage(), re);
			throw re;
		} finally {
			tr.postReadTable();
		}
		return ret;
	}
	
	private String checkCSVT(String schema, String table) throws RuntimeException{
		String csvtName = "";
		final String DATABASE_PATH = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath();
		
		if(schema.equalsIgnoreCase(DISTANCES_DEFAULT_FILE))
			csvtName = DATABASE_PATH + DISTANCES_DEFAULT_FILE + SCHEMA_SEPARATOR + DISTANCES_DEFAULT_FILE + GeneralCsv.CSVT_EXTENSION;
		else
			csvtName = DATABASE_PATH + schema + SCHEMA_SEPARATOR + table + GeneralCsv.CSVT_EXTENSION;
		
		File test = new File(csvtName);
		if(!test.exists()){
			String modClassDescriptorName = AppCtxProvider.getApplicationContext().getBean(UniverseManagerImpl.class).getOriginalName(schema + SCHEMA_SEPARATOR + table);
			if(modClassDescriptorName.isEmpty()){
				// trying to check in the synthesis parameters
				try {
					modClassDescriptorName = AppCtxProvider.getApplicationContext().getBean(FalcPropertyPlaceholderConfigurer.class).getOldConfiguration("syn.data_table");
				} catch (IllegalAccessException | InvocationTargetException e) {
					_log.error(e.getMessage(), e);
				}
				if(modClassDescriptorName.isEmpty()){
					_log.error("Unable to load csvt file");
					throw new RuntimeException("Unable to load csvt file " + schema + "/" + table);
				}
			}
			csvtName = DATABASE_PATH + modClassDescriptorName + CSVT_EXTENSION;
		}
		
		return csvtName;
	}
	
	public List<String> getColumnsNames(String tableName) throws RuntimeException{
		return this.getColumnsNames(tableName, tableName);
	}
	
	public List<String> getColumnsNames(String tableName, String csvtTableName){
		TableReader tr = new TableReader();
		final String DATABASE_PATH = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath();
		tr.typesRead(DATABASE_PATH + csvtTableName + CSVT_EXTENSION, GeneralCsv.TYPES_SEPARATOR);
		tr.preReadTable(DATABASE_PATH + tableName + CSV_EXTENSION, COLUMN_SEPARATOR);
		tr.postReadTable();
		return Arrays.asList(tr.getColumnsNames());
	}
		
	public List<Object[]> loadTableGeneral(String tableName, List<String> columns, String where)
			throws RuntimeException{
		_log.debug("selecting table name " + tableName + " and columns " + ((columns != null) ? StringUtil.packageStringListToString1(columns, StringUtil.WITH_PAR) : "all") + ", where " + where);
		final String DATABASE_PATH = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath();
		
		List<Object[]> ret = new ArrayList<Object[]>();
		TableReader tr = new TableReader();
		
		String schema = tableName.substring(0, tableName.lastIndexOf(SCHEMA_SEPARATOR));
		tableName = tableName.substring(tableName.lastIndexOf(SCHEMA_SEPARATOR) + 1, tableName.length());
		
		String fileName = DATABASE_PATH + schema + SCHEMA_SEPARATOR + tableName + GeneralCsv.CSV_EXTENSION;
		String fileNameT = this.checkCSVT(schema, tableName);
		if(fileNameT.equals("")){
			throw new RuntimeException("Types file not found!");
		}
		
		tr.typesRead(fileNameT, GeneralCsv.TYPES_SEPARATOR);
		tr.preReadTable(fileName, GeneralCsv.COLUMN_SEPARATOR);
		
		tr.prepareStatement(where, tr.getColumnsMap());
		if(columns == null)
			columns = Arrays.asList(tr.getColumnsNames());
		
		Integer lineCounter = 1;
		String readLine = "";
		String readColumn = "";
		try{
			while((readLine = tr.readNextLine()) != null){
				readColumn = "";
				Object[] line = new Object[columns.size()];
				
				String[] values = readLine.split(GeneralCsv.COLUMN_SEPARATOR);
				int i = 0;
				for(String columnName : columns){
					readColumn = columnName;
					int index = tr.getColumnsMap().get(columnName).columnIndex;
					line[i] = tr.cast(values[index].replaceAll("\"", ""), tr.getColumnsMap().get(columnName).type);
					i++;
				}
				if(tr.fits(line))
					ret.add(line);
				
				lineCounter++;
			}
		} catch (RuntimeException re) {
			_log.error(re.getMessage(), re);
			_log.debug("Read Column/Line: " + readColumn + " / " + readLine);
			throw re;			
		} finally {
			tr.postReadTable();
		}
		_log.info("From table " + tableName + " were " + lineCounter + " lines red");
		
		return ret;
	}
	
	public List<Object[]> loadTableGeneral(String schema, String tableName, List<String> columns, String where)
			throws RuntimeException{
		return this.loadTableGeneral(schema + SCHEMA_SEPARATOR + tableName, columns, where);
	}
	
	public void createFile(String fileName, String content) throws RuntimeException{
		File f = new File(fileName);
		if(f.exists()){
			_log.warn("The file " + fileName + " exists. It will be overwritten!");
		}
		if(!f.getParentFile().exists() && !f.getParentFile().mkdirs()){
			throw new RuntimeException("Could't create directory "+ f.getParent());
		}
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(f, false))){
			bw.write(content);
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		} 
	}
}
