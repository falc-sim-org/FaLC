package org.falcsim.agentmodel.sublocations.dao;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.dao.jdbc.csv.CsvTrasaction;
import org.falcsim.agentmodel.sublocations.domain.LocationSubset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * Implementation of Updates data in the public.location_subset_development table
 * 
 * @author regioConcept AG
 * @version 1
 *
 */

@Repository
public class UpdateLocationSubsetImpl implements UpdateLocationSubset {

	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private CsvProperties properties;

	/*private static final String sub_schema = AppCtxProvider.getApplicationContext().getBean(RunParameters.class).getPopulation_sub_schema();*/
	
	
	
	@Override
	public Map<Integer, LocationSubset> loadAllLocations() {
		String table = ClassDescriptor.getClassDescriptor(LocationSubset.class).getTableName();
		String schema = table.substring(0, table.indexOf("/"));
		String tableName = table.substring(table.indexOf("/") + 1);
		Map<Integer, Map<String, ?>> map = generalDao.loadAllFieldsIntoAMap("locid", schema, tableName);
		TreeMap<Integer, LocationSubset> retMap = new TreeMap<Integer, LocationSubset>();
		for(Integer locid : map.keySet()){
			Map<String, ?> values = map.get(locid);
			LocationSubset locationSubset = new LocationSubset();
			locationSubset.setLocId(locid);
			locationSubset.setDenot(map.get(locid).get("denot").toString());
			for(int i = 0; i < LocationSubset.COLUMNS_COUNT; i++){
				String loc_h = values.get(String.format("locsubseth_%02d", i)).toString();
				String loc_b = values.get(String.format("locsubsetb_%02d", i)).toString();
				locationSubset.setColumn_b(i, loc_b);
				locationSubset.setColumn_h(i, loc_h);
			}
			retMap.put(locid, locationSubset);
		}
		return retMap;
	}

	@Override
	public void updateLocationSubset(Map<Integer, LocationSubset> map, int locid,
			int columnID, String subsetH, String subsetB, String denot) {
		if(map.containsKey(locid)){
			map.get(locid).setColumn_b(columnID, subsetB);
			map.get(locid).setColumn_h(columnID, subsetH);
		} else {
			LocationSubset ls = new LocationSubset();
			ls.setLocId(locid);
			ls.setColumn_b(columnID, subsetB);
			ls.setColumn_h(columnID, subsetH);
			ls.setDenot(denot);
			map.put(locid, ls);
		}	
	}

	@Override
	public void saveLocationSubset(Map<Integer, LocationSubset> map) {
		generalDao.saveOrUpdate(LocationSubset.class, null, new ArrayList<LocationSubset>(map.values()), null);
		/*generalDao.saveTable(subset_schema + properties.getSchemaDelimiter() + table_name, 
				new ArrayList<LocationSubset>(map.values()));*/
		CsvTrasaction.commitLast();
	}	
}
