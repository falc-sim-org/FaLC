package org.falcsim.agentmodel.service.domain;

/** Households formation parameters
*
* @author regioConcept AG
* @version 1.0
* @since   1.0 
*/
public class HhFormationParameters {
	private Double hhformationProbability;
	private Integer hhformationMinage;
	
	public Double getHhformationProbability() {
		return hhformationProbability;
	}
	public void setHhformationProbability(Double hhformationProbability) {
		this.hhformationProbability = hhformationProbability;
	}
	public Integer getHhformationMinage() {
		return hhformationMinage;
	}
	public void setHhformationMinage(Integer hhformationMinage) {
		this.hhformationMinage = hhformationMinage;
	}
	
	
}
