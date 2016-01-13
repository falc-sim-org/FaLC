package org.falcsim.agentmodel.common.load;

import java.util.Map;

/** Interface of population coefficients loader
* 
* @author regioConcept AG
* @version 1.0
* @since 0.5 
*/
public interface PopulationDataLoader {
	/**
	 * loads a key field and a value field into a map from a given table in a given schema
	 * 
	 * @param keyField
	 * @param schema
	 * @param tableName
	 * @param valueField
	 * @return the map
	 */
	public Map<Integer, Double> loadIntegerKeyCoeffsMap(String keyField, String schema, String tableName, String valueField);

}
