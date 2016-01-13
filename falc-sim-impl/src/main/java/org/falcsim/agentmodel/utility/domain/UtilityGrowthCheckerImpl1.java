package org.falcsim.agentmodel.utility.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UtilityGrowthCheckerImpl1 extends UtilityGrowthChecker {

	@Autowired
	private UtilityParameters up2;
	private double max_location_growth_households = 0.;
	private double max_location_growth_businesses = 0.;
	private Map<Integer, LocationStatus> maxFutureEntities;
	private Map<String, String> overridesMap;
	
	private static final  String MAXGROWTH = "relocation_max_growth_factors";
	private static final  String OVERRIDE = "override";
	private static final  String LOCATION = "LOCATION";	
	private static final  String HID = "H";
	private static final  String BID = "B";
	
	@Override
	public double getMax_location_growth_households() {
		return max_location_growth_households;
	}
	@Override
	public void setMax_location_growth_households(double max_location_growth) {
		this.max_location_growth_households = max_location_growth;
	}
	@Override
	public double getMax_location_growth_businesses() {
		return max_location_growth_businesses;
	}
	@Override
	public void setMax_location_growth_businesses(double max_location_growth) {
		this.max_location_growth_businesses = max_location_growth;
	}
	
	@Override
	public void init(List<Location> locs) {
		max_location_growth_households = Double.parseDouble( up2.getUtilMap().get(MAXGROWTH).get(HID) ); //value should be 1.04 like 4%
		max_location_growth_businesses = Double.parseDouble( up2.getUtilMap().get(MAXGROWTH).get(BID) );
		
		overridesMap = up2.getUtilMap().get(OVERRIDE + "-" + MAXGROWTH + "-" + LOCATION);
		calculateFutureMaxNumberOfEntities(locs, max_location_growth_households, max_location_growth_businesses);
	}

	@Override
	public void reset() {
		maxFutureEntities = null;
	}
	
	@Override
	public boolean checkLocationHouseholds(Location loc){
		int locid = loc.getId();
		int tmpNumOfHHLic = maxFutureEntities.get(locid).getNumOfResidentsActualState();
		int tmpFutureNumOfHHLic = maxFutureEntities.get(locid).getNumOfResidentsFuture();
		
		boolean status = tmpNumOfHHLic < tmpFutureNumOfHHLic;
		if(!status && overridesMap != null ){
			String key = HID + "-" + String.valueOf(locid);
			if(overridesMap.get(key) != null ){
				double new_factor = Double.parseDouble( overridesMap.get(key) );
				int tmpStartState = maxFutureEntities.get(locid).getNumOfResidentsStart();
				tmpFutureNumOfHHLic = (int)(tmpStartState * new_factor);
				
				status = tmpNumOfHHLic < tmpFutureNumOfHHLic;
			}
		}
		if(!status){
			if(loc.getMG_overlimit_H() == null || !loc.getMG_overlimit_H() ){
				synchronized(loc){
					loc.setMG_overlimit_H(true);
				}
			}
		}
		return status;
	}

	@Override
	public boolean checkLocationBusinesses(Location loc){
		int locid = loc.getId();
		int tmpNumOfBBLic = maxFutureEntities.get(locid).getNumOfWorkersActualState();
		int tmpFutureNumOfBBLic = maxFutureEntities.get(locid).getNumOfWorkersFuture();
		
		boolean status = tmpNumOfBBLic < tmpFutureNumOfBBLic;
		if(!status && overridesMap != null ){
			String key = BID + "-" + String.valueOf(locid);
			if(overridesMap.get(key) != null ){
				double new_factor = Double.parseDouble( overridesMap.get(key) );
				int tmpStartState = maxFutureEntities.get(locid).getNumOfWorkersStart();
				tmpFutureNumOfBBLic = (int)(tmpStartState * new_factor);
				
				status = tmpNumOfBBLic < tmpFutureNumOfBBLic;
			}
		}	
		if(!status){
			if(loc.getMG_overlimit_B() == null || !loc.getMG_overlimit_B() ){
				synchronized(loc){
					loc.setMG_overlimit_B(true);
				}
			}
		}		
		return status;
	}
	
	@Override
	public void updateLocationHouseholdsCurrentStatus(int fromLoc, int toLoc, Household h){
		LocationStatus stat = maxFutureEntities.get(fromLoc);
		int count = getLivePersons(h.getPersons());
		synchronized(stat){
			stat.updateCurrentResidents(-1 * count);
		}
		stat = maxFutureEntities.get(toLoc);	
		synchronized(stat){
			stat.updateCurrentResidents(count);
		}
	}
	
	@Override
	public void updateLocationBusinessesCurrentStatus(int fromLoc, int toLoc, Business b){
		LocationStatus stat = maxFutureEntities.get(fromLoc);
		int count = getLivePersons(b.getPersons());
		synchronized(stat){
			stat.updateCurrentWorkers(-1 * count);
		}
		stat = maxFutureEntities.get(toLoc);
		synchronized(stat){
			stat.updateCurrentWorkers(count);
		}
	}
	
	@Override
	public void updateLocationStatus(List<Location> locs){
		for(Location loc : locs){
			int currNum = 0;
			LocationStatus stat = maxFutureEntities.get(loc.getId());
			currNum = getLiveResidents(loc.getHouseholds());

			stat.setNumOfResidentsBeforeMove(currNum);
			stat.setNumOfResidentsActualState(currNum);
			
			currNum = getLiveWorkers(loc.getBusinesses());
			stat.setNumOfWorkersBeforeMove(currNum);
			stat.setNumOfWorkersActualState(currNum);
		}
	}
	
	private void calculateFutureMaxNumberOfEntities(List<Location> locs, double factorH, double factorB){
		maxFutureEntities = new HashMap<Integer, LocationStatus>();
		for(Location loc : locs){
			LocationStatus stat = new LocationStatus();
			stat.setNumOfResidentsStart( getLiveResidents(loc.getHouseholds()) );
			stat.setNumOfWorkersStart( getLiveWorkers(loc.getBusinesses()) );
			stat.setNumOfResidentsFuture( (int)(stat.getNumOfResidentsStart() * factorH));
			stat.setNumOfWorkersFuture( (int)(stat.getNumOfWorkersStart() * factorB));
			stat.setNumOfResidentsBeforeMove(0);
			stat.setNumOfResidentsActualState(0);
			stat.setNumOfWorkersBeforeMove(0);
			stat.setNumOfWorkersActualState(0);
			maxFutureEntities.put(loc.getId(), stat);
		}
	}
	
	private int getLiveResidents(List<Household> list){
		int count = 0;
		for(Household h : list){
			if(h.getDclosing() == null ){
				count += getLivePersons(h.getPersons());
			}
		}
		return count;
	}
	
	private int getLiveWorkers(List<Business> list){
		int count = 0;
		for(Business b : list){
			if(b.getDclosing() == null ){
				count += getLivePersons(b.getPersons());
			}
		}
		return count;
	}
	
	private int getLivePersons(List<Person> list){
		int count = 0;
		for(Person p : list){
			if(p.getDdeath() == null )count++;
		}
		return count;
	}
}
