package org.falcsim.agentmodel.utility.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.dao.jdbc.DynamicEntityTableMapper;
import org.falcsim.agentmodel.domain.GeneralizedSimpleLocationDistance;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class UtilityParameters_2A extends UtilityParameters {
	
	public static Logger logger =  Logger.getLogger(UtilityParameters_2A.class);
	private static final String FILTER_COL_A = "from";
	private static final String FILTER_COL_B = "to";
	
	private int distTableLoadedYear = 0;
	private String distTableLoadedName = "";
	
	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private UniverseServiceUtil universeService;
	@Autowired
	private ServiceParameters sp;	
	@Autowired
	private DynamicEntityTableMapper detm;
	
	@Override
	public void init(List<Location> locs) {
		logger.info("   - init utility functions");
		initUtilMap();
		logger.info("   - cache distances");
		initDistanceMap(locs);
		logger.info("   - done");
	}

	@Override
	public void initDistanceMap(List<Location> locs){
		logger.info("   - cache distances - starts");
		int localDistTableLoadedYear = sp.getCurrentYear();
		ClassDescriptor cd = ClassDescriptor.getClassDescriptor(GeneralizedSimpleLocationDistance.class);
		String localDistTableLoadedName = cd.getTableName();

		if(distTableLoadedYear == 0 || "".equals(distTableLoadedName) || (localDistTableLoadedYear != distTableLoadedYear && !distTableLoadedName.equals(localDistTableLoadedName))){
			//load
			distTableLoadedYear = localDistTableLoadedYear;
			distTableLoadedName = localDistTableLoadedName;
			
			logger.info("   	- cache distances - loading CSV");
			
			newDistanceMap = new HashMap<Integer, Map<Integer,Double>>();
			newDistanceTimeMap = new HashMap<Integer, Map<Integer,Double>>();
			String filter = "";
			if(locs.size() > 0){
				String locationsIds = StringUtil.packageLocsString(locs);
				filter = FILTER_COL_A + " in " + locationsIds + " AND " + FILTER_COL_B + " in " + locationsIds;
			}
			
			List<GeneralizedSimpleLocationDistance> distances =  generalDao.readAll(GeneralizedSimpleLocationDistance.class, filter);
			
			for(GeneralizedSimpleLocationDistance simpleDist : distances){
				//check distances vs. locations, OBSOLETE
				if(universeService.selectLocationById(simpleDist.getLocationA()) != null && 
						universeService.selectLocationById(simpleDist.getLocationB()) != null){
					Map<Integer,Double> locmap = null;
					Map<Integer,Double> locmapTime = null;
					if(!newDistanceMap.containsKey(simpleDist.getLocationA())){
						locmap = new HashMap<Integer,Double>();
						locmapTime = new HashMap<Integer,Double>();
						newDistanceMap.put(simpleDist.getLocationA(), locmap);
						newDistanceTimeMap.put(simpleDist.getLocationA(), locmapTime);
					}
					else{
						locmap = newDistanceMap.get(simpleDist.getLocationA());
						locmapTime = newDistanceTimeMap.get(simpleDist.getLocationA());
					}
					locmap.put(simpleDist.getLocationB(), simpleDist.getDistance());
					locmapTime.put(simpleDist.getLocationB(), simpleDist.getTime());
				}
			}
			distances = null;			
		}
		else{
			logger.info("   	- cache distances - CSV is allready cached in memory");
		}
	}

}