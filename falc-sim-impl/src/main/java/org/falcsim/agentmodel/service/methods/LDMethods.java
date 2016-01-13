package org.falcsim.agentmodel.service.methods;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;

/** Life and death methods models people being born and dying.
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public interface LDMethods {
	
	public void beBornAndDie(Location loc);
	public static Logger logger =  Logger.getLogger(LDMethods.class);
	public void init();

}
