package org.falcsim.agentmodel.domain.idgenerators;

import org.falcsim.agentmodel.domain.idgenerators.dao.IdGenerationDao;

/** Generates object id 
* Gets the highest id from the object table db and increments from there
* @author regioConcept AG
* @version 0.5
* 
*/
public interface IdGenerator {
	public  void setIdGenerationMethods(IdGenerationDao an_igm);

}
