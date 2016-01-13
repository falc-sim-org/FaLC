package org.falcsim.agentmodel.dao.jdbc.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.dao.jdbc.ConcurrentHashMapCaseInsensitive;
import org.falcsim.agentmodel.domain.ICSVRead;

/**
 * Loads whole CSV file 
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class CsvSelectOperations {
	private static final Logger _log = Logger.getLogger(CsvSelectOperations.class);
	
	public static final String SCHEMA_DELIMITER = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getSchemaDelimiter();
	
	public static final String EXTENSION = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getColumnDefinitionExtension();
	
	public static final String CSV_EXTENSION = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getTableExtension();
	
	public static final String DELIMITER = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getColumnsSeparator();
	/**
	 * A concurrentHashMap is used for the storage of tables which were used
	 * The value contains types for the table
	 */
	private static String FILE_PATH = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath();
	
	private static final Object locker = new Object();
	private static final ConcurrentHashMapCaseInsensitive<String>  classDescriptorTableMaper = 
			new ConcurrentHashMapCaseInsensitive<String>();
	
	
	@SuppressWarnings({ "unchecked", "resource" })
	public static <T> List<T> selectAll(final Class<T> clazz) throws RuntimeException {
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(clazz);
		_log.debug("Starting select all from custom select " + cd.getTableName());
		List<T> retList = new ArrayList<T>();
		File file = new File(CsvSelectOperations.FILE_PATH + cd.getTableName() + CSV_EXTENSION);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			// first line skip
			String line = br.readLine();
			_log.debug("first line: " + line);
			// first instance
			line = br.readLine();
			_log.debug("second line: " + line);
			while(line != null){
				Object o = clazz.newInstance();
				((ICSVRead)o).getRowValues(line.split(DELIMITER, -1));
				retList.add((T) o);
				line = br.readLine();
			}
			_log.debug("Finished loading " + cd.getTableName());
			return retList;
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			_log.error("Unable to read table " + cd.getTableName() + " " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
