package org.falcsim.agentmodel.utility.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* Randomize list of universe entities for multi thread processing
* 
*
* @author regioConcept AG
* @version 1.0
*/
@Component
public class RandomizedList {
	public static Logger logger =  Logger.getLogger(RandomizedList.class);
	
	@Autowired
	private RandomGenerator rg;
	@Autowired
	private UniverseServiceUtil universeService;
	
	public List<Integer> getRandomListOfLocations(List<Location> locs){
		logger.info("Prepare list of object");
		List<Integer> souceList = new ArrayList<Integer>();
		for(Location loc : locs){
			souceList.add(loc.getId());
		}
		return randomizeList(souceList);
	}
	
	
	public List<RandomizedListObject> getRandomObjectListOfAgents(List<Household> hhs){
		logger.info("Prepare list of object");
		List<RandomizedListObject> souceList = new ArrayList<RandomizedListObject>();
		for(Household h : hhs){
			souceList.add( new RandomizedListObject(universeService.selectLocationById(h.getLocationId()), h) );
		}
		return randomizeObjectList(souceList);
	}	
	
	
	public List<RandomizedListObject> getRandomObjectListOfHouseholds(List<Location> locs){
		logger.info("Prepare list of object");
		List<RandomizedListObject> souceList = new ArrayList<RandomizedListObject>();
		for(Location loc : locs){
			for(Household h : loc.getHouseholds()){
				souceList.add( new RandomizedListObject(loc, h) );
			}
		}
		return randomizeObjectList(souceList);
	}
	
	public List<Integer> getRandomListOfHouseholds(List<Location> locs){
		logger.info("Prepare list of object");
		List<Integer> souceList = new ArrayList<Integer>();
		for(Location loc : locs){
			for(Household h : loc.getHouseholds()){
				souceList.add(h.getId());
			}
		}
		return randomizeList(souceList);
	}
		
	public List<RandomizedListObject> getRandomObjectListOfBusinesses(List<Location> locs){
		logger.info("Prepare list of object");
		List<RandomizedListObject> souceList = new ArrayList<RandomizedListObject>();
		for(Location loc : locs){
			for(Business b : loc.getBusinesses()){
				souceList.add( new RandomizedListObject(loc, b) );
			}
		}
		return randomizeObjectList(souceList);
	}
	
	public List<Integer> getRandomListOfBusinesses(List<Location> locs){
		logger.info("Prepare list of object");
		List<Integer> souceList = new ArrayList<Integer>();
		for(Location loc : locs){
			for(Business b : loc.getBusinesses()){
				souceList.add(b.getId());
			}
		}
		return randomizeList(souceList);
	}
	
	public List<RandomizedListObject> randomizeObjectList(List<RandomizedListObject> list){
		logger.info("Randomize list : " + String.valueOf(list.size()) );
		int sourceCount = list.size();
		List<RandomizedListObject> randomList = new ArrayList<RandomizedListObject>(sourceCount);
		int tmpSourceCount = sourceCount;
		for(int i = 0; i < sourceCount; i++){
			int index = rg.nextInt(tmpSourceCount);
			int tmpIndex = index;
			while( index < tmpSourceCount && list.get(index) == null ){
				index++;
			}
			if(index == tmpSourceCount){
				index = tmpIndex;
				while( index > 0 && list.get(index) == null){
					index--;	
				}
			}
			RandomizedListObject id = list.get(index);
			if(id == null){
				try {
					throw new RCCustomException("can not randomize list");
				} catch (RCCustomException e) {
					logger.error(e.getMessage(), e);
					System.exit(ExitCodes.RANDOM_LIST_ERROR.getValue());
				}
			}
			randomList.add(list.get(index));
			list.set(index, null);
		}
		logger.info("Randomization done");
		return randomList;
	}
	
	public List<Integer> randomizeList(List<Integer> list){
		logger.info("Randomize list : " + String.valueOf(list.size()) );
		int sourceCount = list.size();
		List<Integer> randomList = new ArrayList<Integer>(sourceCount);
		int tmpSourceCount = sourceCount;
		for(int i = 0; i < sourceCount; i++){
			int index = rg.nextInt(tmpSourceCount);
			int tmpIndex = index;
			while( index < tmpSourceCount && list.get(index) == 0 ){
				index++;
			}
			if(index == tmpSourceCount){
				index = tmpIndex;
				while( index > 0 && list.get(index) == 0){
					index--;	
				}
			}
			int id = list.get(index);
			if(id == 0){
				try {
					throw new RCCustomException("can not randomize list");
				} catch (RCCustomException e) {
					logger.error(e.getMessage(), e);
					System.exit(ExitCodes.RANDOM_LIST_ERROR.getValue());
				}
			}
			randomList.add(list.get(index));
			list.set(index, 0);
		}
		logger.info("Randomization done");
		return randomList;
	}
	
	public List<Integer> randomizeListOld1(List<Integer> list){
		logger.info("Randomize list : " + String.valueOf(list.size()) );
		int sourceCount = list.size();
		List<Integer> randomList = new ArrayList<Integer>(sourceCount);
		int tmpSourceCount = sourceCount;
		for(int i = 0; i < sourceCount; i++){
			int index = rg.nextInt(tmpSourceCount);
			tmpSourceCount--;
			randomList.add(list.get(index));
			list.remove(index);
		}
		logger.info("Randomization done");
		return randomList;
	}
}
