package org.falcsim.agentmodel.sublocations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.app.util.CommunicationClass;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.sublocations.dao.UpdateLocationSubset;
import org.falcsim.agentmodel.sublocations.domain.LocationSubset;
import org.falcsim.agentmodel.sublocations.domain.SublocationsParameters;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("GenerateLocationsSubset")
public class GenerateLocationsSubsetImpl implements GenerateLocationsSubset {
	/**
	 * universe functionality
	 */
	@Autowired
	private UniverseServiceUtil universeService;
	@Autowired
	private UtilityParameters up;
	@Autowired
	private RandDevice randDevice;
	@Autowired
	private SublocationsParameters sublocpar;
	@Autowired
	private UpdateLocationSubset uls;
	
	private static final Integer m2km = 1; //table is in KM
	

	
	@Override
	public void reset() {

	}
	
	@Override
	public void generate() throws Exception {
		generate(true);
	}
	
	private List<Integer> getIntegerListFromString(String filter){
		List<Integer> locFilter = new ArrayList<Integer>();
		String[] locStr = filter.split(",");
		for(String str : locStr){
			if(str != null && str.trim().length() > 0){
				int locid = Integer.parseInt(str.trim());
				locFilter.add(locid);
			}
		}
		return locFilter;
	}
	
	@Override
	public void generate(boolean saveUniverse) throws Exception {
		logger.info("GenerateLocationsSubsetImpl started... ");
		List<Location> locs = universeService.selectAllLocations();
		
		if (!up.isDistanceMapInitialized()){
			logger.info("Loading distances... ");
			up.initDistanceMap(locs);
		}

		List<Integer> locFilterH = getIntegerListFromString( sublocpar.getSubsetFilterH() );
		List<Integer> locFilterB = getIntegerListFromString( sublocpar.getSubsetFilterB() );
		
		Map<Integer, LocationSubset> allLocations = new HashMap<Integer, LocationSubset>();
		CommunicationClass.setMaxProgress(String.valueOf(sublocpar.getGenerate_years()));
		
		for(int i = 0; i <= sublocpar.getGenerate_years(); i++){
			logger.info("Calculate subsets for year: " + String.valueOf(i+2000));
			CommunicationClass.reportProgress();
			
			for(Location loc : locs){
				List<Integer> hhlist = new ArrayList<Integer>();
				List<Integer> bblist = new ArrayList<Integer>();
				
				for(Location destLoc : locs){
					boolean isHHSublocation = false;
					boolean isBBSublocation = false;

					if(loc.getId() == destLoc.getId()){
						isHHSublocation = true;
						isBBSublocation = true;
					}
					
					double distance = up.getDistance(loc.getId(), destLoc.getId())/m2km;
					
					if(distance <= sublocpar.getInnerCircle()){	//innerCircle
						if(!isHHSublocation){
							isHHSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getInnerCircleProb());
						}
						if(!isBBSublocation){
							isBBSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getInnerCircleProb());
						}
					}
					if(distance <= sublocpar.getOuterCircle()){	//outerCircle
						if(!isHHSublocation){
							isHHSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getOuterCircleProb());	
						}
						if(!isBBSublocation){
							isBBSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getOuterCircleProb());	
						}
					}
					if(loc.getMotorway_access() == 1 && destLoc.getMotorway_access() == 1 ){	//motorway access
						if(distance <= sublocpar.getMotorwayAccessH()){
							if(!isHHSublocation){
								isHHSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getMotorwayAccessProbH());	
							}
						}
						if(distance <= sublocpar.getMotorwayAccessB()){
							if(!isBBSublocation){
								isBBSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getMotorwayAccessProbB());	
							}
						}
					}
					if(destLoc.isAgglomeration()){	//agglomeration
						if(!isHHSublocation){
							isHHSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getAglomerationProbH());	
						}
						if(!isBBSublocation){
							isBBSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getAglomerationProbB());	
						}	
					}
					if(destLoc.isLargeCity() || destLoc.getUrban_centre() == 1){  //big city, urban centre check added
						if(!isHHSublocation){
							isHHSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getBigCitiesProbH());	
						}
						if(!isBBSublocation){
							isBBSublocation = randDevice.assignRandomWeightedBoolean(sublocpar.getBigCitiesProbB());
						}	
					}
					
					if(isHHSublocation){
						//apply filter e.g. airports for households
						if( !locFilterH.contains(destLoc.getId()) ){
							hhlist.add(destLoc.getId());
						}
					}
					if(isBBSublocation){
						if( !locFilterB.contains(destLoc.getId()) ){
							bblist.add(destLoc.getId());
						}	
					}
				}
				uls.updateLocationSubset(allLocations, loc.getId(), i, listToString(hhlist), listToString(bblist), loc.getName());
			}
		}
		uls.saveLocationSubset(allLocations);

		logger.info("GenerateLocationsSubsetImpl Done");
	}

	private String listToString(List<Integer> list){
		Collections.sort(list);
		StringBuilder sb = new StringBuilder();
		for(Integer val : list){
			sb.append(String.valueOf(val) + ",");
		}
		if(sb.length() > 0){
			sb.delete(sb.length()-1, sb.length()-1);
		}
		return sb.toString();
	}
}
