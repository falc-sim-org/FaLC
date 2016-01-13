package org.falcsim.agentmodel.sublocations.domain;

/** Locations subsets generator parameters
*
* @author regioConcept AG
* @version 1.0
* @since   1.0 
*/
public class SublocationsParameters {
	
	private boolean active;
	
	private int generate_years = 10;
	private Float innerCircle = 25f;
	private Float innerCircleProb = 0.9f;
	private Float outerCircle = 50f;
	private Float outerCircleProb = 0.5f;
	private Float aglomerationProbH = 0.1f;
	private Float aglomerationProbB = 0.1f;
	private Float bigCitiesProbH = 0.1f;
	private Float bigCitiesProbB = 0.1f;
	
	private Float motorwayAccessH = 30f;
	private Float motorwayAccessB = 50f;
	private Float motorwayAccessProbH = 0.1f;
	private Float motorwayAccessProbB = 0.1f;	
	
	private String subsetFilterH = "";
	private String subsetFilterB = "";
	
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public int getGenerate_years() {
		return generate_years;
	}
	public void setGenerate_years(int generate_years) {
		this.generate_years = generate_years;
	}
	public Float getInnerCircle() {
		return innerCircle;
	}
	public void setInnerCircle(Float innerCircle) {
		this.innerCircle = innerCircle;
	}
	public Float getInnerCircleProb() {
		return innerCircleProb;
	}
	public void setInnerCircleProb(Float innerCircleProb) {
		this.innerCircleProb = innerCircleProb;
	}
	public Float getOuterCircle() {
		return outerCircle;
	}
	public void setOuterCircle(Float outerCircle) {
		this.outerCircle = outerCircle;
	}
	public Float getOuterCircleProb() {
		return outerCircleProb;
	}
	public void setOuterCircleProb(Float outerCircleProb) {
		this.outerCircleProb = outerCircleProb;
	}
	public Float getAglomerationProbH() {
		return aglomerationProbH;
	}
	public void setAglomerationProbH(Float aglomerationProbH) {
		this.aglomerationProbH = aglomerationProbH;
	}
	public Float getAglomerationProbB() {
		return aglomerationProbB;
	}
	public void setAglomerationProbB(Float aglomerationProbB) {
		this.aglomerationProbB = aglomerationProbB;
	}
	public Float getBigCitiesProbH() {
		return bigCitiesProbH;
	}
	public void setBigCitiesProbH(Float bigCitiesProbH) {
		this.bigCitiesProbH = bigCitiesProbH;
	}
	public Float getBigCitiesProbB() {
		return bigCitiesProbB;
	}
	public void setBigCitiesProbB(Float bigCitiesProbB) {
		this.bigCitiesProbB = bigCitiesProbB;
	}
	public Float getMotorwayAccessH() {
		return motorwayAccessH;
	}
	public void setMotorwayAccessH(Float motorwayAccessH) {
		this.motorwayAccessH = motorwayAccessH;
	}
	public Float getMotorwayAccessB() {
		return motorwayAccessB;
	}
	public void setMotorwayAccessB(Float motorwayAccessB) {
		this.motorwayAccessB = motorwayAccessB;
	}
	public Float getMotorwayAccessProbH() {
		return motorwayAccessProbH;
	}
	public void setMotorwayAccessProbH(Float motorwayAccessProbH) {
		this.motorwayAccessProbH = motorwayAccessProbH;
	}
	public Float getMotorwayAccessProbB() {
		return motorwayAccessProbB;
	}
	public void setMotorwayAccessProbB(Float motorwayAccessProbB) {
		this.motorwayAccessProbB = motorwayAccessProbB;
	}
	
	public String getSubsetFilterH() {
		return subsetFilterH;
	}
	public void setSubsetFilterH(String subsetFilterH) {
		this.subsetFilterH = subsetFilterH;
	}
	public String getSubsetFilterB() {
		return subsetFilterB;
	}
	public void setSubsetFilterB(String subsetFilterB) {
		this.subsetFilterB = subsetFilterB;
	}
	
}
