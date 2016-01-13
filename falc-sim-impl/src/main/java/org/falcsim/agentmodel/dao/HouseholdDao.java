package org.falcsim.agentmodel.dao;

import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;


/**
 * Dao interface for Household
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */
public interface HouseholdDao {

	public Household selectHouseholdById(Integer householdId);

	public List<Household> selectHouseholdsByLocation(Integer locationId);

	public void saveOrUpdateHousehold(Household household);

	public void saveOrUpdateHouseholdList(List<Household> householdList);

	public void deleteHousehold(Household household);

	public List<Household> selectAllHouseholds();

	public void deleteAllHouseholds();

	public void deleteHouseholdList(List<Household> householdList);

	public Map<Integer, List<Household>> selectHouseholdsMapByLocationList(
			List<Location> locs);

	public Household selectRandomHousehold();

}
