package org.falcsim.agentmodel.statistics.domain;

/** 
* A probability interval, not necessarily normalized.
* 
* @author regioConcept AG
 * @version 1.0
 * @since   0.5 
* 
*/
public class IntegerIdProbInterval {
	/** 
	 *  Integer id corresponding to the interval
	 */
	Integer id;
	/** 
	 *  lower bound of the interval
	 */
	Integer lower;
	/** 
	 *  upper bound of the interval
	 */
	Integer upper;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getLower() {
		return lower;
	}
	public void setLower(Integer lower) {
		this.lower = lower;
	}
	public Integer getUpper() {
		return upper;
	}
	public void setUpper(Integer upper) {
		this.upper = upper;
	}
	
	
	
	

}
