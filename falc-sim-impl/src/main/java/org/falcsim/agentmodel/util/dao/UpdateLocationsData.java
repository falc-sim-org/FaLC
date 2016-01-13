package org.falcsim.agentmodel.util.dao;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.service.Service;


/**Updates data in the location table.
 * 
 * @author regioConcept AG
 * @version 0.5
 *
 */
public interface UpdateLocationsData {
	
	public static Logger logger =  Logger.getLogger(UpdateLocationsData.class);
	/** Updates the coefficients fields
	 * 
	 */
	public void update(boolean withSubsets);
}
