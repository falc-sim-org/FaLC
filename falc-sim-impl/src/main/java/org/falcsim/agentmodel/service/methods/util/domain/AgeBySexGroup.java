package org.falcsim.agentmodel.service.methods.util.domain;

/**
 * Holds person counts by gender
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */
public class AgeBySexGroup {

	public int mancount;
	public int womancount;
	public AgeBySexGroup(){
		this.mancount = 0;
		this.womancount = 0;
	}
}
