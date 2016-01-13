package org.falcsim.agentmodel.service.methods;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.ActiveAgent;

/** Models move entity between locations
*
* @author regioConcept AG
* @version 0.5
* 
*/
public interface SMethods {
		
	public void move(ActiveAgent ag, Integer locId);
	public  static Logger logger =  Logger.getLogger(SMethods.class);

}
