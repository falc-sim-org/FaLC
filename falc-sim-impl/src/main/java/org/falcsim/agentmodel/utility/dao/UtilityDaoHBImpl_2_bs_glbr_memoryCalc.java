package org.falcsim.agentmodel.utility.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.dao.AbstractDao;
import org.falcsim.agentmodel.demography.methods.DemographyMethods;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.service.domain.RelocationParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;
import org.falcsim.agentmodel.sublocations.domain.SublocationsParameters;
import org.falcsim.agentmodel.util.xml.UtilitiesXMLConstants;
import org.falcsim.agentmodel.utility.domain.UtilityGrowthChecker;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.falcsim.agentmodel.utility.domain.UtilitySublocations;
import org.falcsim.agentmodel.utility.landusage.LandUsage;
import org.falcsim.agentmodel.utility.util.MathExpressionParser;
import org.falcsim.agentmodel.utility.util.MathExpressionParserImplParsii;
import org.springframework.beans.factory.annotation.Autowired;

import parsii.tokenizer.ParseException;

public class UtilityDaoHBImpl_2_bs_glbr_memoryCalc extends AbstractDao implements UtilityDaoHB {

	public enum RelocationType {
		 HOUSEHOLDS, BUSINESSES, MIGRANTS; 
	}
	
	@Autowired
	private UtilityParameters up2;
	@Autowired
	private SublocationsParameters sublocpar;
	/**
	 * universe functionality
	 */
	@Autowired
	private UniverseServiceUtil universeService;
	@Autowired
	private UtilitySublocations usl;
	@Autowired
	private LandUsage landLimits;
	@Autowired
	private UtilityGrowthChecker ugch;
	@Autowired
	private RelocationParameters rp;
	@Autowired
	private DemographyMethods dm;
	@Autowired
	private ServiceParameters sp;
	
	//private String  locTableName;
	private static double landLimitExceedConst = Double.MAX_VALUE;
	
	private static final  String H_FUNCTIONS=UtilitiesXMLConstants.H_FUNCTIONS;
	private static final  String B_FUNCTIONS=UtilitiesXMLConstants.B_FUNCTIONS;
	private static final  String PARAMETERS=UtilitiesXMLConstants.PARAMETERS;
	
	private Map<String, String> h_function_map;
	private Map<String, String> b_function_map;
	private Map<Integer, Map<String, Double>> b_location_base_function_map;
	private Map<Integer, Double> h_location_base_function_map;
	
	private static final  String SEP=RCConstants.SEP;
	private static final  String BASE = "BASE";
	private static final  String UTFB = "UTF_B_type";
	private static final  String MOVE = UtilitiesXMLConstants.MOVE;
	private static final  String X = UtilitiesXMLConstants.X;
	
	private static final Integer m2km = 1; //000; - now we have dist table in KM
	private Map<Integer, Map<Integer, Integer>> hhSizes;
	private Map<Integer, Map<Integer, Integer>> hhAges;
	private Map<Integer, Map<Integer, Double>> sameSectorMap;
	
	private List<String> relocationParams;
	private Map<Integer, Map<String, Double>> relocationCache;
	
	private Map<Thread, MathExpressionParser> parserThreadMapH;
	private Map<Thread, MathExpressionParser> parserThreadMapM;
	private Map<Thread, MathExpressionParser> parserThreadMapB;
	
	private Boolean initialized = false;
	
	@Override
	public void reset(){
		initialized = false;
	}
	
