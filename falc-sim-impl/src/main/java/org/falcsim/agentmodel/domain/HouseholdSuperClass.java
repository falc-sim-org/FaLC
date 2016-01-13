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

import org.falcsim.agentmodel.domain.idgenerators.HouseholdIdGenerator;


/**
 * Core class of FaLC's conceptual module
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class HouseholdSuperClass implements ActiveAgentP {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "HOUSEHOLD_ID", nullable = false)
	private int id = 0;

	@Column(name = "LOCATION_ID")
	private Integer locationId;
	@Column(name = "RUN")
	private Integer run = 0;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "household_id")
	private List<Person> persons = new ArrayList<Person>();

	protected HouseholdSuperClass() {
	};
	
	protected HouseholdSuperClass(boolean initializeId){
		if(initializeId){
			id = HouseholdIdGenerator.createId();
		}
	}

	public HouseholdSuperClass(Integer id, Integer locationId, Integer run,
			List<Person> persons) {
		super();
		this.id = id;
		this.locationId = locationId;
		this.run = run;
		this.persons = persons;
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

	public Integer getRun() {
		return run;
	}

	public void setRun(Integer run) {
		this.run = run;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

}
