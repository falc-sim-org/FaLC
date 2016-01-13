package org.falcsim.agentmodel.service.domain;

import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.service.methods.util.StepMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** State parameters for the service module
* @author regioConcept AG
* @version 1.0
* @since   1.0 
*/
@Component
public class ServiceParametersImpl  implements ServiceParameters{
	
	@Autowired
	private StepMethods sm;
	@Autowired
	private RunParameters rp;
	
	Integer currentTimeMarker;
	Integer startYear;	
	Integer currentYear;
	Integer currentRun;
	Integer currentReferenceRun;

	@Override
	public Integer getCurrentRun() {
		return currentRun;
	}

	@Override
	public void setCurrentRun(Integer currentRun) {
		this.currentRun = currentRun;
	}
	
	@Override
	public Integer getStartYear() {
		return startYear;
	}
	
	@Override
	public void setStartYear(Integer startYear) {
		this.startYear = startYear;
	}	
	
	@Override
	public Integer getCurrentYear() {
		return currentYear;
	}
	
	@Override
	public void setCurrentYear(Integer currentYear) {
		this.currentYear = currentYear;
	}

	
	@Override
	public Integer getCurrentTimeMarker() {
		return currentTimeMarker;
	}
	
	@Override
	public void setCurrentTimeMarker(Integer currentTimeMarker) {
		this.currentTimeMarker = currentTimeMarker;
	}
	
	@Override
	public void update(){
		currentTimeMarker += 1;
		currentYear = startYear + currentTimeMarker;
		currentRun += 1;
	}
	
	@Override
	public Integer getCurrentReferenceRun() {
		return currentReferenceRun;
	}
	@Override
	public void setCurrentReferenceRun(Integer currentReferenceRun) {
		this.currentReferenceRun = currentReferenceRun;
	}
	
	@Override
	public void init(){
		currentTimeMarker = sm.getStep() + 1;
		currentYear = rp.getApp_theYear() + currentTimeMarker;
		currentRun = 1;
		currentReferenceRun = 0;
	}

	
}
