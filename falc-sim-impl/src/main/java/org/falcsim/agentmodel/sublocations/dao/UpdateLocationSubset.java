package org.falcsim.agentmodel.sublocations.dao;

import java.util.Map;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.sublocations.domain.LocationSubset;

/**Updates data in the public.location_subset_development table
 * 
 * @author regioConcept AG
 * @version 1.0
 *
 */
public interface UpdateLocationSubset {
	public static Logger logger =  Logger.getLogger(UpdateLocationSubset.class);
	
	/** Updates the subset fields
	 * 
	 */
	public Map<Integer, LocationSubset> loadAllLocations();
		
	public void updateLocationSubset(Map<Integer, LocationSubset> map, int locid, int columnID, String subsetH, String subsetB, String denot);
	
	public void saveLocationSubset(Map<Integer, LocationSubset> map);
}
