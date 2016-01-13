package org.falcsim.agentmodel.service.methods;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.NotImplementedException;
import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.demography.methods.DemographyMethods;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.service.domain.ControlTotalResidentsParameters;
import org.falcsim.agentmodel.service.domain.MigrationParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.service.methods.util.RunIndicators;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.util.UniverseUtil;
import org.falcsim.agentmodel.util.UtilCounter;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.falcsim.agentmodel.utility.UtilityMove;
import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MigrationImpl1 implements EWMethods {
	
	@Autowired
	private ServiceParameters sp;
	@Autowired	
	private GeneralDao generalDao;
	@Autowired
	private UniverseServiceUtil universeService;
	@Autowired
	private UtilCounter uc;	
	@Autowired
	DemographyMethods dm;
	@Autowired
	private RCRandomGenerator rg;	
	@Autowired
	private RandDevice randDevice;	
	@Autowired
	private UtilityMove um;
	@Autowired
	private RunIndicators ri;
	@Autowired
	private MigrationParameters mp;
	@Autowired
	private ControlTotalResidentsParameters ctr;

	private static final double IMMIGRANTS_CORRECTION_CH = 1.0; //0.0971 for SUA;	
	
	private static final int DATALEVEL_CH = 1;	
	private static final int DATALEVEL_CAN = 2;
	private static final int DATALEVEL_MUN = 3;
	private static final int DATALEVEL_LOC = 4;
		
	private static final String  probsIdField = "region";
	private static final String  immProbsTable = "households_migration_residents";
	private static final String  migrProbsTable = "control_totals_residents";
	private static final String  nameColumn = "name";
	private static final String  immHHProbsIdField = "Type";
	private static final String  immHHProbsTable = "households_migration_shares";	
	private static final String public_schema = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDefaultSchema();	
	
	private Integer currYear = 0;
	private Integer run;
	private int refrun;
	
	private Map<Integer, Map<String, ?>> immProps;
	private Map<Integer, Map<String, ?>> migrProbs;
	private Map<Integer, Map<String, ?>> immHouseholdsProbs;
	
	private Map<Integer, int[]> indicators;
	
	@Override
	public void init() {
		indicators = new TreeMap<Integer, int[]>();
		
		run = sp.getCurrentTimeMarker();
		currYear = sp.getCurrentYear();
		refrun = sp.getCurrentReferenceRun();
		
		immProps = generalDao.loadFieldsIntoAMap(probsIdField, "", public_schema, immProbsTable, prepareImmProbColsList());
		migrProbs = generalDao.loadFieldsIntoAMap(probsIdField, "", public_schema, migrProbsTable, prepareImmProbColsList());
		immHouseholdsProbs = generalDao.loadFieldsIntoAMap(immHHProbsIdField, "", public_schema, immHHProbsTable, prepareImmHHProbColsList());
	}
	
	@Override
	public void migrate(Location loc) {
		throw new NotImplementedException();
	}

	@Override
	public void writeCSVindicators() {
		String path = ri.getReferenceSubfolder();		
		File f = ri.checkExportDir(path); 
		f = new File(f, "migration_" + currYear + ".csv");
		BufferedWriter writerh = null;
		try
		{
		    writerh = new BufferedWriter( new FileWriter(f));
		    writerh.write("\"locid\";\"Name\";\"IMM_H_NEW\";\"IMM_R_NEW\";\"MIGR_H_DEL\";\"MIGR_R_DEL\";\"MIGR_H_NEW\";\"MIGR_R_NEW\";\"RELOC\"\r\n");

		    for(int locid : indicators.keySet()){		    	
		    	int[] array = indicators.get(locid);
		    	String name = universeService.selectLocationById(locid).getName();
		    	writerh.write(String.valueOf(locid) + ";\"" + name + "\";");
				for(int i = 0; i < 8; i++){
					writerh.write(String.valueOf(array[i]) + ";");
				}
		    	writerh.write("\r\n");
		    }
		}
	    catch ( IOException e)
	    {
	    	
	    }		
		finally
		{
		    try
		    {
		        if ( writerh != null) writerh.close( );
		    }
		    catch ( IOException e)
		    {
		    }
		}		
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void migrate(List<Location> locs, MigrantionType type) throws IllegalArgumentException {
		if(immProps == null || immProps.size() == 0) throw new IllegalArgumentException("");

		int immCounter = 0;
				
		//Select source map
		Map<Integer, Map<String, ?>> regionMap = type == MigrantionType.MIGRANTS ? immProps : migrProbs;
		Integer mapDataLevel = type == MigrantionType.MIGRANTS ? mp.getMigrationDataLevel() : ctr.getControlTotalsDataLevel();
		
		List<Household> migrationBlist = new ArrayList<Household>();
		for(Integer key : regionMap.keySet()){			
			Map<String, Integer> map = (Map<String, Integer>)regionMap.get(key);
			logger.info("Processing " + (type == MigrantionType.MIGRANTS ? "Migration" : "Control totals residents") + " for region " + map.get(nameColumn) );
			
			if( checkRegionInUniverse(key, mapDataLevel) ){			
				int num_of_migrants = 0;
				
				String colName = String.valueOf(currYear);
				if( type == MigrantionType.MIGRANTS){
					num_of_migrants = map.get(colName);		//imm_prob_prefix +
					
					num_of_migrants = (int)(num_of_migrants * IMMIGRANTS_CORRECTION_CH);
				}
				else{
					int num_of_residents = map.get(colName);
					if(num_of_residents != 0){
						num_of_migrants = num_of_residents - getFalcResidents(key, mapDataLevel);
					}
				}

				if(num_of_migrants != 0 ){
					List<Household> list = null;	//households to move
					List<Location> locList = null;	//locations where can move, relocation by utility function
					
					if(num_of_migrants < 0){
						//select families to move out of region
						list = emigrateHouseholds(key, mapDataLevel, -1 * num_of_migrants);
						//prepare list of not in region locations
						locList = new ArrayList<Location>(universeService.selectAllLocations());
						List<Location> tmplist = regionLocations(key, mapDataLevel);
						locList.removeAll(tmplist);	
						
						storeRI(list, type, true);
						
						//make household and persons in it death instead of move them out
						for(Household h : list){
							h.setDclosing(dm.assignDateOnYear(currYear));
							h.setRun(run);
							Location locOld = universeService.selectLocationById(h.getLocationId());
							locOld.getHouseholds().remove(h);
							for(Person p : h.getPersons()){
								p.setDdeath(dm.assignDateOfDeath(currYear));
								p.setRun(run);
								p.setPartnership_since(null);
							}
							h.getPersons().clear();
						}
						list.clear();
						logger.info(String.valueOf(-1 * num_of_migrants) + " persons deleted from universe");
					}
					else{
						//list of in region locations
						locList = regionLocations(key, mapDataLevel);
						Map<Location, Double> locRankMap = rankLocationsByEmployees(locList);	//Map<HouseholdSize, <LocID, Rank>>						
						//generate families and set their initial location				
						list = immigrateNewFamilies(num_of_migrants, locRankMap);	
						storeRI(list, type, false);
						logger.info(String.valueOf(num_of_migrants) + " persons immigrated into universe");
					}
					
					if(list.size() > 0 ){
						//Relocate by utility function
						for(Household h : list){							
							immCounter += UniverseUtil.getLivePersons(h.getPersons());
						}
						if( type == MigrantionType.CONTROL_TOTALS_RESIDENTS ){
							migrationBlist.addAll(list);
						}
					}
				}
			}
			else{
				logger.info("SKIPPED");
			}
		}
		
		//call modified utility function (List<Household>, List<Location>) households to move, locations where they can move...		
		if( type == MigrantionType.CONTROL_TOTALS_RESIDENTS && migrationBlist.size() > 0 ){
			try {
				logger.info("Call modified UF after universe corrections for " + migrationBlist.size() + " households");
				um.moveSelectedHouseholdsIntoSelectedLocationsThreads(migrationBlist, locs);
			} catch (RCCustomException e) {
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.MODELS_MIGRATION_ERROR.getValue());
			}	
			finally{
				migrationBlist.clear();
			}
		}		
		uc.addCount3(immCounter);			
	}
	
	private void storeRI(List<Household> list, MigrantionType type, boolean deleted){
		for(Household h : list){
			int locid = h.getLocationId();
			int size = h.getPersons().size();
			
			int[] array = null;
			if(indicators.containsKey(locid)){
				array = indicators.get(locid);
			}
			else{
				array = new int[8];
				for(int i = 0; i < 8; i++){
					array[i] = 0;
				}
				indicators.put(locid, array);
			}
			if(type == MigrantionType.MIGRANTS){
				array[0] += 1;
				array[1] += size;
			}
			else{
				if(deleted){
					array[2] += 1;
					array[3] += size;	
				}
				else{
					array[4] += 1;
					array[5] += size;					
				}
			}
		}		
	}	
	
	private Map<Location, Double> rankLocationsByEmployees(List<Location> locList){
		Map<Location, Double> map = new HashMap<Location, Double>();
		
		int emplInRegion = 0;
		for(Location loc : locList){ 	
			int emplInLocation = 0;
			for(Business b : loc.getBusinesses()){
				int size = b.getPersons().size();
				emplInRegion += size;
				emplInLocation += size;				
			}
			map.put(loc, (double)emplInLocation);
		}
		for(Location loc : locList){
			double value = emplInRegion == 0 ? 0. : map.get(loc) / emplInRegion;
			map.put(loc, value);
		}
		return map;
	}
			
	private List<Household> immigrateNewFamilies(int num_of_migrants, Map<Location, Double> locRankMap){
		List<Household> list = new ArrayList<Household>();
		
		int populatedPeople = 0;
		for(Integer key : immHouseholdsProbs.keySet()){
			if(key != 0){									//0 is Single = default
				Map<String, ?> map = immHouseholdsProbs.get(key);
				
				double prob = (Double)map.get("Share");
				int count = (Integer)map.get("Persons");				
				int new_res = (int)Math.floor(num_of_migrants * prob);
				int tmp_new_res = (int)Math.round(num_of_migrants * prob);
				if( num_of_migrants - populatedPeople - tmp_new_res > 0){
					new_res = tmp_new_res;	//we can populate rounded number - sometimes one more, which sometimes allow us fill one more household...
					//more heuristics can follow, like can we fill more that e.g. half family and have peoples for full family, do it...
				}
				List<Household> tmplist = new ArrayList<Household>();
				while(new_res > count){
					//create next family
					Household h = createNewHousehold(map);
					tmplist.add(h);
					new_res -= h.getPersons().size();
					populatedPeople += h.getPersons().size();
				}
				distributeHouseholds(tmplist, locRankMap);
				list.addAll(tmplist);
			}
		}
		if( num_of_migrants - populatedPeople > 0){
			Map<String, ?> map = immHouseholdsProbs.get(0);
			int count = (Integer)map.get("Persons");
			int new_res = num_of_migrants - populatedPeople;
			List<Household> tmplist = new ArrayList<Household>();
			while(new_res >= count){
				//create next family
				Household h = createNewHousehold(map);
				tmplist.add(h);
				new_res -= h.getPersons().size();
				populatedPeople += h.getPersons().size();
			}
			distributeHouseholds(tmplist, locRankMap);
			list.addAll(tmplist);
		}
		return list;
	}
	
	private void distributeHouseholds(List<Household> list, Map<Location, Double> locRankMap){
		int index = 0;
		double maxRank = -1;
		Location bestLocation = null;
		for(Location key : locRankMap.keySet()){
			double perc = locRankMap.get(key); 
			if(perc > maxRank){
				maxRank = perc;
				bestLocation = key;
			}
			int count = (int)Math.floor(perc * list.size());
			for(int i = 0; i < count; i++){
				Household h = list.get(index++);
				h.setLocationId(key.getId());
				key.getHouseholds().add(h);
			}
		}
		for(int i = index; i < list.size(); i++){
			Household h = list.get(i);
			h.setLocationId(bestLocation.getId());
			bestLocation.getHouseholds().add(h);
		}
	}
	
	private Household createNewHousehold(Map<String, ?> map){
		Household household = new Household(true);		 //location is not known yet
		household.setType_1(RCConstants.STATUS_IMMIGRANT);
		household.setDfoundation(dm.assignDateOnYear(currYear));
		household.setRun(run);	
		List<Person> persons = new ArrayList<>();
		household.setPersons(persons);
		
		//fill males, females,...children get parents IDs
		int count = (Integer)map.get("Persons");
		int age_diff = (Integer)map.get("age_diff");
		int age_m = (Integer)map.get("age_m");
		int age_w = (Integer)map.get("age_w");
		int age_ch1 = (Integer)map.get("age_ch1");
		int age_ch2 = (Integer)map.get("age_ch2");
		switch(count){
			case 1:
				Person p = new Person(true);
				int age = 0;
				if(randDevice.assignRandomWeightedBoolean(mp.getGenderBoyProbability()) ){					
					p.setSex(RCConstants.MALE);
					age = age_m;					
				}
				else{
					p.setSex(RCConstants.FEMALE);
					age = age_w;
				}
				p.setDbirth( dm.assignDateOfBirth(currYear, age - age_diff, age + age_diff ) );				
				p.setHouseholdId(household.getId());
				p.setRun(run);
				persons.add(p);
				break;
				
			case 2:	
				createPerson(household, RCConstants.MALE, age_m - age_diff, age_m + age_diff, RCConstants.H_Partner);
				createPerson(household, RCConstants.FEMALE, age_w - age_diff, age_w + age_diff, RCConstants.H_Partner);
				break;
				
			case 3:
				createPerson(household, RCConstants.MALE, age_m - age_diff, age_m + age_diff, RCConstants.H_Partner);
				createPerson(household, RCConstants.FEMALE, age_w - age_diff, age_w + age_diff, RCConstants.H_Partner);
				int gender = randDevice.assignRandomWeightedBoolean(mp.getGenderBoyProbability()) ? RCConstants.MALE : RCConstants.FEMALE;
				createPerson(household, gender, 0, age_ch1, RCConstants.KID);
				break;
				
			case 4:
				createPerson(household, RCConstants.MALE, age_m - age_diff, age_m + age_diff, RCConstants.H_Partner);
				createPerson(household, RCConstants.FEMALE, age_w - age_diff, age_w + age_diff, RCConstants.H_Partner);
				gender = randDevice.assignRandomWeightedBoolean(mp.getGenderBoyProbability()) ? RCConstants.MALE : RCConstants.FEMALE;
				createPerson(household, gender, 0, age_ch1, RCConstants.KID);
				gender = randDevice.assignRandomWeightedBoolean(mp.getGenderBoyProbability()) ? RCConstants.MALE : RCConstants.FEMALE;
				createPerson(household, gender, 0, age_ch2, RCConstants.KID);
				break;
		}
		dm.setPersonParentsInHousehold(household);
		return household;
	}
	
	private Person createPerson(Household h, int gender, int minage, int maxage, int position){
		Person p = new Person(true);
		p.setStatus_2(RCConstants.STATUS_IMMIGRANT);
		p.setSex(RCConstants.FEMALE);
		p.setDbirth( dm.assignDateOfBirth(currYear, minage, maxage ) );
		p.setHouseholdId(h.getId());
		p.setPosition_in_hh(RCConstants.H_Partner);
		p.setRun(run);				
		h.getPersons().add(p);
		return p;	
	}
	
	private List<Location> regionLocations(int key, int dataLevel){
		List<Location> list = new ArrayList<Location>();
		switch(dataLevel){
			case DATALEVEL_CH:
				for(Location loc : universeService.selectAllLocations()){
					list.add( loc );		
				}
				break;
			case DATALEVEL_CAN:
				for(Location loc : universeService.selectAllLocations()){
					if(loc.getLocationId().equals(key)){
						list.add( loc );		
					}
				}
				break;
			case DATALEVEL_MUN:
				for(Location loc : universeService.selectAllLocations()){
					if(loc.getMunicipalityId().equals(key)){
						list.add( loc );		
					}
				}
				break;
			case DATALEVEL_LOC:
				Location loc = universeService.selectLocationById(key);
				if(loc != null){
					list.add( loc );
				}
				break;	
		}
		return list;
	}
	
	private List<Household> emigrateHouseholds(int key, int dataLevel, int num_of_migrants){
		List<Household> OutHHlist = new ArrayList<Household>();
		
		List<Location> list = regionLocations(key, dataLevel);		
		List<Household> HHlist = new ArrayList<Household>();
		for(Location loc : list){
			HHlist.addAll(loc.getHouseholds());
		}	
		if(HHlist.size() > 0 ){		
			while(num_of_migrants > 0 ){
				if(HHlist.size() > 0 ){	
					int index = rg.nextInt(HHlist.size(), null);
					Household h = HHlist.get(index);
					//do not take new or closed households
					if(h.getRun() != currYear && h.getDclosing() == null){
						int numofPersons = UniverseUtil.getLivePersons(h.getPersons());
						if(numofPersons > 0){
							num_of_migrants -= numofPersons;
							OutHHlist.add(h);
						}
					}
					HHlist.remove(h);
				}
				else{
					logger.warn("Could not migrate " + num_of_migrants + " persons of of region: " + key);
					break;
				}
			}
		}
		return OutHHlist;
	}
		
	private boolean checkRegionInUniverse(int key, int dataLevel){
		boolean valid = false;
		switch(dataLevel){
			case DATALEVEL_CH:
				valid = true;
				break;
			case DATALEVEL_CAN:				
				for(Location loc : universeService.selectAllLocations()){
					if(loc.getLocationId().equals(key)){
						valid = true;
						break;		
					}
				}				
				break;
			case DATALEVEL_MUN:
				for(Location loc : universeService.selectAllLocations()){
					if(loc.getMunicipalityId().equals(key)){
						valid = true;
						break;		
					}
				}
				break;
			case DATALEVEL_LOC:
				Location loc = universeService.selectLocationById(key);
				if(loc != null){
					valid = true;
				}
				break;	
		}				
		return valid;
	}
	
	private int getFalcResidents(int key, int dataLevel){
		int resCount = 0;
		List<Location> list = regionLocations(key, dataLevel);		
		for(Location loc : list){
			for(Household h : loc.getHouseholds()){
				resCount += UniverseUtil.getLivePersons(h.getPersons());
			}
		}		
		return resCount;
	}
		
	private List<String> prepareImmProbColsList(){
		List<String> cols = new ArrayList<>();
		cols.add(probsIdField);
		cols.add(nameColumn);		
		cols.add(String.valueOf(currYear) );		//imm_prob_prefix + 		
		return cols;
	}
	
	private List<String> prepareImmHHProbColsList(){
		List<String> cols = new ArrayList<>();
		cols.add(immHHProbsIdField);
		cols.add("name");
		cols.add("Persons");
		cols.add("Share");		
		cols.add("age_m");	
		cols.add("age_w");	
		cols.add("age_ch1");	
		cols.add("age_ch2");	
		cols.add("age_diff");			
		return cols;
	}
	
}
