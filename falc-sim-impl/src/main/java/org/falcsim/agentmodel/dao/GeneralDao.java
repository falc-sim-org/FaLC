package org.falcsim.agentmodel.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.domain.GeneralizedLocationDistance;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;

/**
 * class encapsulates database operations
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public interface GeneralDao {

	/**
	 * read entities
	 * @param clazz
	 * @param sqlLimit
	 * @return loaded object list
	 */
	public <T> List<T> readAll(final Class<T> clazz, final String sqlLimit);
	
	public <T> List<T> readAll(final Class<T> clazz);
	
	public <T> void checkChanges(final Class<T> clazz,
			final Collection<T> srcList, final List<T> updateList,
			final List<T> insertList);
	
	public <T> void saveOrUpdate(final Class<T> clazz,
			final List<T> updateList, final List<T> insertList,
			final List<T> deleteList);
	
	public <T> void saveTable(final String tableName, final List<T> insertList);
	
	/**
	 * get list of primary keys
	 * @param classDescriptor
	 * @param hsql
	 * @return list of primary keys
	 */
	public List<Integer> getPrimaryKeys(final ClassDescriptor classDescriptor, final String hsql);
	
	/**
	 * clear database table of the entity
	 * @param clazz
	 */
	public <T> void truncateTable(Class<T> clazz);
	
	/**
	 * return table name of entity 
	 * @param clazz
	 * @return actual class table name
	 */
	public <T> String getTableName(Class<T> clazz);
	public String getTableName(String tableName);
	public <T> String getTableFilePath(Class<T> clazz);
	public String getTableFilePath(String tableName);
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//HibernateUtil
	public void executeQuery(final String queryStr);
	
	public void truncateTable(final String tableName);
	
	public String getTableNameForClass(Class<?> entityClass);
	
	public void executeStoredProcedure(final String queryStr);
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//UtilDao
	
	/**Loads the values from the listed columns colnames where the id value is contained in ids (" id1, id2, ..., idn") from table tablename in the default schema
	 *  into a map  Map<Integer, Map<String, ?>> where the key Integer refers to the table id contained in ids and the String key is the name of the column
	 * 
	 * @param idFieldName
	 * @param ids
	 * @param tableName
	 * @param colNames
	 * @return the map
	 */
	public Map<Integer, Map<String, ?>> loadFieldsIntoAMap(String idFieldName, String ids, String tableName, List<String> colNames);
	/** Loads the values from the listed columns colnames where the id value is contained in ids (" id1, id2, ..., idn") from table tablename in  schema 
	 *  into a map  Map<Integer, Map<String, ?>> where the key Integer refers to the table id contained in ids and the String key is the name of the column
	 * 
	 * @param idFieldName
	 * @param ids
	 * @param schema
	 * @param tableName
	 * @param colNames
	 * @return the map
	 */
	public Map<Integer, Map<String, ?>> loadFieldsIntoAMap(String idFieldName, String ids,  String schema, String tableName, List<String> colNames);
	
	/**
	 * @see kiadFieldsIntoAMap(String idFieldName, String ids, String tableName, List<String> colNames) where the column filter can be modified
	 * @param idFieldName
	 * @param columnFilter
	 * @param ids
	 * @param schema
	 * @param tableName
	 * @param colNames
	 * @return
	 */
	public Map<Integer, Map<String, ?>> loadFieldsIntoAMap(String idFieldName, String columnFilter, String ids, String schema, String tableName, List<String> colNames);
	/*** Loads  the values of all the colums from table tablename in  schema
	 *  into a map  Map<Integer, Map<String, ?>> where the key Integer refers to the table id and the String key is the name of the column
	 * 
	 * @param idFieldName
	 * @param schema
	 * @param tableName
	 * @return the map
	 */
	public Map<Integer, Map<String, ?>> loadAllFieldsIntoAMap(String idFieldName, String schema, String tableName);
	/** Inserts the values stored in a Map<Integer, Map<String, Object>> recordMap) map into a table, where the Integer key of the map corresponds to the id field of the table
	 * amd the String keys to the table's columns 
	 * 
	 * @param idFieldName
	 * @param tableName
	 * @param fieldNames
	 * @param recordMap
	 */
	public void insertRecords(String idFieldName, String tableName, List<String> fieldNames,  Map<Integer, Map<String, Object>> recordMap);
	/**
	 * 
	 * @param tableName
	 * @return the list of the column names in the table
	 */
	public List<String> selectColumnNames(String tableName);
	/** Returns a list of the values of a specified column for all the records in a table in schema
	 * 
	 * @param columnName
	 * @param schema
	 * @param tableName
	 * @return the list
	 */
	public List<?> selectColumnValuesToList(String columnName, String schema,  String tableName);
	/** Copies a set of records  listed in ids by id from the table originTable to the table targetTable;
	 * 
	 * @param idFieldName
	 * @param originTable
	 * @param targetTable
	 * @param ids
	 */
	public void copyRecords(String idFieldName, String originTable, String targetTable, List<String> ids);
	/** Loads the values from the listed columns colnames where the id value is contained in ids (" id1, id2, ..., idn") from table tablename in schema
	 *  into a map  Map<String, Map<String, ?>> where the first String key refers to the table id contained in ids and the String key is the name of the column
	 * 
	 * @param idFieldName
	 * @param ids
	 * @param schema
	 * @param tableName
	 * @param colNames
	 * @return the map
	 */
	public Map<String, Map<String, ?>> loadFieldsIntoAMapWithStringId(String idFieldName,
			String ids, String schema, String tableName, List<String> colNames);
	
	/** Execute SQL query string in database
	 * 
	 * @param query
	 */
	public List<Object []> executeQuery2(String query);
	public List<Object []> executeQuery2(String query, Class... classes);
		
	/** Create new empty the same table as template with all constraints, indexes,...
	 * 
	 * @param template
	 * @param newTable
	 */
	public void createTableFromTemplate(String template, String newTable, Class<?> clazz);
	
	public void copyTable(String source, String destination);
	
	/** Copy data from source table to destination, source table must exists, dest. table will be dropped
	 * 
	 * @param fromtable
	 * @param toTable
	 */
	public void backupTable(String fromtable, String toTable );

	/** Check if table exists in database
	 * 
	 * @param tablename
	 */
	public boolean checkTable(String tablename );

	/** Clean (drop) tables from reference runs
	 * 
	 * @param schema
	 * @param tablenames list
	 */
	public void cleanReferenceRuns(String schema, List<String> tablenames );
	
	//---------------------------------------------------------------------------------------------------------------------
	//DBObjectsUtil
	
	//---------------------------------------------------------------------------------------------------------------------
	//DBObjectsMethods
	public String getStepsTableName();

	public String getLocationsTableName();

	public String getDistancesTableName();

	public String getPersonsTableName();

	public String getBusinessesTableName();

	public String getHouseholdsTableName();

		
	//---------------------------------------------------------------------------------------------------------------------
	//DAMethods
	
	public List<Household> getHouseholdsByLocation(Integer id);

	public List<Location> getLocationsByDistanceFromLocation(Integer locId,
			Float radius);
	
	public GeneralizedLocationDistance getGeneralizedDistance(Integer locationIdA, Integer locationIdB);
	public Integer getCurrentPopulationByLocation(Integer locId);

	public List<Location> getLocations(String query);
	
	public Map<String, Double> getDeathTable();

	public Map<Integer, Double> getBirthCoefficients();
	
	public void truncatePopulationTables();
	
	public  List<String> getFieldNamesList(String tableName);
		
	public void reattach(Object obj);

	public Map<Integer, Map<String, ?>> getDataMapForLocs(List<Location> ls);
	
	public Map<Integer, Map<String, ?>> getDataMapForTable(String idStr,  String schema, String tableName);
	
	public Map<Integer, Map<String, ?>> getDataMapForLocs(String idStr, List<Location> ls, String schema, String tableName);
	
	public Map<Integer, Map<String, ?>> getSelectedDataMapForLocs(String idStr, List<Location> ls, String schema, String tableName,  String[] fieldsArr);
	
	public Map<Integer, Map<String, ?>> getSelectedDataMapForLocs(String idStr, List<Location> ls, String tableName,  String[] fieldsArr);


}
