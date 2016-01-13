package org.falcsim.agentmodel.domain;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * Core class of FaLC's conceptual module
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
public class MunicipalitySuperClass {

	@Id
	@Column(name = "munid")
	protected int id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "cantid")
	private int locationId;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

}
