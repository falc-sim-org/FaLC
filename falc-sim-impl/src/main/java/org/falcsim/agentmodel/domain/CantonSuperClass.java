package org.falcsim.agentmodel.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Core class of FaLC's conceptual module
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
public class CantonSuperClass {

	@Id
	@Column(name = "cantid")
	protected int locationId;

	@Column(name = "name")
	private String name;
	
	@Column(name = "shortname")
	private String shortname;
	
	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	
}
