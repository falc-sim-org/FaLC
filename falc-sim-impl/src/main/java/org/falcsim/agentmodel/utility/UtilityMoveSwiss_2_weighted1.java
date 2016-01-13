package org.falcsim.agentmodel.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.utility.constant.Mod1Constants;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.demography.methods.DemographyMethods;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.RelocationInfo;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.service.domain.RelocationParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.service.methods.SMethods;
import org.falcsim.agentmodel.service.methods.util.RunIndicators;
import org.falcsim.agentmodel.service.methods.util.RunIndicators.ExportType;
import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsParameters;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;
import org.falcsim.agentmodel.util.UniverseUtil;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.falcsim.agentmodel.util.xml.UtilitiesXMLConstants;
import org.falcsim.agentmodel.utility.dao.UtilityDaoHB;
import org.falcsim.agentmodel.utility.domain.RipisExportInfo;
import org.falcsim.agentmodel.utility.domain.RipisType;
import org.falcsim.agentmodel.utility.domain.UtilityGrowthChecker;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.falcsim.agentmodel.utility.landusage.LandUsage;
import org.falcsim.agentmodel.utility.util.RandomizedList;
import org.falcsim.agentmodel.utility.util.RandomizedListObject;
import org.falcsim.agentmodel.utility.util.UtilitiesUtil;
import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Moving businesses and households from one location to another according to
 * the utility functions Same as UtilityMoveSwiss_2, but with weighted moving
 * probabilities
 * 
 * @author regioConcept AG
 * 
 */

public class UtilityMoveSwiss_2_weighted1 implements UtilityMove {

	@Autowired
	private RunParameters runParameters;
	@Autowired
	private RCRandomGenerator rg;
	@Autowired
	private RandDevice randDevice;
	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private DemographyMethods demogMs;
	@Autowired
	private SMethods sMethods;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private UtilityParameters up;
	@Autowired
	private UtilitiesUtil uu;
	@Autowired
	private UtilityDaoHB utilDaoHB;
	@Autowired
	private UniverseServiceUtil universeService;
	@Autowired
	private LandUsage landLimits;
	@Autowired
	private UtilityGrowthChecker ugch;
	@Autowired
	private RandomizedList rndList;
	@Autowired
	private RelocationParameters rp;
	
	@Autowired
	private RipisExportInfo ripisExp;
	@Autowired 
	private RunIndicatorsParameters riParams;
	@Autowired 
	private RunIndicators ri;
	
	static final String SEP = RCConstants.SEP;
	private static final String MOVE_B = UtilitiesXMLConstants.MOVE_B;
	private static final String MOVE_H = UtilitiesXMLConstants.MOVE_H;
	private static final String X = UtilitiesXMLConstants.X;
	private static final String RATE_FACTORS = "RATE_FACTORS";
	
	private Map<String, String> mpb;
	private Map<String, String> mph;
	private Map<String, Double> rrfactor;
	private Integer run;
	private Integer currYear;
	private static Logger logger = Logger.getLogger(UtilityMoveSwiss_2_weighted1.class);
	private Double medHouss = 0.;
	private Integer resMovers = 0;
		
	private Map<Integer, Map<String, ?>> ageMap;
	private Map<Integer, Integer> workersMap;
	private Integer avgWorkers;
	private Map<Integer, Integer> residentsMap;
	private Integer avgResidents;
	
	private final static String ageField = Mod1Constants.ageField;
	private final static String moveOutProbField =  Mod1Constants.moveOutProbField;
	private final static String relocateByAGeTable= Mod1Constants.relocateByAGeTable;
	public static final String public_schema = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDefaultSchema();

	private Map<Integer, Map<String, ?>> closuresProbs;
	private static final String  closuresProbsTable = "firms_relocation";
	private static final String  closuresProbsIdField = "sector_id";
	private static final String  migration_prob_prefix = "M";
	private String migration_prob_column_str;
	
	/**
	 * max number of running threads
	 */
	private int maxThreadCount = 4;
	private Integer movedBusinesses;
	private Integer movedHouseHolds;

	public void init() {
		logger.info("UtilityMoveSwiss_2_weighted1 initializing...");
		maxThreadCount = runParameters.getService_maximum_threads() > 0 ? runParameters.getService_maximum_threads() : 4;

		run = sp.getCurrentTimeMarker();
		currYear = sp.getCurrentYear();	
		
		ripisExp.Clear();
		
		utilDaoHB.init(up.getLocs());
		mpb = up.getUtilMap().get(MOVE_B);
		mph = up.getUtilMap().get(MOVE_H);
		fillAgeMap();
		fillMigrationMap();
		fillResidentsMap(up.getLocs());
		fillWorkersMap(up.getLocs());
		rrfactor = new HashMap<String, Double>();
		for(String key : up.getUtilMap().get(RATE_FACTORS).keySet()){
			rrfactor.put(key, Double.parseDouble( up.getUtilMap().get(RATE_FACTORS).get(key) ) );	
		}
		
		logger.info("UtilityMoveSwiss_2_weighted1 initializing...done");
	}

	private void fillWorkersMap(List<Location> locs){
		workersMap = new HashMap<Integer, Integer>();
		int sumWorkers = 0;
		avgWorkers = -1;	
		for(Location loc : locs){
			workersMap.put(loc.getId(), loc.getActualWorkers());
			sumWorkers += loc.getActualWorkers();	
		}
		avgWorkers = sumWorkers / locs.size();
	}
	
