package org.falcsim.agentmodel.dao;

import java.util.List;

import org.falcsim.agentmodel.domain.Location;
	
/** Dao interface for Location
*
* @author regioConcept AG
* @version 1.0
* @since   0.5 
* 
*/	
public interface LocationDao  {
		
		public Location selectLocationById(Integer LocationId);
		public void updateLocation(Location Location);
		public void saveOrUpdateLocation(Location location);
		public List<Location> selectAllLocations();
		public List<Location> selectLocationsByLocationId(Integer integer);
		public List<Location> selectLocationsByCriterion(String criterion);
		public Location selectRandomLocation();
		public void saveOrUpdateLocations(List<Location> locs);
		public List<Integer> selectLocationIdsByCriterion(String criterion);

		
	}
