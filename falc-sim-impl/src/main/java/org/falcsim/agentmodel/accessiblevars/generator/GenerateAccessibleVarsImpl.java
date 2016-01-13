package org.falcsim.agentmodel.accessiblevars.generator;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.falcsim.agentmodel.accessiblevars.domain.AccessVariableData;
import org.falcsim.agentmodel.accessiblevars.domain.AccessibleVarsParameters;
import org.falcsim.agentmodel.accessiblevars.domain.TravelData;
import org.falcsim.agentmodel.app.UniverseManager;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.GeneralizedLocationDistanceDao;
import org.falcsim.agentmodel.distances.domain.DistanceRecord;
import org.falcsim.agentmodel.domain.GeneralizedLocationDistance;
import org.falcsim.agentmodel.domain.GeneralizedSimpleLocationDistance;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.util.StringUtil;
import org.falcsim.agentmodel.util.xml.JDOMUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Generates accessibility variables for each location
* 	generate() will use standard data from distance table
*
* @author regioConcept AG
* @version 1.0
* @since   0.5 
* 
*/
@Component
public class GenerateAccessibleVarsImpl implements GenerateAccessibleVars {
	
	private static final String FILTER_COL_A = "from";
	private static final String FILTER_COL_B = "to";

	private static final String VARXML_VARIABLE = "variable";
	private static final String VARXML_UNIVERSE = "change_variable";
	private static final String VARXML_FORCECALC = "execute";
	private static final String VARXML_YEAR = "year";
	private static final String VARXML_ID = "id";
	private static final String VARXML_TYPE = "type";
	private static final String VARXML_PARAMETERS = "parameters";
	private static final String VARXML_MAXDISTANCE = "maxdistance";
	private static final String VARXML_BETA = "beta";
	private static final String VARXML_MINVALUE = "minvalue";
	private static final String VARXML_LOG = "uselog";
	private static final String VARXML_TABLE = "table";
	
	/**
	 * accessible vars parameters
	 */
	@Autowired
	private AccessibleVarsParameters avp;
	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private UniverseManager uman;

	/**
	 * universe functionality
	 */
	@Autowired
	private UniverseServiceUtil universeService;
	
	@Autowired
	private GeneralizedLocationDistanceDao glddao;
		
	private Boolean validExecuteYear;
	
