package org.falcsim.agentmodel.service.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.service.domain.FirmographyParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.util.UtilCounter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BusinessQuittingEmpImpl implements BusinessQuittingEmp {
	
	@Autowired
	private FirmographyParameters fps;
	@Autowired
	private RandDevice randDevice;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private UtilCounter uc;
	@Autowired
	private GeneralDao generalDao;
	
	private static final String  quittingEmpProbsTable = "firms_employement_durations";
	private static final String  quittingEmpProbsIdField = "\"Duration\"";
	private static final String  quittingEmpProbsIdRow = "P";
	private static final String  public_schema = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDefaultSchema();
	
	private int run;
	private Integer currYear;
	
	private Map<String, Map<String, ?>> quittingEmpProbs;
	

	@Override
	public void quitEmployees(Location loc) {
		List<Business> businesses = loc.getBusinesses();
		
		//employees quit company
		int quittedJobs = 0;
		for (Business bus : businesses){

			List<Person> quitted = new ArrayList<Person>();
			for (Person p : bus.getPersons()){
				boolean personQuitJob = false;
				if(p.getDdeath() != null){
					personQuitJob = true;
				}
				else{
					//calculate person age
					DateTime dBirth = new DateTime(p.getDbirth());
					int age = currYear - dBirth.getYear();
					
					if(p.getPosition_in_bus() == null){
						p.setPosition_in_bus(RCConstants.EMPLOYEE);
					}
					if(p.getPosition_in_bus() != RCConstants.CEO){
						Integer jobStartYear = p.getEmployed_since();
						if(jobStartYear != null){
							Integer jobDuration = currYear > jobStartYear ? currYear - jobStartYear : 0;
							//retirement
							if(age > fps.getMaximumJobAge()){
								personQuitJob = true;
							}
							//because of probability interval...
							if(jobDuration < 1) jobDuration = 1;
							if(jobDuration > fps.getMaximumJobDuration()) jobDuration = fps.getMaximumJobDuration();
							Double quitJob = getQuittingEmpProb(jobDuration);
							if (randDevice.assignRandomWeightedBoolean(quitJob, p, sp.getCurrentYear(), sp.getCurrentReferenceRun() )){
								personQuitJob = true;
							}
	
						}
					}
					else{
						if(p.getPosition_in_bus() == RCConstants.CEO){
							//CEO retirement, maybe we can make rule different for small and big companies
							if(age > fps.getMaximumCeoAge()){
								personQuitJob = true;
							}
						}
					}
				}
				if(personQuitJob){
					quitBusiness(bus, p);
					quittedJobs++;
					quitted.add(p);
				}
			}
			//bus.setRun(run);
			if(quitted.size() > 0){
				bus.getPersons().removeAll(quitted);
				for (Person p : bus.getPersons()){
					if(p.getDdeath() != null){
						boolean personQuitJob = true;
					}
				}
			}
		}
		loc.getEventsStatus().wrk_quited = quittedJobs;
		uc.setCount6(uc.getCount6() + quittedJobs);
		logger.info("     Quitted Jobs: " + quittedJobs);
	}

	@Override
	public void init() {
		run = sp.getCurrentTimeMarker();
		currYear = sp.getCurrentYear();	
		quittingEmpProbs = generalDao.loadFieldsIntoAMapWithStringId(quittingEmpProbsIdField, "", public_schema, quittingEmpProbsTable, prepareQuittingEmpColsList());
	}

	private double getQuittingEmpProb(Integer jobDuration){
		String column_str = "\"" + String.valueOf(jobDuration) + "\"";
		return (Double)quittingEmpProbs.get(quittingEmpProbsIdRow).get(column_str);
	}
	
	private List<String> prepareQuittingEmpColsList(){
		List<String> quittingEmpColNames = new ArrayList<>();
		quittingEmpColNames.add(quittingEmpProbsIdField);
		for(int i = 1; i <= fps.getMaximumJobDuration(); i++){
			quittingEmpColNames.add("\"" + String.valueOf(i) + "\"");
		}
		return quittingEmpColNames;
	}
	
	private void quitBusiness(Business bus, Person p){
		p.setBusinessId(null);
		p.setPosition_in_bus(null);
		p.setEmployed_since(null);
		p.setFinance_1(null);
		p.setRun(run);
	}
	
	
}
