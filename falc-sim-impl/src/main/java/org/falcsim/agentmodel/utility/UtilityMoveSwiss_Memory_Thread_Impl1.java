package org.falcsim.agentmodel.utility;

import java.util.List;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.util.threading.ThreadManager;
import org.falcsim.agentmodel.util.threading.ThreadManagerImpl1;
import org.falcsim.agentmodel.util.threading.ThreadManager.ProcessCallback;
import org.springframework.beans.factory.annotation.Autowired;

public class UtilityMoveSwiss_Memory_Thread_Impl1 implements UtilityMove {

	@Autowired
	private ThreadManager thman;
	@Autowired
	private UniverseServiceUtil universeService;
	@Autowired
	private RandDevice randDevice;
	
	@Override
	public void moveHB(Location loc, List<Location> locs)
			throws RCCustomException {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveH(Location loc, List<Location> locs)
			throws RCCustomException {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveB(Location loc, List<Location> locs)
			throws RCCustomException {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveHTAllLocations(List<Location> locs)
			throws RCCustomException {
		
		int count1 = universeService.selectAllHouseholds().size();
		int count2 = 0;
		for(Location loc : locs){
			count2 += loc.getHouseholds().size();
		}
		if(count1 != count2){
			
		}
		
		myclass tmp = new myclass();
		thman.run(4, universeService.selectAllHouseholds(), Household.class, tmp, this);
	}

	@Override
	public void moveBTAllLocations(List<Location> locs)
			throws RCCustomException {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveSelectedHouseholdsIntoSelectedLocationsThreads(
			List<Household> hhs, List<Location> locs) throws RCCustomException {
		// TODO Auto-generated method stub
		
	}

}

class myclass implements ProcessCallback{
	private static Logger logger = Logger.getLogger(ProcessCallback.class);
	
	@Override
    public <T> void execute(int thCount, List<T> list, Class<?> clazz, Object...beans){
    	logger.info("Inside execute");
    }
	
	@Override
    public void finallize(){
		logger.info("Inside finalyze");
    }
}

