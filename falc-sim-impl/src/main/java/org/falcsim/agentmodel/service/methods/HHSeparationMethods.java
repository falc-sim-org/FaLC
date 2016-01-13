package org.falcsim.agentmodel.service.methods;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;

/**
 * Household separation methods, models people leaving household to create a new one.
 * 
 * @author regioConcept AG
 * @version 0.5
 *
 */
public interface HHSeparationMethods {
	
	public void init();
	public void peoplePart(Location loc);
	public  static Logger logger =  Logger.getLogger(HHSeparationMethods.class);
}
