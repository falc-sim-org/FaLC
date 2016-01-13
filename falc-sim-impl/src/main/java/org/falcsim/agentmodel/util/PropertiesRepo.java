package org.falcsim.agentmodel.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/** Loads properties from property files and keeps them available at runtime.
 * 
 * @author  regioConcept AG
 *
 */
@Component
public class PropertiesRepo {
	
	private static final Logger logger = Logger.getLogger(PropertiesRepo.class);
	
	Properties properties;


	public Properties getProperties() {
		return properties;
	}


	public void setProperties(Properties properties) {
		this.properties = properties;
	}


	public void init(String  propertiesFilePath)  {

		Properties prop = new Properties();
		InputStream fis;
		try {
			ClassPathResource resource = new ClassPathResource(propertiesFilePath);
			fis = resource.getInputStream();
			prop.load(fis);
			fis.close();
		} catch (IOException  e) {
			logger.error(e.getMessage(), e);
		} 
		
		setProperties(prop);		
	}

}
