package org.falcsim.agentmodel.service.domain;

/** Businesses evolution parameters
*
* @author regioConcept AG
* @version 1.0
* @since   1.0 
*/
public class FirmographyParameters {
	
	private Integer smallCompanyMaxEmployees;
	private Double smallCompanyCreateProb;
	private Integer bigCompanyMaxEmployees;
	private Integer maximumJobDuration;
	private Integer minimumJobAge;
	private Integer maximumJobAge;
	private Integer minimumCeoAge;
	private Integer maximumCeoAge;
	private Double growthPercentage;
	private Double maximalEmploymentLevel;
	
	public Integer getSmallCompanyMaxEmployees() {
		return smallCompanyMaxEmployees;
	}
	public void setSmallCompanyMaxEmployees(Integer smallCompanyMaxEmployees) {
		this.smallCompanyMaxEmployees = smallCompanyMaxEmployees;
	}
	public Double getSmallCompanyCreateProb() {
		return smallCompanyCreateProb;
	}
	public void setSmallCompanyCreateProb(Double smallCompanyCreateProb) {
		this.smallCompanyCreateProb = smallCompanyCreateProb;
	}
	public Integer getBigCompanyMaxEmployees() {
		return bigCompanyMaxEmployees;
	}
	public void setBigCompanyMaxEmployees(Integer bigCompanyMaxEmployees) {
		this.bigCompanyMaxEmployees = bigCompanyMaxEmployees;
	}
	public Integer getMaximumJobDuration() {
		return maximumJobDuration;
	}
	public void setMaximumJobDuration(Integer maximumJobDuration) {
		this.maximumJobDuration = maximumJobDuration;
	}	
	public Integer getMinimumJobAge() {
		return minimumJobAge;
	}
	public void setMinimumJobAge(Integer minimumJobAge) {
		this.minimumJobAge = minimumJobAge;
	}
	public Integer getMaximumJobAge() {
		return maximumJobAge;
	}
	public void setMaximumJobAge(Integer maximumJobAge) {
		this.maximumJobAge = maximumJobAge;
	}
	public Integer getMinimumCeoAge() {
		return minimumCeoAge;
	}
	public void setMinimumCeoAge(Integer minimumCeoAge) {
		this.minimumCeoAge = minimumCeoAge;
	}
	public Integer getMaximumCeoAge() {
		return maximumCeoAge;
	}
	public void setMaximumCeoAge(Integer maximumCeoAge) {
		this.maximumCeoAge = maximumCeoAge;
	}
	public Double getGrowthPercentage() {
		return growthPercentage;
	}
	public void setGrowthPercentage(Double growthPercentage) {
		this.growthPercentage = growthPercentage;
	}
	
	public Double getMaximalEmploymentLevel() {
		return maximalEmploymentLevel;
	}
	
	public void setMaximalEmploymentLevel(Double maximalEmploymentLevel) {
		this.maximalEmploymentLevel = maximalEmploymentLevel;
	}
}
