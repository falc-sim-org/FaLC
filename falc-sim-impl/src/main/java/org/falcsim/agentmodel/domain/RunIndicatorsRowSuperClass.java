package org.falcsim.agentmodel.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Run Indicator row value 
 * 
 * @author regioConcept AG
 * @version 1.0
 * @version 0.5 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "run_indicators")
public class RunIndicatorsRowSuperClass {
	
	@Column(name = "LOCID")
	private Integer locid;

	@Column(name = "RUNID")
	private Integer referencerun;

	@Column(name = "YEARID")
	private Integer year;
	
	public Integer getLocid() {
		return locid;
	}

	public void setLocid(Integer locid) {
		this.locid = locid;
	}

	public Integer getReferencerun() {
		return referencerun;
	}

	public void setReferencerun(Integer referencerun) {
		this.referencerun = referencerun;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

}
