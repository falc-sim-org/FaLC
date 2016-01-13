package org.falcsim.agentmodel.demography.methods;

import java.util.Date;
import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;
import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Default implementation of the basic demographic methods
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
@Component
public class DemographyMethodsSimpleImpl implements DemographyMethods {
	
	@Autowired
	private RCRandomGenerator randomGenerator;
	@Autowired
	private RandomGenerator rg;	
	@Autowired
	private RandDevice randDevice;
	@Autowired
	private RunParameters rp;
	
	private final DateTimeZone dtz = DateTimeZone.forID("CET");
	
	@Override
	public Date assignDateOfDeath(Integer theYear){
		
		DateTime dd= new DateTime(theYear, rg.nextInt(12)+1, rg.nextInt(28)+1, 12, 0, dtz);
		return dd.toDate();
	}
	
	@Override
	public  Date assignDateOfBirth(Integer startYear, Integer minAge, Integer maxAge){
		Integer randDiff=0;
		if (minAge < maxAge)
			randDiff =  rg.nextInt(maxAge-minAge);
		Integer age = minAge  +randDiff;
		DateTime dd= new DateTime(startYear-age, rg.nextInt(12)+1, rg.nextInt(28)+1, 12, 0, dtz);
		return dd.toDate();
	}

	@Override
	public Integer getAgeOfPerson(Person p, Integer currentYear) {
		return (currentYear - new DateTime(p.getDbirth()).getYear());
	}
	
	@Override
	public void assignDateOfBirth (Integer startYear, Person p, List<RealIdProbInterval> ripis) throws RCCustomException{
		 Integer age = randDevice.assignWeightedRandomId(ripis);
		 Integer year = startYear-age;
		 DateTime dd= new DateTime(year, rg.nextInt(12)+1, rg.nextInt(28)+1, 12, 0, dtz);
		 p.setDbirth(dd.toDate());
	}
	
	@Override
	public Date assignDateOnYear(Integer year){
		DateTime dd= new DateTime(year, rg.nextInt(12)+1, rg.nextInt(28)+1, 12, 0, dtz);
		return dd.toDate();
	}

	private void setPersonParents(Person person, Person mother, Person father){
		person.setMotherId(mother == null ? null : mother.getId() );
		person.setFatherId(father == null ? null : father.getId() );
	}	
	
	@Override
	public void setPersonParentsInHousehold(Household h){
		Person mother = null; 
		Person father = null;
		for(Person p : h.getPersons()){
			if(p.getPosition_in_hh() != null && p.getPosition_in_hh() == RCConstants.H_Partner){
				if(p.getSex() != null && p.getSex() == RCConstants.FEMALE) mother = p;
				if(p.getSex() != null && p.getSex() == RCConstants.MALE) father = p;
			}
		}
		//assign parents IDs to all persons
		for(Person p : h.getPersons()){
			p.setDecissionNumber(null);
			if(p.getPosition_in_hh() != null && p.getPosition_in_hh() == RCConstants.KID ){		
				setPersonParents(p, mother, father);
			}
		}
	}
	
	@Override
	public void setRelocationTypeForHouseholds(List<Location> locs, Integer year)
	{
		for(Location loc : locs){
			for(Household h : loc.getHouseholds()){
				h.setType_2(getHouseholdTypeForRelocation(h, year));
			}
		}
	}
	
	@Override
	public int getHouseholdTypeForRelocation(Household h, Integer year)
	{
		int type = RCConstants.HH_TYPE_UNKNOWN;	//Unknown
		if(h.getPersons() != null && h.getPersons().size() > 0){
			int age = 0;
			int size = 0;
			int num_p = 0;
			int num_c = 0;
			for(Person p : h.getPersons()){
				if(p.getDdeath() == null){
					size++;
					age += getAgeOfPerson(p, year);
					if(p.getPosition_in_hh() != null && p.getPosition_in_hh() == RCConstants.H_Partner){
						num_p++;
					}
					if(p.getPosition_in_hh() != null && p.getPosition_in_hh() == RCConstants.KID ){	
						num_c++;
					}
				}
			}
			double avg_age = size > 0 ? (double)age / size : 0;
			
			if(h.getPersons().size() == 1) type = RCConstants.HH_TYPE_SINGLE; //Single
			else{
				if(h.getPersons().size() == 2 && /*num_p == 2 &&*/ num_c == 0) type = RCConstants.HH_TYPE_PAIR; //Pair
				else{
					if(num_c > 0 && num_p > 0) type = RCConstants.HH_TYPE_WITH_CHILDREN; //With children
				}				
			}			
			if(avg_age >= 60) type = RCConstants.HH_TYPE_ELDER; //Elder		
		}		
		h.setType_2(type); 	//set type_2
		return type;	
	}
}
