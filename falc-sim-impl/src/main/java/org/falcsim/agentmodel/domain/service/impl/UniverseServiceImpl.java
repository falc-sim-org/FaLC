package org.falcsim.agentmodel.domain.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.csv.GeneralCsv;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.dao.jdbc.csv.CsvTrasaction;
import org.falcsim.agentmodel.dao.util.ClassStringUtil;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.Step;
import org.falcsim.agentmodel.domain.Universe;
import org.falcsim.agentmodel.domain.idgenerators.BusinessIdGenerator;
import org.falcsim.agentmodel.domain.idgenerators.HouseholdIdGenerator;
import org.falcsim.agentmodel.domain.idgenerators.PersonIdGenerator;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.hist.dao.HistoryDao;
import org.falcsim.agentmodel.hist.dao.StepDao;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.service.methods.util.StepMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Synchronized loading/saving of whole Universe, universe service utilities
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
@Service("universeService")
public class UniverseServiceImpl implements UniverseServiceUtil {

	private final static Logger _log = Logger
			.getLogger(UniverseServiceImpl.class);

	private UniverseState universeState = UniverseState.CLEAR;
	
	/**
	 * cache loaded universe
	 */
	private Universe universeCache = null;

	/**
	 * lock object for synchronization
	 */
	private final Object lock = new Object();
	
	/**
	 * steps cache
	 */

	private List<Step> steps;
	/**
	 * cantons cache
	 */
	private List<Canton> cantons;
	
	/**
	 * municipalities cache
	 */
	private List<Municipality> municipalities;	
	
	/**
	 * locations cache
	 */
	List<Location> locations;
	/**
	 * businesses cache
	 */
	private List<Business> businesses;
	/**
	 * households cache
	 */
	private List<Household> households;
	/**
	 * persons cache
	 */
	private List<Person> persons;
		
	/**
	 * canton index
	 */
	private Map<Integer, Canton> idxCanton;
	/**
	 * municipality index
	 */
	private Map<Integer, Municipality> idxMunicipality;	
	/**
	 * location index
	 */
	private Map<Integer, Location> idxLocation;
	/**
	 * household index
	 */
	private Map<Integer, Household> idxHousehold;
	
	/**
	 * business index
	 */
	private Map<Integer, Business> idxBusiness;
	/**
	 * person index
	 */
	private Map<Integer, Person> idxPerson;
	
	/**
	 * location class descriptor
	 */
	private final static ClassDescriptor locationClassDescriptor;

	/**
	 * location indexes as they were loaded from DB
	 */
	private Set<Integer> idxLoadedLocation;
	/**
	 * household indexes as they were loaded from DB
	 */
	private Set<Integer> idxLoadedHousehold;
	/**
	 * business indexes as they were loaded from DB
	 */
	private Set<Integer> idxLoadedBusiness;
	/**
	 * person indexes as they were loaded from DB
	 */
	private Set<Integer> idxLoadedPerson;
	/**
	 * location parameters indexes as they were loaded from DB
	 */
	private Set<Integer> idxLoadedLocationParameter;
	/**
	 * household parameter indexes as they were loaded from DB
	 */
	private Set<Integer> idxLoadedHouseholdParameter;
	/**
	 * business parameters indexes as they were loaded from DB
	 */
	private Set<Integer> idxLoadedBusinessParameter;
	
	/**
	 * steps DAO
	 */
	@Autowired
	private StepDao sdao;

	@Autowired
	private ServiceParameters sp;
	
	@Autowired
	private HistoryDao historyDao;

	@Autowired
	private GeneralDao generalDao;
	
	@Autowired
	private BusinessIdGenerator bIdGen;

	@Autowired
	private HouseholdIdGenerator hIdGen;
	
	@Autowired
	private PersonIdGenerator pIdGen;
	
	@Autowired
	private StepMethods sm;
	
	@Autowired
	private RunParameters rp;
	
	@Autowired
	private UniverseServiceUtilities universeServiceFnc;
	
	/**
	 * session factory
	 */
	// private SessionFactory sessionFactory;