	@Override
	public void generate(Integer year, Boolean forceCalculation) throws Exception {	
		Map<Integer, AccessVariableData> confdata = new HashMap<Integer, AccessVariableData>();
		validExecuteYear = false;
		
		logger.info("Load config file...");		
		Set<String> DisttableNames = loadXMLConfig(avp.getXmlpath(), confdata, year);
		
		if(!forceCalculation && !validExecuteYear){			
			//cancel AV calculation
			logger.warn("AV calculation disabled for current year");
			return;
		}
		
		logger.info("Get Universe...");
		List<Location> locations = universeService.selectAllLocations();
		
		logger.info("Load distance tables...");
		Map<String, Map<Integer,Map<Integer,TravelData>>> map = new HashMap<String, Map<Integer,Map<Integer,TravelData>>>();
		for(String str : DisttableNames) {
			initDistanceMap(map, locations, str);
		}
		logger.info("Prepare data...");
		AccessVariableData accessVariableData = new AccessVariableData(avp.getAccessLimit(), avp.getAccessBeta(),
				avp.getAccessTiiIntervals(), avp.isAccessMathLog(), 0.);	//avp.getAccessMinValue()
		confdata.put(0, accessVariableData);
		
		logger.info("Calculate numbers of Residents and Workers...");
		for (Location location : locations) {
			accessVariableData.addOpportunity(location.getId(),	
				(location.getActualResidents() == null ) ? 0 : location.getActualResidents(), 
				(location.getActualWorkers() == null ) ? 0 : location.getActualWorkers() );
		}
		
		for(int avid = 1; avid <= 12; avid++){			
			if(!confdata.containsKey(avid)){
				for (Location location : locations) {
					switch(avid){
						case 1: location.setAv_1(0.); break;
						case 2: location.setAv_2(0.); break;
						case 3: location.setAv_3(0.); break;
						case 4: location.setAv_4(0.); break;
						case 5: location.setAv_5(0.); break;
						case 6: location.setAv_6(0.); break;
						case 7: location.setAv_7(0.); break;
						case 8: location.setAv_8(0.); break;
						case 9: location.setAv_9(0.); break;
						case 10: location.setAv_10(0.); break;
						case 11: location.setAv_11(0.); break;
						case 12: location.setAv_12(0.); break;
					}					
				}
			}
			else{
				logger.info("Calculate AV with ID:" + avid);			
				double accessVarsMinValue = confdata.get(avid).getMinvalue();
				int count = 0;
				int size = locations.size();
				
				for (Location location : locations) {	
															
					count++;
					if (count % 100 == 0) {
						logger.info(count +"  locations:  of "+size+"  processed");
					}
		
					double AVvalue = 0;	
					Integer idLoc = location.getId();			
					
					Map<Integer, TravelData> tmpmap = map.get(confdata.get(avid).getTablename()).get(idLoc);
								
					for(Integer idTo : tmpmap.keySet() ){
						double time = 0;
						TravelData traveldata = tmpmap.get(idTo);
						if(traveldata == null) 
							throw new IllegalArgumentException("Map do not contains record of valid type");
						
						long numPersons = 0;
						if("residents".equals( confdata.get(avid).getType()) ){
							numPersons = accessVariableData.getOpportunity(idTo).numResidents;
						}
						else if("workers".equals( confdata.get(avid).getType()) ){
							numPersons = accessVariableData.getOpportunity(idTo).numEmployees;
						}
						else if("total".equals( confdata.get(avid).getType()) ){
							numPersons = accessVariableData.getOpportunity(idTo).numResidents + accessVariableData.getOpportunity(idTo).numEmployees;
						}
						
						if (traveldata.km < confdata.get(avid).getLimit() ) {	
							if (idLoc.equals(idTo)) {
								// tii by settings
								time = traveldata.min; // > 0 ? traveldata.min : accessVariableData.getTii(numPersons);
								if(time == 0){
									throw new Exception("Travel time is not defined in distance table!");
								}
							} else {
								time = traveldata.min;
							}
							
							if(numPersons != 0){
								 double partialAV = confdata.get(avid)
										.norm(numPersons
												* Math.exp(confdata.get(avid).getBeta()
														* time));
								 AVvalue += (partialAV < accessVarsMinValue ? accessVarsMinValue : partialAV);
							}
						}
					}

					switch(avid){
						case 1: location.setAv_1(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 2: location.setAv_2(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 3: location.setAv_3(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 4: location.setAv_4(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 5: location.setAv_5(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 6: location.setAv_6(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 7: location.setAv_7(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 8: location.setAv_8(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 9: location.setAv_9(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 10: location.setAv_10(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 11: location.setAv_11(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
						case 12: location.setAv_12(AVvalue < accessVarsMinValue ? accessVarsMinValue : AVvalue); break;
					}
				}
			}
		}
		logger.info("Update universe...");
		for (Location location : locations) universeService.updateLocation(location);
		logger.info("AV Done...");
	}
	
	private DistanceRecord getDistanceRecord( Integer locid, Map<Integer,?> map ){
		Object obj = map.get(locid);
		if(obj instanceof DistanceRecord) return (DistanceRecord)obj; 
		if(obj instanceof GeneralizedLocationDistance){
			GeneralizedLocationDistance gdObj = (GeneralizedLocationDistance)obj;
			
			DistanceRecord rd = new DistanceRecord();
			rd.distance = gdObj.getDistance_A();
			rd.carTime = gdObj.getDistance_B();
			rd.publicTime = gdObj.getDistance_D();
			rd.bicycleTime = gdObj.getDistance_F();
			
			return rd;
		}
		return null;
	}
	
	private void initDistanceMap(Map<String, Map<Integer,Map<Integer,TravelData>>> distmap, List<Location> locs, String tablename){
		logger.info("   - cache distances - CSV starts");
		
		Map<Integer, Map<Integer, TravelData>> map = null;
		if(!distmap.containsKey(tablename)){
			map = new HashMap<Integer, Map<Integer, TravelData>>();
			distmap.put(tablename, map);
		}
		else return;
		
		String filter = "";
		if(locs.size() > 0){
			String locationsIds = StringUtil.packageLocsString(locs);
			filter = FILTER_COL_A + " in " + locationsIds + " AND " + FILTER_COL_B + " in " + locationsIds;
		}		
		//backup, set table name and resture
		List<GeneralizedSimpleLocationDistance> distances = null;
		String currDistanceTable = uman.getCurrentDistanceTable();
		uman.setCurrentDistanceTable(tablename);
		try{
			distances =  generalDao.readAll(GeneralizedSimpleLocationDistance.class, filter);
		}
		finally{
			uman.setCurrentDistanceTable(currDistanceTable);
		}
		for(GeneralizedSimpleLocationDistance dist : distances){
			//check distances vs. locations
			if(universeService.selectLocationById(dist.getLocationA()) != null && 
					universeService.selectLocationById(dist.getLocationB()) != null){
				Map<Integer, TravelData> locmap = null;
				
				if(!map.containsKey(dist.getLocationA())){
					locmap = new HashMap<Integer, TravelData>();
					map.put(dist.getLocationA(), locmap);
				}
				else{
					locmap = map.get(dist.getLocationA());
				}
				locmap.put(dist.getLocationB(), new TravelData(dist.getDistance(), dist.getTime()));
			}
		}
		distances = null;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Integer, Object> getGeneralizedDistanceRecordsForLocation( Integer locationIdA, Map<Integer,?> distmap ){
		Map<Integer, Object> map = null;
		if(distmap != null){
			map = (Map<Integer, Object>) distmap.get(locationIdA);
		}
		else{					
			map = new HashMap<Integer, Object>();		
			List<GeneralizedLocationDistance> gldList = glddao.selectGeneralizedDistances(locationIdA);
			for(GeneralizedLocationDistance gld : gldList){		
				map.put(gld.getLocationBId(), gld);
			}	
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private Set<String> loadXMLConfig(String filepath, Map<Integer, AccessVariableData> confdata, Integer curryear){	
		Set<String> DisttableNames = new HashSet<String>();
		logger.info("Accessibility Vars - loadXMLConfig: " + filepath);		
		JDOMUtil jdu = new JDOMUtil();
		Document document = jdu.loadJDom(filepath);
		
		Element rootNode = document.getRootElement();
		Iterator<Element> iterator = rootNode.getDescendants(new ElementFilter(VARXML_VARIABLE));
		while (iterator.hasNext()){
			Element el = iterator.next();
			Integer id = Integer.parseInt(el.getAttributeValue(VARXML_ID));
			String type = el.getAttributeValue(VARXML_TYPE);
			Element param = el.getChild(VARXML_PARAMETERS);
			
			long maxdist = Long.parseLong(param.getAttributeValue(VARXML_MAXDISTANCE));
			Double beta = Double.parseDouble(param.getAttributeValue(VARXML_BETA));
			Double minvalue = Double.parseDouble(param.getAttributeValue(VARXML_MINVALUE));
			Boolean uselog = Boolean.parseBoolean(el.getAttributeValue(VARXML_LOG));
			String tablename = param.getAttributeValue(VARXML_TABLE);
			
			AccessVariableData accessVariableData = new AccessVariableData(maxdist, beta, null, uselog, minvalue); //avp.getAccessTiiIntervals()
			accessVariableData.setTablename(tablename);
			accessVariableData.setType(type);
			confdata.put(id, accessVariableData);
		}	
		
		if(curryear != null){
			iterator = rootNode.getDescendants(new ElementFilter(VARXML_FORCECALC));
			while (iterator.hasNext()){
				Element el = iterator.next();
				Integer year = Integer.parseInt(el.getAttributeValue(VARXML_YEAR));
				if(year.compareTo(curryear) == 0){
					validExecuteYear = true;
					break;
				}
			}			
						
			Map<Integer, Map<Integer, String>> tmpMap = new TreeMap<Integer, Map<Integer, String>>();
			iterator = rootNode.getDescendants(new ElementFilter(VARXML_UNIVERSE));
			while (iterator.hasNext()){
				Element el = iterator.next();
				Integer id = Integer.parseInt(el.getAttributeValue(VARXML_ID));
				Integer year = Integer.parseInt(el.getAttributeValue(VARXML_YEAR));
				String tablename = el.getAttributeValue(VARXML_TABLE);
				Map<Integer, String> tmpRow = null;
				if(!tmpMap.containsKey(year)){
					tmpRow = new HashMap<Integer, String>();
					tmpMap.put(year, tmpRow);
				}
				else tmpRow = tmpMap.get(year);
				tmpRow.put(id, tablename);
			}	 
			
			int currtableyear = 0;
			for(Integer year : tmpMap.keySet()){
				if(year.compareTo(curryear) <= 0) currtableyear = year; //highest lower year
				else{
					Map<Integer, String> tmpRow = tmpMap.get(currtableyear);
					for(Integer id : tmpRow.keySet()){
						if(confdata.containsKey(id)){
							AccessVariableData accessVariableData = confdata.get(id);
							accessVariableData.setTablename(tmpRow.get(id));
						}						
					}
					break;
				}
			}			
		}
		for(AccessVariableData accessVariableData : confdata.values()){
			DisttableNames.add(accessVariableData.getTablename());	
		}
		return DisttableNames;
	}
	
	private File checkExportDir(String folder, String subfolder){
		File fdir = new File(folder);
		if(!fdir.exists()) fdir.mkdirs();
		if(subfolder != null && !"".equals(subfolder)){
			fdir = new File(fdir.getAbsoluteFile(), subfolder);
			if(!fdir.exists()) fdir.mkdirs();
		}
		return fdir;
	}	
}
