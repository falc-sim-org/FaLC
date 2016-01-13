package org.falcsim.agentmodel.service.domain;

/** Demography(Life and Death) parameters
*
* @author regioConcept AG
* @version 1.0
* @since   1.0 
*/
public class DemographyParameters {
	private Integer maxAge;
	private Double probMaxAgeDie;
	private Integer motherMinAge;
	private Integer motherMaxAge;
	private Double genderBoyProbability;
	
	public Integer getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
	public Double getProbMaxAgeDie() {
		return probMaxAgeDie;
	}
	public void setProbMaxAgeDie(Double probMaxAgeDie) {
		this.probMaxAgeDie = probMaxAgeDie;
	}
	public Integer getMotherMinAge() {
		return motherMinAge;
	}
	public void setMotherMinAge(Integer motherMinAge) {
		this.motherMinAge = motherMinAge;
	}
	public Integer getMotherMaxAge() {
		return motherMaxAge;
	}
	public void setMotherMaxAge(Integer motherMaxAge) {
		this.motherMaxAge = motherMaxAge;
	}
	public Double getGenderBoyProbability() {
		return genderBoyProbability;
	}
	public void setGenderBoyProbability(Double genderBoyProbability) {
		this.genderBoyProbability = genderBoyProbability;
	}
	
}