	static {
		try {
			locationClassDescriptor = new ClassDescriptor(Location.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Universe getUniverse(){
		return universeCache;
	}
	
	@Override
	public UniverseState getUniverseState(){
		return universeState;
	}
	
	@Override
	public void clearUniverse(){
		universeCache = null;
		universeState = UniverseState.CLEAR;
	}
	
	/**
	 * save or update location
	 */
	@Override
	public void saveOrUpdateLocation(Location location) {
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		// location should be reused from cached one so no update needed,
		// check just save
		Location tmpLocation = idxLocation.get(location.getId());
		if (tmpLocation == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Adding new location " + location.getId());
			}
			idxLocation.put(location.getId(), location);
			locations.add(location);
		} else {
			if (tmpLocation != location) {
				throw new RuntimeException(
						"Location with this id already exists.");
			}
		}
		refreshLocationIndex(location);
	}

	/**
	 * select locations by criterion
	 */
	@Override
	public List<Location> selectLocationsByCriterion(String query) {
		List<Location> locations;
		if (universeCache == null) {
			loadUniverse();
		}
		locations = generalDao.readAll(Location.class, "".equals(query.trim()) ? "" : "subarea_nr in " + query);
		List<Integer> ints = new ArrayList<Integer>();
		for(Location l : locations){
			ints.add(l.getId());
		}
		locations.clear();
		Location location;
		for(Integer i : ints){
			location = idxLocation.get(i);
			if(location == null){
				throw new RuntimeException("Location not in universe");
			} 
			locations.add(location);
		}
		return locations;
	}

	/**
	 * update location
	 */
	@Override
	public void updateLocation(Location location) {
		saveOrUpdateLocation(location);
	}

	/**
	 * save or update list of locations
	 */
	@Override
	public void saveOrUpdateLocations(List<Location> locations) {
		for (Location location : locations) {
			saveOrUpdateLocation(location);
		}
	}

	/**
	 * select all locations
	 */
	@Override
	public List<Location> selectAllLocations() {
		List<Location> locations;
		if (universeCache == null) {
			loadUniverse();
		}
		locations = this.locations;
		return locations;
	}

	@Override
	public List<Canton> selectAllCantons() {
		List<Canton> cantons;
		if (universeCache == null) {
			loadUniverse();
		}
		cantons = this.cantons;
		return cantons;
	}
	
	@Override
	public List<Municipality> selectAllMunicipalities(){
		List<Municipality> municipalities;
		if (universeCache == null) {
			loadUniverse();
		}
		municipalities = this.municipalities;
		return municipalities;
	}
	
	@Override
	public Canton selectCantonById(int cantonId) {
		Canton canton;
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		canton = idxCanton.get(cantonId);
		return canton;
	}
	
	@Override
	public Municipality selectMunicipalityById(int municipalityId){
		Municipality municipality;
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		municipality = idxMunicipality.get(municipalityId);
		return municipality;
	}
	
	
	/**
	 * select location by locationId
	 */
	@Override
	public Location selectLocationById(int locationId) {
		Location location;
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		location = idxLocation.get(locationId);
		return location;
	}

	/**
	 * select locations by distance from location
	 */
	@Override
	public List<Location> selectLocationsByDistanceFromLocation(int locationId,
			float radius) {
		throw new RuntimeException(
				"Method selectLocationsByDistanceFromLocation is incosistent");
	}

	/**
	 * select location ids by criterion
	 */
	@Override
	public List<Integer> selectLocationIdsByCriterion(String criterion) {
		GeneralCsv csv = new GeneralCsv();
		
		List<Object[]> tmp = csv.loadTableGeneral(locationClassDescriptor.getTableName(), 
				null, 
				"".equals(criterion.trim()) ? "" : "subarea_nr in " + criterion );
		List<Integer> ret = new ArrayList<Integer>();
		for(Object[] o : tmp){
			ret.add((Integer)o[0]);
		}
		return ret;
	}

	/**
	 * select businesses map by location list
	 */
	@Override
	public Map<Integer, List<Business>> selectBusinessesesMapByLocationList(
			List<Location> locations) {
		throw new RuntimeException(
				"Method selectBusinessesesMapByLocationList should not be used!");
	}

	@Override
	public List<Business> selectAllBusinesses() {
		List<Business> businesses;
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		businesses = this.businesses;
		return businesses;
	}

	/**
	 * select business by businessId
	 * 
	 * @param businessId
	 * @return Business 
	 */
	@Override
	public Business selectBusinessById(int businessId) {
		Business business;
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		business = idxBusiness.get(businessId);
		if (business == null) {
			throw new RuntimeException("Universe inconsistency");
		}
		return business;
	}

	/**
	 * select households by locationId
	 * 
	 * @param locationId
	 * @return List of Households in location
	 */
	@Override
	public List<Household> selectHouseholdsByLocationId(int locationId) {
		throw new RuntimeException(
				"Method selectHouseholdsByLocationId should not be used, access location instead");
	}

	/**
	 * select household by householdId
	 */
	@Override
	public Household selectHouseholdById(int householdId) {
		Household household;
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		household = idxHousehold.get(householdId);
		if (household == null) {
			throw new RuntimeException("Universe inconsistency");
		}
		return household;
	}

	/**
	 * save or update household
	 */
	@Override
	public void saveOrUpdateHousehold(Household household) {
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		Household tmpHousehold = idxHousehold.get(household.getId());
		if (tmpHousehold == null) {
			households.add(household);
			idxLocation.get(household.getLocationId()).getHouseholds()
					.add(household);
		} else {
			if (tmpHousehold != household) {
				throw new RuntimeException(
						"Household with this id already exists.");
			}
		}
		refreshHouseholdIndex(household);
	}

	/**
	 * select all households
	 */
	@Override
	public List<Household> selectAllHouseholds() {
		List<Household> households;
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		households = this.households;
		return households;
	}

	/**
	 * return map of households for given list of locations
	 */
	@Override
	public Map<Integer, List<Household>> selectHouseholdsMapByLocationList(
			List<Location> locations) {
		throw new RuntimeException(
				"Method selectHouseholdsMapByLocationList should not be used, access locations directly");
	}

	/**
	 * return count of persons by location
	 */
	@Override
	public int selectPersonsCountByLocation(int locationId) {
		int count = 0;
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		for (Household household : locations.get(locationId).getHouseholds()) {
			count += household.getPersons() == null ? 0 : household
					.getPersons().size();
		}
		return count;
	}

	/**
	 * return list of all persons
	 */
	@Override
	public List<Person> selectAllPersons() {
		List<Person> persons;
		if (universeCache == null) {
			throw new RuntimeException("Universe not initialized");
		}
		persons = this.persons;
		return persons;
	}

	/**
	 * return loaded universe, fill local cache
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Universe loadUniverse() {
		synchronized (lock) {
			if (universeCache == null) {
				_log.info("Loading Universe");
				universeState = UniverseState.LOADING;
				
				_log.info("JDBC approach");
				try {							
					
					steps = Collections.synchronizedList(generalDao.readAll(Step.class));

					sp.setStartYear(rp.getApp_theYear());
					if(steps.size() > 0){
						sm.setStep(steps.get(0).getStep());			//set current step
						sp.setStartYear(steps.get(0).getYear());	//set start year
					}
					sp.setCurrentTimeMarker( sm.getStep() );		//set current time maker (cycle)
					sp.setCurrentYear( sp.getStartYear() + sp.getCurrentTimeMarker() );	//set current year	
					sp.setCurrentRun( 0 );
					
					//, "".equals(locationQuery.trim()) ? "" : "cantid in " + locationQuery
					cantons = (List<Canton>) Collections.synchronizedList(generalDao.readAll(Canton.class)); 					
					//, "".equals(locationQuery.trim()) ? "" : "cantid in " + locationQuery	
					municipalities = (List<Municipality>) Collections.synchronizedList(generalDao.readAll(Municipality.class));		
					if(rp.getApp_systemQuery() == null){
						rp.setApp_systemQuery("");
					}
					
					locations = (List<Location>) Collections.synchronizedList(generalDao.readAll(Location.class, 
							"".equals(rp.getApp_systemQuery().trim()) ? "" : "subarea_nr in " + rp.getApp_systemQuery()));
					
					businesses = (List<Business>) Collections.synchronizedList(generalDao.readAll(Business.class));					
					households = (List<Household>) Collections.synchronizedList(generalDao.readAll(Household.class));
					persons = (List<Person>) Collections.synchronizedList(generalDao.readAll(Person.class));
					
					SortUniverseAndInitializeIdGenerators();
					
					idxCanton = new ConcurrentHashMap<Integer, Canton>(100);
					for (Canton canton : cantons) {
						idxCanton.put(canton.getLocationId(), canton);
					}
					idxMunicipality = new ConcurrentHashMap<Integer, Municipality>(100);
					for (Municipality municipality : municipalities) {
						idxMunicipality.put(municipality.getId(), municipality);
					}
					
					idxLocation = new ConcurrentHashMap<Integer, Location>(100);
					for (Location location : locations) {
						idxLocation.put(location.getId(), location);
					}
					idxLoadedLocation = new HashSet<Integer>();
					idxLoadedLocation.addAll(idxLocation.keySet());
					
					idxHousehold = new ConcurrentHashMap<Integer, Household>(
							100);
					for (Household hh : households) {
						idxHousehold.put(hh.getId(), hh);
						Location location = idxLocation.get(hh.getLocationId());
						if (location.getHouseholds() == null) {
							location.setHouseholds((List<Household>) Collections
									.synchronizedCollection(new ArrayList<Household>()));
						}
						location.getHouseholds().add(hh);
					}
					idxLoadedHousehold = new HashSet<Integer>();
					idxLoadedHousehold.addAll(idxHousehold.keySet());
					
					idxBusiness = new ConcurrentHashMap<Integer, Business>(100);
					for (Business b : businesses) {
						idxBusiness.put(b.getId(), b);
						Location location = idxLocation.get(b.getLocationId());
						if (location == null) {
							_log.fatal("Not found locaton with id "
									+ b.getLocationId());
							continue;
						}
						if (location.getBusinesses() == null) {
							location.setBusinesses((List<Business>) Collections
									.synchronizedCollection(new ArrayList<Business>()));
						}
						location.getBusinesses().add(b);
					}
					idxLoadedBusiness = new HashSet<Integer>();
					idxLoadedBusiness.addAll(idxBusiness.keySet());
					
					idxPerson = new ConcurrentHashMap<Integer, Person>(100);
					for (Person person : persons) {
						idxPerson.put(person.getId(), person);
						Household hh = idxHousehold
								.get(person.getHouseholdId());
						if (hh.getPersons() == null) {
							hh.setPersons((List<Person>) Collections
									.synchronizedCollection(new ArrayList<Person>()));
						}
						hh.getPersons().add(person);
						if (person.getBusinessId() != null) {
							Business b = idxBusiness
									.get(person.getBusinessId());
							// <!-- added by mifr
							if(b == null)
								continue;
							// --> till here
							if (b.getPersons() == null) {
								b.setPersons((List<Person>) Collections
										.synchronizedCollection(new ArrayList<Person>()));
							}
							b.getPersons().add(person);
						}
					}
					idxLoadedPerson = new HashSet<Integer>();
					idxLoadedPerson.addAll(idxPerson.keySet());
					
					universeCache = new Universe();
					universeCache.setLocations(this.locations);
					
					universeServiceFnc.updateLocationsTableInUniverse(universeCache);
					
				} catch (Exception e) {
					_log.error(e, e);
					throw new RuntimeException(e);
				}
				universeState = UniverseState.LOADED;
				_log.info("Universe loaded");
				
			}
		}
		return universeCache;
	}

	private void SortUniverse(){
		_log.info("Sorting Universe...");
		UniverseComparator ucomp = new UniverseComparator();
		Collections.sort(cantons, ucomp);
		Collections.sort(municipalities, ucomp);
		Collections.sort(locations, ucomp);
		Collections.sort(businesses, ucomp);
		Collections.sort(households, ucomp);
		Collections.sort(persons, ucomp);
		_log.info("Universe Sorted...");
	}
	
	private void SortUniverseAndInitializeIdGenerators(){
		SortUniverse();

		bIdGen.initializeFromUniverse(businesses.size() == 0 ? 0 : businesses.get(businesses.size()-1).getId() );
		hIdGen.initializeFromUniverse(households.size() == 0 ? 0 : households.get(households.size()-1).getId() );
		pIdGen.initializeFromUniverse(persons.size() == 0 ? 0 : persons.get(persons.size()-1).getId() );
		
		_log.info("ID Generators set...");	
	}
	
	@Override
	public void resetUniverseIndexes() {
		synchronized (lock) {
			if (universeCache != null) {
				_log.info("Reset Universe Indexes");
				try{
					// check consistency
					checkUniverseConsistency(universeCache);
					//refreshUniverseIndex(universeCache);
					
					int countL = idxLocation.size();
					int countH = idxHousehold.size();
					int countB = idxBusiness.size();
					int countP = idxPerson.size();
					
					idxLocation.clear();
					idxHousehold.clear();
					idxBusiness.clear();
					idxPerson.clear();
					for(Location loc : universeCache.getLocations()){
						idxLocation.put(loc.getId(), loc);
						for(Household h : loc.getHouseholds()){
							idxHousehold.put(h.getId(), h);
							for(Person p : h.getPersons()){
								idxPerson.put(p.getId(), p);
							}
						}
						for(Business b : loc.getBusinesses()){
							idxBusiness.put(b.getId(), b);
						}
					}
	
					_log.info("L : " + String.valueOf(countL - idxLocation.size())  );
					_log.info("H : " + String.valueOf(countH - idxHousehold.size())  );
					_log.info("P : " + String.valueOf(countP - idxPerson.size())  );
					_log.info("B : " + String.valueOf(countB - idxBusiness.size())  );
				} catch (Exception e) {
					_log.error(e, e);
					throw new RuntimeException(e);
				}
				
				_log.info("Reset Universe Indexes done");
			}
			else{
				_log.info("Universe not loaded - can not reset indexes");
			}
		}
	}
	
	/**
	 * save or update universe
	 */
	@Override
	public void saveOrUpdateUniverse(boolean saveStepTable, boolean freeUniverseCache) {
		saveOrUpdateUniverse(universeCache, saveStepTable, freeUniverseCache);
	}	
	
	/**
	 * save or update universe
	 */
	@Override
	public void saveOrUpdateUniverse(boolean saveStepTable) {
		saveOrUpdateUniverse(universeCache, saveStepTable, true);
	}

	/**
	 * save or update universe
	 */
	@Override
	public void saveOrUpdateUniverse(Universe universe) {
		saveOrUpdateUniverse(universe, true, true);
	}
	
	/**
	 * save or update universe
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void saveOrUpdateUniverse(Universe universe, boolean saveStepTable, boolean freeUniverseCache) {
		synchronized (lock) {
			// rebuild indexes from current universe
			_log.info("Saving universe - rebuild indexes");
			universeState = UniverseState.SAVING;

			// check consistency
			checkUniverseConsistency(universe);
			
			refreshUniverseIndex(universe);

			_log.info("Saving locations");
			_log.info("Number of locations: " + idxLocation.values().size());
			generalDao.saveOrUpdate(Location.class, null, new ArrayList(idxLocation.values()), null);

			_log.info("Saving households");
			_log.info("Number of households: " + idxHousehold.values().size());
			generalDao.saveOrUpdate(Household.class, null, new ArrayList(idxHousehold.values()), null);
			
			_log.info("Saving businesses");
			_log.info("Number of businesses: " + idxBusiness.values().size());
			generalDao.saveOrUpdate(Business.class, null, new ArrayList(idxBusiness.values()), null);

			_log.info("Saving persons");
			_log.info("Number of persons: " + idxPerson.values().size());
			generalDao.saveOrUpdate(Person.class, null, new ArrayList(idxPerson.values()), null);

			/*_log.info("Saving location parameters");
			_log.info("Number of location parameters: " + idxLocationParameters.values().size());
			generalDao.saveOrUpdate(LocationParameters_Impl_1.class, null, new ArrayList(idxLocationParameters.values()), null);*/

			/*_log.info("Saving business parameters");
			_log.info("Number of business parameters: " + idxBusinessParameters.values().size());
			generalDao.saveOrUpdate(BusinessParameters_ImplA.class, null, new ArrayList(idxBusinessParameters.values()), null);*/

			/*_log.info("Saving household parameters");
			_log.info("Number of household parameters: " + idxHouseholdParameters.values().size());
			generalDao.saveOrUpdate(HouseholdParameters_ImplA.class, null, new ArrayList(idxHouseholdParameters.values()), null);*/

			CsvTrasaction.commitAll();
			
			if (saveStepTable){
				sdao.updateSteps(sp.getCurrentTimeMarker(), sp.getStartYear() );
				CsvTrasaction.commitLast();
			}
			checkUniverseCsvtFiles();
			
			if(freeUniverseCache){
				universeCache = null;
				universeState = UniverseState.CLEAR;
			}
			else{
				universeState = UniverseState.LOADED;
			}
		}
	}