	@Override
	public void init(List<Location> locs) {
		logger.info("Initializing UtilityDaoHBImpl_2A - bs_glbr_cached memoryCalc...");
			
		if (!up2.isDistanceMapInitialized()){
			logger.info("     - Distance map");
			up2.initDistanceMap(locs);
		}
		//TODO: It is important to load sublocations each year, because we have different set for each year
		//if (!usl.getInitialized()){ //managed by SwitchData
		logger.info("     - Sublocations map");
		usl.init(locs);
		//}
			
		if(!initialized){	
			logger.info("     - Functions(Initializing UtilityDaoHBImpl_2A - bs_glbr_cached memoryCalc)");
			h_function_map = up2.getUtilMap().get(H_FUNCTIONS);
			b_function_map = up2.getUtilMap().get(B_FUNCTIONS);			
			initialized = true;
		}
		else{
			logger.info("UtilityDaoHBImpl_2A - bs_glbr_cached memoryCalc...allready Initialized");	
		}
		
		//these data must be calculated each year
		logger.info("     - Select numbers");
		hhSizes = selectNumberOfHouseholdsByLocationListAndNumberOfPersons(locs);
		hhAges = selectNumberOfHouseholdsByLocationAndOldestPersonGroup(locs);
		
		logger.info("     - fillsameSectorMap");
		sameSectorMap = fillsameSectorMap(locs);
		logger.info("     - cache reloc function data");
		fillrelocationCachedData(locs);
		
		parserThreadMapH = new HashMap<Thread, MathExpressionParser>();
		parserThreadMapM = new HashMap<Thread, MathExpressionParser>();
		parserThreadMapB = new HashMap<Thread, MathExpressionParser>();
	}

	@Override
	public List<RealIdProbInterval> assignProbsForB(final Business b, final Location loc, final List<Location> locs) {
		return assignProbsForB(b, loc, locs, false);
	}
	
