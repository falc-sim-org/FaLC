package org.falcsim.agentmodel.utility.domain;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;


public abstract class UtilitySublocations {

	public static Logger logger =  Logger.getLogger(UtilitySublocations.class);
	
	private boolean initialized = false;
	private boolean defaultValue;
	private Map<Integer, List<Integer>> householdMap;
	private Map<Integer, List<Integer>> businessdMap;

	public boolean getInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public boolean getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Map<Integer, List<Integer>> getHouseholdMap() {
		return householdMap;
	}

	public void setHouseholdMap(Map<Integer, List<Integer>> householdMap) {
		this.householdMap = householdMap;
	}

	public Map<Integer, List<Integer>> getBusinessdMap() {
		return businessdMap;
	}

	public void setBusinessdMap(Map<Integer, List<Integer>> businessdMap) {
		this.businessdMap = businessdMap;
	}
	
	public boolean isHouseholdSublocation(Integer sourceLoc, Integer destLoc){
		
		return isSublocations(sourceLoc, destLoc, householdMap);
	}
	
	public boolean isBusinessSublocation(Integer sourceLoc, Integer destLoc){
		
		return isSublocations(sourceLoc, destLoc, businessdMap);
	}
	
	private boolean isSublocations(Integer sourceLoc, Integer destLoc, Map<Integer, List<Integer>> map){
		boolean retValue = defaultValue;
		if(map != null){
			if(sourceLoc != null){
				if(map.containsKey(sourceLoc)){
					List<Integer> list = map.get(sourceLoc);
					retValue = list.size() > 0 ? list.contains(destLoc) : true; //if subset is empty, it can go anywhere
				}
			}
			else{
				//if no location is defined as source, check if dest location is at least available as destination (airports)
				for(Integer key :map.keySet()){
					List<Integer> list = map.get(key);
					if(list.size() == 0 ){			//if empty list exists for location
						retValue = true;
						break;
					}
					if( list.contains(destLoc)){
						retValue = true;
						break;
					}
				}
			}
		}
		return retValue;
	}
	
	public abstract void init(List<Location> locs);
	public abstract void reset();
}