	private void checkUniverseConsistency(Universe universe) {
		//TODO: check that business location is same as location business
		//+ households, persons,...
		//dates are below current year...
	}

	/**
	 * save or update universe
	 */
	@Override
	public void saveOrUpdateUniverse() {
		saveOrUpdateUniverse(universeCache);
	}

	/**
	 * add universe history
	 */
	@Override
	public void addHistory() {
		synchronized (lock) {
			if (universeCache != null) {
				throw new RuntimeException("Universe not yet stored.");
			}
			historyDao.make_history();
		}
	}

	/**
	 * add history of universe
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void saveHistory(List list, Class cls){
		synchronized (lock) {
			generalDao.saveOrUpdate(cls, null, list, null);
		}
	}
	
	/**
	 * refresh universe indexes
	 * 
	 * @param universe
	 */
	private void refreshUniverseIndex(Universe universe) {
		for (Location location : universe.getLocations()) {
			refreshLocationIndex(location);
		}
	}

	/**
	 * refresh location index
	 * 
	 * @param location
	 */
	private void refreshLocationIndex(Location location) {

		idxLocation.put(location.getId(), location);
		for (Household household : location.getHouseholds()) {
			refreshHouseholdIndex(household);
		}
		for (Business business : location.getBusinesses()) {
			refreshBusinessIndex(business);
		}
	}

