package org.falcsim.agentmodel.service.methods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EducationChangeImpl implements EducationChange {
	@Autowired
	private RandDevice randDevice;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private GeneralDao generalDao;
	
	private static final String  educationProbsTable = "education_level_age_rates";
	private static final String  educationProbsIdField = "edu_lvl_id";
	private static final String  age_prob_prefix = "age";
	
	private Integer currYear;
	
	private Map<Integer, Map<String, ?>> educationProbs;
	
	@Override
	public void process(Location loc) {
		List<Household> households = loc.getHouseholds();

		int educatedPersons = 0;
		for (Household hh : households){
			List<Person> persons = hh.getPersons();
			for (Person person : persons){
				if(person.getDdeath() == null){
					for(int eduLevel = 1; eduLevel <= 9; eduLevel++){
						Double probGradue = getPersonEducationLevelProb(person, eduLevel);
						if (randDevice.assignRandomWeightedBoolean(probGradue, person, sp.getCurrentYear(), sp.getCurrentReferenceRun() )){
							//educatePerson(person, eduLevel);
							educatedPersons++;
						}
					}
				}
			}
		}
		logger.info("    Educated persons: " + educatedPersons);
		logger.info("-----------------------------");
	}

	private double getPersonEducationLevelProb(Person p, Integer eduLevel){
		double retProd = 0.;
		
		if(eduLevel < p.getEducation()) return retProd; 		   //we do not want to decrease education
		if(eduLevel == 4 && p.getEducation() != 3) return retProd; //university after maturity
		
		Map<String, ?> eduLevelMap = educationProbs.get(eduLevel);
		DateTime dBirth = new DateTime(p.getDbirth());
		int age = currYear - dBirth.getYear();

		String probKey = "";
		for(String key : eduLevelMap.keySet()){
			String[] strint = key.replace(age_prob_prefix, "").split("_");
			probKey = key;
			if(age < Integer.parseInt(strint[0]) ) break;		
		}
		if(!"".equals(probKey)){
			Object obj = eduLevelMap.get(probKey);	
			retProd = ((BigDecimal)obj).doubleValue();
		}
		return retProd;
	}
		
	@Override
	public void init() {
		currYear = sp.getCurrentYear();	
		educationProbs = generalDao.loadFieldsIntoAMap(educationProbsIdField, "", educationProbsTable, prepareEducationColsList());
	}

	private List<String> prepareEducationColsList(){
		List<String> closureColNames = new ArrayList<>();
		closureColNames.add(educationProbsIdField);
		closureColNames.add(age_prob_prefix + "00_14");
		closureColNames.add(age_prob_prefix + "15_17");
		closureColNames.add(age_prob_prefix + "18_19");
		closureColNames.add(age_prob_prefix + "20_24");
		closureColNames.add(age_prob_prefix + "25_29");
		closureColNames.add(age_prob_prefix + "30_39");
		closureColNames.add(age_prob_prefix + "40_54");
		closureColNames.add(age_prob_prefix + "55_64");
		closureColNames.add(age_prob_prefix + "65_xx");
		return closureColNames;
	}
	
}
