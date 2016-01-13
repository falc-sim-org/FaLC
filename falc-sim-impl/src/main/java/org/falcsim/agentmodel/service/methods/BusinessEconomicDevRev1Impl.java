package org.falcsim.agentmodel.service.methods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.service.domain.FirmographyParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("BusinessEconomicDevRev1")
public class BusinessEconomicDevRev1Impl implements BusinessEconomicDevRev1 {

	@Autowired
	private FirmographyParameters fps;
	@Autowired
	private ServiceParameters sp;
	private Integer currYear;
	
	private Double growth_businesses_perc;
	private Map<Integer, Map<Integer,Integer>> bussdevamap;
	
	@Override
	public Double getGrowth_businesses_perc() {
		return growth_businesses_perc;
	}
	
	@Override
	public void calculateEconomicVariables(List<Location> locs){
		bussdevamap = new HashMap<Integer, Map<Integer,Integer>>();
		int total_productive_persons = 0;
		int previous_total_productive_persons = 0;
		for (Location loc : locs){
			List<Household> hs = loc.getHouseholds();
			for (Household h : hs){
				List<Person> ps = h.getPersons();
				for (Person p: ps ){
					DateTime dBirth = new DateTime(p.getDbirth());
					int age = currYear - dBirth.getYear();
					int prev_age = (currYear-1) - dBirth.getYear();
					if(p.getDdeath() == null){
						if(age >= fps.getMinimumJobAge() && age <= fps.getMaximumJobAge()){
							total_productive_persons++;
						}
						if(prev_age >= fps.getMinimumJobAge() && prev_age <= fps.getMaximumJobAge()) previous_total_productive_persons++;
					}
					else{
						DateTime dDdeath= new DateTime(p.getDdeath());
						if(currYear-1 - dDdeath.getYear() < 0 ){	
							if(prev_age >= fps.getMinimumJobAge() && prev_age <= fps.getMaximumJobAge()) previous_total_productive_persons++;	
						}
					}
				}
			}
		}
		if(previous_total_productive_persons == 0){
			growth_businesses_perc = 1.00 + fps.getGrowthPercentage().doubleValue()/100.00;
		}
		else{
			double tmp = fps.getGrowthPercentage().doubleValue()/100.00;
			double tmp2 = (double)total_productive_persons / (double)previous_total_productive_persons;
			growth_businesses_perc = tmp + tmp2;
		}
		if(growth_businesses_perc < 1.01) growth_businesses_perc = 1.00 + fps.getGrowthPercentage().doubleValue()/100.00;
		
		logger.info("     Previous 18-64    : " + previous_total_productive_persons);
		logger.info("     Persons  18-64    : " + total_productive_persons);		
		logger.info("     Growth %         : " + growth_businesses_perc);
		
		for (Location loc : locs){
			Map<Integer,Integer> loc_bussdevamap = new HashMap<Integer,Integer>();
			bussdevamap.put(loc.getId(), loc_bussdevamap);
			for (Business b : loc.getBusinesses()){
				int btype = b.getType_1();
				int workplaces = b.getNr_of_jobs();
				
				if(loc_bussdevamap.containsKey(btype)){
					workplaces += loc_bussdevamap.get(btype);
				}
				loc_bussdevamap.put(btype, workplaces);
			}
			for(int btype : loc_bussdevamap.keySet()){
				int future_workplaces = (int)Math.ceil(loc_bussdevamap.get(btype) * growth_businesses_perc);
				loc_bussdevamap.put(btype, future_workplaces);
			}
		}		
	}

	@Override
	public int getNumOfNewBusinesses(Location loc, int sector){
		int curr_workplaces = 0;
		for (Business b : loc.getBusinesses()){
			if(b.getDclosing() == null){
				if(b.getType_1() == sector ) curr_workplaces += b.getNr_of_jobs();
			}
		}
		int future_workplaces = 0;
		if(bussdevamap.get(loc.getId()).containsKey(sector) ){
			future_workplaces = bussdevamap.get(loc.getId()).get(sector);
		}
		else{
			future_workplaces = curr_workplaces;
		}
		int new_businesses = (int)Math.round( ((double)future_workplaces - (double)curr_workplaces) / (double)num_of_workplaces_per_company);
		if(new_businesses < 0) new_businesses = 0;
		return new_businesses;
	}
	
	@Override
	public void init() {
		currYear = sp.getCurrentYear();
	}

}
