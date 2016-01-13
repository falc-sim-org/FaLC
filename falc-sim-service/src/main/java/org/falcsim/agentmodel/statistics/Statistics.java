package org.falcsim.agentmodel.statistics;

import java.util.List;

import org.apache.log4j.Logger;

/** 
* Encapsulate statistics functions calculated on stored run indicators files
*  - generate average run indicators directory
*  - calculate R2 analysis file
* 
* @author regioConcept AG
* @version 1.0
* @since   1.0 
*/
public interface Statistics {

	public void initialize(String directory);
	public boolean calculateAVG(boolean avg, boolean min, boolean max, boolean median);
	public int calculateR2Analyse(int year, boolean withMedian);
	
	public String getDirectory();
	public String getErrorText();	
	
	public static Logger logger =  Logger.getLogger(Statistics.class);
}
