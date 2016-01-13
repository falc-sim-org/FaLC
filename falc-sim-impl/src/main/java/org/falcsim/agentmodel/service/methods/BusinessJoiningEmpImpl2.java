package org.falcsim.agentmodel.service.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.RandomGenerator;
import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.demography.methods.DemographyMethods;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.service.domain.FirmographyParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;
import org.falcsim.agentmodel.util.UniverseUtil;
import org.falcsim.agentmodel.util.UtilCounter;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.falcsim.agentmodel.utility.dao.UtilityDaoHB;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.falcsim.exit.ExitCodes;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessJoiningEmpImpl2 implements BusinessJoiningEmp {
	
	@Autowired
	private FirmographyParameters fps;
	@Autowired
	private RandDevice randDevice;
	@Autowired
	private RCRandomGenerator rg;
	@Autowired
	private RandomGenerator rndgen;	
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private UtilCounter uc;
	@Autowired
	private UtilityParameters up;
	@Autowired
	UtilityDaoHB utilDaoHB;
	@Autowired
	private DemographyMethods dm;
	
	private static final Integer INCOME_RANGE=50000;
	private static final Integer INCOME_BASE_CEO=50000;
	private static final Integer INCOME_BASE_WORKERS=20000;
	private static final double minDist = 2;		//swith from M -> KM
	
	private int run;
	private Integer currYear;
	private Map<Integer, Integer> countPersonsMap;
	private Map<Integer, List<Person>> freeEmployees;
	
	private int number_jobs = 0;
	private int number_workers = 0;
	private int number_employees = 0;
	private double employmentLevel;
	
	@Override
	public void joinEmployees(Location loc) {
		//not implemented...
	}

	private void printStatistics(List<Location> locs, boolean showunfilled){
		
		int countClosedOld = 0;
		int countClosed = 0;
		int countOpened = 0;
		logger.debug("=========================JOINEMP============================================");
		for(Location loc : locs){
			int workers = 0;
			int workplaces = 0;
			boolean withError = false;
			for(Business b : loc.getBusinesses()){
				if(b.getDclosing() != null){
					countClosed++;
					if(b.getRun() != run) countClosedOld++;
				}
				else countOpened++;
				workers += b.getPersons().size();
				workplaces += b.getNr_of_jobs();
				if((b.getNr_of_jobs() == null || b.getNr_of_jobs() == 0) && b.getDclosing() == null){
					logger.warn("   - Zero company: " + String.valueOf(b.getId()));
					withError = true;
				}
				if(b.getPersons().size() == 0 && b.getDclosing() == null){
					if(b.getRun() != run){
						// sometimes this also became normal, because of evolution
						//logger.warn("   - Empty company: " + String.valueOf(b.getId()) + " / " + String.valueOf(b.getNr_of_jobs()) );
						//withError = true;
					}
				}
				if(b.getPersons().size() != b.getNr_of_jobs() && b.getDclosing() == null){
						if(showunfilled){
							// this became normal, because of evolution
							//logger.warn("   - Unfilled company: " + String.valueOf(b.getId()) + " = " + b.getPersons().size() + " / " + String.valueOf(b.getNr_of_jobs()) );
							//withError = true;
						}
				}
				int wrongpersons = 0;
				for(Person p : b.getPersons()){
					if(!p.getBusinessId().equals(b.getId())){
						p.setBusinessId(b.getId());
					}
					if(p.getDdeath() == null && p.getBusinessId() == null){
						DateTime dBirth = new DateTime(p.getDbirth());
						int age = currYear - dBirth.getYear();
						if(age < fps.getMinimumJobAge() && age >= fps.getMaximumJobAge()){
							wrongpersons++;
						}
					}
					if(p.getDdeath() != null){
						wrongpersons++;
					}
				}
				if(wrongpersons > 0){
					logger.warn("   - Wrong company: " + String.valueOf(b.getId()));
					withError = true;
				}
			}
			if(withError){
				logger.debug("   - " +loc.getId()+ " - "+loc.getName() + " - "+ loc.getLocationId() );
				logger.debug("   - Workers:    " + String.valueOf(workers));
				logger.debug("   - Workplaces: " + String.valueOf(workplaces));
			}
		}
		if(countClosed > 0 ){
			logger.debug("   - Closed: " + String.valueOf(countClosed));
			logger.debug("   - Opened: " + String.valueOf(countOpened));
			logger.debug("   - OLD Closed: " + String.valueOf(countClosedOld));
			
		}
		logger.debug("=====================================================================");	
	}
	
	@Override
	public void joinEmployees(List<Location> locs) {

		logger.info("Joining Emp");
		double probFillJob = 1.;
		calculateEmploymentLevel(locs);
		if(employmentLevel > fps.getMaximalEmploymentLevel()){
			probFillJob = (number_jobs - number_employees) == 0 ? 0. : 
					((double)number_workers * (double)fps.getMaximalEmploymentLevel() - (double)number_employees) / ((double)number_jobs - (double)number_employees);
		}

		logger.info("            - Max. emploment level     : " + fps.getMaximalEmploymentLevel());
		logger.info("            - Cur. number of jobs      : " + number_jobs);
		logger.info("            - Cur. number of workers   : " + number_workers);
		logger.info("            - Cur. number of employees : " + number_employees);
		logger.info("            - Cur. emploment level     : " + employmentLevel);
		logger.info("            - Employment ratio         : " + probFillJob);
		
		logger.info("            - prepare free empl. table");
		generateWorkersMap(locs);
		int count = 0;
		int size = locs.size();
		
		List<Integer> locationList = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) locationList.add(i);
		
		while(locationList.size() > 0){
			Integer locPos = locationList.get(rndgen.nextInt(locationList.size())); //random get location
			locationList.remove(locPos);
			Location loc = locs.get(locPos);

			count++;
			logger.info("Processing "+count+"-th of "+size+" locations ( " + loc.getName() + " ).");
			
			List<RealIdProbInterval> ripis = prepareRipisMatrix(loc.getId(), locs);
			
			List<Business> businesses = loc.getBusinesses();
			//employees join company
			int joinedEmployees = 0;
			for (Business bus : businesses){				
				List<Person> employed = new ArrayList<Person>();
				//has free jobs
				if(bus.getPersons().size() < bus.getNr_of_jobs()){
					//free places will be filled
					for(int i = 0; i < bus.getNr_of_jobs() - bus.getPersons().size(); i++){
						try{
							boolean fillJob = true;
							if(getCEO(bus) != null ){
								fillJob = randDevice.assignRandomWeightedBoolean(probFillJob, getCEO(bus), sp.getCurrentYear() * (i+1), sp.getCurrentReferenceRun() );
							}
							else{
								fillJob = randDevice.assignRandomWeightedBoolean(probFillJob);
							}
							if(probFillJob == 1 || fillJob  ){
								Person newEmpl = getEmployee(loc, bus, ripis, locs); 
								if(newEmpl != null){
									employed.add(newEmpl);
									joinedEmployees++;
								}
								else{
									logger.info("     Joining eployee problem: cannot find free worker");	
								}
							}
						} catch (RCCustomException e) {
							logger.error(e.getMessage(), e);
							System.exit(ExitCodes.MODELS_BUSINESS_ERROR.getValue());
						}
					}
				}
				
				//bus.setRun(run);
				bus.getPersons().addAll(employed);
				if(bus.getPersons().size() > 0 ){
					//we need CEO, check
					if(! hasCEO(bus)){
						selectCEO(bus);
					}
				}
				else{
					//close empty companies
					loc.getEventsStatus().bb_closed_no_people ++;
					bus.setDclosing(dm.assignDateOnYear(sp.getCurrentYear()));
					bus.setNr_of_jobs(0);
					bus.setRun(run);
				}

			}
			loc.getEventsStatus().wrk_joined = joinedEmployees;
			uc.setCount7(uc.getCount7() + joinedEmployees);
			logger.info("     Joined Jobs: " + joinedEmployees);
		}
		
		printStatistics(locs, true);
	}
	
	
	@Override
	public void init() {	
		run = sp.getCurrentTimeMarker();
		currYear = sp.getCurrentYear();		
		
		if (!up.isDistanceMapInitialized()){
			up.initDistanceMap(up.getLocs());
		}
	}

	private List<RealIdProbInterval> prepareRipisMatrix(Integer locId, List<Location> locs)
	{
		List<RealIdProbInterval> ripis = new ArrayList<>();
		double tot=0;
		double totOld=0;
		for (Location lic : locs){
			if(countPersonsMap.get(lic.getId()) > 0){
				RealIdProbInterval ripi = new RealIdProbInterval();
				ripi.setId(lic.getId());
				totOld=tot;
				//tot += (countPersonsMap.get(locId)/Math.max(minDist, up.getDistanceMap().get(locId+SEP+lic.getId())));
				tot += (countPersonsMap.get(lic.getId()).doubleValue()/Math.max(minDist, up.getDistance(locId, lic.getId())));
				ripi.setLower(totOld);
				ripi.setUpper(tot);
				ripis.add(ripi);
			}
		}
		List<RealIdProbInterval> list = new ArrayList<RealIdProbInterval>();
		for(RealIdProbInterval ripi : ripis){
			if(ripi.getLower() == 0 && ripi.getUpper() == 0) list.add(ripi);
		}
		for(RealIdProbInterval ripi : list)ripis.remove(ripi);
		return ripis;
	}
	
	private void calculateEmploymentLevel(List<Location> locs){
		number_jobs = 0;
		number_workers = 0;
		number_employees = 0;
		for (Location loc : locs) {
			for (Business b : loc.getBusinesses()){
				if(b.getDclosing() == null ){
					number_jobs += b.getNr_of_jobs();	
				}
			}
			for (Household h : loc.getHouseholds()){
				for (Person p: h.getPersons() ){
					if(p.getDdeath() == null){
						DateTime dBirth = new DateTime(p.getDbirth());
						int age = currYear - dBirth.getYear();
						if(age >= fps.getMinimumJobAge() && age <= fps.getMaximumJobAge()){
							number_workers++;
						}
						if(p.getBusinessId() != null){
							number_employees++;
						}
					}
				}
			}
		}
		employmentLevel = number_workers == 0 ? Integer.MAX_VALUE : (double)number_jobs / (double)number_workers;
	}
	
	private void generateWorkersMap(List<Location> locs){
		freeEmployees = new HashMap<Integer, List<Person>>();
		countPersonsMap = new HashMap<Integer, Integer>() ;
		for (Location loc : locs) {
			List<Person> freeEmpLoc = new ArrayList<Person>();
			freeEmployees.put(loc.getId(), freeEmpLoc);
			int countPersons = 0;
			for (Household h : loc.getHouseholds()){
				List<Person> ps = h.getPersons();
				//countPersons += ps.size();
				for (Person p: ps ){
					if(p.getDdeath() == null && p.getBusinessId() == null){
						DateTime dBirth = new DateTime(p.getDbirth());
						int age = currYear - dBirth.getYear();
						if(age >= fps.getMinimumJobAge() && age <= fps.getMaximumJobAge()){
							freeEmpLoc.add(p);
							countPersons++;
						}
					}
				}
			}
			countPersonsMap.put(loc.getId(), countPersons);
		}
	}
	
	private boolean hasCEO(Business bus){
		for (Person p : bus.getPersons()){
			try{
				if(p.getPosition_in_bus() == RCConstants.CEO){
					return true;	
				}
			}
			catch(Exception e){
				//Person a = p;
			}
		}
		return false;
	}
	
	private void selectCEO(Business bus){
		List<Person> pers = bus.getPersons();
		if(pers.size() > 0 ){
			for (Person p : pers){
				DateTime dBirth = new DateTime(p.getDbirth());
				int age = currYear - dBirth.getYear();
				if(age >= fps.getMinimumCeoAge() && age <= fps.getMaximumCeoAge() ){
					changePositionCEO(p);
					return;
				}
			}
			//someone must do this
			changePositionCEO(pers.get(0));
		}
	}
	
	private Person getEmployee(Location loc, Business bus, List<RealIdProbInterval> ripis, List<Location> locs) throws RCCustomException{
		List<Person> freeEmpLoc = null;
		if(ripis.size() > 0){
			int loops = 10000;
			int count = 0;
			do{
				loops--; //just to be sure to not stay in cycle
				Integer locTargetId = 0;
				try{
					Person ceo = getCEO(bus);
					locTargetId = up.getLocationChoiceManagedByID() ? 
							ceo != null ? randDevice.assignWeightedRandomId(rg, ripis, ceo, currYear, sp.getCurrentReferenceRun() * (count+1) ) : randDevice.assignWeightedRandomId(ripis)
							: randDevice.assignWeightedRandomId(ripis);
				}
				catch(RCCustomException e){
					for (RealIdProbInterval rpi : ripis) {
						logger.info("Ripis id=" + rpi.getId() + " min:" + rpi.getLower() + " max:" + rpi.getUpper());
					}
					throw e;
				}
				freeEmpLoc = freeEmployees.get(locTargetId);
				if(freeEmpLoc.size() > 0){
					//Person p = freeEmpLoc.get(rg.nextInt(freeEmpLoc.size(),0,0,0, RandomGeneratorModelID.None)); 
					Person p = freeEmpLoc.get(rndgen.nextInt(freeEmpLoc.size() ) ); 
					freeEmpLoc.remove(p);
					int actPerson = countPersonsMap.get(locTargetId) -1;
					countPersonsMap.put(locTargetId, actPerson);
					joinBusiness(bus, p);
					return p;
				}
				else{
					List<RealIdProbInterval> tmpripis = prepareRipisMatrix(loc.getId(), locs);
					ripis.clear();ripis.addAll(tmpripis);
					logger.info("RIPIS recalculated for: " + loc.getName() + ", no workers in: " + String.valueOf(locTargetId));
				}
				count++;
			}while(freeEmpLoc.size() == 0 && loops > 0 && ripis.size() > 0 );
		}
		logger.warn("Cannot allocate worker for: " + loc.getName());
		return null;
	}
	
	private void joinBusiness(Business bus, Person p){
		p.setBusinessId(bus.getId());
		p.setPosition_in_bus(RCConstants.EMPLOYEE);
		p.setEmployed_since(currYear);
		p.setFinance_1(INCOME_BASE_WORKERS+rndgen.nextInt(INCOME_RANGE));
		p.setRun(run);
	}	
	
	private void changePositionCEO( Person p){
		p.setRun(run);
		p.setPosition_in_bus(RCConstants.CEO);
		p.setFinance_1(INCOME_BASE_CEO+rndgen.nextInt(INCOME_RANGE));
	}
	
	private Person getCEO(Business bus){
		List<Person> pers = bus.getPersons();
		if(pers.size() > 0 ){
			for (Person p : pers){
				if(p.getPosition_in_bus() == RCConstants.CEO) return p;
			}
		}
		return null;
	}
}
