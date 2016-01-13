package org.falcsim.agentmodel.utility.dao;

import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;


/** Populates a probability list List<RealIdProbInterval>  on the basis of the utility function provided
*
* @author regioConcept AG
* @version 0.5
* 
*/
public interface UtilityDaoHB {
	
	void init(List<Location> locs);
	public List<RealIdProbInterval> assignProbsForB(Business b, final Location loc, final List<Location> locs);
	public List<RealIdProbInterval> assignProbsForB(Business b, final Location loc, final List<Location> locs, final boolean allowTheSameLocation);
	public List<RealIdProbInterval> assignProbsForH(Household h, final Location loc, final List<Location> locs, final Map<Integer, Business> bussmap, 
			final boolean allowTheSameLocation, final boolean migrantsHH);
	public Map<Integer, Map<Integer, Integer>> selectNumberOfHouseholdsByLocationListAndNumberOfPersons(
			List<Location> locs);
	public void reset();
}
