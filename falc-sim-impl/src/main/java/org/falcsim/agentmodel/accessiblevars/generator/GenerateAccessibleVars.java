package org.falcsim.agentmodel.accessiblevars.generator;

import java.util.Map;

import org.apache.log4j.Logger;


/** 
* Accessibility variables generator interface
* 
*/
public interface GenerateAccessibleVars {
	/** 
	 * Generate accessible variables for all locations
	*/
	public void generate(Integer year, Boolean forceCalculation) throws Exception;
	
	public static Logger logger =  Logger.getLogger(GenerateAccessibleVars.class);
}
