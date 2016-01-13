package org.falcsim.agentmodel.service.domain;

/** Migration parameters
*
* @author regioConcept AG
* @version 1.0
* @since   1.0 
*/
public class MigrationParameters {
	private Integer migrationDataLevel;
	private Double genderBoyProbability;
	
	public Integer getMigrationDataLevel() {
		return migrationDataLevel;
	}
	public void setMigrationDataLevel(Integer migrationDataLevel) {
		this.migrationDataLevel = migrationDataLevel;
	}
	public Double getGenderBoyProbability() {
		return genderBoyProbability;
	}
	public void setGenderBoyProbability(Double genderBoyProbability) {
		this.genderBoyProbability = genderBoyProbability;
	}
}
