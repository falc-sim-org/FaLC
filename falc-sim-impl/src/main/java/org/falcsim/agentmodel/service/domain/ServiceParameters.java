package org.falcsim.agentmodel.service.domain;

/** Interface - State parameters for the service module
* @author regioConcept AG
* @version 1.0
* @since   1.0 
*/
public interface ServiceParameters {

	public void init();
	
	public void update();
	
	public Integer getCurrentTimeMarker();

	void setCurrentTimeMarker(Integer currentTimeMarker);

	public Integer getStartYear();

	public void setStartYear(Integer startYear);	
	
	void setCurrentYear(Integer currentYear);

	public Integer getCurrentYear();


	public void setCurrentRun(Integer currentRun);
	
	public Integer getCurrentRun();
	
	public Integer getCurrentReferenceRun();
	public void setCurrentReferenceRun(Integer currentReferenceRun);
}
