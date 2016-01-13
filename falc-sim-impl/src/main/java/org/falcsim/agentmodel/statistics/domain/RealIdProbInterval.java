package org.falcsim.agentmodel.statistics.domain;

import org.falcsim.agentmodel.util.StringUtil;
/** 
* A probability interval, non necessarily normallized
* @author regioConcept AG
* @version 0.5
* 
*/
public class RealIdProbInterval implements Comparable<RealIdProbInterval>{
	/** 
	* Integer id of the interval
	*/
	private int id;
	/** 
	* lower bound of the interval
	*/
	private double lower;
	/** 
	* upper bound of the interval
	*/
	private double upper;
	
	public RealIdProbInterval(){
		
	}
	

	public RealIdProbInterval(int id, double lower, double upper){
		this.id = id;
		this.lower = lower;
		this.upper = upper;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public double getLower() {
		return lower;
	}
	public void setLower(Double lower) {
		this.lower = lower;
	}
	public double getUpper() {
		return upper;
	}
	public void setUpper(Double upper) {
		this.upper = upper;
	}
	
	
	@Override
	public String toString() {
		return "id = "+ StringUtil.fix(id) +
				" lower = "+StringUtil.fix(lower)+
				" upper = "+StringUtil.fix(upper);
	}


	@Override
	public int compareTo(RealIdProbInterval c) {
		return getId().compareTo(c.getId());
	}

}
