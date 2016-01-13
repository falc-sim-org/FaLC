package org.falcsim.agentmodel.service.methods;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;

/** Quitting employees methods models leaving employees from companies
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public interface BusinessQuittingEmp {
	public void quitEmployees(Location loc);
	public static Logger logger =  Logger.getLogger(BusinessQuittingEmp.class);
	public void init();
}
