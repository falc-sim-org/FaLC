package org.falcsim.agentmodel.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;


/** Extension of the GeneralizedLocationDistanceSuperClass class
 * corresponds to the distances table
*
* @author regioConcept AG
* @version 0.5
* 
*/
@Entity
@Table(name="distances")
public class GeneralizedLocationDistance extends GeneralizedLocationDistanceSuperClass implements ICSVRead{

	@Override
	public void getRowValues(String[] values, Object... objects) {
		
		setLocationAId(ClassStringUtil.parseInt(values[0]));
		setLocationBId(ClassStringUtil.parseInt(values[1]));
		
		setDistance_A(ClassStringUtil.parseDouble(values[2]).floatValue());
		setDistance_B(ClassStringUtil.parseDouble(values[3]).floatValue());
		setDistance_C(ClassStringUtil.parseDouble(values[4]).floatValue());
		setDistance_D(ClassStringUtil.parseDouble(values[5]).floatValue());
		setDistance_E(ClassStringUtil.parseDouble(values[6]).floatValue());
		setDistance_F(ClassStringUtil.parseDouble(values[7]).floatValue());
		setDistance_G(ClassStringUtil.parseDouble(values[8]).floatValue());
		setDistance_H(ClassStringUtil.parseDouble(values[9]).floatValue());
	}

}
