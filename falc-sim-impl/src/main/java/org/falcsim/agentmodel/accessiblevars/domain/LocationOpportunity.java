package org.falcsim.agentmodel.accessiblevars.domain;

/**
 * Location data class for accessibility variables generator
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */

public class LocationOpportunity {
	/**
	 * number of residents in location
	 */
	public long numResidents;
	/**
	 * number of employees in location
	 */	
	public long numEmployees;

	public LocationOpportunity(long numResidents, long numEmployees) {
		this.numResidents = numResidents;
		this.numEmployees = numEmployees;
	}
}