	private void fillResidentsMap(List<Location> locs){
		residentsMap = new HashMap<Integer, Integer>();
		int sumResidents = 0;
		avgResidents = -1;	
		for(Location loc : locs){
			residentsMap.put(loc.getId(), loc.getActualResidents());
			sumResidents += loc.getActualResidents();	
		}
		avgResidents = sumResidents / locs.size();
	}
	
	private void fillAgeMap(){
		
		List<String> lsColNames = new ArrayList<>();
		lsColNames.add(ageField);
		lsColNames.add(moveOutProbField);
		ageMap = generalDao.loadFieldsIntoAMap(ageField, "", public_schema, relocateByAGeTable, lsColNames);
	}
	
	private void fillMigrationMap(){
		List<String> lsColNames = new ArrayList<>();
		lsColNames.add(closuresProbsIdField);
		migration_prob_column_str = migration_prob_prefix + currYear;
		lsColNames.add(migration_prob_column_str);
		closuresProbs = generalDao.loadFieldsIntoAMap(closuresProbsIdField, "", public_schema, closuresProbsTable, lsColNames);
	}
	
	
	/**
	 * Moving households for list of locations at once with randomized households
	 * 
	 * @throws RCCustomException
	 * 
	 */
	public void moveSelectedHouseholdsIntoSelectedLocationsThreads(List<Household> hhs, List<Location> locs) throws RCCustomException {
		//what if utility function is not initialized
		
		logger.info("Calculate Lang Usage Limits...");
		landLimits.calculate(universeService.selectAllLocations());
		logger.info("Update relocation locations status.");
		ugch.updateLocationStatus(universeService.selectAllLocations());
		
		logger.info("Moving households ... in " + String.valueOf(maxThreadCount) + " threads");
		int movedHouseHolds = 0;
		List<RandomizedListObject> list = rndList.getRandomObjectListOfAgents(hhs);
		
		//prepare map of all businesses, will be used in relocation when accessing person->business->location
		Map<Integer, Business> busmap = new HashMap<Integer, Business>();
		for(Location loc : universeService.selectAllLocations()) for(Business b : loc.getBusinesses()) busmap.put(b.getId(), b);
		//prepare map with old location id for each moving households
		Map<Household, Integer> hhmap = new HashMap<Household, Integer>();		
		for(Household h : hhs) hhmap.put(h, h.getLocationId());
		
		int lhhSize = list.size(); 
		int perThread = lhhSize / maxThreadCount; 
		int modThread = lhhSize % maxThreadCount; 
													
		CountDownLatch latch = new CountDownLatch(maxThreadCount); 
																	
		Thread thread = null;
		MoveMigrationHouseholdsInThreadRandomList moveHInThread = null;
		List<MoveMigrationHouseholdsInThreadRandomList> threadCache = new ArrayList<>(maxThreadCount);

		// create threads
		int current = 0;
		int step = perThread + 1;
		for (int i = 0; i < maxThreadCount; i++) {
			if (step != perThread) {
				if (modThread != 0) {
					modThread--;
				} else {
					step = perThread;
				}
			}
			moveHInThread = new MoveMigrationHouseholdsInThreadRandomList(i, locs, list.subList(current, current + step), list.size(), 
												medHouss, mph, run, latch, ageMap, moveOutProbField, currYear, busmap,
												sp.getCurrentReferenceRun() );
			moveHInThread.setBeans(utilDaoHB, randDevice, sMethods, demogMs, landLimits, ugch, rg, up);

			threadCache.add(moveHInThread);
			thread = new Thread(moveHInThread);
			thread.start();
			current += step;
		}
		logger.info("Moving households - Threads started");
		
		try { // wait till all threads finish
			latch.await();
		} catch(InterruptedException e) {
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.THREAD_ERROR.getValue());
		}
		logger.info("Moving households - Threads finished");
		
		// now count all results
		for (MoveMigrationHouseholdsInThreadRandomList threadObj : threadCache) {
			movedHouseHolds += threadObj.getCounter2();
		}
		
		//update locations lists
		for(Household h : hhs) {
			if(h.getLocationId() != hhmap.get(h) ){
				Location locNew = universeService.selectLocationById(h.getLocationId());
				locNew.getHouseholds().add(h);
				Location locOld = universeService.selectLocationById(hhmap.get(h));
				locOld.getHouseholds().remove(h);
			}
		}

