package org.falcsim.agentmodel.dao.jdbc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.dao.csv.GeneralCsv;
import org.falcsim.agentmodel.dao.jdbc.csv.CsvUpdateOperations;
import org.falcsim.agentmodel.dao.jdbc.csv.CsvSelectOperations;
import org.falcsim.agentmodel.domain.ICSVRead;
import org.springframework.beans.factory.BeanFactory;

class ValueHolder<T> {
	private T value;

	public ValueHolder() {
	}

	public ValueHolder(T value) {
		this.setValue(value);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}

/**
 * JDBC access to database with entities synchronization
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class JDBCApproach {

	private final static Logger _log = Logger.getLogger(JDBCApproach.class);
	
	private static int MAX_THREADS = 4;

	static {
		BeanFactory factory = (BeanFactory) AppCtxProvider
				.getApplicationContext();
		RunParameters rp = factory.getBean(RunParameters.class);
		if (rp.getService_maximum_threads() > 0)
			MAX_THREADS = rp.getService_maximum_threads();
	}

	/**
	 * get table name for given class - should be dynamically evaluated
	 * @param clazz
	 * @return current table name for class
	 */
	public static <T> String getTableNameForClass(Class<T> clazz){
		ClassDescriptor classDescriptor = ClassDescriptor.getClassDescriptor(clazz);
		return classDescriptor.getTableName();
	}
	
	public static <T> void truncateTable(Class<T> clazz){
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(clazz);
		truncateTable(clazz, cd.getTableName());
	}
	
	public static void truncateTable(String tableName){
		CsvUpdateOperations.truncateTable(null, tableName);
	}
	
	public static <T> void truncateTable(Class<T> clazz, String tableName){
		//<!-- custom truncate table implementation
		CsvUpdateOperations.truncateTable(clazz, tableName);
		//executeUpdate(session, "delete from " + classDescriptor.getTableName());
		//-->
	}
	
