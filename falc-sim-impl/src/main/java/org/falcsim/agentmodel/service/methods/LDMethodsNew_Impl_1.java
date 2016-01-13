package org.falcsim.agentmodel.service.methods;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.RandomGenerator;
import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.demography.methods.DemographyMethods;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.service.domain.DemographyParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.util.UtilCounter;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.falcsim.exit.ExitCodes;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LDMethodsNew_Impl_1 implements LDMethods{
	
	@Autowired
	private RandDevice randDevice;
	@Autowired
	private RCRandomGenerator randomGenerator;
	@Autowired
	private UtilCounter uc;
	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private DemographyMethods demogMs;
	@Autowired
	private UniverseServiceUtil universeService;
	@Autowired
	private RandomGenerator rg;	
	@Autowired
	private DemographyParameters dp;
	
	private Double PROB_DIE_OLD;
	private Integer MAX_AGE;
	private int  lowerBound;
	private int  upperBound;
	private String  birthProbsTable = "demography_fertility";
	private String  deathProbsTable = "demography_mortality";
	
	
	private static final String PERC="%";
	private static final Integer MALE=RCConstants.MALE;
	private static final Integer FEMALE=RCConstants.FEMALE;
	private Integer currYear;
	private Integer run;
	private Map<Integer, Map<String, ?>> deathProbs;
	private Map<Integer, Map<String, ?>> birthProbs;
	private static final int  birthAgeStep = 5;
	private static final String  birthProbsIdField = "from_age";
	private static final String  birthProbPrefix = "f";
	private static final String  deathProbsIdField = "age";
	private static final String  male_death_probPrefix = "s_m";
	private static final String  female_death_probPrefix = "s_f";
	private String male_death_probStr;
	private String female_death_probStr;
	private String birth_probStr;
	private static final int birthSampleSizeToPercentRedFactor = 1000;// 1000 women on a 5 years age interval 
	private static final Integer Allocate_Orphan_Tries=20;
	private static final String public_schema = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDefaultSchema();
	
	public void init(){
		run = sp.getCurrentTimeMarker();
		currYear = sp.getCurrentYear();			
		
		PROB_DIE_OLD = dp.getProbMaxAgeDie();
		MAX_AGE = dp.getMaxAge();
		lowerBound = dp.getMotherMinAge();
		upperBound = dp.getMotherMaxAge();
		
		deathProbs = generalDao.loadFieldsIntoAMap(deathProbsIdField, "", public_schema, deathProbsTable, prepareDeathColsList());
		birthProbs = generalDao.loadFieldsIntoAMap(birthProbsIdField, "", public_schema, birthProbsTable  , prepareBirthColsList());
	}


	@Override
	public void beBornAndDie(Location loc) {
		
		int locBirths=0;
		int locDeaths=0;
		int deadHouseholds=0;
		//dAMethods.reattach(loc);
		boolean dead = false;
		List<Person> orphans= new ArrayList<>();
		List<Household> hs = loc.getHouseholds();
		int houssSize = hs.size();
		for (Household h : hs){
			List<Person> members= h.getPersons();
			int hsize = members.size();
			Iterator<Person> it = members.iterator();
			Boolean add = false;
			List<Person> newComers = new ArrayList<Person>();
			Double probDie;
			while (it.hasNext()){
				dead = false;
				Person p = it.next();
				DateTime dBirth = new DateTime(p.getDbirth());
				int ageN = currYear - dBirth.getYear();
				try{
					probDie = getProbDeathForPerson(p.getSex(), ageN);
					if (probDie==null && ageN > MAX_AGE){
						probDie = PROB_DIE_OLD;
					} else if (probDie==null){
						throw new NullPointerException();
					}
					if (!die(p, probDie )){
						if(givesBirth(p, ageN)){
							add=true;
							newComers.add(newBorn(p, h));
							locBirths++;
						}
					}else {
						dead = true;
						locDeaths++;
						if (hsize==1){
							h.setRun(run);
							h.setDclosing(p.getDdeath());
							deadHouseholds++;
						} else {
							boolean relocateKids = true;
							it = members.iterator();
							while (it.hasNext()){
								Person k = it.next();
								if (demogMs.getAgeOfPerson(k, currYear) >= 18 && k.getDdeath() == null){ //original was >19, but I found not dead partner at 18 yrs
									relocateKids = false;
									if(k.getPartnership_since() != null){
										k.setPartnership_since(null);
										//if(k.getPosition_in_hh() != null) k.setPosition_in_hh(RCConstants.HP_P);
										//else k.setPosition_in_hh(null);
										
										break;
									}
								}
							}
							if (relocateKids == true){
								if(add){
									logger.error("Orphans and birth in one HH");
								}
								h.setDclosing(p.getDdeath());
								deadHouseholds++;
								it = members.iterator();
								while (it.hasNext()){
									Person k = it.next();
									if (k.getDdeath()!=null){
										orphans.add(k);
										it.remove();
									}
								}
							}
							if (dead){
								break;
							}
						}
					}
				} catch (Exception e){
					logger.error(p.getId()+" 's death caused an error. Date of birth:"+p.getDbirth()+ " "+e.getMessage());
					logger.error(e.getMessage(), e);
					System.exit(ExitCodes.MODELS_LD_METHODS_ERROR.getValue());
				}
			}
			if (add){
				members.addAll(newComers);
			}
		}
		
		if (orphans.size()>0){
			allocateOrphans (hs, orphans, houssSize, loc);
		}
		
		uc.addCount1(locBirths);
		uc.addCount2(locDeaths);
		loc.getEventsStatus().res_birth = locBirths;
		loc.getEventsStatus().res_death = locDeaths;
		loc.getEventsStatus().hh_closed = deadHouseholds;
		logger.info(locBirths+" births and "+locDeaths+" death at location "+loc.getName()+". "+deadHouseholds +" households diappeared. Population balance is "+(locBirths-locDeaths));		
	} 

	private Boolean givesBirth(Person p, Integer age){

		if (p.getSex().equals(MALE))
			return false;

		if (randDevice.assignRandomWeightedBoolean(getBirthProbFromAge(age, lowerBound, upperBound, birthAgeStep, birthProbs), p, currYear, sp.getCurrentReferenceRun())){
			return true;
		}	

		return false;
	}

	private Boolean die(Person p, Double probDie){

		//if (randDevice.assignRandomWeightedBoolean(probDie/100, p.getId(), currYear, sp.getCurrentReferenceRun(), RandomGeneratorModelID.LifeAndDeath)){
		if (randDevice.assignRandomWeightedBoolean(probDie, p, currYear, sp.getCurrentReferenceRun() )){	// /100
			p.setDdeath(demogMs.assignDateOfDeath(currYear));
			p.setRun(run);
			p.setPartnership_since(null);
			//p.setPosition_in_hh(null);
			//logger.info("RIP "+ p.getId()+ ", who died in "+(theYear+ run)+ " He was born in "+(new DateTime(p.getDbirth()).getYear()));
			return true;
		}

		return false;
	}

	private Person newBorn(Person mum, Household h){
		Person p = new Person(true);
		Integer dec_number = mum.getDecissionNumber() != null ? mum.getDecissionNumber() : 
						mum.getMotherId() == null ? null : mum.getMotherId();
		p.setDecissionNumber(dec_number);
		p.setMotherId(mum.getId());
		if(mum.getPosition_in_hh() != null && mum.getPosition_in_hh() == RCConstants.H_Partner){
			for(Person p_h : h.getPersons()){
				if(p_h.getPosition_in_hh() != null && p_h.getPosition_in_hh() == RCConstants.H_Partner){
					if(p_h.getSex() != null && p_h.getSex() == RCConstants.MALE){
						p.setFatherId(p_h.getId());
						break;
					}
				}
			}
		}
		
		Date dBirth = demogMs.assignDateOfBirth(currYear, 0, 0);
		p.setDbirth(dBirth);
		if (randDevice.assignRandomWeightedBoolean(0.5, p, currYear, sp.getCurrentReferenceRun()))
			p.setSex(MALE);
		else
			p.setSex(FEMALE);
		p.setHouseholdId(mum.getHouseholdId());
		p.setPosition_in_hh(RCConstants.KID);
		p.setRun(run);
		//logger.info("Happy news! "+mum.getId()+" had a baby on  "+dBirth.toString());
		return p;

	}
	
	private double getProbDeathForPerson(Integer sex, int age){
		
		if (sex==MALE){
			//return Double.parseDouble( ((String) deathProbs.get(age).get(male_death_probStr)).replaceFirst(PERC, ""));
			return (Double)deathProbs.get(age).get(male_death_probStr);
		} else {
			return (Double)deathProbs.get(age).get(female_death_probStr);
		}
	}
	
	private List<String> prepareDeathColsList(){
		List<String> deathcColNames = new ArrayList<>();
		male_death_probStr =male_death_probPrefix+currYear;
		female_death_probStr = female_death_probPrefix+currYear;
		deathcColNames.add(deathProbsIdField);
		deathcColNames.add(male_death_probStr);
		deathcColNames.add(female_death_probStr);
		
		return deathcColNames;
	}
	
	private List<String> prepareBirthColsList(){
		List<String> birthColNames = new ArrayList<>();
		birth_probStr = birthProbPrefix+currYear;
		birthColNames.add(birthProbsIdField);
		birthColNames.add(birth_probStr);
		
		return birthColNames;
	}
	
	private double getBirthProbFromAge(int age , int lowerBound, int upperBound, int birthAgeStep, Map<Integer, Map<String, ?>> birthProbs){
		int index;
		if(age<lowerBound || age>upperBound){
			return 0.;
		} else {
			index = (age/birthAgeStep)*birthAgeStep;// maps e.g. 29 into 25
			return ((Double) birthProbs.get(index).get(birth_probStr))/birthSampleSizeToPercentRedFactor;
		}
			
	}
	
	private boolean allocateOrphans (List<Household> hs, List<Person> orphans, int houssSize, Location loc){
		
		Household newHome;
		int nrOrphans = orphans.size();
		int orphansCounter = 0;
		for (Person orphan : orphans){
			int tries = Allocate_Orphan_Tries;
			int count = 0;
			while(count<tries){
				newHome = hs.get(rg.nextInt(houssSize) );
				//newHome = hs.get(randomGenerator.nextInt(houssSize, orphan + 100 * count, currYear, sp.getCurrentReferenceRun()));
				count++;
				if(newHome.getDclosing() == null){
					//remove orphan from old household
					int oldHHid = orphan.getHouseholdId();
					Household oldHH = universeService.selectHouseholdById(oldHHid);
					if(oldHH != null ){
						oldHH.getPersons().remove(orphan);
					}
					//and put it into new one
					orphan.setHouseholdId(newHome.getId());
					newHome.getPersons().add(orphan);
					orphansCounter++;
					break;
				}				
			}
			
		}
		if (orphansCounter!=nrOrphans){
			logger.info("Not all "+orphans.size()+" orphans were given a new home.");
			return false;
		}
		logger.info(orphans.size()+" were given a new home.");
		return true;
	}

}