		logger.info("Households moved: " + String.valueOf(movedHouseHolds));
	}
	
	/**
	 * Moving households for all locations at once with randomized households
	 * 
	 * @throws RCCustomException
	 * 
	 */
	public void moveHTAllLocations(List<Location> locs) throws RCCustomException {
		logger.info("Moving households ... in " + String.valueOf(maxThreadCount) + " threads");
		movedHouseHolds = 0;
		resMovers = 0;
		List<RandomizedListObject> list = rndList.getRandomObjectListOfHouseholds(locs);
		
		//prepare map of all businesses, will be used in relocation when accessing person->business->location
		Map<Integer, Business> busmap = new HashMap<Integer, Business>();
		for(Location loc : locs) for(Business b : loc.getBusinesses()) busmap.put(b.getId(), b);
		
		int lhhSize = list.size(); 
		int perThread = lhhSize / maxThreadCount; 
		int modThread = lhhSize % maxThreadCount; 
													
		CountDownLatch latch = new CountDownLatch(maxThreadCount); 
																	
		Thread thread = null;
		MoveHInThreadRandomList moveHInThread = null;
		List<MoveHInThreadRandomList> threadCache = new ArrayList<>(maxThreadCount);

		// create threads
		int current = 0;
		int step = perThread + 1;
		for (int i = 0; i < maxThreadCount; i++) {
			if (step != perThread) {
				if (modThread != 0) {
					modThread--;
				} else {
					step = perThread;
				}
			}
			moveHInThread = new MoveHInThreadRandomList(i, locs, list.subList(current, current + step), list.size(), 
												medHouss, mph, run, latch, ageMap, moveOutProbField, currYear, busmap,
												riParams.getRun_indicators_export_ripis_households() ? riParams.getExportRipisLocationsSet() : null,
												rp.getHhMayRelocateInFirstYear(), rp.getHhMayChooseCurrentLocation(), rp.getHhRelocationRateCorrection(), 
												sp.getCurrentReferenceRun(),
												residentsMap, avgResidents, rrfactor );
			moveHInThread.setBeans(utilDaoHB, randDevice, sMethods, demogMs, landLimits, ugch, rg, up);

			threadCache.add(moveHInThread);
			thread = new Thread(moveHInThread);
			thread.start();
			current += step;
		}
		logger.info("Moving households - Threads started");
		
		try { // wait till all threads finish
			latch.await();
		} catch(InterruptedException e) {
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.THREAD_ERROR.getValue());
		}
		logger.info("Moving households - Threads finished");
		
		Map<Integer, int[]> movingProgres = new HashMap<Integer,int[]>();
		
		// now count all results
		for (MoveHInThreadRandomList threadObj : threadCache) {
			movedHouseHolds += threadObj.getCounter2();
			RipisExportInfo.mergeRipisExportInfo(RipisType.HouseholdRipis, ripisExp, threadObj.getRipisExp() );
			
			if(riParams.getRun_indicators_export_level() >= RunIndicatorsParameters.run_indicators_export_level_FULL){
				Map<Integer, int[]> thrPM = threadObj.getMovingProgres();
				for(int key : thrPM.keySet()){
					int[] poleThr = thrPM.get(key);
					int[] pole = null;
					if(!movingProgres.containsKey(key)){
						movingProgres.put(key, pole = new int[6]);
						for(int i = 0; i < 6; i ++) pole[i] = 0;
					}
					else pole = movingProgres.get(key);
					
					pole[0] += poleThr[0];
					pole[1] += poleThr[1];
					pole[2] += poleThr[2];
					pole[3] += poleThr[3];
				}
			}
		}

		for(Location loc : locs){
			List<Household> remList = new ArrayList<Household>();
			for(Household h : loc.getHouseholds()){
				if(h.getLocationId() != loc.getId()){
					int h_livePersons = getLivePersons(h.getPersons());
					
					Map<Integer, RelocationInfo> relocmap = loc.getRelocationMap();
					RelocationInfo rinfo = null;
					if(relocmap.containsKey(h.getLocationId() )) rinfo = relocmap.get(h.getLocationId());
					else{
						rinfo = new RelocationInfo(10);
						relocmap.put(h.getLocationId(), rinfo);
					}
					rinfo.households += 1;
					rinfo.residents += h_livePersons;					
					
					remList.add(h);
					loc.getEventsStatus().hh_go_out++;
					loc.getEventsStatus().res_go_out += h_livePersons;	
					
					Location locNew = universeService.selectLocationById(h.getLocationId());
					locNew.getHouseholds().add(h);
					locNew.getEventsStatus().hh_come_in++;
					locNew.getEventsStatus().res_come_in += h_livePersons;					
					
					if(riParams.getRun_indicators_export_level() >= RunIndicatorsParameters.run_indicators_export_level_FULL){
						int[] pole = null;
						if(!movingProgres.containsKey(h.getLocationId())){
							movingProgres.put(h.getLocationId(), pole = new int[6]);
							for(int i = 0; i < 6; i ++) pole[i] = 0;
						}
						else pole = movingProgres.get(h.getLocationId());
						pole[4]++;
						pole[5] += h_livePersons;
					}
					h.setRun(run);
				}
			}
			loc.getHouseholds().removeAll(remList);
		}
		
		logger.info("Households moved: " + String.valueOf(movedHouseHolds));
		logger.info("Current year: " + sp.getCurrentYear());
		if(riParams.getRun_indicators_export_level() >= RunIndicatorsParameters.run_indicators_export_level_FULL){
			ri.exportMovingProgress(movingProgres, ExportType.HouseholdRI);
		}
	}
	private int getLivePersons(List<Person> list){
		int count = 0;
		for(Person p : list){
			if(p.getDdeath() == null )count++;
		}
		return count;
	}
	
	/**
	 * Moving businesses for all locations at once with randomized businesses
	 * 
	 * @throws RCCustomException
	 * 
	 */
	public void moveBTAllLocations(List<Location> locs) throws RCCustomException {
		logger.info("Moving businesses ... in " + String.valueOf(maxThreadCount) + " threads");
		movedBusinesses = 0;
		List<RandomizedListObject> list = rndList.getRandomObjectListOfBusinesses(locs);
		
		int lhhSize = list.size(); 
		int perThread = lhhSize / maxThreadCount; 
		int modThread = lhhSize % maxThreadCount; 
													
		CountDownLatch latch = new CountDownLatch(maxThreadCount); 
																	
		Thread thread = null;
		MoveBInThreadRandomList moveBInThread = null;
		List<MoveBInThreadRandomList> threadCache = new ArrayList<>(maxThreadCount);

		// create threads
		int current = 0;
		int step = perThread + 1;
		for (int i = 0; i < maxThreadCount; i++) {
			if (step != perThread) {
				if (modThread != 0) {
					modThread--;
				} else {
					step = perThread;
				}
			}
			moveBInThread = new MoveBInThreadRandomList(i, locs, list.subList(current, current + step), mpb, run, latch, 
					closuresProbs, migration_prob_column_str, workersMap, avgWorkers, rrfactor, 
					riParams.getRun_indicators_export_ripis_business() ? riParams.getExportRipisLocationsSet() : null,
							rp.getBbMayRelocateInFirstYear(), rp.getBbMayChooseCurrentLocation(), rp.getBbRelocationRateCorrection(), 
							sp.getCurrentReferenceRun(), currYear );
			moveBInThread.setBeans(utilDaoHB, randDevice, sMethods, landLimits, ugch, rg, up);

			threadCache.add(moveBInThread);
			thread = new Thread(moveBInThread);
			thread.start();
			current += step;
		}
		logger.info("Moving businesses - Threads started");
		
		try { // wait till all threads finish
			latch.await();
		} catch(InterruptedException e) {
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.THREAD_ERROR.getValue());
		}
		logger.info("Moving businesses - Threads finished");

		Map<Integer, int[]> movingProgres = new HashMap<Integer,int[]>();
		// now count all results
		for (MoveBInThreadRandomList threadObj : threadCache) {
			movedBusinesses += threadObj.getCounter2();
			RipisExportInfo.mergeRipisExportInfo(RipisType.BusinessRipis, ripisExp, threadObj.getRipisExp() ); 
			
			if(riParams.getRun_indicators_export_level() >= RunIndicatorsParameters.run_indicators_export_level_FULL){
				Map<Integer, int[]> thrPM = threadObj.getMovingProgres();
				for(int key : thrPM.keySet()){
					int[] poleThr = thrPM.get(key);
					int[] pole = null;
					if(!movingProgres.containsKey(key)){
						movingProgres.put(key, pole = new int[13]);
						for(int i = 0; i < 13; i ++) pole[i] = 0;
					}
					else pole = movingProgres.get(key);
					
					pole[0] += poleThr[0];
					pole[1] += poleThr[1];
					pole[2] += poleThr[2];
					pole[3] += poleThr[3];
					pole[6] += poleThr[6];
					pole[7] += poleThr[7];
					pole[8] += poleThr[8];
					pole[9] += poleThr[9];					
					pole[12] += poleThr[12];	
				}
			}
		}

		for(Location loc : locs){
			List<Business> remList = new ArrayList<Business>();
			for(Business b : loc.getBusinesses()){
				if(b.getLocationId() != loc.getId()){
					int b_livePersons = getLivePersons(b.getPersons());
							
					Map<Integer, RelocationInfo> relocmap = loc.getRelocationMap();
					RelocationInfo rinfo = null;
					if(relocmap.containsKey(b.getLocationId() )) rinfo = relocmap.get(b.getLocationId());
					else{
						rinfo = new RelocationInfo(10);
						relocmap.put(b.getLocationId(), rinfo);
					}
					rinfo.businesses += 1;
					rinfo.jobs += b.getNr_of_jobs();
					rinfo.workers += b_livePersons;
					rinfo.businessesbysector[ b.getType_1()-1 ]  += 1;
					rinfo.workersbysector[ b.getType_1()-1 ]  += b_livePersons;
					
					remList.add(b);
					loc.getEventsStatus().bb_go_out++;
					loc.getEventsStatus().wrk_go_out += b_livePersons;	
					
					Location locNew = universeService.selectLocationById(b.getLocationId());
					locNew.getBusinesses().add(b);
					locNew.getEventsStatus().bb_come_in++;
					locNew.getEventsStatus().wrk_come_in += b_livePersons;
					
					if(riParams.getRun_indicators_export_level() >= RunIndicatorsParameters.run_indicators_export_level_FULL){
						int[] pole = null;
						if(!movingProgres.containsKey(b.getLocationId())){
							movingProgres.put(b.getLocationId(), pole = new int[13]);
							for(int i = 0; i < 13; i ++) pole[i] = 0;
						}
						else pole = movingProgres.get(b.getLocationId());
						if (b.getRun() == null || b.getRun() < run){
							pole[4]++;
							pole[5] += b_livePersons;
						}
						else if (b.getRun() == run){
							pole[10]++;
							pole[11] += b_livePersons;
						}
					}
					b.setRun(run);
				}
			}
			loc.getBusinesses().removeAll(remList);
		}
		
		logger.info("Businesses moved: " + String.valueOf(movedBusinesses));
		logger.info("Current year: " + sp.getCurrentYear());
		if(riParams.getRun_indicators_export_level() >= RunIndicatorsParameters.run_indicators_export_level_FULL){
			ri.exportMovingProgress(movingProgres, ExportType.BusinessRI);
		}
	}

	public int getMaxThreadCount() {
		return maxThreadCount;
	}

	public void setMaxThreadCount(int maxThreadCount) {
		this.maxThreadCount = maxThreadCount;
	}

	@Override
	public void moveHB(Location loc, List<Location> locs)
			throws RCCustomException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveH(Location loc, List<Location> locs)
			throws RCCustomException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveB(Location loc, List<Location> locs)
			throws RCCustomException {
		// TODO Auto-generated method stub
		
	}
}




