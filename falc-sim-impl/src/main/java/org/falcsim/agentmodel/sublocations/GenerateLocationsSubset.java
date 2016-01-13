package org.falcsim.agentmodel.sublocations;

import org.apache.log4j.Logger;

/** Saves locations subset to table
* 	generate() will use standard data from distance table
*
* @author regioConcept AG
* @version 1.0
* 
*/
public interface GenerateLocationsSubset {
	/** Generate locations subset for all locations and update locations_zones table
	 * 
	*/
	public void generate() throws Exception;
	public void generate(boolean saveUniverse) throws Exception;
	public void reset();
	
	public static Logger logger =  Logger.getLogger(GenerateLocationsSubset.class);
}
