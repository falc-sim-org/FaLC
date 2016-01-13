package org.falcsim.agentmodel.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;


/**
 * Extension of the core BusinessSuperClass class of FaLC's conceptual module,
 * corresponds to the businesses table
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Table(name = "businesses", schema = "population")
public class Business extends BusinessSuperClass implements ActiveAgent,
		Serializable, ICSVWrite, ICSVRead {

	private static final long serialVersionUID = 1L;

	@Column(name = "TYPE_1")
	private Integer type_1;

	@Column(name = "TYPE_2")
	private Integer type_2;
	
	@Column(name = "TYPE_3")
	private Integer type_3;

	@Column(name = "INCOME")
	private Integer finance_1;// say, total income, or profit

	@Column(name="nr_of_jobs")
	private Integer nr_of_jobs;
	
	@Column(name="dfoundation")
	private Date dfoundation;
	
	@Column(name="dclosing")
	private Date dclosing;

	//@Transient
	//private int[] employees_props;

	/**
	 * create new instance, do not initialize id
	 */
	public Business(){
		super();
	}
	
	/**
	 * create new instance, initialize id if requested only
	 * @param initializeId
	 */
	public Business(boolean initializeId){
		super(initializeId);
	}
	
	public Integer getType_1() {
		return type_1;
	}

	public void setType_1(Integer type) {
		this.type_1 = type;
	}

	public Integer getType_2() {
		return type_2;
	}

	public void setType_2(Integer type_2) {
		this.type_2 = type_2;
	}

	public Integer getType_3() {
		return type_3;
	}

	public void setType_3(Integer type_3) {
		this.type_3 = type_3;
	}

	public Integer getNr_of_jobs() {
		return nr_of_jobs;
	}

	public void setNr_of_jobs(Integer nr_of_jobs) {
		this.nr_of_jobs = nr_of_jobs;
	}

	public Date getDfoundation() {
		return dfoundation;
	}

	public void setDfoundation(Date dfoundation) {
		this.dfoundation = dfoundation;
	}

	public Date getDclosing() {
		return dclosing;
	}

	public void setDclosing(Date dclosing) {
		this.dclosing = dclosing;
	}

	public Integer getFinance_1() {
		return finance_1;
	}

	public void setFinance_1(Integer finance_1) {
		this.finance_1 = finance_1;
	}

	@Override
	public String getRowString(String separator, Object ... others) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.checkNull(super.getId()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getLocationId()) + separator);
		sb.append(ClassStringUtil.checkNull(this.type_1) + separator);
		sb.append(ClassStringUtil.checkNull(this.type_2) + separator);
		sb.append(ClassStringUtil.checkNull(this.type_3) + separator);
		sb.append(ClassStringUtil.checkNull(this.finance_1) + separator);
		sb.append(ClassStringUtil.checkNull(super.getRun()) + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.formatDate(this.dfoundation) + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.formatDate(this.dclosing) + ClassStringUtil.QUOTE + separator);
		sb.append(this.nr_of_jobs);
		return sb.toString();
	}

	@Override
	public void getRowValues(String[] values, Object... objects) {
		super.setId(ClassStringUtil.parseInt(values[0]));
		super.setLocationId(ClassStringUtil.parseInt(values[1]));
		this.type_1 = ClassStringUtil.parseInt(values[2]);
		this.type_2 = ClassStringUtil.parseInt(values[3]);
		this.type_3 = ClassStringUtil.parseInt(values[4]);
		this.finance_1 = ClassStringUtil.parseInt(values[5]);
		super.setRun(ClassStringUtil.parseInt(values[6]));
		this.dfoundation = ClassStringUtil.parseDate(values[7]);
		this.dclosing = ClassStringUtil.parseDate(values[8]);
		this.nr_of_jobs = ClassStringUtil.parseInt(values[9]);
	}

}
