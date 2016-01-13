package org.falcsim.agentmodel.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.dao.jdbc.JDBCApproach;
import org.falcsim.agentmodel.dao.jdbc.csv.CsvTrasaction;
import org.falcsim.agentmodel.distances.domain.DistanceRecord;
import org.falcsim.agentmodel.distances.domain.SimpleDistanceRecord;
import org.falcsim.agentmodel.domain.GeneralizedLocationDistance;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.util.StringUtil;
import org.springframework.stereotype.Repository;

@Repository("GeneralizedLocationDistanceDao")
public class GeneralizedLocationDistanceDaoImpl extends AbstractDao implements
		GeneralizedLocationDistanceDao {

	@Override
	@Deprecated
	public void saveOrUpdateGeneralizedDistance(
			GeneralizedLocationDistance generalizedLocationDistance) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void updateGeneralizedLocationDistance(
			GeneralizedLocationDistance gld) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public GeneralizedLocationDistance selectGeneralizedDistance(
			Integer locationAId, Integer locationBId) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public List<GeneralizedLocationDistance> selectGeneralizedDistances(
			Integer locationIdA) {

		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void saveOrUpdateGeneralizedDistanceList(
			List<GeneralizedLocationDistance> generalizedDistanceList) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void deleteAllGeneralizedDistances() {
		
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void deleteGeneralizedDistancesList(
			List<GeneralizedLocationDistance> generalizedDistanceList) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public List<GeneralizedLocationDistance> selectAllGeneralizedDistances() {
		return JDBCApproach.readAll(GeneralizedLocationDistance.class);
	}

	@Override
	@Deprecated
	public void deleteGeneralizedDistance(
			GeneralizedLocationDistance generalizedLocationDistance) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public Map<String, GeneralizedLocationDistance> selectGeneralizedDistancesMapForLocationsList(
			List<Location> locs) {

		String LocSetC = StringUtil.packageLocsString(locs);

		return getGldMap(LocSetC, LocSetC);
	}

	@Override
	public Map<String, Float> selectDistance_A_MapForLocationsList(
			List<Location> locs) {

		String LocSetC = StringUtil.packageLocsString(locs);

		return getGldMap_A(LocSetC, LocSetC);
	}

	private Map<String, Float> getGldMap_A(String qStr1, String qStr2) {

		Map<String, Float> map = new HashMap<String, Float>();
		/*List<GeneralizedLocationDistance> list = JDBCApproach.readAll(GeneralizedLocationDistance.class,
				"where locationAId in " + qStr1 + " and locationBId in "
						+ qStr2);

		for (GeneralizedLocationDistance gld : list) {
			map.put(gld.getLocationAId().toString() + SEP
					+ gld.getLocationBId().toString(), gld.getDistance_A());
		}
		return map;
		*/
		throw new RuntimeException("Modify me");
	}
	
	private Map<String, GeneralizedLocationDistance> getGldMap(String qStr1,
			String qStr2) {

		Map<String, GeneralizedLocationDistance> parameters = new HashMap<String, GeneralizedLocationDistance>();
		throw new RuntimeException("Modify me!");
		/*
		List<GeneralizedLocationDistance> list = null;JDBCApproach.readAll(
				GeneralizedLocationDistance.class,
				"where locationAId in " + qStr1 + " and locationBId in "
						+ qStr2);

		for (GeneralizedLocationDistance gd : list) {
			parameters.put(gd.getLocationAId().toString() + SEP
					+ gd.getLocationBId().toString(), gd);
		}

		return parameters;*/
	}

	@Override
	public Map<String, GeneralizedLocationDistance> selectGeneralizedDistancesMapForLocsFromLoc(
			Location loc, List<Location> locs) {

		return getGldMap("(" + String.valueOf(loc.getId()) + ")",
				StringUtil.packageLocsString(locs));
	}

	/**
	 * prepare distances table - make sure that table will contain at least empty record to every location from given locationAId
	 * <i>will save problems with sequence, second level cache, insert or update problem</i>
	 */
	@Override
	@Deprecated
	public void prepareDistances(long locationAId) {

		throw new RuntimeException("Unsupported method");
		
	}
	
	/**
	 * effective batch update of database records with no cache, scrollable cursor
	 * @param locationAId location from
	 * @param distance1 map of car distances
	 * @param distance2 map of car times
	 * @param distance3 map of bicycle distances
	 * @param distance4 map of bicycle times
	 * @param distance5 map of pt distances
	 * @param distance6 map of pt times
	 */
	@Override
	@Deprecated
	public void batchUpdate(long locationAId, Map<Integer, Float> distance1,
			Map<Integer, Float> distance2, Map<Integer, Float> distance3,
			Map<Integer, Float> distance4, Map<Integer, Float> distance5,
			Map<Integer, Float> distance6, Float NaNvalue) {

		throw new RuntimeException("Unsupported method");
	}
	

	/**
	 * effective batch update of database records with no cache, scrollable cursor
	 * @param locationAId location from
	 * @param map of distance records per location
	 */
	@Override
	@Deprecated
	public void batchUpdate(long locationAId, Map<Integer, DistanceRecord> map) {
		throw new RuntimeException("Unsupported method");
	}
	
	/**
	 * prepare distances table - make sure that table will contain at least empty record to every location from given locationAId
	 * <i>will save problems with sequence, second level cache, insert or update problem</i>
	 */
	@Override
	@Deprecated
	public void patchDistances(Float NaNvalue) {
		throw new RuntimeException("Unsupported method");
	}
	
	@Override
	public void updateDistances(Integer locA, Integer locB, DistanceRecord dr, String tableName){
		JDBCApproach.append(dr, tableName, locA, locB);
	}
	
	@Override
	public void updateSimpleDistances(Integer locA, Integer locB, SimpleDistanceRecord dr, String tableName){
		JDBCApproach.append(dr, tableName, locA, locB);
	}	
	
	@Override
	public void finishDistancesUpdate(String tableName){
		JDBCApproach.finishAppend(tableName);
		CsvTrasaction.commitLast();
	}

	@Override
	public void startDistancesUpdate(String tableName) {
		JDBCApproach.startAppend(tableName, DistanceRecord.class);
	}
	
	@Override
	public void startSimpleDistancesUpdate(String tableName) {
		JDBCApproach.startAppend(tableName, SimpleDistanceRecord.class);
	}	
}
