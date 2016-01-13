package org.falcsim.agentmodel.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;


/**
 * Extension of the core HouseholdSuperClass class of FaLC's conceptual module,
 * corresponds to the households table
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Table(name = "households", schema = "population")
public class Household extends HouseholdSuperClass implements ActiveAgent, ICSVWrite, ICSVRead {

	@Column(name="type_1")
	private Integer type_1;
	@Column(name="type_2")
	private Integer type_2;	//will be used as household type for relocation model, it is not used and already save/load to/from DB
	@Column(name="type_3")
	private Integer type_3;
	
	@Column(name="dfoundation")
	private Date dfoundation;

	@Column(name="dclosing")
	private Date dclosing;

	private Integer finance_1;

	/**
	 * create new instance and do not initialize id
	 */
	public Household() {
	};
	
	/**
	 * create new instance and initialize id if requested
	 * @param initializeId
	 */
	public Household(boolean initializeId){
		super(initializeId);
	}

	
	public Household(int id, Integer locationId, Integer run,
			List<Person> persons, Integer type_1, Integer finance_1) {
		super(id, locationId, run, persons);
		this.type_1 = type_1;
		this.finance_1 = finance_1;
	}

	
	public Integer getType_1() {
		return type_1;
	}

	public void setType_1(Integer type_1) {
		this.type_1 = type_1;
	}

	public Integer getFinance_1() {
		return finance_1;
	}

	public void setFinance_1(Integer finance_1) {
		this.finance_1 = finance_1;
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

	@Override
	public String getRowString(String separator, Object ... others) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.checkNull(super.getId()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getRun()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getLocationId()) + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.formatDate(this.dfoundation) + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.formatDate(this.dclosing) + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.checkNull(this.type_1) + separator);
		sb.append(ClassStringUtil.checkNull(this.type_2) + separator);
		sb.append(ClassStringUtil.checkNull(this.type_3) + separator);
		// FIXME
		sb.append(separator);
		
		return sb.toString();
	}

	@Override
	public void getRowValues(String[] values, Object... objects) {
		super.setId(ClassStringUtil.parseInt(values[0]));
		super.setRun(ClassStringUtil.parseInt(values[1]));
		super.setLocationId(ClassStringUtil.parseInt(values[2]));
		this.dfoundation = ClassStringUtil.parseDate(values[3]);
		this.dclosing = ClassStringUtil.parseDate(values[4]);
		this.type_1 = ClassStringUtil.parseInt(values[5]);
		this.type_2 = ClassStringUtil.parseInt(values[6]);
		this.type_3 = ClassStringUtil.parseInt(values[7]);
	}

}
