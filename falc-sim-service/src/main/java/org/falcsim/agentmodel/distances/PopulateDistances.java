package org.falcsim.agentmodel.distances;

import org.apache.log4j.Logger;

/** Saves calculated costs/distances to the generalized costs/distances table.
*
* @author regioConcept AG
 * @version 1.0
 * @since   0.5 
* 
*/
public interface PopulateDistances {
	
	/** Populates the generalized costs/distances table.
	 * 
	*/
	public void populate();
	public static Logger logger =  Logger.getLogger(PopulateDistances.class);
	public void reset();
	
}
