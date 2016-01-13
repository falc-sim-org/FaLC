package org.falcsim.agentmodel.service.methods;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;


/** Models change education level for residents according to age and probability (not implemented)   
*
* @author regioConcept AG
* @version 0.1
* 
*/
public interface EducationChange {
	public void process(Location loc);
	public static Logger logger =  Logger.getLogger(BusinessRiseAndFall.class);
	public void init();
}
