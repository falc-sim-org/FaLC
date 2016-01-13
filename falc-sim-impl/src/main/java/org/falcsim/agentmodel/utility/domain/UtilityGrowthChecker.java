package org.falcsim.agentmodel.utility.domain;

import java.util.List;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;

public abstract class UtilityGrowthChecker {
	public static Logger logger =  Logger.getLogger(UtilitySublocations.class);
	
	public abstract double getMax_location_growth_households();
	public abstract void setMax_location_growth_households(double max_location_growth);
	public abstract double getMax_location_growth_businesses();
	public abstract void setMax_location_growth_businesses(double max_location_growth);
	
	public abstract void init(List<Location> locs);
	public abstract void updateLocationStatus(List<Location> locs);
	public abstract void reset();
	
	public abstract boolean checkLocationHouseholds(Location loc);
	public abstract boolean checkLocationBusinesses(Location loc);	
	
	public abstract void updateLocationHouseholdsCurrentStatus(int fromLoc, int toLoc, Household h);
	public abstract void updateLocationBusinessesCurrentStatus(int fromLoc, int toLoc, Business b);
}
