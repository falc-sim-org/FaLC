package org.falcsim.agentmodel.util.xml;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.core.io.ClassPathResource;

/** JDOM utility class
 * 
 * @author regioConcept AG
 * @version 0.5
 *
 */
public class JDOMUtil {
	
	private static final Logger logger = Logger.getLogger(JDOMUtil.class);
	
	/** Reads a file and returns a corresponding jdom document
	 * 
	 * @param filepath
	 * @return the jdom document
	 */	
	public Document loadJDom(String filepath){
		SAXBuilder builder = new SAXBuilder();
		ClassPathResource resource = new ClassPathResource(filepath);
		try {
			return (Document) builder.build(resource.getInputStream());
		}
		catch (IOException e) {
				//e.printStackTrace();
				File f = new File(filepath);
				try {
					return (Document) builder.build(new FileInputStream(f));
				} catch (JDOMException | IOException e1) {
					logger.error(e1.getMessage(), e1);
					return null;
				}
		}
		catch (JDOMException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		
	}

}
