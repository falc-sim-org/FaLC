package org.falcsim.agentmodel.service.methods;

import java.util.List;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;

/** Joining business methods models filling free work places by employees
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public interface BusinessJoiningEmp {
	public void joinEmployees(Location loc);
	public void joinEmployees(List<Location> locs);
	public static Logger logger =  Logger.getLogger(BusinessJoiningEmp.class);
	public void init();
}
