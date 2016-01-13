package org.falcsim.agentmodel.dao.jdbc.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.dao.jdbc.ConcurrentHashMapCaseInsensitive;
import org.falcsim.agentmodel.dao.jdbc.FieldDescriptor;
import org.falcsim.agentmodel.dao.util.ClassStringUtil;
import org.falcsim.agentmodel.domain.ICSVWrite;

/**
 * Encapsulates CSV table update operation with temporary file 
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public final class CsvUpdateOperations {
	private static final Logger _log = Logger.getLogger(CsvUpdateOperations.class); 
	
	private static ConcurrentHashMapCaseInsensitive<BufferedWriter> writers = 
			new ConcurrentHashMapCaseInsensitive<BufferedWriter>();
	
	public static final String TEMPORARY_EXTENSION = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getTemporaryTableExtension();
	public static final String CSV_EXTENSION = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getTableExtension();
	public static final String CSVT_EXTENSION = ".csvt";
	private static final String FILE_PATH = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath();
	private static final String COLUMN_SEPARATOR =
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getColumnsSeparator();
	private static final String ENCODING =
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getCharset();
	
	public static <T> void truncateTable(Class<T> clazz, String tableName) throws RuntimeException{
		File temporaryFile = new File(FILE_PATH + tableName + CsvUpdateOperations.TEMPORARY_EXTENSION);
		try {
			if(!temporaryFile.createNewFile())
				if(!temporaryFile.exists())
					throw new RuntimeException("Unable to create a temp file " + temporaryFile.toString());
		} catch (IOException e) {
			_log.warn("The creation of a temporary table throw an exception " + e.getMessage());
			throw new RuntimeException(e);
		}
		
		try{
			if(clazz == null){
				startWriting(tableName);
			} else {
				startWriting(tableName, clazz);
			}
		} catch(IOException ioe){
			_log.error(ioe.getMessage(), ioe);
		} finally {
			try {
				closeWriting(tableName);
			} catch (IOException e) {
				_log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		CsvTrasaction.commitLast();
	}
	
	public static <T> void saveEntities(List<T> list, Class<?> clazz) throws RuntimeException {
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(clazz);
		String fileName = FILE_PATH + cd.getTableName();
		File tempFile = new File(fileName + CsvUpdateOperations.TEMPORARY_EXTENSION);
		new File(tempFile.getParent()).mkdirs();
		try{
			Writer bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile, true), ENCODING));
			String header = ClassStringUtil.getHeader(clazz, COLUMN_SEPARATOR);
			bw.append(header + System.lineSeparator());
			CsvTrasaction.addToTransaction(fileName);
			_log.debug("Start writing entities");
			for(T object : list){
				if(object instanceof ICSVWrite)
					bw.append(((ICSVWrite)object).getRowString(COLUMN_SEPARATOR) + System.lineSeparator());
				else
					bw.append(getRowString(cd, object, COLUMN_SEPARATOR) + System.lineSeparator());
			}
			_log.debug("Finished writing entities");
			bw.close();
		} catch (Exception e){
			_log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public static <T> void saveEntities(List<T> list, String tableName, Object... others) throws RuntimeException {
		try {
			startWriting(tableName);
			_log.debug("starting to write table " + tableName + " count: " + list.size());
			for(T object : list)
				writeData(object, tableName, others);
			_log.debug("finished writing table " + tableName);
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
		} finally {
			try {
				closeWriting(tableName);
			} catch (IOException e) {
				_log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	}
	
	public static boolean startWriting(Class<?> clazz) throws IOException {
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(clazz);
		return startWriting(cd.getTableName(), null);
	}
	
	public static boolean startWriting(String tableName) throws IOException {
		return startWriting(tableName, null);
	}
	
	public static <T> boolean startWriting(String tableName, Class<T> clazz) throws IOException {
		String fileName = FILE_PATH + tableName;
		if(CsvUpdateOperations.writers.containsKey(tableName))
			return true;
		File tempFile = new File(fileName + CsvUpdateOperations.TEMPORARY_EXTENSION);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile, true), ENCODING));
		CsvUpdateOperations.writers.put(tableName, bw);
		String header = "";
		if(clazz == null)
			header = CsvUpdateOperations.getHeader(tableName);
		else
			header = ClassStringUtil.getHeader(clazz, COLUMN_SEPARATOR);
		
		bw.append(header + System.lineSeparator());
		CsvTrasaction.addToTransaction(fileName);
		return false;
	}
	
	public static <T> void writeData(List<T> list, Class<?> clazz, Object... others)
			throws IOException {
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(clazz);
		_log.info("Starting to write the entities");
		for(T item : list){
			writeData(item, cd, others);
		}
		_log.info("Finished writing the entities");
	}
	
	public static <T> void writeData(T object, ClassDescriptor cd, Object... others) 
			throws IOException {
		BufferedWriter bw = CsvUpdateOperations.writers.get(cd.getTableName());
		if(object instanceof ICSVWrite)
			bw.append(((ICSVWrite)object).getRowString(COLUMN_SEPARATOR, others) + System.lineSeparator());
		else
			bw.append(getRowString(cd, object, COLUMN_SEPARATOR) + System.lineSeparator());
	}
	
	public static <T> void writeData(T object, String tableName, Object... others) 
			throws IOException{
		BufferedWriter bw = CsvUpdateOperations.writers.get(tableName);
		try{
			bw.append(((ICSVWrite)object).getRowString(COLUMN_SEPARATOR, others) + System.lineSeparator());
		}
		catch(Exception e){
			_log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public static void closeWriting(String tableName) throws IOException{
		BufferedWriter bw = CsvUpdateOperations.writers.get(tableName);
		bw.close();
		CsvUpdateOperations.writers.remove(tableName);
	}
	
	/**
	 * 
	 * @param cd
	 * @param object
	 * @param separator
	 * @return CSV row string from entity
	 */
	private static <T> String getRowString(ClassDescriptor cd, T object, String separator) throws RuntimeException{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < cd.getFields().size(); i++){
			FieldDescriptor fd = cd.getFields().get(i);
			try{
				if(fd.getFieldClass() == Double.class || fd.getFieldClass() == double.class || 
					fd.getFieldClass() == float.class || fd.getFieldClass() == Float.class)
					sb.append(fd.getGetMethod().invoke(object));
				else if(fd.getFieldClass() == Integer.class || fd.getFieldClass() == int.class ||
						fd.getFieldClass() == Long.class || fd.getFieldClass() == long.class)
					sb.append(fd.getGetMethod().invoke(object));
				else if(fd.getFieldClass() == Date.class)
					sb.append(ClassStringUtil.QUOTE + ClassStringUtil.formatDate((Date)fd.getGetMethod().invoke(object)) + ClassStringUtil.QUOTE);
				else 
					sb.append(ClassStringUtil.QUOTE + fd.getGetMethod().invoke(object).toString() + ClassStringUtil.QUOTE);
			} catch(Exception e){
				_log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
			if(i < cd.getFields().size() - 1)
				sb.append(separator);
		}
		return sb.toString();
	}
	
	/**
	 * Return the header given by the table name as string
	 * @param tableName
	 * @return text to be written into a file
	 * @throws RuntimeException
	 */
	private static String getHeader(String tableName) throws RuntimeException {
		StringBuilder header = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(Paths.get(FILE_PATH + tableName + CSV_EXTENSION).toString()));
			header.append(br.readLine());
		} catch (IOException e) {
			_log.error("Reading of " + FILE_PATH + tableName + CSV_EXTENSION + " failed " + e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				_log.error("Failed to close resource " + e.getMessage());
				throw new RuntimeException(e);
			}
		}
		return header.toString();
	}

	public static void copyTable(String source, String destination) throws IOException {
		File src = new File(FILE_PATH + source + CSV_EXTENSION);
		File dst = new File(FILE_PATH + destination + CSV_EXTENSION);		
		new File(dst.getParent()).mkdirs();		
		Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
	
		File srcT = new File(FILE_PATH + source + CSVT_EXTENSION);
		File dstT = new File(FILE_PATH + destination + CSVT_EXTENSION);
		if(srcT.exists() && ! dstT.exists()){
			Files.copy(srcT.toPath(), dstT.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}	
	}
	
	public static void deleteSchema(String schema) throws IOException {
		FileUtils.deleteDirectory(new File(FILE_PATH + schema));
	}
}
