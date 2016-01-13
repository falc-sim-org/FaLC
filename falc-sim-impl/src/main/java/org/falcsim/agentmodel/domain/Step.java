package org.falcsim.agentmodel.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;

/**
 * Domain class for Step, corresponds to the steps table
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Table(name = "steps", schema = "population")
public class Step implements Serializable, ICSVRead, ICSVWrite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "STEP")
	private Integer step;
	@Column(name = "YEAR")
	private Integer year;	
	@Column(name = "created")
	private Timestamp created;

	public Step(){
		
	}
	
	public Step(Integer step, Integer year) {
		this.created = new Timestamp(System.currentTimeMillis());
		this.step = step;
		this.year = 0;
		this.year = year;		
	}
	
	public Integer getStep() {
		return step;
	}
	
	public void setStep(Integer step) {
		this.step = step;
	}

	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}	
	
	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Integer created) {
		this.created = new Timestamp(System.currentTimeMillis());
	}

	@Override
	public String getRowString(String separator, Object... others) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.QUOTE);
		sb.append(ClassStringUtil.formatTimestamp(this.created));
		sb.append(ClassStringUtil.QUOTE);
		sb.append(separator);
		sb.append(this.step);
		sb.append(separator);
		sb.append(this.year);		
		return sb.toString();
	}

	@Override
	public void getRowValues(String[] values, Object... objects) {
		this.created = ClassStringUtil.parseTimestamp(values[0]);
		this.step = ClassStringUtil.parseInt(values[1]);
		this.year = ClassStringUtil.parseInt(values[2]);
	}

}
