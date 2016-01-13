package org.falcsim.agentmodel.service.domain;

/** Relocation parameters
*
* @author regioConcept AG
* @version 1.0
* @since   1.0 
*/
public class RelocationParameters {
	private Boolean hhMayRelocateInFirstYear;
	private Boolean hhMayChooseCurrentLocation;
	private Boolean hhRelocationRateCorrection;
	
	private Boolean bbMayRelocateInFirstYear;
	private Boolean bbMayChooseCurrentLocation;
	private Boolean bbRelocationRateCorrection;
	
	private Integer limitGtGmz;
	
	private Boolean landUseLimitationOn;
	private Boolean limitationCheckEachMoveOn;
	
	private Double landLimitProbability;
	
	private Integer relocationMinDistance;
	private Integer relocationMaxDistance;
	private Boolean locationGrowthLossActive;
	
	public Boolean getHhMayRelocateInFirstYear() {
		return hhMayRelocateInFirstYear;
	}
	public void setHhMayRelocateInFirstYear(Boolean hhMayRelocateInFirstYear) {
		this.hhMayRelocateInFirstYear = hhMayRelocateInFirstYear;
	}
	public Boolean getHhMayChooseCurrentLocation() {
		return hhMayChooseCurrentLocation;
	}
	public void setHhMayChooseCurrentLocation(Boolean hhMayChooseCurrentLocation) {
		this.hhMayChooseCurrentLocation = hhMayChooseCurrentLocation;
	}
	public Boolean getHhRelocationRateCorrection() {
		return hhRelocationRateCorrection;
	}
	public void setHhRelocationRateCorrection(Boolean hhRelocationRateCorrection) {
		this.hhRelocationRateCorrection = hhRelocationRateCorrection;
	}
	public Boolean getBbMayRelocateInFirstYear() {
		return bbMayRelocateInFirstYear;
	}
	public void setBbMayRelocateInFirstYear(Boolean bbMayRelocateInFirstYear) {
		this.bbMayRelocateInFirstYear = bbMayRelocateInFirstYear;
	}
	public Boolean getBbMayChooseCurrentLocation() {
		return bbMayChooseCurrentLocation;
	}
	public void setBbMayChooseCurrentLocation(Boolean bbMayChooseCurrentLocation) {
		this.bbMayChooseCurrentLocation = bbMayChooseCurrentLocation;
	}
	public Boolean getBbRelocationRateCorrection() {
		return bbRelocationRateCorrection;
	}
	public void setBbRelocationRateCorrection(Boolean bbRelocationRateCorrection) {
		this.bbRelocationRateCorrection = bbRelocationRateCorrection;
	}
	public Integer getLimitGtGmz() {
		return limitGtGmz;
	}
	public void setLimitGtGmz(Integer limitGtGmz) {
		this.limitGtGmz = limitGtGmz;
	}
	public Boolean getLandUseLimitationOn() {
		return landUseLimitationOn;
	}
	public void setLandUseLimitationOn(Boolean landUseLimitationOn) {
		this.landUseLimitationOn = landUseLimitationOn;
	}
	public Boolean getLimitationCheckEachMoveOn() {
		return limitationCheckEachMoveOn;
	}
	public void setLimitationCheckEachMoveOn(Boolean limitationCheckEachMoveOn) {
		this.limitationCheckEachMoveOn = limitationCheckEachMoveOn;
	}
	public Double getLandLimitProbability() {
		return landLimitProbability;
	}
	public void setLandLimitProbability(Double landLimitProbability) {
		this.landLimitProbability = landLimitProbability;
	}
	public Integer getRelocationMinDistance() {
		return relocationMinDistance;
	}
	public void setRelocationMinDistance(Integer relocationMinDistance) {
		this.relocationMinDistance = relocationMinDistance;
	}
	public Integer getRelocationMaxDistance() {
		return relocationMaxDistance;
	}
	public void setRelocationMaxDistance(Integer relocationMaxDistance) {
		this.relocationMaxDistance = relocationMaxDistance;
	}	
	public Boolean getLocationGrowthLossActive() {
		return locationGrowthLossActive;
	}
	public void setLocationGrowthLossActive(Boolean locationGrowthLossActive) {
		this.locationGrowthLossActive = locationGrowthLossActive;
	}
}
