package org.falcsim.agentmodel.domain;

/** 
* CSV file entity writer interface 
*
* @author regioConcept AG
* @version 0.5
* 
*/
public interface ICSVWrite {
	/**
	 * Returns a single row for a entity which implements 
	 * this interface
	 * 
	 * @param separator - column separator used for csv
	 * @return String to be written
	 */
	public String getRowString(String separator, Object ... others);
}
