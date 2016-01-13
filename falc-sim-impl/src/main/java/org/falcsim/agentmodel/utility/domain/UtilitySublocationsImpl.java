package org.falcsim.agentmodel.utility.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.falcsim.agentmodel.domain.Location;

public class UtilitySublocationsImpl extends UtilitySublocations {
	
	@Override
	public void init(List<Location> locs) {
		logger.info("Initializing UtilitySublocationsImpl ... ");
				
		setDefaultValue(false);
		Map<Integer, List<Integer>> householdMap = new HashMap<Integer, List<Integer>>();
		Map<Integer, List<Integer>> businessdMap = new HashMap<Integer, List<Integer>>();
		
		for(Location loc : locs){
			List<Integer> hhlist = new ArrayList<Integer>();
			householdMap.put(loc.getId(), hhlist);
			List<Integer> bblist = new ArrayList<Integer>();
			businessdMap.put(loc.getId(), bblist);
			
			if(loc.getLocationSubsetH() != null){
				String[] hhStrings = loc.getLocationSubsetH().split(",");
				for(String str : hhStrings){
					if(str != null && str.length() > 0){
						try{
							hhlist.add(Integer.parseInt(str));
						}catch(NumberFormatException e){}
					}
				}
			}
			if(loc.getLocationSubsetB() != null){
				String[] bbStrings = loc.getLocationSubsetB().split(",");
				for(String str : bbStrings){
					if(str != null && str.length() > 0){
						try{
							bblist.add(Integer.parseInt(str));
						}catch(NumberFormatException e){}
					}
				}
			}
		}
		
		setHouseholdMap(householdMap);
		setBusinessdMap(businessdMap);
		setInitialized(true);
		logger.info("Initializing UtilitySublocationsImpl Done");
	}
	
	@Override
	public void reset() {
		setHouseholdMap(null);
		setDefaultValue(true);
		setBusinessdMap(null);
		setInitialized(false);
	}
}