	@Override
	public List<RealIdProbInterval> assignProbsForB(final Business b, final Location loc, final List<Location> locs, final boolean allowTheSameLocation) {
		List<RealIdProbInterval> ripis = new ArrayList<RealIdProbInterval>();

		List<Object[]> lsu = new ArrayList<Object[]>();
		try {	
			Integer blocId = b.getLocationId();
			for (Location lic:locs){
				if (allowTheSameLocation || (lic.getId() != blocId)){
					if(!sublocpar.getActive() || usl.isBusinessSublocation(loc.getId(), lic.getId())){
						if(lic.getId() == blocId ||	!rp.getLocationGrowthLossActive() || ugch.checkLocationBusinesses(lic ) ){
							//Float distance = up2.getDistanceMap().get(loc.getId()+SEP+lic.getId());
							double distance = up2.getDistance(loc.getId(), lic.getId()); // /m2km;
							double time = up2.getTime(loc.getId(), lic.getId()); // /m2km;
							
							if(rp.getRelocationMinDistance() > 0){								
								if(distance < rp.getRelocationMinDistance()) distance = rp.getRelocationMinDistance();
							}																							
							if(distance < 0) distance = rp.getRelocationMinDistance() > 0 ? rp.getRelocationMinDistance() : 2.;
							if(time < 0) time = rp.getRelocationMinDistance() > 0 ? rp.getRelocationMinDistance() : 2.;	
							if(rp.getRelocationMaxDistance() > 0){
								if(distance > rp.getRelocationMaxDistance()) distance = rp.getRelocationMaxDistance();
								if(time > rp.getRelocationMaxDistance()) time = rp.getRelocationMaxDistance();
							}									
									
							int type_2_str =  b.getType_2() == null ? 0 : b.getType_2();
							double bs_glbr_q_norm = getSameSectorMapValue(blocId, b.getType_1());
							
							lsu.add( getProbsCalcForB(lic.getId(), distance, time, b.getType_1(), type_2_str, bs_glbr_q_norm));
						}
					}
				}
			}
			
			Double tot_old = 0.0;
			Double tot=0.0;
			for (Object[] lu : lsu){
				Integer id = (Integer) lu[0];
				Double util = lu[1]!= null ? ((Number) lu[1]).doubleValue() : 0.0;
				RealIdProbInterval rip = new RealIdProbInterval();
				rip.setId(id);
				tot_old = tot;
				tot += (util == landLimitExceedConst ? landLimits.getLandRestrictionInterval() : Math.exp(util));
				ripis.add(new RealIdProbInterval(id, tot_old, tot));
			}	
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return ripis;
	}
	
	private Object[] getProbsCalcForB(Integer locId, double distance, double time, int sector, int type_2_str, double bs_glbr_q_norm){
		Object[] tmp = new Object[2];
		tmp[0] = locId;
		
		try{
			Double val = 0.;
			if(landLimits.checkLandUsageWrk(locId) || landLimits.checkLandUsageAll(locId) ){
				val = landLimitExceedConst; //landLimits.getLandRestrictionInterval();
			}
			else{	
				Double baseval = b_location_base_function_map.get(locId).get(BASE +SEP+ UTFB + type_2_str +SEP+ sector);
				if(baseval == null){
					baseval = b_location_base_function_map.get(locId).get(BASE +SEP+ UTFB + type_2_str +SEP+ X);
				}
						
				MathExpressionParser parser = hasMathParser(RelocationType.BUSINESSES);
				Map<String,Double> appParamMap = new HashMap<String,Double>();
				if(parser == null){
					Map<String,Double> paramMap = relocationCache.get(locId);
					
					String queryStr_move = b_function_map.get(MOVE +SEP+ UTFB + type_2_str +SEP+ sector);
					if (queryStr_move == null){
						queryStr_move = b_function_map.get(MOVE +SEP+ UTFB + type_2_str +SEP+ X);
					}
					
					queryStr_move = queryStr_move.replace("_bs_glbr_q_norm", "bs_glbr_q_norm").replace("_distance", "distance").replace("_time", "time");
					List<String> appParams = new ArrayList<String>(Arrays.asList("basevalue", "bs_glbr_q_norm", "distance", "time"));
					appParams.addAll(paramMap.keySet());
					parser = getMathParser(RelocationType.BUSINESSES, "basevalue + " + queryStr_move, appParams.toArray( new String[0]) );
					appParamMap.putAll(paramMap);
				}

				appParamMap.put("basevalue", baseval);
				appParamMap.put("bs_glbr_q_norm", bs_glbr_q_norm);
				appParamMap.put("distance", distance);
				appParamMap.put("time", time);
				
				val = parser.evaluate(appParamMap);
			}
			
			tmp[1] = val;
		}
		catch(Exception e){
			tmp[1] = landLimitExceedConst;
			logger.error(e.getMessage(), e);
			//TODO: finish exception process
		}
		return tmp;	
	}
	
	private MathExpressionParser hasMathParser(RelocationType type){
		if(type == RelocationType.HOUSEHOLDS)
			return parserThreadMapH.containsKey(Thread.currentThread()) ? parserThreadMapH.get(Thread.currentThread()) : null;
		else if(type == RelocationType.MIGRANTS)
			return parserThreadMapM.containsKey(Thread.currentThread()) ? parserThreadMapM.get(Thread.currentThread()) : null;
			else
				return parserThreadMapB.containsKey(Thread.currentThread()) ? parserThreadMapB.get(Thread.currentThread()) : null;
	}
	
	private MathExpressionParser getMathParser(RelocationType type, String expression, String[] params) throws ParseException{
		MathExpressionParser parser = null;
		if(type == RelocationType.HOUSEHOLDS){
			if(parserThreadMapH.containsKey(Thread.currentThread()) ){
				parser = parserThreadMapH.get(Thread.currentThread());
			}
			else{
				synchronized(parserThreadMapH){
					parser = new MathExpressionParserImplParsii();
					parser.init(expression, params);
					parserThreadMapH.put(Thread.currentThread(), parser);
				}
			}
		}
		else{
			if(type == RelocationType.MIGRANTS){
				if(parserThreadMapM.containsKey(Thread.currentThread()) ){
					parser = parserThreadMapM.get(Thread.currentThread());
				}
				else{
					synchronized(parserThreadMapM){
						parser = new MathExpressionParserImplParsii();
						parser.init(expression, params);
						parserThreadMapM.put(Thread.currentThread(), parser);
					}
				}
			}
			else{
				if(parserThreadMapB.containsKey(Thread.currentThread()) ){
					parser = parserThreadMapB.get(Thread.currentThread());
				}
				else{
					synchronized(parserThreadMapB){
						parser = new MathExpressionParserImplParsii();
						parser.init(expression, params);
						parserThreadMapB.put(Thread.currentThread(), parser);
					}
				}
			}
		}
		return parser;
	}
	
	private Object[] getProbsCalcForH(boolean migrantedHH, Integer locId, double distance_wp, double distance, double time_wp, double time, 
			Float sameSizeHHs_rate, Integer finance_1, double sameAgeGroupHHs_rate){
		Object[] tmp = new Object[2];
		tmp[0] = locId;	
		try{
			Double val = 0.;
			if( landLimits.checkLandUsageRes(locId) || landLimits.checkLandUsageAll(locId) ){
				val =  landLimitExceedConst; //landLimits.getLandRestrictionInterval();
			}
			else{
				Double baseval = h_location_base_function_map.get(locId);
												
				MathExpressionParser parser = null;
				if(!migrantedHH) parser = hasMathParser(RelocationType.HOUSEHOLDS);
				else  parser = hasMathParser(RelocationType.MIGRANTS);
					
				Map<String,Double> appParamMap = new HashMap<String,Double>();
				Map<String,Double> paramMap = relocationCache.get(locId);
				appParamMap.putAll(paramMap);
				if(parser == null){					
					String queryStr_move = "";
					if(!migrantedHH) queryStr_move = h_function_map.get(UtilitiesXMLConstants.MOVE +SEP+ X);
					else queryStr_move = h_function_map.get(UtilitiesXMLConstants.MOVE_MIGR +SEP+ X);
					
					queryStr_move = queryStr_move.replace("_income", "income").replace("_portion_hh_ss", "portion_hh_ss").replace("_distance_wp", "distance_wp")
							.replace("_distance", "distance").replace("_portion_hh_sa", "portion_hh_sa").replace("_time_wp", "time_wp").replace("_time", "time");
					List<String> appParams = new ArrayList<String>(Arrays.asList("basevalue", "income", "portion_hh_ss", "distance_wp", "distance", "portion_hh_sa", "time_wp", "time"));
					appParams.addAll(paramMap.keySet());
					if(!migrantedHH) parser = getMathParser(RelocationType.HOUSEHOLDS, "basevalue + " + queryStr_move, appParams.toArray( new String[0]) );
					else parser = getMathParser(RelocationType.MIGRANTS, "basevalue + " + queryStr_move, appParams.toArray( new String[0]) );
					
					//appParamMap.putAll(paramMap);
				}							
				appParamMap.put("basevalue", baseval);
				appParamMap.put("income", finance_1.doubleValue());
				appParamMap.put("portion_hh_ss", sameSizeHHs_rate.doubleValue());
				appParamMap.put("distance_wp", distance_wp);
				appParamMap.put("distance", distance);
				appParamMap.put("portion_hh_sa", sameAgeGroupHHs_rate);
				appParamMap.put("time_wp", time_wp);
				appParamMap.put("time", time);
				
				val = parser.evaluate(appParamMap);
				//val = 0.01;
			}
			
			tmp[1] = val;
		}
		catch(Exception e){
			tmp[1] = landLimitExceedConst;
			logger.error(e.getMessage(), e);
			//TODO: finish exception
		}
		return tmp;	
	}
		
	@Override
	public List<RealIdProbInterval> assignProbsForH(final Household h, final Location loc, final List<Location> locs, final Map<Integer, Business> bussmap, 
			final boolean allowTheSameLocation, final boolean migrantsHH) {
		List<RealIdProbInterval> ripis = new ArrayList<RealIdProbInterval>();
		
		final Integer sameSizeHHsInLoc = hhSizes.get(loc.getId()).get(h.getPersons().size());
		final Integer totalHHsInLoc = loc.getHouseholds().size();
		Integer locIdMainEarner = findBusinessLocationOfHouseholdEarner(h, bussmap);		
		locIdMainEarner = findBusinessLocationOfHouseholdOldestEarner(h, bussmap, sp.getCurrentYear());

		Float sameSizeHHs_rate = 0f;
		if (totalHHsInLoc > 0){
			sameSizeHHs_rate = (sameSizeHHsInLoc==null ? new Integer(0) : sameSizeHHsInLoc).floatValue()/totalHHsInLoc.floatValue();
		}
		Integer finance_1 = h.getFinance_1()==null ? 100000 : h.getFinance_1();
		
		double sameAgeGroupHHs_rate = 0;
		//if(migrantsHH){
			int hhAge = getAgeGroup( getAgeOfOldestPersonInHouseholds(h, sp.getCurrentYear()) );
			Integer sameAgeInLoc = hhAges.get(loc.getId()).get(hhAge);		
			sameAgeGroupHHs_rate = (sameAgeInLoc == null ? new Integer(0) : sameAgeInLoc).doubleValue()/totalHHsInLoc.doubleValue();
		//}
		
		List<Object[]> lsu = new ArrayList<Object[]>();
		try {	
			Integer hlocId = h.getLocationId();
			for (Location lic:locs){
				if (allowTheSameLocation || (lic.getId() != hlocId)){				
					//if business location of main earner is null, check against household location
					if (locIdMainEarner == null){
						locIdMainEarner = hlocId;
					}
					//if this is the same or pass thru sublocations set
					if(!sublocpar.getActive() || usl.isHouseholdSublocation( hlocId, lic.getId() )){ //locIdMainEarner == null || //lic.getId() == hlocId || 
						
						if(lic.getId() == hlocId ||	!rp.getLocationGrowthLossActive() || ugch.checkLocationHouseholds(lic) ){
							
							double distance_wp;
							double time_wp;
							if (locIdMainEarner != null){
								try {
									distance_wp = up2.getDistance(lic.getId(), locIdMainEarner); ///m2km;
									time_wp = up2.getTime(lic.getId(), locIdMainEarner);
								} catch (Exception e){
									logger.warn("The working location of this households' main earner is not present in the distance map");
									distance_wp = 0.;
									time_wp = 0.;
								}
							}
							else {
								distance_wp = 0.;
								time_wp = 0.;
							}
							double distance = up2.getDistance(lic.getId(), hlocId); ///m2km;
							double time = up2.getTime(loc.getId(), lic.getId()); // /m2km;

							if(distance_wp < 0) distance_wp = rp.getRelocationMinDistance() > 0 ? rp.getRelocationMinDistance() : 2.;							
							if(distance < 0) distance = rp.getRelocationMinDistance() > 0 ? rp.getRelocationMinDistance() : 2.;
							if(time < 0) time = rp.getRelocationMinDistance() > 0 ? rp.getRelocationMinDistance() : 2.;
							if(time_wp < 0) time_wp = rp.getRelocationMinDistance() > 0 ? rp.getRelocationMinDistance() : 2.;
							
							if(rp.getRelocationMinDistance() > 0){
								if(distance_wp < rp.getRelocationMinDistance()) distance_wp = rp.getRelocationMinDistance();
								if(distance < rp.getRelocationMinDistance()) distance = rp.getRelocationMinDistance();
								if(time < rp.getRelocationMinDistance()) time = rp.getRelocationMinDistance();
								if(time_wp < rp.getRelocationMinDistance()) time_wp = rp.getRelocationMinDistance();
							}							
														
							lsu.add( getProbsCalcForH(migrantsHH, lic.getId(), distance_wp, distance, time_wp, time, sameSizeHHs_rate, finance_1, sameAgeGroupHHs_rate));
						}
					}
				}
			}
			
			Double tot_old = 0.0;
			Double tot=0.0;
			for (Object[] lu : lsu){
				Integer id = (Integer) lu[0];
				Double util = lu[1]!= null ? ((Number) lu[1]).doubleValue() : 0.0;
				RealIdProbInterval rip = new RealIdProbInterval();
				rip.setId(id);
				tot_old = tot;
				tot += (util == landLimitExceedConst ? landLimits.getLandRestrictionInterval() : Math.exp(util));
				ripis.add(new RealIdProbInterval(id, tot_old, tot));
			}	
			
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		
		return ripis;
	}
	
	@Override
	public Map<Integer, Map<Integer, Integer>> selectNumberOfHouseholdsByLocationListAndNumberOfPersons(List<Location> locs){
		Map<Integer, Map<Integer, Integer>> mp = new HashMap<Integer, Map<Integer, Integer>>();
		for (Location loc:locs){
			Map<Integer, Integer> mpl = new HashMap<Integer, Integer>();
						
			for(Household h : loc.getHouseholds()){
				int size = h.getPersons().size();
				int count = 1;
				if(mpl.containsKey(size)){
					count += mpl.get(size);
				}
				mpl.put(size, count);
			}
			
			mp.put(loc.getId(), mpl);
		}

		return mp;
	}
	
	private Person getOldestPersonInHouseholds(Household h, int currentYear){
		Person retP = null;
		int maxAge = 0;
		for(Person p : h.getPersons()){
			int age = dm.getAgeOfPerson(p, currentYear); 
			if(maxAge < age){
				maxAge = age;
				retP = p;
			}
		}				
		return retP;
	}	
	
	private int getAgeOfOldestPersonInHouseholds(Household h, int currentYear){
		int maxAge = 0;
		for(Person p : h.getPersons()){
			int age = dm.getAgeOfPerson(p, currentYear);
			if(maxAge < age) maxAge = age;
		}				
		return maxAge;
	}
	
	private int getAgeGroup(int age){
		int ageGroup = 0;
		if(age > 0){
			if(age < 40 ) ageGroup = 1;
			else if(age <= 65 ) ageGroup = 2;
			else if(age > 65 ) ageGroup = 3;
		}
		return ageGroup;
	}
	
	private Map<Integer, Map<Integer, Integer>> selectNumberOfHouseholdsByLocationAndOldestPersonGroup(List<Location> locs){
		Map<Integer, Map<Integer, Integer>> mp = new HashMap<Integer, Map<Integer, Integer>>();
		for (Location loc:locs){
			Map<Integer, Integer> mpl = new HashMap<Integer, Integer>();
						
			for(Household h : loc.getHouseholds()){
				if(h.getPersons().size() > 0){
					int oldestAge = getAgeOfOldestPersonInHouseholds(h, sp.getCurrentYear() );
					int agegroup = getAgeGroup(oldestAge);
					
					int count = 1;
					if(mpl.containsKey(agegroup)){
						count += mpl.get(agegroup);
					}
					mpl.put(agegroup, count);
				}
			}			
			mp.put(loc.getId(), mpl);
		}

		return mp;
	}	
	
	private Integer findBusinessLocationOfHouseholdEarner(Household h, final Map<Integer, Business> bussmap){
		
		Integer businessId=-1;
		Integer noBusinessId=-1;
		Integer income = -1;
		for (Person p : h.getPersons()){
			if (p.getBusinessId() != null ){
				try {
					if (p.getFinance_1()> income){
						income = p.getFinance_1();
						businessId = p.getBusinessId();					
					}
				} catch (NullPointerException e){
					logger.error(" Check whether some people have a business_id but no income");
				}
			}			
		}
		if (!noBusinessId.equals(businessId)){
			try{
				if(bussmap == null)
					return universeService.selectBusinessById(businessId).getLocationId();
				else
					return bussmap.get(businessId).getLocationId();
			}
			catch(RuntimeException e){
				logger.error(e.getMessage(), e);
			}
		}
		
		return null;
	}
	
	private Integer findBusinessLocationOfHouseholdOldestEarner(Household h, final Map<Integer, Business> bussmap, int currentYear){
		
		Integer businessId=-1;
		Integer noBusinessId=-1;

		int maxAge = 0;
		for(Person p : h.getPersons()){
			int age = dm.getAgeOfPerson(p, currentYear);			
			if(maxAge < age && p.getBusinessId() != null){
				maxAge = age;
				businessId = p.getBusinessId();
			}
		}				

		if (!noBusinessId.equals(businessId)){
			try{
				if(bussmap == null)
					return universeService.selectBusinessById(businessId).getLocationId();
				else
					return bussmap.get(businessId).getLocationId();
			}
			catch(RuntimeException e){
				logger.error(e.getMessage(), e);
			}
		}
		
		return null;
	}	
	
	
	private Map<Integer, Map<Integer, Double>> fillsameSectorMap(List<Location> locs){
		double norm_val_sub=-0.061;
		double norm_val_div = 0.043;
		double checkZeroDivide = 0.0001;
		
		Map<Integer, Map<Integer, Double>> mp = new HashMap<Integer, Map<Integer, Double>>();
		
		for (Location loc:locs){
			Map<Integer, Double> mpl = new HashMap<Integer, Double>();
			
			int workersTotal = 0;
			for(Business b : loc.getBusinesses()){
				int count = b.getPersons().size();
				int sector = b.getType_1();
				workersTotal += count;
				
				if(mpl.containsKey(sector)){
					count += mpl.get(sector);
				}
				mpl.put(sector, (double)count);
			}
			for(int sector : mpl.keySet()){
				double value = mpl.get(sector);
				value = value/(workersTotal + checkZeroDivide);
				value = (value - norm_val_sub) / norm_val_div;
				mpl.put(sector, value);
			}
			mp.put(loc.getId(), mpl);
		}

		return mp;
	}
	
	public Map<Integer, Map<String, Double>> prepareRelocationProbData(List <Location> locs, List<String> columns)
	{
		Map<Integer, Map<String, Double>> map = new HashMap<Integer, Map<String, Double>>();
		for(Location loc : locs){
			Map<String, Double> list = new HashMap<String, Double>();
			map.put(loc.getId(), list);
			for(String col : columns){
				if("res_tot".equals(col)) list.put(col, (double)loc.getActualResidents());
				else if("emp_tot".equals(col)) list.put(col, (double)loc.getActualWorkers());	
				else if("flat_rent".equals(col)) list.put(col, (double)loc.getFlat_rent());
				else if("av_1".equals(col)) list.put(col, (double)loc.getAv_1());
				else if("av_2".equals(col)) list.put(col, (double)loc.getAv_2());
				else if("av_3".equals(col)) list.put(col, (double)loc.getAv_3());
				else if("av_4".equals(col)) list.put(col, (double)loc.getAv_4());
				else if("av_5".equals(col)) list.put(col, (double)loc.getAv_5());
				else if("av_6".equals(col)) list.put(col, (double)loc.getAv_6());
				else if("av_7".equals(col)) list.put(col, (double)loc.getAv_7());
				else if("av_8".equals(col)) list.put(col, (double)loc.getAv_8());
				else if("av_9".equals(col)) list.put(col, (double)loc.getAv_9());
				else if("av_10".equals(col)) list.put(col, (double)loc.getAv_10());
				else if("av_11".equals(col)) list.put(col, (double)loc.getAv_11());
				else if("av_12".equals(col)) list.put(col, (double)loc.getAv_12());				
				else if("settlement_area".equals(col)) list.put(col, (double)loc.getSettlement_area());
				else if("urban_centre".equals(col)) list.put(col, (double)loc.getUrban_centre());
				else if("resident_landprice_n".equals(col)) list.put(col, (double)loc.getResident_landprice_n());
				else if("landuse_density_n".equals(col)) list.put(col, (double)loc.getLanduse_density_n());
				else if("university_degree_n".equals(col)) list.put(col, (double)loc.getUniversity_degree_n());
				else if("diversity_sector_n".equals(col)) list.put(col, (double)loc.getDiversity_sector_n());	
				else if("tax_holdingcomp_n".equals(col)) list.put(col, (double)loc.getTax_holdingcomp_n());
				else if("tax_partnership_n".equals(col)) list.put(col, (double)loc.getTax_partnership_n());
				else if("tax_companies_n".equals(col)) list.put(col, (double)loc.getTax_companies_n());
				else if("motorway_access".equals(col)) list.put(col, (double)loc.getMotorway_access());
				else if("railway_access".equals(col)) list.put(col, (double)loc.getRailway_access());
				else if("accessibility_t_n".equals(col)) list.put(col, (double)loc.getAccessibility_t_n());
				else if("bus_dev_mun_n".equals(col)) list.put(col, (double)loc.getBus_dev_mun_n());
				else if("bus_dev_cant_n".equals(col)) list.put(col, (double)loc.getBus_dev_cant_n());
				else if("var1".equals(col)) list.put(col, (double)loc.getVar1());
				else if("var2".equals(col)) list.put(col, (double)loc.getVar2());
				else if("var3".equals(col)) list.put(col, (double)loc.getVar3());
				else if("var4".equals(col)) list.put(col, (double)loc.getVar4());
				else if("var5".equals(col)) list.put(col, (double)loc.getVar5());
				else if("var6".equals(col)) list.put(col, (double)loc.getVar6());
				else if("var7".equals(col)) list.put(col, (double)loc.getVar7());
				else if("var8".equals(col)) list.put(col, (double)loc.getVar8());
				else if("var9".equals(col)) list.put(col, (double)loc.getVar9());
				else if("var10".equals(col)) list.put(col, (double)loc.getVar10());
			}
		}
		return map;
	}
	
	private void fillrelocationCachedData(List<Location> locs){
		relocationParams = new ArrayList<String>();
		for(String key : up2.getUtilMap().get(PARAMETERS).keySet()) relocationParams.add(key);
		//relocationCache = uu.loadRelocationProbData(locs, relocationParams);
		relocationCache = prepareRelocationProbData(locs, relocationParams);
		
		h_location_base_function_map = new HashMap<Integer, Double>();
		for(Integer locId : relocationCache.keySet() ){
			Map<String,Double> paramMap = relocationCache.get(locId);
			
			for(String key : h_function_map.keySet() ){
				if(key.indexOf(BASE +SEP+ X) == 0){
					double val = 0.;
					try{
						MathExpressionParserImplParsii parser = new MathExpressionParserImplParsii();
						parser.init(h_function_map.get(key), paramMap.keySet().toArray(new String[0]) );				
						val = parser.evaluate(paramMap);											
						
					}
					catch(Exception e){
						logger.error(e.getMessage(), e);
					}
					h_location_base_function_map.put(locId, val);
				}
			}	
		}
		
		b_location_base_function_map = new HashMap<Integer, Map<String, Double>>();
		for(Integer locId : relocationCache.keySet() ){
			Map<String,Double> paramMap = relocationCache.get(locId);

			Map<String,Double> map = new HashMap<String, Double>();
			b_location_base_function_map.put(locId, map);
			for(String key : b_function_map.keySet() ){
				if(key.indexOf(BASE +SEP+ UTFB) == 0){
					double val = 0.;
					try{
						MathExpressionParserImplParsii parser = new MathExpressionParserImplParsii();
						parser.init(b_function_map.get(key), (String[])paramMap.keySet().toArray(new String[0]) );				
						val = parser.evaluate(paramMap);						
						
					}
					catch(Exception e){
						logger.error(e.getMessage(), e);
					}
					map.put(key, val);
				}
			}
		}
	}
		
	private double getSameSectorMapValue(int locid, int sector ){
		Double val = sameSectorMap.get(locid).get(sector);
		if (val == null){
			return 0.;
		}
		return val;
	}
	
}
