package org.falcsim.agentmodel.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.falcsim.agentmodel.domain.idgenerators.BusinessIdGenerator;


/**
 * Core class of FaLC's conceptual module
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BusinessSuperClass implements ActiveAgentP {

	@Id
	@Column(name = "BUSINESS_ID", nullable = false)
	private int id = 0;
	@Column(name = "LOCATION_ID")
	private Integer locationId;
	@Column(name = "RUN")
	private Integer run = 0;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "business_id", insertable = false, updatable = false)
	private List<Person> persons = new ArrayList<Person>();

	
	/**
	 * create new instance - id not initialized
	 */
	protected BusinessSuperClass(){		
	}
	
	/**
	 * create new instance and initialize id if requested
	 * @param initializeId
	 */
	protected BusinessSuperClass(boolean initializeId){
		if(initializeId){
			id = BusinessIdGenerator.nextId();
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	
	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	
	public Integer getRun() {
		return run;
	}

	public void setRun(Integer run) {
		this.run = run;
	}

}
