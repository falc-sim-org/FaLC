package org.falcsim.agentmodel.common.load;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Population coefficients loader implementation
* 
* @author regioConcept AG
* @version 1.0
* @since 0.5 
*/
@Component
public class PopulationDataLoader_Impl_1 implements PopulationDataLoader{
	
	@Autowired
	private GeneralDao generalDao;
	
	private static final Logger _log = Logger.getLogger(PopulationDataLoader_Impl_1.class);
	
	public Map<Integer, Double> loadIntegerKeyCoeffsMap(String keyField, String schema, String tableName, String valueField){
		Map<Integer, Double> ageMap= new HashMap<Integer, Double>();
		Map<Integer, Map<String, ?>> mp = generalDao.loadAllFieldsIntoAMap(keyField, schema, tableName);
		
		for (Map.Entry<Integer, Map<String, ?>> entry : mp.entrySet()) {
			ageMap.put( entry.getKey(), new Double (entry.getValue().get(valueField).toString()));
		}
				
		return ageMap;
	}

}
