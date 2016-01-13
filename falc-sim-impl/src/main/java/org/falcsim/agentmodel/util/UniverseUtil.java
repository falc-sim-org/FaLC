package org.falcsim.agentmodel.util;

import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Person;

public class UniverseUtil {
	
	public static int getLivePersons(List<Person> list){
		int count = 0;
		for(Person p : list){
			if(p.getDdeath() == null )count++;
		}
		return count;
	}
	
	public static Person getCEO(Business bus){
		List<Person> pers = bus.getPersons();
		if(pers.size() > 0 ){
			for (Person p : pers){
				if(p.getPosition_in_bus() == RCConstants.CEO) return p;
			}
		}
		return null;
	}
	
	public static Person getHouseholdMainEarner(Household h){
		Person mainEarner = null;
		for (Person p : h.getPersons()){
			if (p.getBusinessId() != null ){				
				if(mainEarner == null || p.getFinance_1() > mainEarner.getFinance_1() ){
					mainEarner = p;
				}
			}			
		}
		if(mainEarner == null){
			for (Person p : h.getPersons()){
				if(mainEarner == null || (mainEarner.getDbirth() != null && mainEarner.getDbirth().compareTo(p.getDbirth()) > 0 )){
					mainEarner = p;
				}		
			}
		}
		return mainEarner;
	}
	
}