class MoveHInThreadRandomList implements Runnable {

	private static Logger logger =  Logger.getLogger(MoveHInThreadRandomList.class);
	
	// processing beans
	private UtilityDaoHB utilDaoHB;
	private RandDevice randDevice;
	private SMethods sMethods;
	private DemographyMethods demogMs;
	private LandUsage landLimits;
	private UtilityGrowthChecker ugch;
	private RCRandomGenerator rg;
	private UtilityParameters up;

	private final CountDownLatch latch;

	private Map<Integer, Business> busmap = null;
	private List<Location> locs = null;
	private List<RandomizedListObject> lhh = null;
	private int runNumber = 0;
	private int threadNumber = -1;
	private int resMovers = 0;

	private Map<Integer, Map<String, ?>> ageMap;
	private String moveOutProbField;
	private int currYear = 0;
	private int currReferenceRun = 0;
	private List<Integer> ripisLocList;
	private boolean relocate_new_households;
	private boolean new_households_select_same_loc;
	private boolean apply_rrf;	
	private Map<Integer, Integer> residentsMap;
	private Integer avgResidents;
	private Map<String, Double> rrfactor;

	// counters
	private int counter1 = 0;
	private int counter2 = 0;
	
	private RipisExportInfo ripisExp = new RipisExportInfo();
	private Map<Integer, int[]> movingProgres = new HashMap<Integer,int[]>();

