package org.falcsim.agentmodel.dao;

import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.distances.domain.DistanceRecord;
import org.falcsim.agentmodel.distances.domain.SimpleDistanceRecord;
import org.falcsim.agentmodel.domain.GeneralizedLocationDistance;
import org.falcsim.agentmodel.domain.Location;
import org.springframework.context.annotation.Scope;


/** Dao interface for GeneralizedLocationDistance
*
* @author regioConcept AG
* @version 1.0
* @since   0.5 
* 
*/
@Scope("prototype")	
public interface GeneralizedLocationDistanceDao {
	/**
	 * 
	 * @param locationIdA location from
	 * @param locationIdB location to
	 * selects the generalized distance between two locations 
	 */
	public GeneralizedLocationDistance selectGeneralizedDistance(Integer locationIdA, Integer locationIdB);
	/**
	 * @param locationIdA location from
	 * selects the generalized distances from locationIdA to all other locations
	 */
	public List<GeneralizedLocationDistance> selectGeneralizedDistances(Integer locationIdA);
	/**
	 * @param generalizedLocationDistance
	 * saves or updates the generalized distance
	 */
	public void saveOrUpdateGeneralizedDistance(GeneralizedLocationDistance generalizedLocationDistance);
	/**
	 * @param generalizedDistanceList
	 * saves or updates the generalized distance list
	 */
	public void saveOrUpdateGeneralizedDistanceList(List<GeneralizedLocationDistance> generalizedDistanceList);
	/**
	 * selects all generalized distances
	 */
	public List<GeneralizedLocationDistance> selectAllGeneralizedDistances();
	/**
	 * deletes all generalized distances
	 */
	public void deleteAllGeneralizedDistances() ;
	/**
	 * deletes all generalized distances
	 */
	public void deleteGeneralizedDistance(GeneralizedLocationDistance generalizedLocationDistance) ;
	/**
	 * @param generalizedDistanceList
	 * deletes the generalized distance list
	 */
	public void deleteGeneralizedDistancesList(List<GeneralizedLocationDistance> generalizedDistanceList);
	/**
	 * @param locs
	 * loads all distances between locations in the list locs into a map
	 */
	public Map<String, GeneralizedLocationDistance> selectGeneralizedDistancesMapForLocationsList(List<Location> locs);
	/**
	 * @param loc
	 * @param locs
	 * loads the distances between a location loc and the locations in the list locs into a map
	 */
	public Map<String, GeneralizedLocationDistance> selectGeneralizedDistancesMapForLocsFromLoc(Location loc, List<Location> locs);
	/**
	 * @param gld
	 * updates a generalized distance
	 */
	public void updateGeneralizedLocationDistance(GeneralizedLocationDistance gld);
	/**
	 * @param  locs
	 *  loads all distance_A values for distances between locations in the list locs into a map
	 */
	public Map<String, Float> selectDistance_A_MapForLocationsList(List<Location> locs);
	/**
	 * prepare distances table - make sure that table will contain at least empty record to every location from given locationAId
	 * <i>will save problems with sequence, second level cache, insert or update problem</i>
	 */
	public void prepareDistances(long locationAId);
	/**
	 * effective batch update of database records with no cache, scrollable cursor
	 * @param locationAId location from
	 * @param distance1 map of distances
	 * @param distance2 map of times
	 * @param distance3 map of bicycle distances
	 * @param distance4 map of bicycle times
	 * @param distance5 map of pt distances
	 * @param distance6 map of pt times
	 */
	public void batchUpdate(long locationAId, Map<Integer, Float> distance1, Map<Integer, Float> distance2, Map<Integer, Float> distance3, Map<Integer, Float> distance4, Map<Integer, Float> distance5, Map<Integer, Float> distance6, Float NaNvalue);
	public void batchUpdate(long locationAId, Map<Integer, DistanceRecord> map);
	
	public void startDistancesUpdate(String tableName);
	public void updateDistances(Integer locA, Integer locB, DistanceRecord dr, String tableName);
	public void updateSimpleDistances(Integer locA, Integer locB, SimpleDistanceRecord dr, String tableName);	
	public void finishDistancesUpdate(String tableName);
	public void startSimpleDistancesUpdate(String tableName);
	
	/** It is obsolete */
	public void patchDistances(Float NaNvalue);
	
}
