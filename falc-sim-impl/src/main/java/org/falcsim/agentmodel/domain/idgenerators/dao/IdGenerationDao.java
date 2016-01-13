package org.falcsim.agentmodel.domain.idgenerators.dao;

import org.falcsim.agentmodel.dao.AbstractDao;
import org.springframework.stereotype.Component;

/** Generates object id 
* Gets the highest id from the object table db 
* @author regioConcept AG
* @version 0.5
* @deprecated
*/
@Component
public class IdGenerationDao extends AbstractDao{
	

	/**
	 * select max id for given entity
	 * @param clazz
	 * @return max used Id
	 */
	public <T> Integer getMaxIdFromTable(Class<T> clazz){
		throw new RuntimeException("Implement me");
		/*int value = 0;
		List<Object[]> rslt = JDBCApproach.executeQuery(getSession(), "select max(id) from "+ clazz.getName(), clazz);
		if(rslt != null && rslt.size() == 1 && rslt.get(0).length == 1){
			if(rslt.get(0)[0] == null){
				value = 0;
			}else{
				value = Integer.valueOf(rslt.get(0)[0].toString());
			}			
		}else{
			throw new RuntimeException();
		}
		return value;*/
	}

}
