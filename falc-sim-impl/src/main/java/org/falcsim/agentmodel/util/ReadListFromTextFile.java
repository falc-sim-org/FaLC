package org.falcsim.agentmodel.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/** Conveniency method to populate a List from a text file
* 
* @author  regioConcept AG
*
*/
public class ReadListFromTextFile {
	
	private static final Logger logger = Logger.getLogger(ReadListFromTextFile.class);
	
	public List<String> read(String textFilePath) throws IOException {

	        BufferedReader in = null;
	        FileReader fr = null;
	        List<String> list = new ArrayList<String>();

	        try {
	            fr = new FileReader(textFilePath);
	            in = new BufferedReader(fr);
	            String str;
	            while ((str = in.readLine()) != null) {
	                list.add(str);
	            }
	        } catch (Exception e) {
	        	logger.error(e.getMessage(), e);
	        } finally {
	            in.close();
	            fr.close();
	        }

	        return list;

	}

}
