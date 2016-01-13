package org.falcsim.agentmodel.entropy.domain;

/** Parameters for the entropy module
* @author regioConcept AG
* @version 0.5
* 
*/
public class EntropyParameters {
	
	Integer numStep;
	Integer saveOption;
	Double parameter1;

	public Double getParameter1() {
		return parameter1;
	}
	public void setParameter1(Double parameter1) {
		this.parameter1 = parameter1;
	}
	public Integer getNumStep() {
		return numStep;
	}
	public void setNumStep(Integer numStep) {
		this.numStep = numStep;
	}
	public Integer getSaveOption() {
		return saveOption;
	}
	public void setSaveOption(Integer saveOption) {
		this.saveOption = saveOption;
	}
	
	

}