	public static void copyTable(String source, String destination) throws RuntimeException {
		try{
			CsvUpdateOperations.copyTable(source, destination);
		} catch (IOException e){
			_log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public static void deleteSchema(String schemaName) throws RuntimeException {
		try{
			CsvUpdateOperations.deleteSchema(schemaName);
		} catch (IOException ioe){
			_log.error(ioe.getMessage(), ioe);
			throw new RuntimeException(ioe);
		}
	}
	
	public static <T> List<T> readAll(final Class<T> clazz) {
		try {
				GeneralCsv csv = new GeneralCsv();
				if(ICSVRead.class.isAssignableFrom(clazz)){
					return csv.loadTableEntity(clazz, "");
			} else {
				throw new RuntimeException("Implement Me");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
 
	public static <T> void saveList(final String tableName, final List<T> entities){
		CsvUpdateOperations.saveEntities(entities, tableName);
	}
	
	public static <T> void saveList(final Class<?> clazz, final List<T> entities) throws RuntimeException {
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(clazz);
		_log.debug("Sorting entities + " + clazz);
		final FieldDescriptor fd = cd.getPrimaryKey();
		Collections.sort(entities, new Comparator<T>(){
			@Override
			public int compare(T o1, T o2) {
				try {
					return ((Integer)fd.getGetMethod().invoke(o1)).compareTo((Integer)fd.getGetMethod().invoke(o2));
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					_log.error(e.getMessage(), e);
					return 0;
				}
			}
		});
		CsvUpdateOperations.saveEntities(entities, clazz);
	}
	
	public static void finishAppend(String tableName) throws RuntimeException {
		try {
			CsvUpdateOperations.closeWriting(tableName);
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
		}
	}
	
	public static <T> void startAppend(String tableName, Class<T> clazz) throws RuntimeException{
		try {
			CsvUpdateOperations.startWriting(tableName, clazz);
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
		}
	}
	
	public static void startAppend(String tableName) throws RuntimeException{
		try {
			CsvUpdateOperations.startWriting(tableName);
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
		}
	}
	
	public static <T> void append(final T entity, String tableName, final Object ... values)
			 throws RuntimeException {
		try {
			CsvUpdateOperations.writeData(entity, tableName, values);
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
		}
	}

	
	
	/**
	 * fill statement with the instance values
	 * 
	 * @param stmt
	 * @param classDescriptor
	 * @param obj
	 * @return index
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	public static int fillStatement(PreparedStatement stmt,
			ClassDescriptor classDescriptor, Object obj)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SQLException {

		int idx = 1;

		for (FieldDescriptor field : classDescriptor.getFields()) {
			if (field.getFieldClass() == int.class
					|| field.getFieldClass() == Integer.class) {
				if (field.getFieldClass() == int.class) {
					stmt.setInt(idx++,
							(Integer) field.getGetMethod().invoke(obj));
				} else {
					Integer rslt = (Integer) field.getGetMethod().invoke(obj);
					if (rslt == null) {
						stmt.setNull(idx++, Types.INTEGER);
					} else {
						stmt.setInt(idx++, rslt);
					}
				}

				continue;
			}
			if (field.getFieldClass() == Date.class) {
				Date rslt = (Date) field.getGetMethod().invoke(obj);
				if (rslt == null) {
					stmt.setNull(idx++, Types.DATE);
				} else {
					stmt.setDate(idx++, new java.sql.Date(rslt.getTime()));
				}
				continue;
			}
			if (field.getFieldClass() == String.class) {
				String rslt = (String) field.getGetMethod().invoke(obj);
				if (rslt == null) {
					stmt.setNull(idx++, Types.VARCHAR);
				} else {
					stmt.setString(idx++, rslt);
				}
				continue;
			}
			if (field.getFieldClass() == BigDecimal.class) {
				BigDecimal rslt = (BigDecimal) field.getGetMethod().invoke(obj);
				stmt.setBigDecimal(idx++, rslt);
				continue;
			}
			if (field.getFieldClass() == Boolean.class) {
				Boolean rslt = (Boolean) field.getGetMethod().invoke(obj);
				if (rslt == null) {
					stmt.setNull(idx++, Types.BOOLEAN);
				} else {
					stmt.setBoolean(idx++, rslt);
				}
				continue;
			}
			if (field.getFieldClass() == boolean.class) {
				boolean rslt = (boolean) field.getGetMethod().invoke(obj);
				stmt.setBoolean(idx++, rslt);
				continue;
			}
			if (field.getFieldClass() == Double.class) {
				Double rslt = (Double) field.getGetMethod().invoke(obj);
				if (rslt == null) {
					stmt.setNull(idx++, Types.DOUBLE);
				} else {
					stmt.setDouble(idx++, rslt);
				}
				continue;
			}
			if (field.getFieldClass() == double.class) {
				double rslt = (double) field.getGetMethod().invoke(obj);
				stmt.setDouble(idx++, rslt);
				continue;
			}
			if (field.getFieldClass() == Float.class) {
				Float rslt = (Float) field.getGetMethod().invoke(obj);
				if (rslt == null) {
					stmt.setNull(idx++, Types.FLOAT);
				} else {
					stmt.setFloat(idx++, rslt);
				}
				continue;
			}
			if (field.getFieldClass() == float.class) {
				float rslt = (float) field.getGetMethod().invoke(obj);
				stmt.setFloat(idx++, rslt);
				continue;
			}
		}

		return idx;
	}

	/**
	 * fill primary key in the statement from the index
	 * 
	 * @param stmt
	 * @param classDescriptor
	 * @param obj
	 * @param idx
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	public static void fillPrimaryKey(PreparedStatement stmt,
			ClassDescriptor classDescriptor, Object obj, int idx)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SQLException {

		// primary key
		if (classDescriptor.getPrimaryKey().getFieldClass() == int.class
				|| classDescriptor.getPrimaryKey().getFieldClass() == Integer.class) {
			if (classDescriptor.getPrimaryKey().getFieldClass() == int.class) {
				stmt.setInt(idx++, (Integer) classDescriptor.getPrimaryKey()
						.getGetMethod().invoke(obj));
			} else {
				Integer rslt = (Integer) classDescriptor.getPrimaryKey()
						.getGetMethod().invoke(obj);
				if (rslt == null) {
					stmt.setNull(idx++, Types.INTEGER);
				} else {
					stmt.setInt(idx++, rslt);
				}
			}
		}
		if (classDescriptor.getPrimaryKey().getFieldClass() == String.class) {
			String rslt = (String) classDescriptor.getPrimaryKey()
					.getGetMethod().invoke(obj);
			if (rslt == null) {
				stmt.setNull(idx++, Types.VARCHAR);
			} else {
				stmt.setString(idx++, rslt);
			}
		}
	}

	/**
	 * fill primary key from single instance
	 * @param stmt
	 * @param obj
	 * @param idx
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	public static void fillPrimaryKey(PreparedStatement stmt, Object obj,
			int idx) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SQLException {

		// primary key
		if (obj.getClass() == int.class || obj.getClass() == Integer.class) {
			if (obj.getClass() == int.class) {
				stmt.setInt(idx++, (Integer) obj);
			} else {
				stmt.setInt(idx++, (Integer) obj);
			}
		}
		if (obj.getClass() == String.class) {
			stmt.setString(idx++, (String) obj);
		}
	}

}