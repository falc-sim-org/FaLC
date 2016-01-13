package org.falcsim.agentmodel.domain;

/** Specifies common key fields for Household, Business and Location.
*
* @author regioConcept AG
* @version 0.5
* 
*/
public interface ActiveAgent {
	
	public int getId();
	public void setId(int id);
	public Integer getLocationId();
	public void setLocationId(Integer LocationId);
	public void setRun(Integer run);
	public Integer getRun();


}