	public MoveHInThreadRandomList(int threadNumber, List<Location> locs, List<RandomizedListObject> lhh, int lhhSize, double medHouss, Map<String, String> mph,
			int runNumber, CountDownLatch latch, Map<Integer, Map<String, ?>> ageMap, String moveOutProbField, Integer currYear, final Map<Integer, Business> busmap,
			List<Integer> ripisLocList, boolean relocate_new_households, boolean new_households_select_same_loc, boolean apply_rrf, Integer currReferenceRun,
			Map<Integer, Integer> residentsMap, Integer avgResidents, Map<String, Double> rrfactor) {
		this.threadNumber = threadNumber;

		this.locs = locs;
		this.lhh = lhh;
		this.runNumber = runNumber;
		this.latch = latch;
		this.ageMap = ageMap;
		this.currYear = currYear;
		this.currReferenceRun = currReferenceRun;
		this.moveOutProbField = moveOutProbField;
		this.busmap = busmap;
		this.ripisLocList = ripisLocList;
		this.relocate_new_households = relocate_new_households;
		this.new_households_select_same_loc = new_households_select_same_loc;
		this.apply_rrf = apply_rrf;
		
		this.residentsMap = residentsMap;
		this.avgResidents = avgResidents;
		this.rrfactor = rrfactor;
	}

	/**
	 * provide beans to the thread
	 * 
	 * @param utilDaoHB
	 * @param randDevice
	 * @param sMethods
	 */
	public void setBeans(UtilityDaoHB utilDaoHB, RandDevice randDevice, SMethods sMethods, DemographyMethods demogMs, LandUsage landLimits, 
			UtilityGrowthChecker ugch, RCRandomGenerator rg, UtilityParameters up) {
		this.utilDaoHB = utilDaoHB;
		this.randDevice = randDevice;
		this.sMethods = sMethods;
		this.demogMs = demogMs;
		this.landLimits = landLimits;
		this.ugch = ugch;
		this.rg = rg.clone();
		this.up = up;
	}

