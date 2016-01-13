package org.falcsim.agentmodel.service.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.RandomGenerator;
import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.demography.methods.DemographyMethods;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.util.UtilCounter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessRiseAndFallImpl implements BusinessRiseAndFall{
	/**
	 * firmography parameters
	 */
	@Autowired
	private BusinessEconomicDevRev1 becdrev1;
	
	@Autowired
	private RandDevice randDevice;
	@Autowired
	private RandomGenerator rg;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private UtilCounter uc;
	@Autowired
	private DemographyMethods dm;
	@Autowired
	private GeneralDao generalDao;
	
	private static final String  closuresProbsTable = "firms_closure";
	private static final String  closuresProbsIdField = "sector_id";
	private static final String  closuresProbsNameField = "sector";
	private static final String  closure_prob_prefix = "D";
	private static final String public_schema = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDefaultSchema();
	
	private int run;
	private Integer currYear;
	
	private Map<Integer, Map<String, ?>> closuresProbs;
	private String closure_prob_column_str;
	
	@Override
	public void process(Location loc) {
		List<Business> businesses = loc.getBusinesses();
			
		//get closed businesses
		int closedBusinesses = 0;
		int closedBusinessesJobs = 0;
		for (Business bus : businesses){
			Double probClose = getCurrYearClosureProbBySector(bus.getType_1());
			//randDevice.assignRandomWeightedBoolean(probClose, id, sp.getCurrentYear(), sp.getCurrentReferenceRun(), RandomGeneratorModelID.BBDestroy )
			boolean destroyBusiness = false;
			if(getCEO(bus) != null ){
				destroyBusiness = randDevice.assignRandomWeightedBoolean(probClose, getCEO(bus), sp.getCurrentYear(), sp.getCurrentReferenceRun() );
			}
			else{
				destroyBusiness = randDevice.assignRandomWeightedBoolean(probClose);
			}
			if (destroyBusiness ){
				closedBusinesses++;
				closedBusinessesJobs += bus.getNr_of_jobs();
				closeBusiness(bus);
			}
		}
		loc.getEventsStatus().bb_closed = closedBusinesses;
		loc.getEventsStatus().bb_closed_jobs = closedBusinessesJobs;
		uc.setCount5(uc.getCount5() + closedBusinesses);
		logger.info("     Closed businesses: " + closedBusinesses);
		
		int opennedBusinesses = 0;
		int opennedBusinessesJobs = 0;
		
		//register new businesses per sectors
		List<Business> newCompanies = new ArrayList<Business>();
		//int num_of_new_business = becd.getFuture_businesses_count() - becd.getTotal_active_businesses() + closedBusinesses;
		int num_of_new_business = 0;
		for(int sector = 1; sector <= 10; sector++){
			//int buss_counter = (int)Math.round(num_of_new_business * becd.getBusinessesPercSector(sector));
			int buss_counter = becdrev1.getNumOfNewBusinesses(loc, sector);
			num_of_new_business += buss_counter;
			
			while(buss_counter > 0){
				int num_of_emplyees = BusinessEconomicDevRev1.num_of_workplaces_per_company;
//				if (randDevice.assignRandomWeightedBoolean(fps.getSmallCompanyCreateProb())){
//					num_of_emplyees = 1 + rg.nextInt(fps.getSmallCompanyMaxEmployees());
//				}
//				else{
//					num_of_emplyees = fps.getSmallCompanyMaxEmployees() +1 + rg.nextInt(fps.getBigCompanyMaxEmployees() - fps.getSmallCompanyMaxEmployees());	
//				}
				Business bus = createBusiness(loc, sector, num_of_emplyees);				
				opennedBusinesses++;
				opennedBusinessesJobs += num_of_emplyees;
				newCompanies.add(bus);
				buss_counter--;
			}
		}
		if(newCompanies.size() > 0){
			loc.getBusinesses().addAll(newCompanies);
		}
		
		loc.getEventsStatus().bb_created = opennedBusinesses;
		loc.getEventsStatus().bb_created_jobs = opennedBusinessesJobs;
		uc.setCount4(uc.getCount4() + opennedBusinesses);
		logger.info("     Opened businesses: planned = " + num_of_new_business + " / real = " + opennedBusinesses);
		logger.debug("-----------------------------");
	}
	
	@Override
	public void init() {
		run = sp.getCurrentTimeMarker();
		currYear = sp.getCurrentYear();	
		closuresProbs = generalDao.loadFieldsIntoAMap(closuresProbsIdField, "", public_schema, closuresProbsTable, prepareClosureColsList());
	}
	
	private double getCurrYearClosureProbBySector(Integer sector){
		return (Double)closuresProbs.get(sector).get(closure_prob_column_str);
	}
	
	private List<String> prepareClosureColsList(){
		List<String> closureColNames = new ArrayList<>();
		closure_prob_column_str = closure_prob_prefix + currYear;
		closureColNames.add(closuresProbsIdField);
		closureColNames.add(closuresProbsNameField);
		closureColNames.add(closure_prob_column_str);
		return closureColNames;
	}	
	
	private void closeBusiness(Business bus) {
		bus.setDclosing(dm.assignDateOnYear(sp.getCurrentYear()));
		bus.setNr_of_jobs(0);
		bus.setRun(run);
		for (Person p : bus.getPersons()){
			p.setBusinessId(null);
			p.setPosition_in_bus(null);
			p.setFinance_1(null);
			p.setEmployed_since(null);
			p.setRun(run);
		}
		bus.getPersons().clear();
	}

	private Business createBusiness(Location loc, Integer sector, int size){
		Business business = new Business(true);
		business.setLocationId(loc.getId());
		business.setDfoundation( new DateTime(currYear, rg.nextInt(12)+1, rg.nextInt(28)+1, 0, 0).toDate() );
		business.setType_1(sector);
		business.setType_2(1+rg.nextInt(3));
		business.setNr_of_jobs(size);
		business.setRun(run);
		return business;
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
