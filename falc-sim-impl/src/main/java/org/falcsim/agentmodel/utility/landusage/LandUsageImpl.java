package org.falcsim.agentmodel.utility.landusage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.service.domain.RelocationParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.utility.util.UtilitiesUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class LandUsageImpl extends LandUsage {

	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private UtilitiesUtil uu;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private RunParameters rp;
	@Autowired
	private RelocationParameters relocParameters;
	
	private boolean initialized = false;
	
	private static final Float landRestrictionInterval = 0.001f;
	
	private static final String  public_schema = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDefaultSchema();
	private static final String  fa_m2_per_person_table_name = "landusage_m2_per_person";
	private static final String  fa_m2_per_person_column_name = "sector";
	private static final String  fa_m2_per_person_column_prefix = "u";
	private static final String  uf_assumtions_table_name = "landusage_uf_assumptions";
	private static final String  uf_assumtions_column_name = "code";
	private static final String  bzr_calibration_table_name = "landusage_bzr_calibration";
	private static final String  bzr_calibration_column_name = "locid";
		
	private List<Location> locs = null;
	private Map<Integer, Map<String, ?>> fa_m2_per_person;
	private Map<Integer, Map<String, ?>> uf_assumtions;
	private Map<Integer, Map<String, ?>> bzr_calibration;
	private Map<Integer, Map<Integer, Double>> building_zone_regulation;
	
	private Map<Integer, Location> locationMap;
	
	@Override
	public double getLandRestrictionInterval(){
		return relocParameters.getLandLimitProbability() == 0 ? landRestrictionInterval : relocParameters.getLandLimitProbability();
	}
	
	@Override
	public boolean checkLandUsageRes(int locid){
		if(!relocParameters.getLandUseLimitationOn())
			return false;
		Location loc = locationMap.get(locid);
		if(loc.getUsedFloorAreaRes() < loc.getMaxFloorAreaRes()) return false;
		else{
			if(loc.getLU_overlimit_H() == null || !loc.getLU_overlimit_H() ){
				synchronized(loc){
					loc.setLU_overlimit_H(true);
				}
			}			
		}
		return true;
	}
	
	@Override
	public boolean checkLandUsageWrk(int locid){
		if(!relocParameters.getLandUseLimitationOn())return false;
		Location loc = locationMap.get(locid);
		if(loc.getUsedFloorAreaWrk() < loc.getMaxFloorAreaWrk()) return false;
		else{
			if(loc.getLU_overlimit_B() == null || !loc.getLU_overlimit_B() ){
				synchronized(loc){
					loc.setLU_overlimit_B(true);
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean checkLandUsageAll(int locid){
		if(!relocParameters.getLandUseLimitationOn())
			return false;
		Location loc = locationMap.get(locid);
		if(loc.getUsedFloorAreaAll() < loc.getMaxFloorAreaAll()) return false;
		else{
			if(loc.getLU_overlimit_T() == null || !loc.getLU_overlimit_T() ){
				synchronized(loc){
					loc.setLU_overlimit_T(true);
				}
			}		
/*		
			if(loc.getLU_overlimit_H() == null || !loc.getLU_overlimit_H() ){
				synchronized(loc){
					loc.setLU_overlimit_H(true);
				}
			}
			if(loc.getLU_overlimit_B() == null || !loc.getLU_overlimit_B() ){
				synchronized(loc){
					loc.setLU_overlimit_B(true);
				}
			}
			*/
		}
		return true;
	}
		
	@Override
	public double getMaxLandUsageResidents(Location loc){
		Map<Integer, Double> map = building_zone_regulation.get(loc.getId());
		double maxtotal = 0.;
		if(map != null){
			for(Integer code : map.keySet()){
				double maxRes = map.get(code) * getMaxUFResidential(code);
				maxtotal += maxRes;
			}
		}
		else{
			logger.error("MaxResidents - TABLE: building_zone_regulation, location not found: " + loc.getName() );
		}
		double bzr_cal_factor = getMaxBZRcalibrationResidents(loc.getId());
		loc.setMaxFloorAreaRes(maxtotal + bzr_cal_factor);
		return maxtotal + bzr_cal_factor;
	}
	
	@Override
	public double getCurrentLandUsageResidents(Location loc){
		int pcount = 0;
		for(Household h : loc.getHouseholds()){
			if(h.getDclosing() == null ) pcount += getLivePersons( h.getPersons() );
		}
		int currYear = sp.getCurrentYear() == null ? rp.getApp_theYear() : sp.getCurrentYear();	
		double m2_per_person = getM2perPerson(loc.getLandtype(), currYear);
		loc.setUsedFloorAreaRes(pcount * m2_per_person);
		return loc.getUsedFloorAreaRes();
	}
	
	@Override
	public double getMaxLandUsageWorkers(Location loc){
		Map<Integer, Double> map = building_zone_regulation.get(loc.getId());
		double maxtotal = 0.;
		if(map != null){
			for(Integer code : map.keySet()){
				double maxWrk = map.get(code) * getMaxUFWorkers(code);
				maxtotal += maxWrk;
			}
		}
		else{
			logger.error("MaxWorkers - TABLE: building_zone_regulation, location not found: " + loc.getName());
		}
		double bzr_cal_factor = getMaxBZRcalibrationWorkers(loc.getId());
		loc.setMaxFloorAreaWrk(maxtotal + bzr_cal_factor);
		return maxtotal + bzr_cal_factor;
	}
	
	@Override
	public double getCurrentLandUsageWorkers(Location loc){
		double currLandUsageTotal = 0.;
		Map<Integer, Integer> bcountTot = new HashMap<Integer, Integer>();
		for(Business b : loc.getBusinesses()){
			if(b.getDclosing() == null ){
				int bcount = getLivePersons( b.getPersons() );
				if(bcountTot.containsKey(b.getType_1())){
					bcount += bcountTot.get(b.getType_1());
					bcountTot.put(b.getType_1(), bcount);
				}
				else bcountTot.put(b.getType_1(), bcount);
			}
		}
		int currYear = sp.getCurrentYear() == null ? rp.getApp_theYear() : sp.getCurrentYear();
		for(Integer sector : bcountTot.keySet()){
			double m2_per_person = getM2perPerson(sector, currYear);
			currLandUsageTotal += m2_per_person * bcountTot.get(sector);
		}
		loc.setUsedFloorAreaWrk(currLandUsageTotal);
		return currLandUsageTotal;
	}
	
	private int getLivePersons(List<Person> list){
		int count = 0;
		for(Person p : list){
			if(p.getDdeath() == null )count++;
		}
		return count;
	}
	
	@Override
	public double getMaxLandUsage(Location loc) {
		if(loc.getId() == 26104){
			int a = 0;
		}
		Map<Integer, Double> map = building_zone_regulation.get(loc.getId());
		double maxtotal = 0.;
		if(map != null){
			for(Integer code : map.keySet()){
				double maxAll = map.get(code) * getMaxUFAll(code);
				maxtotal += maxAll;
			}
		}
		else{
			logger.error("MaxResidents - TABLE: building_zone_regulation, location not found: " + loc.getName() );
		}
		double bzr_cal_factor = getMaxBZRcalibrationAll(loc.getId());
		loc.setMaxFloorAreaAll(maxtotal + bzr_cal_factor);
		return maxtotal + bzr_cal_factor;
	}
	
//	public double getMaxLandUsageOld(Location loc) {
//		double res = getMaxLandUsageResidents(loc);
//		double wrk = getMaxLandUsageWorkers(loc);
//		getCurrentLandUsage(loc);
//		double maxAll = loc.getUsedFloorAreaAll() == 0 ? 0 :
//				res * loc.getUsedFloorAreaRes() / loc.getUsedFloorAreaAll()
//				+ wrk * loc.getUsedFloorAreaWrk() / loc.getUsedFloorAreaAll()
//				+ (res < wrk ? res : wrk);
//		loc.setMaxFloorAreaAll(maxAll);
//		return maxAll;
//	}

	@Override
	public double getCurrentLandUsage(Location loc) {
		double res = getCurrentLandUsageResidents(loc);
		double wrk = getCurrentLandUsageWorkers(loc);
		loc.setUsedFloorAreaAll(res + wrk);
		return res + wrk;
	}

	@Override
	public void updateCurrentLandUsageResidents(int sourceLocId, int destLocId, Household h)
	{
		if(relocParameters.getLimitationCheckEachMoveOn()){
			if(sourceLocId != destLocId){
				Location loc = locationMap.get(sourceLocId);
				Location locNew = locationMap.get(destLocId);
				int currYear = sp.getCurrentYear();	
					
				double m2_per_person = getM2perPerson(loc.getLandtype(), currYear);
				double landUsageChange = getLivePersons( h.getPersons() ) * m2_per_person;
			
				synchronized(loc){
					double currRes = loc.getUsedFloorAreaRes();
					double usedFloorAreaAll = loc.getUsedFloorAreaAll();
					loc.setUsedFloorAreaRes(currRes - landUsageChange);
					loc.setUsedFloorAreaAll(usedFloorAreaAll - landUsageChange);
					
//					double maxAll = loc.getUsedFloorAreaAll() == 0 ? 0 :
//							loc.getMaxFloorAreaRes() * loc.getUsedFloorAreaRes() / loc.getUsedFloorAreaAll() + 
//							loc.getMaxFloorAreaWrk() * loc.getUsedFloorAreaWrk() / loc.getUsedFloorAreaAll() +
//							(loc.getMaxFloorAreaRes() < loc.getMaxFloorAreaWrk() ? loc.getMaxFloorAreaRes() : loc.getMaxFloorAreaWrk() );
//					loc.setMaxFloorAreaAll(maxAll);
				}
				
				m2_per_person = getM2perPerson(locNew.getLandtype(), currYear);
				landUsageChange = getLivePersons( h.getPersons() ) * m2_per_person;
				
				synchronized(locNew){
					double currResNew = locNew.getUsedFloorAreaRes();
					double usedFloorAreaAllnew = locNew.getUsedFloorAreaAll();
					locNew.setUsedFloorAreaRes(currResNew + landUsageChange);
					locNew.setUsedFloorAreaAll(usedFloorAreaAllnew + landUsageChange);
//					double maxAllNew = locNew.getUsedFloorAreaAll() == 0 ? 0 :
//							locNew.getMaxFloorAreaRes() * locNew.getUsedFloorAreaRes() / locNew.getUsedFloorAreaAll() + 
//							locNew.getMaxFloorAreaWrk() * locNew.getUsedFloorAreaWrk() / locNew.getUsedFloorAreaAll() +
//							(locNew.getMaxFloorAreaRes() < locNew.getMaxFloorAreaWrk() ? locNew.getMaxFloorAreaRes() : locNew.getMaxFloorAreaWrk() );
//					locNew.setMaxFloorAreaAll(maxAllNew);
				}
			}
		}
	}
	
	@Override
	public void updateCurrentLandUsageWorkers(int sourceLocId, int destLocId, Business b)
	{
		if(relocParameters.getLimitationCheckEachMoveOn()){
			if(sourceLocId != destLocId){
				Location loc = locationMap.get(sourceLocId);
				Location locNew = locationMap.get(destLocId);
				int currYear = sp.getCurrentYear();	
				
				int sector = b.getType_1();
				double m2_per_person = getM2perPerson(sector, currYear);
				double landUsageChange = m2_per_person * getLivePersons( b.getPersons() );
			
				synchronized(loc){
					double currWrk = loc.getUsedFloorAreaWrk();
					double usedFloorAreaAll = loc.getUsedFloorAreaAll();
					loc.setUsedFloorAreaWrk(currWrk - landUsageChange);
					loc.setUsedFloorAreaAll(usedFloorAreaAll - landUsageChange);
//					double maxAll = loc.getUsedFloorAreaAll() == 0 ? 0 :
//							loc.getMaxFloorAreaRes() * loc.getUsedFloorAreaRes() / loc.getUsedFloorAreaAll() + 
//							loc.getMaxFloorAreaWrk() * loc.getUsedFloorAreaWrk() / loc.getUsedFloorAreaAll() +
//							(loc.getMaxFloorAreaRes() < loc.getMaxFloorAreaWrk() ? loc.getMaxFloorAreaRes() : loc.getMaxFloorAreaWrk() );
//					loc.setMaxFloorAreaAll(maxAll);
				}
				
				synchronized(locNew){
					double currWrkNew = locNew.getUsedFloorAreaWrk();
					double usedFloorAreaAllnew = locNew.getUsedFloorAreaAll();
					locNew.setUsedFloorAreaWrk(currWrkNew + landUsageChange);
					locNew.setUsedFloorAreaAll(usedFloorAreaAllnew + landUsageChange);
//					double maxAllNew = locNew.getUsedFloorAreaAll() == 0 ? 0 :
//								locNew.getMaxFloorAreaRes() * locNew.getUsedFloorAreaRes() / locNew.getUsedFloorAreaAll() + 
//								locNew.getMaxFloorAreaWrk() * locNew.getUsedFloorAreaWrk() / locNew.getUsedFloorAreaAll() +
//								(locNew.getMaxFloorAreaRes() < locNew.getMaxFloorAreaWrk() ? locNew.getMaxFloorAreaRes() : locNew.getMaxFloorAreaWrk() );
//					locNew.setMaxFloorAreaAll(maxAllNew);
				}
			}
		}
	}
	
	
	private void clearValues(List<Location> locs){
		for(Location loc : locs){
			loc.setMaxFloorAreaAll(0.);
			loc.setMaxFloorAreaRes(0.);
			loc.setMaxFloorAreaWrk(0.);
			loc.setUsedFloorAreaAll(0.);
			loc.setUsedFloorAreaRes(0.);
			loc.setUsedFloorAreaWrk(0.);
		}	
	}
	
	@Override
	public void init(List<Location> locs, boolean calculate) throws RCCustomException {
		this.locs = locs;
		loadM2Map();
		
		locationMap = new HashMap<Integer, Location>();
		for(Location loc : locs){ locationMap.put(loc.getId(), loc);}
		clearValues(locs);
		
		if(!initialized){
			loadMaps();
			initialized = true;
		}	
		if(calculate){
			calculate(locs);
		}
	}

	@Override
	public void calculate(List<Location> locs){
		clearValues(locs);
		for(Location loc : locs){
			/*CommunicationClass.reportProgress();*/
			getMaxLandUsageResidents(loc);
			getMaxLandUsageWorkers(loc);
			
			getCurrentLandUsage(loc);
			getMaxLandUsage(loc); //this calculate all, because all values are needed to calc this
		}
	}
	
	@Override
	public void reset() {
		locs = null;
		fa_m2_per_person = null;
		uf_assumtions = null;
		building_zone_regulation = null;
		locationMap = null;
		initialized = false;
	}

	private void loadM2Map() throws RCCustomException{	
		List<String> lsColNames = new ArrayList<>();
		lsColNames.add(fa_m2_per_person_column_name);
		
		lsColNames.add(fa_m2_per_person_column_prefix + String.valueOf(2000));
		if(!rp.getApp_theYear().equals(2000) && !rp.getApp_theYear().equals( sp.getCurrentYear() )){
			lsColNames.add(fa_m2_per_person_column_prefix + String.valueOf(rp.getApp_theYear()));
		}
		if(sp.getCurrentYear() != null ){
			int currYear = sp.getCurrentYear();
			lsColNames.add(fa_m2_per_person_column_prefix + String.valueOf(currYear));
		}
		// <!-- modified by mifr
		fa_m2_per_person = generalDao.loadFieldsIntoAMap(fa_m2_per_person_column_name, "", public_schema, fa_m2_per_person_table_name, lsColNames);
		// -->
	}
	
	private void loadMaps() throws RCCustomException{	
		List<String> lsColNames = new ArrayList<>();
		
		lsColNames.add(uf_assumtions_column_name);
		lsColNames.add("uf");
		lsColNames.add("max_residential");
		lsColNames.add("max_work");
		// <!-- modified by mifr
		uf_assumtions = generalDao.loadFieldsIntoAMap(uf_assumtions_column_name, "", public_schema, uf_assumtions_table_name, lsColNames);	
		// -->
		
		lsColNames.clear();
		lsColNames.add(bzr_calibration_column_name);
		lsColNames.add("obzfa_r");
		lsColNames.add("obzfa_j");
		lsColNames.add("obzfa_r_j");
		// <!-- modified by mifr
		bzr_calibration = generalDao.loadFieldsIntoAMap(bzr_calibration_column_name, "", public_schema, bzr_calibration_table_name, lsColNames);
		// -->
		
		lsColNames.clear();
		lsColNames = null;
		
		building_zone_regulation = uu.loadBuildingZoneRegulationData(locs);
	}
	
	private double getM2perPerson(int landType, int currYear){
		String column = fa_m2_per_person_column_prefix + String.valueOf(currYear);
		Map<String, ?> map = fa_m2_per_person.get(landType );
		Object o = map.get(column);
		return (Double)o;
	}
	
	private double getMaxUFWorkers(int code){
		double retval = 1;
		Map<String, ?> map = uf_assumtions.get(code);
		if(map == null ){
			logger.warn("TABLE: uf_assumtions, code not found");
		}
		Object o1 = map.get("uf");
		Object o2 = map.get("max_work");
		retval = (Double)o1 * (Double)o2;
		return retval;
	}
	
	private double getMaxUFResidential(int code){
		double retval = 1;
		Map<String, ?> map = uf_assumtions.get(code);
		if(map == null ){
			logger.warn("TABLE: uf_assumtions, code not found");
		}
		Object o1 = map.get("uf");
		Object o2 = map.get("max_residential");
		retval = (Double)o1 * (Double)o2;
		return retval;
	}
	
	private double getMaxUFAll(int code){
		double retval = 1;
		Map<String, ?> map = uf_assumtions.get(code);
		if(map == null ){
			logger.warn("TABLE: uf_assumtions, code not found");
		}
		Object o1 = map.get("uf");
		retval = (Double)o1;
		return retval;
	}
	
	private double getMaxBZRcalibrationWorkers(int locid){
		double retval = 0;
		Map<String, ?> map = bzr_calibration.get(locid);
		if(map == null ){
			logger.warn("TABLE: MAX landusage Calibration table, location not found");
		}
		else{
			Object objval = map.get("obzfa_j");
			retval = objval == null ? 0 : (Double)objval;
		}
		return retval;
	}
	
	private double getMaxBZRcalibrationResidents(int locid){
		double retval = 0;
		Map<String, ?> map = bzr_calibration.get(locid);
		if(map == null ){
			logger.warn("TABLE: MAX landusage Calibration table, location not found");
		}
		else{
			Object objval = map.get("obzfa_r");
			retval = objval == null ? 0 : (Double)objval;
		}
		return retval;
	}
	
	private double getMaxBZRcalibrationAll(int locid){
		double retval = 0;
		Map<String, ?> map = bzr_calibration.get(locid);
		if(map == null ){
			logger.warn("TABLE: MAX landusage Calibration table, location not found");
		}
		else{
			Object objval = map.get("obzfa_r_j");
			retval = objval == null ? 0 : (Double)objval;
		}
		return retval;
	}
	
	
	
}