	/**
	 * refresh household index
	 * 
	 * @param household
	 */
	private void refreshHouseholdIndex(Household household) {
		idxHousehold.put(household.getId(), household);
		refreshPersonsIndex(household.getPersons());
	}

	/**
	 * refresh business index
	 * 
	 * @param business
	 */
	private void refreshBusinessIndex(Business business) {
		idxBusiness.put(business.getId(), business);
		refreshPersonsIndex(business.getPersons());
	}

	/**
	 * refresh persons index
	 * 
	 * @param persons
	 */
	private void refreshPersonsIndex(Collection<Person> persons) {
		for (Person person : persons) {
			idxPerson.put(person.getId(), person);
		}

	}

	private void checkUniverseCsvtFiles(){
		String csvtSeparator = ",";
		String path = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath();
		String csvtExt = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getColumnDefinitionExtension();
		
		Class<?> tables[] = new Class[]{
			Business.class,
			Household.class,
			Location.class,
			Person.class,
			Step.class
		};
		
		GeneralCsv csv = new GeneralCsv();
		
		for(int i = 0; i < tables.length; i++){
			String tableName = ClassStringUtil.EMPTY;
			tableName = ClassDescriptor.getClassDescriptor(tables[i]).getTableName();

			File f = new File(path + tableName + csvtExt);
			if(!f.exists()){
				String csvtHeader = ClassStringUtil.getColumnsTypes(tables[i], csvtSeparator);
				csv.createFile(path + tableName + csvtExt, csvtHeader);
			}
		}	
	}
	
