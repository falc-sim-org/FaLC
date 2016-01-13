package org.falcsim.agentmodel.synthese;

import org.apache.log4j.Logger;

/** 
* Fills the population tables for persons, households and businesses.
* creating a population at initial time.
* 
* @author regioConcept AG
 * @version 1.0
 * @since   0.5 
* 
*/
public interface PopulateTables {
	
	public void populate();
	public void save();
	public void reset();
	public static Logger logger =  Logger.getLogger(PopulateTables.class);
}
