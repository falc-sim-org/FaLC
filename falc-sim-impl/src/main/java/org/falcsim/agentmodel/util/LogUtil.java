package org.falcsim.agentmodel.util;

import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.falcsim.agentmodel.RCConstants;

/** 
* Initializes the logger
* 
* @author regioConcept AG
* @version 0.5
* 
*/
public class LogUtil {
	
	public static void log(){
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(RCConstants.LOG4J_PROP_FILE);
		PropertyConfigurator.configure(url);
	}

}
