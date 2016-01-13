package org.falcsim.agentmodel.service.methods;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.common.load.PopulationDataLoader;
import org.falcsim.agentmodel.demography.methods.DemographyMethods;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HHSeparationMethodsImpl_1 implements HHSeparationMethods{

	@Autowired
	private RandDevice rd;
	@Autowired
	private DemographyMethods demogMs;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private PopulationDataLoader pdl;
	
	private Map<Integer, Double> leaveHomeMap;
	private Map<Integer, Double> divorceMap;
	private static final String move_out_age_T = "households_leaving_rates";
	private static final String divorce_by_age_T = "households_divorce_years";
	private static final String leaveHomeProbField = "p_rel_parent";
	private static final String divorceProbField = "p_div_year";
	private static final String ageIdField = "age";
	private static final String yearsIdField = "years";

	private static int currYear;
	private static int run;

	@Override
	public void peoplePart(Location loc) {
		
		run = sp.getCurrentTimeMarker();
		currYear = sp.getCurrentYear();	
		loc.getHouseholds().addAll(kidsMoveOut(loc , currYear, leaveHomeMap ));
		loc.getHouseholds().addAll(peopleDivorce(loc , currYear, divorceMap ));	
	}

	private List<Household> kidsMoveOut(Location loc, int currYear, Map<Integer, Double> leaveHomeMap){

		List<Household> newHouss = new ArrayList<>();
		Person p;
		for(Household h :loc.getHouseholds()){
			Iterator<Person> it = h.getPersons().iterator();
			while(it.hasNext()){
				p = it.next();
				if (p.getPosition_in_hh() != null){
					if( p.getPosition_in_hh().equals(RCConstants.KID)){
						int age = demogMs.getAgeOfPerson(p, currYear);
						if(rd.assignRandomWeightedBoolean(leaveHomeMap.get(age), p, sp.getCurrentYear(), sp.getCurrentReferenceRun() )){
							newHouss.add(createNewMoveOuthousehold(p, loc.getId(), currYear));
							it.remove();
						}
					}
				}
			}
		}
		loc.getEventsStatus().hh_kidsLeave = newHouss.size();
		logger.info(newHouss.size()+ " kids moving on their own");
		return newHouss;
	}

	private List<Household> peopleDivorce(Location loc, int currYear, Map<Integer, Double> divorceMap){
		List<Household> newHouss = new ArrayList<>();
		for(Household h : loc.getHouseholds()){
			for(Person p : h.getPersons()){
				if (p.getPosition_in_hh() != null && p.getPartnership_since() != null){
					int pos = p.getPosition_in_hh();
					if(pos == RCConstants.H_Partner){
						if (timeToDivorce(p, p.getPartnership_since(),  currYear, divorceMap )){
							split(h, currYear, run, newHouss);
							h.setType_3(10);
							break;
						}
					}

				}
			}
		}
		loc.getEventsStatus().hh_divorced = newHouss.size();
		logger.info(newHouss.size()+ " couples divorced");
		return newHouss;
	}

	@Override
	public void init() {
		currYear=sp.getCurrentYear();
		leaveHomeMap = pdl.loadIntegerKeyCoeffsMap(ageIdField, RCConstants.SCHEMA_PUBLIC, move_out_age_T, leaveHomeProbField);	
		divorceMap = pdl.loadIntegerKeyCoeffsMap(yearsIdField, RCConstants.SCHEMA_PUBLIC, divorce_by_age_T, divorceProbField);
	}

	private void split(Household h,  int currYear, int run, List<Household> newHouss){

		int hh_size = h.getPersons().size();
		Integer locId= h.getLocationId();
		Date fdate = demogMs.assignDateOnYear(currYear);
		Household h_f = new Household(true);
		Household h_m = new Household(true);
		h_f.setLocationId(locId);
		h_f.setDfoundation(fdate);
		h_f.setRun(run);
		h_f.setType_3(12);
		h_m.setLocationId(locId);
		h_m.setDfoundation(fdate);
		h_m.setRun(run);
		h_m.setType_3(13);
		Iterator<Person> it = h.getPersons().iterator();
		List<Person> nullTypePersons = new ArrayList<Person>();
		while (it.hasNext()){
			Person p= it.next();
			if (p.getPosition_in_hh()== null){
				logger.warn("Person "+p.getId()+" is in a type "+h.getType_3()+" household , but has null position_in_hh");
				nullTypePersons.add(p);
			//} else if (p.getPosition_in_hh().equals(M_P)){
			} else if (p.getPosition_in_hh().equals(RCConstants.H_Partner) && p.getSex() == RCConstants.MALE){
				p.setPosition_in_hh(null);
				p.setPartnership_since(null);
				p.setHouseholdId(h_m.getId());
				h_m.getPersons().add(p);
			//} else if (p.getPosition_in_hh().equals(F_P)){
			} else if (p.getPosition_in_hh().equals(RCConstants.H_Partner) && p.getSex() == RCConstants.FEMALE){
				p.setPartnership_since(null);
				if(hh_size > 2) p.setPosition_in_hh(RCConstants.H_Partner);
				else p.setPosition_in_hh(null);
				p.setHouseholdId(h_f.getId());
				h_f.getPersons().add(p);
			} else {
				p.setHouseholdId(h_f.getId());
				h_f.getPersons().add(p);
			}
			it.remove();
		}
		
		h.setRun(run);
		h.setDclosing(fdate);
		h.setType_3(11);
		h.getPersons().clear();
		if(nullTypePersons.size() > 0 ){
			h.getPersons().addAll(nullTypePersons);	
		}

		newHouss.add(h_m);
		newHouss.add(h_f);	

	}

	private Household createNewMoveOuthousehold(Person p, Integer locId, int currYear){
		Household h = new Household(true);
		h.setLocationId(locId);
		h.setDfoundation(demogMs.assignDateOnYear(currYear));
		p.setHouseholdId(h.getId());
		p.setPosition_in_hh(null);
		h.getPersons().add(p);
		h.setRun(sp.getCurrentTimeMarker());
		return h;		
	}

	private boolean timeToDivorce(Person p, int togetherSince, int currYear, Map<Integer, Double> divorceMap){

		Double prob = divorceMap.get(currYear-togetherSince);
		if (prob == null){
			return false;
		}
		if (rd.assignRandomWeightedBoolean(divorceMap.get(currYear-togetherSince), p, sp.getCurrentYear(), sp.getCurrentReferenceRun() )){
			return true;
		} 

		return false;

	}

}
