package org.falcsim.agentmodel.service.methods;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;


/** Business creation methods models closing companies and rise new companies according to econimic development
*
* @author regioConcept AG
 * @version 1.0
 * @since   0.5 
*/
public interface BusinessRiseAndFall {
	
	public void process(Location loc);
	public static Logger logger =  Logger.getLogger(BusinessRiseAndFall.class);
	public void init();
}
