package org.falcsim.agentmodel.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;

/**
 * Extension of the core CantonSuperClass class of FaLC's conceptual module,
 * corresponds to the canton table
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Table(name = "cantons", schema = "assumptions")
public class Canton extends CantonSuperClass implements ICSVRead {

	public List<Location> getLocations(){
		return null;
	}
	
	public List<Municipality> getMunicipalities(){
		return null;
	}

	@Override
	public void getRowValues(String[] values, Object... objects) {
		super.setLocationId(ClassStringUtil.parseInt(values[0]));
		super.setName(ClassStringUtil.UnQuote(values[1]));
		super.setShortname(ClassStringUtil.UnQuote(values[2]));		
	}
}
