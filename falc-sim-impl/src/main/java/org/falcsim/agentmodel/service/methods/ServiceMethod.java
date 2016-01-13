package org.falcsim.agentmodel.service.methods;

import org.apache.log4j.Logger;

/** Executes universe models
*
* @author regioConcept AG
 * @version 1.0
 * @since   0.5 
*/
public interface ServiceMethod {
	
	public void proceed();
	public  static Logger logger =  Logger.getLogger(ServiceMethod.class);

}