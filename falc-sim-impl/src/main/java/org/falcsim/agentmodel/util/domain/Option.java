package org.falcsim.agentmodel.util.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** Domain class for options table
*
* @author regioConcept AG
* @version 0.5
* 
*/
@Entity
@Table(name="OPTIONS")
public class Option /*implements Serializable*/ {
	
	private static final long serialVersionUID = 5807306010292819477L;
	
	private String option;
	private Integer value;
	
	public Option(String option, Integer value){
		this.option = option;
		this.value = value;
	}
	
	@Id
	@Column(name="OPTION", nullable=false)
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	
	@Column(name="VALUE", nullable=false)
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}

}