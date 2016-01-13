package org.falcsim.agentmodel.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.csv.GeneralCsv;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.dao.jdbc.DynamicEntityTableMapper;
import org.falcsim.agentmodel.dao.jdbc.JDBCApproach;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.GeneralizedLocationDistance;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.Step;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * implementation of general dao methods that encapsulates database
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public class GeneralDaoImpl extends AbstractDao implements GeneralDao{

	private static Logger logger = Logger.getLogger(GeneralDao.class);
		
	public String geolocationsSourceTableName;
	
	/**
	 * universe functionality
	 */
	@Autowired
	private UniverseServiceUtil universeService;
	@Autowired
	private GeneralizedLocationDistanceDao gdao;
	@Autowired
	private DynamicEntityTableMapper tableMapper;
	@Autowired
	private CsvProperties properties;
	
	@Override
	public <T> List<T> readAll(final Class<T> clazz, final String sqlLimit) {
		GeneralCsv gcsv = new GeneralCsv();
		return gcsv.loadTableEntity(clazz, sqlLimit);
	}
	
	@Override
	public <T> List<T> readAll(final Class<T> clazz) {
		return JDBCApproach.readAll(clazz);
	}
	
	@Override
	@Deprecated
	public <T> void checkChanges(Class<T> clazz, Collection<T> srcList,
			List<T> updateList, List<T> insertList) {
		throw new RuntimeException("Unsupported method");
	}
	
	@Override
	public <T> void saveOrUpdate(Class<T> clazz, List<T> updateList,
			List<T> insertList, List<T> deleteList) {
		JDBCApproach.saveList(clazz, insertList);
	}
	
	public <T> void saveTable(final String tableName, final List<T> insertList){
		JDBCApproach.saveList(tableName, insertList);
	}
	
	@Override
	@Deprecated
	public List<Integer> getPrimaryKeys(ClassDescriptor classDescriptor, String hsql) {
		throw new RuntimeException("Unsupported method " + hsql);
	}
	
	/**
	 * truncate table under current session
	 */
	@Override
	public <T> void truncateTable(Class<T> clazz) {
		JDBCApproach.truncateTable(clazz);
	}

	/**
	 * return table name of the entity
	 */
	@Override
	public <T> String getTableName(Class<T> clazz) {		
		return JDBCApproach.getTableNameForClass(clazz);
	}

	/**
	 * get table name for direct SQL access (without class defined)
	 * 
	 * @return current table name
	 */
	@Override
	public String getTableName(String tableName) {
		return tableMapper.getEntityTable(null, tableName);
	}
	
	/**
	 * get entity file absolute path
	 * 
	 * @return current entity file path
	 */	
	@Override
	public <T> String getTableFilePath(Class<T> clazz){
		return AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath() + 
				JDBCApproach.getTableNameForClass(clazz) + 
				AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getTableExtension(); 
	}
	
	/**
	 * get table file absolute path
	 * 
	 * @return current table file path
	 */		
	@Override
	public String getTableFilePath(String tableName){
		return AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath() + 
				tableMapper.getEntityTable(null, tableName) + 
				AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getTableExtension();
	}
	
	
	@Override
	public void truncateTable(final String tableName) {
		JDBCApproach.truncateTable(tableName);
	}

	@Override
	@Deprecated
	public void executeQuery(final String queryStr) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void executeStoredProcedure(final String queryStr) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public String getTableNameForClass(Class<?> entityClass) {
		return JDBCApproach.getTableNameForClass(entityClass);
	}
	
	@Override
	public Map<Integer, Map<String, ?>> loadFieldsIntoAMap(String idFieldName,
			String ids, String tableName, List<String> colNames) {
		
		return loadFieldsIntoAMap(idFieldName, ids, "",  tableName, colNames);
		
	}


	@Override
	public Map<Integer, Map<String, ?>> loadFieldsIntoAMap(String idFieldName,
			String columnFilter, String ids, String schema, String tableName,
			List<String> colNames) {
		String tableIdent;
		if (colNames.size()<2){
			logger.warn("Thee must be at least two column names including the id field");
			return null;
		}
		if (schema.length()>0)
			tableIdent = schema + properties.getSchemaDelimiter() + tableName;
		else
			tableIdent = tableName;
		
		tableIdent = getTableName(tableIdent);
		
		String whereStr = columnFilter +" in "+ids;
		if (ids == null || ids.length()<1){
			whereStr = "";
		}
		
		Map<Integer, Map<String, ?>> mp= new HashMap<Integer, Map<String, ?>>();
		
		GeneralCsv t = new GeneralCsv();
		List<Object[]> oarrs = t.loadTableGeneral(tableIdent, colNames, whereStr);
		
		for (Object[] oarr : oarrs){
			Map<String,  Object> msp = new HashMap<String,  Object>();
			for(int i=0; i<oarr.length; i++)
			{	
				String colName= (String) colNames.get(i);
				msp.put(colName, oarr[i]);
			}
			mp.put(new Integer(msp.get(idFieldName).toString()), msp);
		}
		return mp;
	}
	
	
	@Override
	public Map<Integer, Map<String, ?>> loadFieldsIntoAMap(String idFieldName, String ids, String schema, String tableName, List<String> colNames) {
		return loadFieldsIntoAMap(idFieldName, idFieldName, ids, schema, tableName, colNames);
	}

	@Override
	public Map<String, Map<String, ?>> loadFieldsIntoAMapWithStringId(String idFieldName,
			String ids, String schema, String tableName, List<String> colNames) {
		String tableIdent;
		if (colNames.size()<2){
			logger.warn("Thee must be at least two column names including the id field");
			return null;
		}
		if (schema.length()>0)
			tableIdent = schema + properties.getSchemaDelimiter() + tableName;
		else
			tableIdent = tableName;
		tableIdent = getTableName(tableIdent);
		
		String whereStr = " where "+idFieldName+" in "+ids;
		if (ids.length()<1){
			whereStr = "";
		}
		
		Map<String, Map<String, ?>> mp= new HashMap<String, Map<String, ?>>();
		final String queryStr = "select "+ StringUtil.packageStringListToString1(colNames, StringUtil.WITHOUT_PAR) +"  from "+tableIdent+whereStr;

		GeneralCsv csv = new GeneralCsv();
		
		List<Object[]> oarrs = csv.loadTableGeneral(tableIdent, colNames, whereStr);

		for (Object[] oarr : oarrs){
			Map<String,  Object> msp = new HashMap<String,  Object>();
			for(int i=0; i<oarr.length; i++)
			{	
				String colName= (String) colNames.get(i);
				msp.put(colName, oarr[i]);
			}
			mp.put(new String(msp.get(idFieldName).toString()), msp);
		}
		return mp;
	}

	@Override
	@Deprecated
	public List<Object []> executeQuery2(String query){
		throw new RuntimeException("Unsupported method");
	}
	
	@Override
	@Deprecated
	public List<Object []> executeQuery2(String query, Class... classes){
		throw new RuntimeException("Unsupported method");
	}
	
	@Override
	public List<String> selectColumnNames (String tableName) throws RuntimeException {
		List<String> colnames;
		GeneralCsv csv = new GeneralCsv();	
		colnames = csv.getColumnsNames(getTableName(tableName), tableName);
		Collections.reverse(colnames);
		return colnames;
	}



	@Override
	@Deprecated
	public void insertRecords(String idFieldName, String tableName,
			List<String> fieldNames,
			Map<Integer, Map<String, Object>> recordMap) {
		throw new RuntimeException("Unsupported method");
	}



	@Override
	@Deprecated
	public void copyRecords(String idFieldName, String originTable, String targetTable,
			List<String> ids) {
		throw new RuntimeException("Unsupported method");
	}



	@Override
	public List<?> selectColumnValuesToList(String columnName, String schema, String tableName) {
		List<Object> rslt = new ArrayList<Object>();
		String newName = getTableName(schema + properties.getSchemaDelimiter() + tableName);
		GeneralCsv csv = new GeneralCsv();
		List<Object[]> objectList = csv.loadTableGeneral(newName, Arrays.asList(new String[] {columnName} ), "");
		
		for(Object[] obj : objectList){
			rslt.add(obj[0]);
		}

		return rslt;
	}



	@Override
	public Map<Integer, Map<String, ?>> loadAllFieldsIntoAMap(
			String idFieldName, String schema, String tableName) {
		logger.debug("load all fields into a map " + schema + tableName);
		return loadFieldsIntoAMap(idFieldName, "", schema, tableName, selectColumnNames(schema + properties.getSchemaDelimiter() + tableName));
	}
	
	@Override
	public void createTableFromTemplate(String template, String newTable, Class<?> clazz){
		JDBCApproach.truncateTable(clazz, newTable);
		logger.info("Table created: " + newTable);	
	
	}
	
	@Override
	public void backupTable(String fromtable, String toTable ){
		if(checkTable(toTable)){
			logger.debug("Table '" + toTable + "' exists, dropp it");
			executeQuery("drop table " + toTable);
			logger.info("Table dropped: " + toTable);
		}
		executeQuery("create table " + toTable + " ( like " + fromtable + " including defaults including constraints including indexes )" );
		logger.info("Table created: " + toTable);
		executeQuery("insert into " + toTable + " select * from " + fromtable);
		//executeQuery("create table " + toTable + " as select * from " + fromtable);
		logger.info("Table backuped into: " + toTable);
	}
	
	@Override
	public boolean checkTable(String tablename ){
		String schema = "";
		String table = "";
		String[] lst = tablename.split("\\.");
		if(lst.length <= 1){
			table = tablename;
		}
		else{
			if( lst.length > 1){
				schema = lst[0];
				table = lst[1];
			}
		}
		String qry = "select true from pg_tables where " + 
				(!"".equals(schema) ? " lower(schemaname) = lower('" + schema + "') and " : "") +
				" lower(tablename) = lower('" + table + "')";
		logger.debug(qry);
		List<Object []> qres = executeQuery2(qry);
		return qres.size() > 0 ? true : false;
	}
	
	@Override
	public String getStepsTableName() {
		return getTableNameForClass(Step.class);
	}
	@Override
	public String getLocationsTableName() {
		return getTableNameForClass(Location.class);
	}
	@Override
	public String getDistancesTableName() {
		return getTableNameForClass(GeneralizedLocationDistance.class);
	}
	@Override
	public String getPersonsTableName() {
		return getTableNameForClass(Person.class);
	}
	@Override
	public String getBusinessesTableName() {
		return getTableNameForClass(Business.class);
	}
	@Override
	public String getHouseholdsTableName() {
		return getTableNameForClass(Household.class);
	}
	
	
	/**
	 * get locations by distance from location - deprecated, use universeService.selectLocationsByDistanceFromLocation instead 
	 */
	@Override
	@Deprecated
	public  List<Location> getLocationsByDistanceFromLocation(Integer locId, Float radius){
		return universeService.selectLocationsByDistanceFromLocation(locId, radius);
		
	}

	@Override
	public GeneralizedLocationDistance getGeneralizedDistance(
			Integer locationIdA, Integer locationIdB) {
		return gdao.selectGeneralizedDistance(locationIdA, locationIdB);
	}
	
	@Override
	public Integer getCurrentPopulationByLocation(Integer locId){
		return universeService.selectPersonsCountByLocation(locId);
	}
	
	
	/**
	 * return list of households by locationId - deprecated, use universeService.selectHouseholdsByLocation instead
	 */
	@Override
	@Deprecated
	public List<Household> getHouseholdsByLocation(Integer id) {
		return universeService.selectHouseholdsByLocationId(id);
	}
	
	/**
	 * return list of locations - deprecated, use universeService.selectLocationsByCriterion instead
	 */
	@Override
	@Deprecated
	public List<Location> getLocations(String query) {
		return universeService.selectLocationsByCriterion(query);
	}

	@Override
	public Map<String, Double> getDeathTable() {
		return null;//cdao.loadDeathTable();
	}
	
	@Override
	public Map<Integer, Double> getBirthCoefficients() {
		return null;//cdao.loadBirthData();
	}

	@Override
	public void truncatePopulationTables() {
		truncateTable(Household.class);
		truncateTable(Business.class);
		truncateTable(Person.class);	
	}

	@Override
	public List<String> getFieldNamesList(String tableName) {
		return selectColumnNames(tableName);
		
	}

	@Override
	public void reattach(Object obj) {
		//universeService.reattach(obj);		
	}
	
	@Override
	public Map<Integer, Map<String, ?>>  getDataMapForLocs(List<Location> ls){
		
		String locsTableName= getLocationsTableName();
		return loadFieldsIntoAMap(RCConstants.LOCID_STR, StringUtil.packageLocsString(ls), locsTableName , getFieldNamesList(locsTableName));
	}
	
	@Override
	public Map<Integer, Map<String, ?>>  getDataMapForLocs(String locidStr, List<Location> ls, String schema, String tableName){
		
		return loadFieldsIntoAMap(locidStr, StringUtil.packageLocsString(ls), schema, tableName , getFieldNamesList(tableName));
	}
	
	@Override
	public Map<Integer, Map<String, ?>> getSelectedDataMapForLocs(String locidStr, List<Location> ls, String schema, String tableName,  String[] selectFields){
		List<String> selfplus = new LinkedList<String>(Arrays.asList(selectFields));
		selfplus.add(0, locidStr);
		return loadFieldsIntoAMap(locidStr, StringUtil.packageLocsString(ls), schema, tableName ,   selfplus);
	}
	
	@Override
	public Map<Integer, Map<String, ?>> getSelectedDataMapForLocs(String locidStr, List<Location> ls, String tableName,  String[] selectFields){
		List<String> selfplus = new LinkedList<String>(Arrays.asList(selectFields));
		selfplus.add(0, locidStr);
		return loadFieldsIntoAMap(locidStr, StringUtil.packageLocsString(ls), tableName ,  selfplus);
	}

	@Override
	public Map<Integer, Map<String, ?>> getDataMapForTable(String idStr,
			String schema, String tableName) {
		return loadAllFieldsIntoAMap(idStr, schema, tableName);
	}

	@Override
	public void cleanReferenceRuns(String schema, List<String> tablenames ) throws RuntimeException {
		JDBCApproach.deleteSchema(schema);
	}
	
	/**
	 * initialize dynamic entities
	 */
	@PostConstruct
	private void init(){
		ClassDescriptor.getClassDescriptor(Location.class);
		ClassDescriptor.getClassDescriptor(Person.class);
		ClassDescriptor.getClassDescriptor(Business.class);
		ClassDescriptor.getClassDescriptor(Household.class);
	}

	@Override
	public void copyTable(String source, String destination) {
		JDBCApproach.copyTable(source, destination);		
	}

}
