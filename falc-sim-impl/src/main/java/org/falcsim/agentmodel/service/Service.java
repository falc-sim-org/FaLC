package org.falcsim.agentmodel.service;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Universe;


/** Service is the key interface of the service module
*
* @author regioConcept AG
* @version 1.0
* 
*/
public interface Service {
	
	public static Logger logger =  Logger.getLogger(Service.class);
	
	/** setUniverse populates the universe with the relevant data	  
	 */
	public  void setUniverse();
	public  Universe getUniverse();
	public void saveUniverse();
	/** processUniverse implements the population dynamics for this model
	 */
	public void processUniverse();
	/** addHistory implements historization of the processed data
	 */
	public void addHistory();
	/** init() loads parameters from properties and configuration files
	 */
	public void init();
	
	/** run post universe tasks
	 */
	public void finalize();
}
