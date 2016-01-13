package org.falcsim.agentmodel.utility.landusage;

import java.util.List;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.service.methods.util.RunIndicators;

public abstract class LandUsage {
	public static Logger logger =  Logger.getLogger(RunIndicators.class);
	
	public abstract double getMaxLandUsageResidents(Location loc);
	public abstract double getCurrentLandUsageResidents(Location loc);
	
	public abstract double getMaxLandUsageWorkers(Location loc);
	public abstract double getCurrentLandUsageWorkers(Location loc);
	
	public abstract double getMaxLandUsage(Location loc);
	public abstract double getCurrentLandUsage(Location loc);
	
	public abstract double getLandRestrictionInterval();
	
	/*
	 * return true if Location is fully used, false if not
	 */
	public abstract boolean checkLandUsageRes(int locid);
	public abstract boolean checkLandUsageWrk(int locid);
	public abstract boolean checkLandUsageAll(int locid);
	
	public abstract void init(List<Location> locs, boolean calculate) throws RCCustomException;
	public abstract void calculate(List<Location> locs);
	public abstract void reset();
	
	
	
	public abstract void updateCurrentLandUsageResidents(int sourceLocId, int destLocId, Household h);
	public abstract void updateCurrentLandUsageWorkers(int sourceLocId, int destLocId, Business b);
	
}