	@Override
	public void run() {
		try {
			for (RandomizedListObject householdId : lhh) {
				Household h = (Household)householdId.a;
				Location loc = householdId.loc;
				counter1++;
				Person hEarner = UniverseUtil.getHouseholdMainEarner(h);
				//int hEarnerId = hEarner != null ? hEarner.getId() : h.getId();
				Double moveProb = getMoveProbForHousehold(h);
				
				h.setType_2( demogMs.getHouseholdTypeForRelocation(h, currYear) );
				if(apply_rrf) moveProb = calcHouseholdsRelocationRate(h.getLocationId(), h.getType_2(), moveProb );
				
				boolean relocateProb = false;

				if(hEarner != null ){
					relocateProb = randDevice.assignRandomWeightedBoolean(rg, moveProb, hEarner, currYear, currReferenceRun );
				}
				else{
					relocateProb = randDevice.assignRandomWeightedBoolean(moveProb);
				}				

				boolean migrantsHH = false;
				boolean canHHmove = false;
				boolean willHHmove = false;
				if(h.getType_1() == RCConstants.STATUS_IMMIGRANT){		//new MIGRANT households will always move
					h.setType_1(null);
					willHHmove = true;	
					migrantsHH = true;
				}
				if ( h.getRun() == null || h.getRun() < runNumber || (h.getRun() == runNumber && relocate_new_households)  ) {	//not new households OR new households with switch on relocate new households
					canHHmove = true;	
				}
				if(willHHmove || (canHHmove && relocateProb) ){
					int[] pole = null;
					if(!movingProgres.containsKey(loc.getId())){
						movingProgres.put(loc.getId(), pole = new int[6]);
						for(int i = 0; i < 6; i ++) pole[i] = 0;
					}
					else pole = movingProgres.get(loc.getId());
					pole[0]++;
					
					List<RealIdProbInterval> ripis = utilDaoHB.assignProbsForH(h, loc, locs, busmap, new_households_select_same_loc, migrantsHH);
					if(ripis.size() > 0){												
						Integer newLocId = up.getLocationChoiceManagedByID() ? 
								hEarner != null ? randDevice.assignWeightedRandomId(rg, ripis, hEarner, currYear, currReferenceRun) : randDevice.assignWeightedRandomId(ripis)
								: randDevice.assignWeightedRandomId(ripis);
						if(ripisLocList != null){
							if(ripisLocList.contains(h.getLocationId())){
								ripisExp.add(RipisType.HouseholdRipis, h.getLocationId(), h.getId(), 0, 0, newLocId, ripis);
							}
						}
						if ( !newLocId.equals(h.getLocationId()) ) {
							pole[1]++;
							pole[2] += UniverseUtil.getLivePersons(h.getPersons());
							counter2++;
							int oldlocid = h.getLocationId();
							sMethods.move(h, newLocId);  //move business from one location list to another is implemented after threads
							
							landLimits.updateCurrentLandUsageResidents(oldlocid, newLocId, h);
							ugch.updateLocationHouseholdsCurrentStatus(oldlocid, newLocId, h);
							resMovers += h.getPersons().size();
						}
						else pole[3]++;
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		latch.countDown();
	}

	private double getMoveProbForHousehold(Household h){
		int countEarn=0;
		int totAge=0;
		for( Person p: h.getPersons()){
			if(p.getFinance_1() != null){
				totAge += demogMs.getAgeOfPerson(p, currYear);
				countEarn++;
			}
		}
		if (countEarn == 0){
			return 0.;
		} else {
			//<!-- @mifr
			/*return ((BigDecimal) ageMap.get(totAge/countEarn).get(moveOutProbField)).doubleValue();*/
			return (Double) ageMap.get(totAge/countEarn).get(moveOutProbField);
			//-->
		}		
	}

	private double calcHouseholdsRelocationRate(int locId, int sector, double overalrate ){
		Double betaFactor = rrfactor.containsKey("RRF_H_" + sector) ? rrfactor.get("RRF_H_" + sector) : rrfactor.get("RRF_H_X");
		Double factorReference = rrfactor.get("RRF_H_REFERENCE");
		if(residentsMap.get(locId) == 0){
			logger.debug("calcHouseholdsRelocationRate divided by 0 for location: " + String.valueOf(locId) );
		}
		//return residentsMap.get(locId) == 0 ? overalrate : (((double)avgResidents / (double)residentsMap.get(locId) -1) * betaFactor + 1) * overalrate;
		double final_probability = (1 - (double)residentsMap.get(locId) / factorReference * betaFactor) * overalrate;
		if(final_probability > 1) final_probability = 1;
		if(final_probability < 0) final_probability = 0;
		
		return final_probability;
	}
	
	private double getHouseholdsRelocationRate( int sector ){
		return rrfactor.containsKey("RRF_H_" + sector) ? rrfactor.get("RRF_H_" + sector) : rrfactor.get("RRF_H_X");
	}
	
	public int getCounter1() {
		return counter1;
	}

	public int getCounter2() {
		return counter2;
	}

	public int getResMovers() {
		return resMovers;
	}
	
	public RipisExportInfo getRipisExp() {
		return ripisExp;
	}
	
	public Map<Integer, int[]> getMovingProgres() {
		return movingProgres;
	}

}

class MoveBInThreadRandomList implements Runnable {
	// processing beans
	private UtilityDaoHB utilDaoHB;
	private RandDevice randDevice;
	private SMethods sMethods;
	private LandUsage landLimits;
	private UtilityGrowthChecker ugch;
	private RCRandomGenerator rg;
	private UtilityParameters up;

	private final CountDownLatch latch;
	private int threadNumber = 0;

	// counters
	private int counter1 = 0;
	private int counter2 = 0;
	
	// holders
	private List<Location> locs = null;
	private List<RandomizedListObject> lbs = null;
	private Map<Integer, Map<String, ?>> closuresProbs;
	private String migration_prob_column_str;
	private int runNumber = 0;
	
	private Map<Integer, Integer> workersMap;
	private Integer avgWorkers;
	private Map<String, Double> rrfactor;
	private List<Integer> ripisLocList;

	private static Logger logger = Logger.getLogger(MoveBInThreadRandomList.class);

	private RipisExportInfo ripisExp = new RipisExportInfo();
	private Map<Integer, int[]> movingProgres = new HashMap<Integer,int[]>();
	private boolean relocate_new_companies;
	private boolean new_companies_select_same_loc;
	private boolean apply_rrf;
	
	private int currReferenceRun;
	private int currYear;
	/**
	 * constructor
	 * 
	 * @param loc
	 * @param locs
	 * @param lbs
	 * @param mpb
	 * @param runNumber
	 * @param latch
	 */
	public MoveBInThreadRandomList(int threadNumber, List<Location> locs, List<RandomizedListObject> lbs, Map<String, String> mpb, int runNumber, CountDownLatch latch, Map<Integer, 
			Map<String, ?>> closuresProbs, String migration_prob_column_str,
			Map<Integer, Integer> workersMap, Integer avgWorkers, Map<String, Double> rrfactor, List<Integer> ripisLocList, boolean relocate_new_companies, boolean new_companies_select_same_loc, boolean apply_rrf,
			Integer currReferenceRun, Integer currYear) {
		this.threadNumber = threadNumber;

		this.locs = locs;
		this.lbs = lbs;
		//this.mpb = mpb;
		this.runNumber = runNumber;

		this.latch = latch;
		
		this.closuresProbs = closuresProbs;
		this.migration_prob_column_str = migration_prob_column_str;
		this.workersMap = workersMap;
		this.avgWorkers = avgWorkers;
		this.rrfactor = rrfactor;
		this.ripisLocList = ripisLocList;
		this.relocate_new_companies = relocate_new_companies;
		this.new_companies_select_same_loc = new_companies_select_same_loc;
		this.apply_rrf = apply_rrf;
		this.currReferenceRun = currReferenceRun;
		this.currYear = currYear;
	}

	/**
	 * provide beans to the thread
	 * 
	 * @param utilDaoHB
	 * @param randDevice
	 * @param sMethods
	 */
	public void setBeans(UtilityDaoHB utilDaoHB, RandDevice randDevice, SMethods sMethods, LandUsage landLimits, 
			UtilityGrowthChecker ugch, RCRandomGenerator rg, UtilityParameters up) {
		this.utilDaoHB = utilDaoHB;
		this.randDevice = randDevice;
		this.sMethods = sMethods;
		this.landLimits = landLimits;
		this.ugch = ugch;
		this.rg = rg.clone();
		this.up = up;
	}

	/**
	 * business logic of the thread
	 */
	@Override
	public void run() {
		try {
			for (RandomizedListObject businessId : lbs) {
				Business b = (Business)businessId.a;
				Location loc = businessId.loc;
				counter1++;

				
				int[] pole = null;
				if(!movingProgres.containsKey(loc.getId())){
					movingProgres.put(loc.getId(), pole = new int[13]); //12-closed
					for(int i = 0; i < 13; i ++) pole[i] = 0;
				}
				else pole = movingProgres.get(loc.getId());
				if( b.getDclosing() != null ){
					pole[12]++;	
				}
				else{
					if (b.getRun() == null || b.getRun() < runNumber){
						double moveProb = getMoveProbForBusiness(b.getType_1() );
						if(apply_rrf) moveProb = calcBusinessRelocationRate(b.getLocationId(), b.getType_1(), moveProb );
						
						//if (randDevice.assignRandomWeightedBoolean(rg, moveProb, ceoId, currYear, currReferenceRun, RandomGeneratorModelID.BBRelocation)){
						Person ceo = UniverseUtil.getCEO(b);
						boolean relocateProb = false;
						if(ceo != null ){
							relocateProb = randDevice.assignRandomWeightedBoolean(rg, moveProb, ceo, currYear, currReferenceRun );
						}
						else{
							relocateProb = randDevice.assignRandomWeightedBoolean(moveProb);
						}
						
						if (relocateProb){
						//if (randDevice.assignRandomWeightedBoolean(moveProb)){
							pole[0]++;
							
							List<RealIdProbInterval> ripis = utilDaoHB.assignProbsForB(b, loc, locs, false);
							if(ripis.size() > 0){
								Integer newLocId = up.getLocationChoiceManagedByID() ? 
										ceo != null ? randDevice.assignWeightedRandomId(rg, ripis, ceo, currYear, currReferenceRun) : randDevice.assignWeightedRandomId(ripis)
										: randDevice.assignWeightedRandomId(ripis);
								if(ripisLocList != null){
									if(ripisLocList.contains(b.getLocationId())){
										ripisExp.add(RipisType.BusinessRipis, b.getLocationId(), b.getId(), b.getType_1(), b.getType_2(), newLocId, ripis);
									}
								}
								if ( !newLocId.equals(b.getLocationId()) ) {
									pole[1]++;
									pole[2] += UniverseUtil.getLivePersons(b.getPersons());
									counter2++;
									int oldlocid = b.getLocationId();
									sMethods.move(b, newLocId); //move business from one location list to another is implemented after threads
															
									landLimits.updateCurrentLandUsageWorkers(oldlocid, newLocId, b);
									ugch.updateLocationBusinessesCurrentStatus(oldlocid, newLocId, b);
								}
								else pole[3]++;
							}
						}
					}
					else{
						//newly created...
						if (b.getRun() == runNumber){
							pole[6]++;
							if( relocate_new_companies){
								double moveProb = 1.;
								if(apply_rrf) moveProb = getBusinessRelocationRate(b.getType_1());
								Person ceo = UniverseUtil.getCEO(b);
								//int ceoId = ceo != null ? ceo.getId() : b.getId();
								boolean relocateProb = false;
								if(ceo != null ){
									relocateProb = randDevice.assignRandomWeightedBoolean(rg, moveProb, ceo, currYear, currReferenceRun );
								}
								else{
									relocateProb = randDevice.assignRandomWeightedBoolean(moveProb);
								}
								
								if (moveProb == 1. || relocateProb) {
									List<RealIdProbInterval> ripis = utilDaoHB.assignProbsForB(b, loc, locs, new_companies_select_same_loc);
									if(ripis.size() > 0){
										Integer newLocId = up.getLocationChoiceManagedByID() ? 
												ceo != null ? randDevice.assignWeightedRandomId(rg, ripis, ceo, currYear, currReferenceRun) : randDevice.assignWeightedRandomId(ripis)
												: randDevice.assignWeightedRandomId(ripis);
										if(ripisLocList != null){
											if(ripisLocList.contains(b.getLocationId())){
												ripisExp.add(RipisType.BusinessRipis, b.getLocationId(), b.getId(), b.getType_1(), b.getType_2() + 100, newLocId, ripis);
											}
										}
										if ( !newLocId.equals(b.getLocationId()) ) {
											pole[7]++;
											pole[8] += UniverseUtil.getLivePersons(b.getPersons());
											counter2++;
											int oldlocid = b.getLocationId();
											sMethods.move(b, newLocId); //move business from one location list to another is implemented after threads
																	
											landLimits.updateCurrentLandUsageWorkers(oldlocid, newLocId, b);
											ugch.updateLocationBusinessesCurrentStatus(oldlocid, newLocId, b);
										}
										else pole[9]++;
									}
								}
								else{
									//TODO:when business not pass...
									pole[9]++;
								}
							}
							else{
								pole[9]++;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.MODELS_MOVING_ERROR.getValue());
		}
		latch.countDown();
	}
		
	private double getMoveProbForBusiness(Integer sector){
		//farmers don't move -> solved by set probs table to 0 for sector = 1
		//<!--mifr
		/*return ((BigDecimal) closuresProbs.get(sector).get(migration_prob_column_str)).doubleValue();*/
		return (Double) closuresProbs.get(sector).get(migration_prob_column_str);
		//-->
	}
	
	private double calcBusinessRelocationRate(int locId, int sector, double overalrate ){		
		Double betaFactor = rrfactor.containsKey("RRF_B_" + sector) ? rrfactor.get("RRF_B_" + sector) : rrfactor.get("RRF_B_X");
		Double factorReference = rrfactor.get("RRF_B_REFERENCE");
		if(workersMap.get(locId) == 0){
			logger.debug("calcBusinessRelocationRate divided by 0 for location: " + String.valueOf(locId) );
		}
		//return workersMap.get(locId) == 0 ? overalrate : (((double)avgWorkers / (double)workersMap.get(locId) -1) * betaFactor + 1) * overalrate;
		double final_probability = (1 - (double)workersMap.get(locId) / factorReference * betaFactor) * overalrate;
		if(final_probability > 1) final_probability = 1;
		if(final_probability < 0) final_probability = 0;
		
		return final_probability;
	}
	
	private double getBusinessRelocationRate( int sector ){
		return rrfactor.containsKey("RRF_B_" + sector) ? rrfactor.get("RRF_B_" + sector) : rrfactor.get("RRF_B_X");
	}
	
	public int getCounter1() {
		return counter1;
	}

	public void setCounter1(int counter1) {
		this.counter1 = counter1;
	}

	public int getCounter2() {
		return counter2;
	}

	public void setCounter2(int counter2) {
		this.counter2 = counter2;
	}
	
	public RipisExportInfo getRipisExp() {
		return ripisExp;
	}
	
	public Map<Integer, int[]> getMovingProgres() {
		return movingProgres;
	}
}


class MoveMigrationHouseholdsInThreadRandomList implements Runnable {

	private static Logger logger =  Logger.getLogger(MoveMigrationHouseholdsInThreadRandomList.class);
	
	// processing beans
	private UtilityDaoHB utilDaoHB;
	private RandDevice randDevice;
	private SMethods sMethods;
	private DemographyMethods demogMs;
	private LandUsage landLimits;
	private UtilityGrowthChecker ugch;
	private RCRandomGenerator rg;
	private UtilityParameters up;

	private final CountDownLatch latch;

	private Map<Integer, Business> busmap = null;
	private List<Location> locs = null;
	private List<RandomizedListObject> lhh = null;
	private int runNumber = 0;
	private int threadNumber = -1;
	private int resMovers = 0;

	private Map<Integer, Map<String, ?>> ageMap;
	private String moveOutProbField;
	private int currYear = 0;
	private int currReferenceRun = 0;
	private int counter2 = 0;

	public MoveMigrationHouseholdsInThreadRandomList(int threadNumber, List<Location> locs, List<RandomizedListObject> lhh, int lhhSize, double medHouss, Map<String, String> mph,
			int runNumber, CountDownLatch latch, Map<Integer, Map<String, ?>> ageMap, String moveOutProbField, Integer currYear, final Map<Integer, Business> busmap,
			Integer currReferenceRun) {
		this.threadNumber = threadNumber;

		this.locs = locs;
		this.lhh = lhh;
		this.runNumber = runNumber;
		this.latch = latch;
		this.ageMap = ageMap;
		this.currYear = currYear;
		this.currReferenceRun = currReferenceRun;
		this.moveOutProbField = moveOutProbField;
		this.busmap = busmap;
	}

	/**
	 * provide beans to the thread
	 * 
	 * @param utilDaoHB
	 * @param randDevice
	 * @param sMethods
	 */
	public void setBeans(UtilityDaoHB utilDaoHB, RandDevice randDevice, SMethods sMethods, DemographyMethods demogMs, LandUsage landLimits, 
			UtilityGrowthChecker ugch, RCRandomGenerator rg, UtilityParameters up) {
		this.utilDaoHB = utilDaoHB;
		this.randDevice = randDevice;
		this.sMethods = sMethods;
		this.demogMs = demogMs;
		this.landLimits = landLimits;
		this.ugch = ugch;
		this.rg = rg.clone();
		this.up = up;
	}

	@Override
	public void run() {
		try {
			for (RandomizedListObject householdId : lhh) {
				Household h = (Household)householdId.a;
				Location loc = householdId.loc;
				Person hEarner = null; //business not assigned yes for households members
				
				if(h.getType_1() == RCConstants.STATUS_IMMIGRANT){
					h.setType_1(null);
					List<RealIdProbInterval> ripis = utilDaoHB.assignProbsForH(h, loc, locs, busmap, true, true);
					if(ripis.size() > 0){												
						Integer newLocId = up.getLocationChoiceManagedByID() ? 
								hEarner != null ? randDevice.assignWeightedRandomId(rg, ripis, hEarner, currYear, currReferenceRun) : randDevice.assignWeightedRandomId(ripis)
								: randDevice.assignWeightedRandomId(ripis);
	
						if ( !newLocId.equals(h.getLocationId()) ) {
							counter2++;
							int oldlocid = h.getLocationId();
							sMethods.move(h, newLocId);  //move business from one location list to another is implemented after threads
							
							landLimits.updateCurrentLandUsageResidents(oldlocid, newLocId, h);
							ugch.updateLocationHouseholdsCurrentStatus(oldlocid, newLocId, h);
							resMovers += h.getPersons().size();
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		latch.countDown();
	}

	public int getCounter2() {
		return counter2;
	}

	public void setCounter2(int counter2) {
		this.counter2 = counter2;
	}
	
}