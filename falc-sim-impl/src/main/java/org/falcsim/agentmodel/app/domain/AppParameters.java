package org.falcsim.agentmodel.app.domain;

/** Parameters to switch on and off universe modules
*
* @author regioConcept AG
* @version 1.0
* @since   0.5  
*/
public class AppParameters {
	
	boolean demographyOn;
	boolean migrationOn;
	boolean control_totals_residentsOn;
	boolean hhSeparationOn;
	boolean hhFormationOn;
	boolean utilitiesHouseholdsOn;
	boolean utilitiesBusinessesOn;

	boolean firmographyOn;
	boolean quittingEmplOn;
	boolean joiningEmplOn;
	
	public boolean getUtilitiesHouseholdsOn() {
		return utilitiesHouseholdsOn;
	}

	public void setUtilitiesHouseholdsOn(boolean utilitiesHouseholdsOn) {
		this.utilitiesHouseholdsOn = utilitiesHouseholdsOn;
	}
	
	public boolean getUtilitiesBusinessesOn() {
		return utilitiesBusinessesOn;
	}

	public void setUtilitiesBusinessesOn(boolean utilitiesBusinessesOn) {
		this.utilitiesBusinessesOn = utilitiesBusinessesOn;
	}
	
	public boolean getControl_totals_residentsOn() {
		return control_totals_residentsOn;
	}

	public void setControl_totals_residentsOn(boolean control_totals_residentsOn) {
		this.control_totals_residentsOn = control_totals_residentsOn;
	}

	public boolean getHhSeparationOn() {
		return hhSeparationOn;
	}

	public void setHhSeparationOn(boolean hhSeparationOn) {
		this.hhSeparationOn = hhSeparationOn;
	}
	
	public boolean getHhFormationOn() {
		return hhFormationOn;
	}

	public void setHhFormationOn(boolean hhFormationOn) {
		this.hhFormationOn = hhFormationOn;
	}

	public boolean getDemographyOn() {
		return demographyOn;
	}

	public void setDemographyOn(boolean demographyOn) {
		this.demographyOn = demographyOn;
	}

	public boolean getFirmographyOn() {
		return firmographyOn;
	}

	public void setFirmographyOn(boolean firmographyOn) {
		this.firmographyOn = firmographyOn;
	}

	public boolean getQuittingEmplOn() {
		return quittingEmplOn;
	}

	public void setQuittingEmplOn(boolean quittingEmplOn) {
		this.quittingEmplOn = quittingEmplOn;
	}

	public boolean getJoiningEmplOn() {
		return joiningEmplOn;
	}

	public void setJoiningEmplOn(boolean joiningEmplOn) {
		this.joiningEmplOn = joiningEmplOn;
	}

	public boolean getMigrationOn() {
		return migrationOn;
	}

	public void setMigrationOn(boolean migrationOn) {
		this.migrationOn = migrationOn;
	}
	
}
