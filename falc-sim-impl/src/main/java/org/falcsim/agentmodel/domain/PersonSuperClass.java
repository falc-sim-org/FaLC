package org.falcsim.agentmodel.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.falcsim.agentmodel.domain.idgenerators.PersonIdGenerator;


/**
 * Core class of FaLC's conceptual module
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersonSuperClass implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PERSON_ID", nullable = false)
	private int id = 0;
	@Column(name = "HOUSEHOLD_ID")
	private Integer householdId;
	@Column(name = "BUSINESS_ID")
	private Integer businessId;
	@Column(name = "RUN")
	private Integer run = 0;
	@Column(name = "DBIRTH")
	private Date dbirth;
	@Column(name = "DDEATH")
	private Date ddeath;
	@Column(name = "SEX")
	private Integer sex;

	@Column(name = "EDUCATION")
	private Integer education;
	
	@Column(name = "MOTHER_ID")
	private Integer motherId;
	
	@Column(name = "FATHER_ID")
	private Integer fatherId;
	
	/**
	 * create new instance - do not initialize id
	 */
	protected PersonSuperClass() {
	};

	/**
	 * create new instance - initialize id only if requested
	 * @param initializeId
	 */
	protected PersonSuperClass(boolean initializeId){
		if(initializeId){
			id = PersonIdGenerator.nextId();
		}
	}

	public PersonSuperClass(int id, Integer householdId,
			Integer businessId, Integer run, Date dbirth, Date ddeath,
			Integer sex, Integer education) {
		super();
		this.id = id;
		this.householdId = householdId;
		this.businessId = businessId;
		this.run = run;
		this.dbirth = dbirth;
		this.ddeath = ddeath;
		this.sex = sex;
		this.education = education;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getHouseholdId() {
		return householdId;
	}

	public void setHouseholdId(Integer householdId) {
		this.householdId = householdId;
	}

	public Integer getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}

	public Integer getRun() {
		return run;
	}

	public void setRun(Integer run) {
		this.run = run;
	}

	public Date getDbirth() {
		return dbirth;
	}

	public void setDbirth(Date dbirth) {
		this.dbirth = dbirth;
	}

	public Date getDdeath() {
		return ddeath;
	}

	public void setDdeath(Date ddeath) {
		this.ddeath = ddeath;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getEducation() {
		return education;
	}

	public void setEducation(Integer education) {
		this.education = education;
	}

	public Integer getMotherId() {
		return motherId;
	}

	public void setMotherId(Integer motherId) {
		this.motherId = motherId;
	}

	public Integer getFatherId() {
		return fatherId;
	}

	public void setFatherId(Integer fatherId) {
		this.fatherId = fatherId;
	}	
	
}
