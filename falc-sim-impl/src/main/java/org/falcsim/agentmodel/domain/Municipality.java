package org.falcsim.agentmodel.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;

/**
 * Extension of the core MunicipalitySuperClass class of FaLC's conceptual module,
 * corresponds to the municipalities table
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Table(name = "municipalities", schema = "assumptions")
public class Municipality extends MunicipalitySuperClass implements ICSVRead{

	public List<Location> getLocations(){
		return null;
	}

	@Override
	public void getRowValues(String[] values, Object... objects) {
		super.setId(ClassStringUtil.parseInt(values[0]));
		super.setName(ClassStringUtil.UnQuote(values[1]));
		super.setLocationId(ClassStringUtil.parseInt(values[2]));
	}
}
