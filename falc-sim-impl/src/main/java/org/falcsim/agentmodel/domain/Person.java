package org.falcsim.agentmodel.domain;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;


/** Extension of the core PersonSuperClass class of FaLC's conceptual module,
 * corresponds to the persons table
*
* @author regioConcept AG
* @version 0.5
* 
*/
@Entity
@Table(name="persons", schema = "population")
public class Person extends PersonSuperClass implements ICSVWrite, ICSVRead /*implements Serializable*/ {
	
	private static final long serialVersionUID = 635786981619856190L;
	
	@Column(name="INCOME")
	private Integer finance_1;
	
	@Column(name="STATUS")
	private Integer status_1;
	
	@Column(name="WORKINGSTATUS")
	private Integer status_2;
	
	@Column(name="position_in_hh")
	private Integer position_in_hh;
	@Column(name="position_in_bus")
	private Integer position_in_bus;
	@Column(name="partnership_since")
	private Integer partnership_since;
	@Column(name="employed_since")
	private Integer employed_since;		
	
	@Column(name="decissionNumber")
	private Integer decissionNumber;

	/**
	 * create new instance, no id assigned
	 */
	public Person(){
		super();
	};
	
	/**
	 * create new instance, create new id if requested
	 * @param initializeId
	 * 	mark if the id should be initialized
	 */
	public Person(boolean initializeId){
		super(initializeId);
	}
	
	public Person(int id, Integer householdId, Integer businessId,
			Integer run, Date dbirth, Date ddeath, Integer sex, 
			Integer finance_1, Integer status_1, Integer status_2, Integer education) {
		super(id, householdId, businessId, run, dbirth, ddeath, sex, education);
		this.finance_1 = finance_1;
		this.status_1 = status_1;
		this.status_2 = status_2;
	}
		
	
	public Integer getFinance_1() {
		return finance_1;
	}
	public void setFinance_1(Integer finance_1) {
		this.finance_1= finance_1;
	}
		
	public Integer getStatus_1() {
		return status_1;
	}
	public void setStatus_1(Integer status_1) {
		this.status_1 = status_1;
	}
	
	public Integer getStatus_2() {
		return status_2;
	}
	public void setStatus_2(Integer status_2) {
		this.status_2 = status_2;
	}
	
	public Integer getPosition_in_hh() {
		return position_in_hh;
	}

	public void setPosition_in_hh(Integer position_in_hh) {
		this.position_in_hh = position_in_hh;
	}

	public Integer getPosition_in_bus() {
		return position_in_bus;
	}

	public void setPosition_in_bus(Integer position_in_bus) {
		this.position_in_bus = position_in_bus;
	}

	public Integer getPartnership_since() {
		return partnership_since;
	}

	public void setPartnership_since(Integer partnership_since) {
		this.partnership_since = partnership_since;
	}

	public Integer getEmployed_since() {
		return employed_since;
	}

	public void setEmployed_since(Integer employed_since) {
		this.employed_since = employed_since;
	}
	
	public Integer getDecissionNumber() {
		return decissionNumber;
	}

	public void setDecissionNumber(Integer decissionNumber) {
		this.decissionNumber = decissionNumber;
	}
	
	@Override
	public String getRowString(String separator, Object ... others) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.checkNull(super.getId()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getRun()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getBusinessId()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getHouseholdId()) + separator);
		sb.append(ClassStringUtil.checkNull(this.position_in_hh) + separator);
		sb.append(ClassStringUtil.checkNull(this.position_in_bus) + separator);
		sb.append(ClassStringUtil.checkNull(this.partnership_since) + separator);
		sb.append(ClassStringUtil.checkNull(this.employed_since) + separator);
		sb.append(ClassStringUtil.checkNull(this.status_2) + separator);
		sb.append(ClassStringUtil.checkNull(super.getSex()) + separator);
		sb.append(ClassStringUtil.checkNull(this.finance_1) + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.formatDate(super.getDbirth()) + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.checkNull(this.status_1) + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.formatDate(super.getDdeath()) + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.checkNull(super.getEducation()) + separator);
		
		sb.append( ClassStringUtil.checkNull(getMotherId()) + separator);
		sb.append( ClassStringUtil.checkNull(getFatherId())  + separator);
		sb.append( ClassStringUtil.checkNull(this.decissionNumber) );
		
		return sb.toString();
	}

	@Override
	public void getRowValues(String[] values, Object... objects) {
		super.setId(ClassStringUtil.parseInt(values[0]));
		super.setRun(ClassStringUtil.parseInt(values[1]));
		super.setBusinessId(ClassStringUtil.parseInt(values[2]));
		super.setHouseholdId(ClassStringUtil.parseInt(values[3]));
		this.position_in_hh = ClassStringUtil.parseInt(values[4]);
		this.position_in_bus = ClassStringUtil.parseInt(values[5]);
		this.partnership_since = ClassStringUtil.parseInt(values[6]);
		this.employed_since = ClassStringUtil.parseInt(values[7]);
		this.status_2 = ClassStringUtil.parseInt(values[8]);
		super.setSex(ClassStringUtil.parseInt(values[9]));
		this.finance_1 = ClassStringUtil.parseInt(values[10]);
		super.setDbirth(ClassStringUtil.parseDate(values[11]));
		this.status_1 = ClassStringUtil.parseInt(values[12]);
		super.setDdeath(ClassStringUtil.parseDate(values[13]));
		super.setEducation(ClassStringUtil.parseInt(values[14]));
		
		setMotherId(ClassStringUtil.parseInt(values[15]) );
		setFatherId(ClassStringUtil.parseInt(values[16]) );
		this.decissionNumber = ClassStringUtil.parseInt(values[17]);
	}

	
}