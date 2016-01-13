package org.falcsim.agentmodel.domain;

/** 
* CSV file entity reader interface 
*
* @author regioConcept AG
* @version 0.5
* 
*/
public interface ICSVRead {
	/**
	 * Fill entity with columns values of CSV row 
	 * 
	 * @param values - column values
	 * @param objects - supplementary objects
	 */	
	public void getRowValues(String[] values, Object...objects);
}