	@Override
	public void populateUniverse() {
		
		UniverseState oldState = universeState;
		universeState =	UniverseState.POPULATING;
		try{
			String csvSeparator = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getColumnsSeparator();
			String csvtSeparator = ",";
			String path = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getPath();
			String csvExt = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getTableExtension();
			String csvtExt = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getColumnDefinitionExtension();
			
			Class<?> tables[] = new Class[]{
				Business.class,
				Household.class,
				Location.class,
				Person.class,
				Step.class
			};
			
			GeneralCsv csv = new GeneralCsv();
			
			for(int i = 0; i < tables.length; i++){
				String tableName = ClassStringUtil.EMPTY;
				
				tableName = ClassDescriptor.getClassDescriptor(tables[i]).getTableName();
				
				String csvHeader = ClassStringUtil.getHeader(tables[i], csvSeparator);
				String csvtHeader = ClassStringUtil.getColumnsTypes(tables[i], csvtSeparator);
				csv.createFile(path + tableName + csvExt, csvHeader);
				csv.createFile(path + tableName + csvtExt, csvtHeader);
			}
		}
		finally{
			universeState = oldState;
		}
	}
}

class UniverseComparator implements Comparator<Object>{	
	@Override
    public int compare(Object c1, Object c2) {
    	if(c1 == null || c2 == null ) return 0;
    	if(c1 instanceof Canton && c2 instanceof Canton){
    		return ((Canton)c1).getLocationId() - ((Canton)c2).getLocationId();
    	}
    	else if(c1 instanceof Municipality && c2 instanceof Municipality){
    		return ((Municipality)c1).getId() - ((Municipality)c2).getId();
    	}
    	else if(c1 instanceof Location && c2 instanceof Location){
    		return ((Location)c1).getId() - ((Location)c2).getId();
    	}    
    	else if(c1 instanceof Household && c2 instanceof Household){
    		return ((Household)c1).getId() - ((Household)c2).getId();
    	} 
    	else if(c1 instanceof Business && c2 instanceof Business){
    		return ((Business)c1).getId() - ((Business)c2).getId();
    	} 
    	else if(c1 instanceof Person && c2 instanceof Person){
    		return ((Person)c1).getId() - ((Person)c2).getId();
    	}     	
    	else return 0;
    }    
};