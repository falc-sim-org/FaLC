package org.falcsim.agentmodel.service.methods;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;

/**
 * Household formation methods, models joining people to create new households.
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public interface HHFormationMethods {
	
	public void init();
	public void peopleJoin(Location loc);
	public  static Logger logger =  Logger.getLogger(HHFormationMethods.class);

}
