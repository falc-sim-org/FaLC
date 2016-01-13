package org.falcsim.agentmodel.app;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.falcsim.agentmodel.accessiblevars.domain.AccessibleVarsParameters;
import org.falcsim.agentmodel.accessiblevars.generator.GenerateAccessibleVars;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.app.domain.ScenarioParameters;
import org.falcsim.agentmodel.app.util.CommunicationClass;

import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil.UniverseState;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.hist.dao.HistoryDao;
import org.falcsim.agentmodel.service.Service;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.service.methods.util.RunIndicators;
import org.falcsim.agentmodel.service.methods.util.StepMethods;
import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsManager;
import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsParameters;
import org.falcsim.agentmodel.statistics.StatisticsParameter;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.falcsim.agentmodel.util.math.random.RandomGeneratorModelID;
import org.falcsim.agentmodel.util.xml.JDOMUtil;
import org.falcsim.agentmodel.utility.dao.UtilityDaoHB;
import org.falcsim.agentmodel.utility.landusage.LandUsage;
import org.falcsim.exit.ExitCodes;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Universe manager implementation.
*  Handles project.xml file, scenarios, reference runs, copy best run, ...
*
* @author regioConcept AG
* @version 1.0
* @since   0.5 
* 
*/
@Component
public class UniverseManagerImpl implements UniverseManager {
	private static Logger logger = Logger.getLogger(UniverseManagerImpl.class);
	
	private static final String CONFIG = "falcconfig";
	private static final String SCENARIO = "scenario";
	private static final String ACTIVE = "active";
	private static final String CONTENT = "content";
	private static final String MARKDIFF = "markdiff";
	private static final String DESCRIPTION = "desc";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String VALUE = "value";	
	private static final String VARXML_UNIVERSE = "universe";
	private static final String VARXML_YEAR = "year";
	private static final String VARXML_TABLE = "table";	
	
	private int[] rndSeeds = null;
	private String currentDistanceTable;
	private int currentReferenceRun;
	private Map<String, String> universeTableNameMap;
	private Map<String, String> generalTableNameMap;	
	
	private List<ScenarioParameters> scenarios = null;
	private boolean runningSynthese = false;
	private String sessionDirectory = "";
	private List<ScenarioParameters> falcProjectParameters = null;
	private Map<Integer, String> distance_evolution = null;
	private List<ScenarioParameters> infoParameters = null;
	
	private Long runsRun = 1L;
	private Double avgRun = 0D;
	
	@Autowired
	private RunParameters runParameters;
	@Autowired
	private StepMethods sm;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private RunIndicators ri;
	@Autowired
	private RunIndicatorsManager riman;
	@Autowired 
	private RunIndicatorsParameters riParams;	
	@Autowired
	private Service service;
	@Autowired
	private UniverseServiceUtil universeservice;
	@Autowired
	private GeneralDao gdao;
	@Autowired
	private RCRandomGenerator rndGen;
	@Autowired
	private RandomGenerator rg;
	@Autowired
	private UtilityDaoHB udHB;
	@Autowired
	private LandUsage lu;
	@Autowired
	CsvProperties csvProperties;
	@Autowired
	RunIndicatorsParameters rip;
	@Autowired
	StatisticsParameter sParameters;
	@Autowired
	private AccessibleVarsParameters avp;	
	
	//SYNTHESIS + AV + INIT HISTORY
	@Autowired
	private HistoryDao historyDao;
	@Autowired
	private GenerateAccessibleVars gAV;
	
	@Override
	public int getCurrentReferenceRun() {
		return currentReferenceRun;
	}
	
	public void setRunnigSynthese(boolean syntheseIsRunning){
		this.runningSynthese = syntheseIsRunning;
	}
	
	public void setSessionDirectory(String date){
		this.sessionDirectory = "_FaLC_sessions" + csvProperties.getSchemaDelimiter() +
			date + "_" + rip.getRun_indicators_description() + csvProperties.getSchemaDelimiter();
	}
	
	public String getCurrentDistanceTable(){
		return currentDistanceTable;
	}
	
	public void setCurrentDistanceTable(String distanceTableName){
		currentDistanceTable = distanceTableName;
	}
	
	public String getOriginalName(String value){
		for(Map.Entry<String, String> pair : this.generalTableNameMap.entrySet()){
			if(pair.getValue().equalsIgnoreCase(value)){
				return pair.getKey();
			}
		}
		return "";
	}
	
	@Override
	public String getEntityTable(String defaultTable){
		if(defaultTable != null){	
			if("distances".equals(defaultTable) && currentDistanceTable != null && !"".equals(currentDistanceTable) ){
				defaultTable = currentDistanceTable;
			}						
			else{
				if("distances".equals(defaultTable) && distance_evolution != null && distance_evolution.size() > 0 ){
					defaultTable = currentDistanceTable;
					if(distance_evolution.containsKey(sp.getCurrentYear()))	defaultTable = distance_evolution.get(sp.getCurrentYear());
					else{
						int currtableyear = 0;
						for(Integer year : distance_evolution.keySet()){
							if(year.compareTo(sp.getCurrentYear()) < 0) currtableyear = year; //highest lower year
							else break;
						}
						if(currtableyear > 0){
							defaultTable = distance_evolution.get(currtableyear);
						}
					}
				}
				else if(generalTableNameMap != null){				
					for(String tablename : generalTableNameMap.keySet()){
						if(tablename.equals(defaultTable)){
							defaultTable = generalTableNameMap.get(tablename);
							break;
						}
					}
				}
			}
		}
		/**
		 * if the synthese or the universe is loaded, cleared and loading then replace the name for population tables
		 * 
		 */
		if(this.runningSynthese || 
				((universeservice.getUniverseState() == UniverseState.LOADING || 
				  universeservice.getUniverseState() == UniverseState.CLEAR ||
				  universeservice.getUniverseState() == UniverseState.LOADED ||
				  universeservice.getUniverseState() == UniverseState.POPULATING )  
				  //&& (scenariosWithUniverseCounter < 2 || runParameters.getRun_reset() ) 
				 )){
			
			// replacing the population with population + subschema
			if(defaultTable.startsWith("population/")){
				if( ! this.runningSynthese && universeservice.getUniverseState() == UniverseState.POPULATING ){
					String name = defaultTable.substring(defaultTable.lastIndexOf("/") + 1, defaultTable.length());
					return this.sessionDirectory + name;
				}
				else{
					String name = defaultTable.substring(defaultTable.lastIndexOf("/"), defaultTable.length());
					return "population" + 
						( runParameters.getPopulation_sub_schema() != null && !"".equals(runParameters.getPopulation_sub_schema()) ?		
						  "/" + runParameters.getPopulation_sub_schema() : "" ) + name;
				}
			}

		} else if((universeservice.getUniverseState() == UniverseState.LOADING || 
				  universeservice.getUniverseState() == UniverseState.CLEAR ||
				  universeservice.getUniverseState() == UniverseState.LOADED ||
				  universeservice.getUniverseState() == UniverseState.POPULATING )  ){ //&& scenariosWithUniverseCounter >= 2
			// replacing the population with last universe store
			if(defaultTable.startsWith("population/")){
				if( universeservice.getUniverseState() == UniverseState.POPULATING ){
					String name = defaultTable.substring(defaultTable.lastIndexOf("/") + 1, defaultTable.length());
					return this.sessionDirectory + name; 					
				}
				else{
					String name = defaultTable.substring(defaultTable.lastIndexOf("/") + 1, defaultTable.length());
					return this.sessionDirectory + name; 
				}
			}
		}
		else {
			if(universeservice.getUniverseState() == UniverseState.SAVING){
				if(defaultTable != null && universeTableNameMap != null && universeTableNameMap.containsKey(defaultTable.trim()) ){
					return universeTableNameMap.get(defaultTable.trim());
				}
				else if(defaultTable.startsWith("population/")){
						return this.sessionDirectory + defaultTable;
					} else {
						return defaultTable;
					}
			} 
		}
		return defaultTable;
	}
	
	@Override
	public void copyUniverse(int refRunID){
		//copy content of 8 tables from reference runs schema to public schema

		String universe_name = "";
		String schema = runParameters.getUniverse_reference_runs_schema() != null && !"".equals(runParameters.getUniverse_reference_runs_schema()) ?
				runParameters.getUniverse_reference_runs_schema() + csvProperties.getSchemaDelimiter() : "";
		String dest_dir = this.sessionDirectory;
		if(runParameters.getUniverse_reference_runs_copy_best_cycle_name() != null && !"".equals(runParameters.getUniverse_reference_runs_copy_best_cycle_clean()) ){
			dest_dir = "population" + csvProperties.getSchemaDelimiter() + runParameters.getUniverse_reference_runs_copy_best_cycle_name() + csvProperties.getSchemaDelimiter();
		}
		try{
			logger.info("Copying locations_zones table (partly)...");			
			gdao.copyTable(this.sessionDirectory + schema + "locations" + universe_name + "_" + refRunID, dest_dir + "locations" + universe_name);
			
			logger.info("Copying households table...");
			gdao.copyTable(this.sessionDirectory + schema + "households" + universe_name + "_" + refRunID, dest_dir + "households" + universe_name);
			
			logger.info("Copying businesses table...");
			gdao.copyTable(this.sessionDirectory + schema + "businesses" + universe_name + "_" + refRunID, dest_dir + "businesses" + universe_name);
			
			logger.info("Copying persons table...");
			gdao.copyTable(this.sessionDirectory + schema + "persons" + universe_name + "_" + refRunID, dest_dir + "persons" + universe_name);
			
			logger.info("Copying step table...");
			gdao.copyTable(this.sessionDirectory + schema + "steps" + universe_name + "_" + refRunID, dest_dir + "steps" + universe_name);
			
			if(runParameters.getUniverse_reference_runs_copy_best_cycle_clean()){
				logger.info("Droping old reference runs tables...");
				schema = runParameters.getUniverse_reference_runs_schema() != null && !"".equals(runParameters.getUniverse_reference_runs_schema()) ?
						runParameters.getUniverse_reference_runs_schema() : "";
						List<String> tablenames = new ArrayList<String>(
								Arrays.asList("locations", "households", "businesses", "persons", "steps") );
				if(schema != null && !"".equals(schema)){
					gdao.cleanReferenceRuns(this.sessionDirectory + schema, tablenames);
				}
			}			
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.UNIVERSE_COPY_ERROR.getValue());
		}
	}
	
	public void runPopulateDistances(){
		
	}
	
	@Override
	public void runSynthese() throws RCCustomException{
		throw new UnsupportedOperationException("Implement this module.");
	}
		
	private void resetUniverse() throws Exception{
		//step table
		sm.init();
		sp.init();		//service parameters - this also set actual universe year
		sp.setCurrentReferenceRun(currentReferenceRun);
		if(currentReferenceRun != 0 ){
			ri.setReferenceSubfolder("run_" + currentReferenceRun );
		}
	}
	
	private void prepareUniverseTables(){
		universeTableNameMap = new HashMap<String, String>();

		if(runParameters.getUniverse_reference_runs()){
			String schema = runParameters.getUniverse_reference_runs_schema() != null && !"".equals(runParameters.getUniverse_reference_runs_schema()) ?
				runParameters.getUniverse_reference_runs_schema() + this.csvProperties.getSchemaDelimiter() : "";
				
				universeTableNameMap.put("population/steps", 		this.sessionDirectory + schema + "steps_" + currentReferenceRun);
				universeTableNameMap.put("population/locations_zones", this.sessionDirectory + schema + "locations_zones_" + currentReferenceRun);
				universeTableNameMap.put("population/locations", this.sessionDirectory + schema + "locations_" + currentReferenceRun);
				universeTableNameMap.put("population/households", 	this.sessionDirectory + schema + "households_" + currentReferenceRun);
				universeTableNameMap.put("population/businesses", 	this.sessionDirectory + schema + "businesses_" + currentReferenceRun);
				universeTableNameMap.put("population/persons", 		this.sessionDirectory + schema + "persons_" + currentReferenceRun);	
				universeTableNameMap.put("assumptions/run_indicators", 		this.sessionDirectory + schema + "run_indicators_" + currentReferenceRun);
		}
		else{
			String schema = "";
					
			universeTableNameMap.put("population/steps", 		this.sessionDirectory + schema + "steps");
			universeTableNameMap.put("population/locations_zones", this.sessionDirectory + schema + "locations_zones");
			universeTableNameMap.put("population/locations", this.sessionDirectory + schema + "locations");
			universeTableNameMap.put("population/households", 	this.sessionDirectory + schema + "households");
			universeTableNameMap.put("population/businesses", 	this.sessionDirectory + schema + "businesses");
			universeTableNameMap.put("population/persons", 		this.sessionDirectory + schema + "persons");
			universeTableNameMap.put("assumptions/run_indicators", 		this.sessionDirectory + schema + "run_indicators");
		}
		
	}
	
	@Override
	public void runUniverse() throws Exception{
		distance_evolution = loadDistanceDefinition(runParameters.getDistances_evolution_xml_path() );
		runParameters.setApp_systemQuery("");		
		currentDistanceTable = "";		
		try{
			currentReferenceRun = 0;
			riman.reset();
			ri.init();						
			if(runParameters.getUniverse_reference_runs()){				
//				ri.init();
				int startIndex = 1;
				if(runParameters.getUniverse_reference_runs_cycles_startfrom() != null && runParameters.getUniverse_reference_runs_cycles_startfrom() > 1){					
					startIndex = runParameters.getUniverse_reference_runs_cycles_startfrom();
					currentReferenceRun = startIndex -1;
					logger.info("Setting start cycle: " + startIndex);
				}
				clearRunStats();
				CommunicationClass.setMaxProgress(String.valueOf((runParameters.getUniverse_reference_runs_cycles() - startIndex + 1) * runParameters.getService_cycles()));
				for (int i = startIndex; i <= runParameters.getUniverse_reference_runs_cycles(); i++) {
					Long runStart = System.currentTimeMillis();
					riman.resetCycle();
					CommunicationClass.writeRunMessage(i);
					currentReferenceRun++;
					//set universe desc. table set
					prepareUniverseTables();
					/*createUniverseTables();*/
					//reset all counters and RI
					resetUniverse();
					//run it
					ExportCurrentParamsValues(true);
					runOneReferenceCycle();
					Long runEnd = System.currentTimeMillis();
					sendRunStatistics(runStart, runEnd, i, startIndex, runParameters.getUniverse_reference_runs_cycles());
					if(universeservice.getUniverseState() != UniverseState.CLEAR){
						universeservice.clearUniverse();
					}
				}
				if(runParameters.getUniverse_reference_runs_statistics() || runParameters.getUniverse_reference_runs_copy_best_cycle() ){
					//int best_ref_run = ri.exportReferenceRuns_R2analysis();						
					//ri.exportReferenceRunsAVG();
					int best_ref_run = runStatistics_AVG_R2(ri.checkExportDir("").getAbsolutePath(), sp.getCurrentYear());
										
					if(	runParameters.getUniverse_reference_runs_copy_best_cycle()){
						if(best_ref_run > 0){
							logger.info("Selected reference run: " + String.valueOf(best_ref_run));
							logger.info("Copying reference run tables into universe...");
							copyUniverse(best_ref_run);
						}
						else{
							throw new RCCustomException("Cannot copy best calculated universe, R2 analysis was not correctly calculated.");
						}
					}
				}				
			}
			else{
				ExportCurrentParamsValues(false);
				CommunicationClass.setMaxProgress(String.valueOf(runParameters.getService_cycles()));
				prepareUniverseTables();
				runSingleUniverse();
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			System.exit(ExitCodes.UNIVERSE_GENERAL_ERROR.getValue());
		}
		finally{
			distance_evolution = null;
		}
	}

	public Integer serviceCounter(Integer i) {
		return i;
	}
	
	private void clearRunStats() {
		runsRun = 1L;
		avgRun = 0D;
	}
	
	private void sendRunStatistics(Long startRun, Long endRun, Integer index, Integer startIndex, Integer numberOfRuns){
		Double duration = Double.valueOf(endRun - startRun);
		CommunicationClass.writeRunStats(duration.longValue(), index);
		
		Double newAverage = avgRun + ((duration - avgRun) / runsRun);
		CommunicationClass.writeSeparator();
		CommunicationClass.writeAverageTimeStats(newAverage);
		
		Integer totalRuns = (numberOfRuns - startIndex) + 1;
		Long estimateFull = newAverage.longValue() * totalRuns;
		Integer currentRun = startIndex - index + 1;
		Long estimateRemain = estimateFull - (newAverage.longValue() * runsRun);
		
		CommunicationClass.writeEstimateForRuns(estimateFull, totalRuns);
		CommunicationClass.writeEstimateRemaining(estimateRemain);
		Date now = new Date((new Date()).getTime() + estimateRemain);
		CommunicationClass.writeEstimateFinish(now);
		CommunicationClass.writeSeparator();
		avgRun = newAverage;
		runsRun++;
	}
	
	private void calcAccessibilityVariables() throws Exception{
		if(!runParameters.getRun_yearly_accessible_variables()){
			logger.info("Generate accessible variables...");
			gAV.generate(null, true);
			logger.info("Generate accessible variables finished...");
		}
	}
	
	private void runOneReferenceCycle() throws Exception{
		setRandomGeneratorSeed();
		service.init();
		calcAccessibilityVariables();
		for (int i = 1; i <= runParameters.getService_cycles(); i++) {
			service.processUniverse();
			service.addHistory();
			service.finalize();
			//ri.export();
			serviceCounter(i);
		}		
	}
	
	private void runSingleUniverse() throws Exception {
		//ri.init();
		resetUniverse();
		setRandomGeneratorSeed();
		service.init();
		calcAccessibilityVariables();
		for (int i = 1; i <= runParameters.getService_cycles(); i++) {
			service.processUniverse();
			service.addHistory();
			service.finalize();
			serviceCounter(i);
		}
	}
	
	private void setRandomGeneratorSeed(){	
		if(runParameters.getUniverse_reference_runs_rg_seed() != null && rndSeeds != null){
			int index = (currentReferenceRun > 0 ? currentReferenceRun -1 : 0) % 10000;			
			int seed = rndSeeds[index];
			logger.info("Set Random generator SEED in cycle " + String.valueOf(currentReferenceRun) + " : " + String.valueOf(seed));
			rndGen.setSeed( seed);
			rg.setSeed(seed);
		}
	}
	
	private void resetScenarioTablesSettings(){
		currentReferenceRun = 0;
		currentDistanceTable = "";
		if(universeTableNameMap != null) universeTableNameMap.clear();
		if(generalTableNameMap != null) generalTableNameMap.clear();
		else generalTableNameMap =  new HashMap<String, String>();
		udHB.reset();
		lu.reset();
	}
	
	@Override
	public void SetScenario(String scenario){
		try{
			resetScenarioTablesSettings();
			BeanFactory factory = (BeanFactory) AppCtxProvider.getApplicationContext();
			FalcPropertyPlaceholderConfigurer fppc = factory.getBean(FalcPropertyPlaceholderConfigurer.class);
			fppc.reset();
			this.reloadFalcProject();
			
			if( scenarios != null){
				for(ScenarioParameters sp : scenarios){ 
					if(sp.getActive() && sp.getId().equals(scenario)){
						this.updateParameters(fppc, sp.getProperties(), sp.getTables());
						return;
					}
				}
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.SCENARIOS_GENERAL_ERROR.getValue());
		}		
	}
	
	@Override
	public List<String> GetActiveScenarios(){
		List<String> list = null;
		if( scenarios != null){
			list = new ArrayList<String>();
			for(ScenarioParameters sp : scenarios) 
				if(sp.getActive()) list.add(sp.getId());
		}
		return list;
	}
	
	@Override
	public void init(String falcProjectDirPath) throws Exception{
		if(!"".equals(falcProjectDirPath)){
			runParameters.setProjectDir(falcProjectDirPath);		
		}
		
		String falcProjectXMLpath = "";
		if("".equals(falcProjectDirPath))	falcProjectXMLpath = "properties/falcProject.xml";
		else falcProjectXMLpath = falcProjectDirPath + "/properties/falcProject.xml";
		
		String falcLocalSettingsXMLpath = "";
		if("".equals(falcProjectDirPath))	falcLocalSettingsXMLpath = "properties/falcLocalSettings.xml";
		else falcLocalSettingsXMLpath = falcProjectDirPath + "/properties/falcLocalSettings.xml";		

		String falcInfoDefXMLpath = "";
		if("".equals(falcProjectDirPath))	falcInfoDefXMLpath = "properties/infoDefinitions.xml";
		else falcInfoDefXMLpath = falcProjectDirPath + "/properties/infoDefinitions.xml";		
		
		logger.info("Loading properties/falcProject.xml and falcLocalSettings.xml");
		runParameters.setProjectfilePath(falcProjectXMLpath);	
		runParameters.setLocalProjectfilePath(falcLocalSettingsXMLpath);	
		runParameters.setExportParameterInfoDefinitionPath(falcInfoDefXMLpath);
		infoParameters = loadScenarios( falcInfoDefXMLpath, false, "info_run");
		
		resetScenarioTablesSettings();
		scenarios = null;
		loadFalcProject(falcProjectXMLpath, falcLocalSettingsXMLpath, falcProjectDirPath);
		try{
			if(runParameters.getUniverse_reference_runs_rg_seed() != null && runParameters.getUniverse_reference_runs_rg_seed() != 0){
				int seed = runParameters.getUniverse_reference_runs_rg_seed();				
				logger.info("Set Random generator SEED " + String.valueOf(seed));
				rndGen.setSeed(seed);					
				rndSeeds = new int[10000]; 
				for(int i = 0; i < 10000; i++){
					rndSeeds[i] = 10000000 + rndGen.nextInt(89999999, RandomGeneratorModelID.None);
				}
				logger.info("Random sequence generated...");
			}
			
			if(runParameters.getRun_scenarios()){
				logger.info("Load scenarios file: " + runParameters.getRun_scenarios_xml_path());
				scenarios = loadScenarios( runParameters.getRun_scenarios_xml_path(), false, "");
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.SCENARIOS_GENERAL_ERROR.getValue());
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<ScenarioParameters> loadScenarios(String filepath, boolean skipScenarioTag, String elementName) throws Exception{
		List<ScenarioParameters> params = new ArrayList<ScenarioParameters>();

		JDOMUtil jdu = new JDOMUtil();
		Document document = jdu.loadJDom(filepath);
		
		Element rootNode = document.getRootElement();
		Iterator<Element> yearNode = null;
		if(skipScenarioTag){
			yearNode = rootNode.getChildren().iterator();
			ScenarioParameters rec = new ScenarioParameters(true, "01", "default");
			while (yearNode.hasNext()){
				Element src = yearNode.next();
				rec.AddSettings(src.getName(), src.getAttributeValue(NAME), src.getAttributeValue(VALUE));					
			}
			params.add(rec);
		} else {			
			yearNode = rootNode.getDescendants(new ElementFilter("".equals(elementName) ? SCENARIO : elementName));
			while (yearNode.hasNext()){
				Element el = yearNode.next();
				ScenarioParameters rec = new ScenarioParameters(Boolean.parseBoolean(el.getAttributeValue(ACTIVE)), el.getAttributeValue(ID), el.getAttributeValue(DESCRIPTION) );
				rec.setMarkdiff( Boolean.parseBoolean(el.getAttributeValue(MARKDIFF,"true") ) );
				rec.setContent( el.getAttributeValue(CONTENT, "diff") );
				for(Element src: (List<Element>)el.getChildren()){
					rec.AddSettings(src.getName(), src.getAttributeValue(NAME), src.getAttributeValue(VALUE));
				}
				if(!ScenarioParameters.exists(params, rec.getId())) params.add(rec);
				else throw new RCCustomException("Duplicated scenarion ID. More active scenarios with the same ID cannot be defined.");					
			}
		}
		
		return params;
	}

	private Map<Integer, String> loadDistanceDefinition(String filepath) throws Exception{
		Map<Integer, String> params = new TreeMap<Integer, String>();

		JDOMUtil jdu = new JDOMUtil();
		Document document = jdu.loadJDom(filepath);			
		try{	
			Element rootNode = document.getRootElement();
			@SuppressWarnings("unchecked")
			Iterator<Element> iterator = rootNode.getDescendants(new ElementFilter(VARXML_UNIVERSE));
			while (iterator.hasNext()){
				Element el = iterator.next();
				Integer year = Integer.parseInt(el.getAttributeValue(VARXML_YEAR));
				String tablename = el.getAttributeValue(VARXML_TABLE);
				params.put(year, tablename);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			params.clear();
		}
		return params;
	}	
	
	
	//@Override
	private void loadFalcProject(String falcProjectXMLpath, String falcLocalSettingsXMLpath, String falcProjectDirPath) throws Exception {
		logger.info("Load project file: " + falcProjectXMLpath);
		if(this.falcProjectParameters == null){
			this.falcProjectParameters = loadScenarios(falcProjectXMLpath, true, "");
			
			if(falcLocalSettingsXMLpath != null && !"".equals(falcLocalSettingsXMLpath) && new File(falcLocalSettingsXMLpath).exists()){
				logger.info("Load local settings file: " + falcProjectXMLpath);
				List<ScenarioParameters> localSettings = loadScenarios(falcLocalSettingsXMLpath, true, "");
				for(String property : localSettings.get(0).getProperties().keySet()){
					String value = localSettings.get(0).getProperties().get(property);
					//if(this.falcProjectParameters.get(0).getProperties().containsKey(property)){
						this.falcProjectParameters.get(0).getProperties().put(property, value);
					//}
				}					
				for(String table : localSettings.get(0).getTables().keySet()){
					String value = localSettings.get(0).getTables().get(table);
					//if(this.falcProjectParameters.get(0).getTables().containsKey(table)){
						this.falcProjectParameters.get(0).getTables().put(table, value);
					//}					
				}
			}else logger.info("Local settings file: " + falcProjectXMLpath + " not found");
			
			//fix relative path for support XML files			
			if(falcProjectDirPath != null && !"".equals(falcProjectDirPath) && new File(falcProjectDirPath).exists()){
				//utility.relocation.source.xml.path
				String ufpath = falcProjectParameters.get(0).getProperties().get("utility.relocation.source.xml.path");
				if(ufpath != null && ! new File(ufpath).exists()){
					if(new File(falcProjectDirPath + "/" + ufpath ).exists()){  //"/properties/utilityFunctions.xml"
						logger.info(falcProjectDirPath + "/" + ufpath + " file found and used");
						falcProjectParameters.get(0).getProperties().put("utility.relocation.source.xml.path", falcProjectDirPath + "/" + ufpath);
					}
				}
				
				//dist.source.file
				String dnpath = falcProjectParameters.get(0).getProperties().get("dist.source.file");
				if(dnpath != null && ! new File(dnpath).exists()){
					if(new File(falcProjectDirPath + "/" + dnpath).exists()){
						logger.info(falcProjectDirPath + "/" + dnpath + " file found and used");
						falcProjectParameters.get(0).getProperties().put("dist.source.file", falcProjectDirPath + "/" + dnpath);
					}
				}				
				
				//falc.scenarios.xml
				String scpath = falcProjectParameters.get(0).getProperties().get("falc.scenarios.xml");
				if(scpath != null && ! new File(scpath).exists()){
					if(new File(falcProjectDirPath + "/" + scpath).exists()){
						logger.info(falcProjectDirPath + "/" + scpath + " file found and used");
						falcProjectParameters.get(0).getProperties().put("falc.scenarios.xml", falcProjectDirPath + "/" + scpath);
					}
				}
				
				//accessibility.variables.xml.path
				String avpath = falcProjectParameters.get(0).getProperties().get("accessibility.variables.xml.path");
				if(avpath != null && ! new File(avpath).exists()){
					if(new File(falcProjectDirPath + "/" + avpath).exists()){
						logger.info(falcProjectDirPath + "/" + avpath + " file found and used");
						falcProjectParameters.get(0).getProperties().put("accessibility.variables.xml.path", falcProjectDirPath + "/" + avpath);
					}
				}	
			
				//distances.xml.path
				String distpath = falcProjectParameters.get(0).getProperties().get("run.universe.distances.xml.path");
				if(distpath != null && ! new File(distpath).exists()){
					if(new File(falcProjectDirPath + "/" + distpath).exists()){
						logger.info(falcProjectDirPath + "/" + distpath + " file found and used");
						falcProjectParameters.get(0).getProperties().put("run.universe.distances.xml.path", falcProjectDirPath + "/" + distpath);
					}
				}	
							
			}			
			
			this.resetScenarioTablesSettings();
		}		
		this.reloadFalcProject();
		AppCtxProvider.getApplicationContext().getBean(FalcPropertyPlaceholderConfigurer.class).setupCustomRunIndicators();
	}
	
	private void reloadFalcProject() throws Exception{
		FalcPropertyPlaceholderConfigurer fppc = AppCtxProvider.getApplicationContext().getBean(FalcPropertyPlaceholderConfigurer.class);
		this.updateParameters(fppc, this.falcProjectParameters.get(0).getProperties(), this.falcProjectParameters.get(0).getTables());
	}
	
	private void updateParameters(FalcPropertyPlaceholderConfigurer fppc, Map<String, String> properties, Map<String, String> tables) throws Exception{
		for(String property : properties.keySet()){
			String value = properties.get(property);
			fppc.updateProperty(property, value);	
		}
		for(String table : tables.keySet()){
			String newtable = tables.get(table);
			generalTableNameMap.put(table, newtable);	
		}
	}
	
	private void ExportCurrentParamsValues(boolean referenceruns){
		for(ScenarioParameters infoRun : infoParameters){
			if(infoRun.getActive()){
				Map<String,String[]> map = new HashMap<String, String[]>();
				for(String property : infoRun.getProperties().keySet()){
					String value = infoRun.getProperties().get(property);
					try{
						String currValue = "";
						currValue = FalcPropertyPlaceholderConfigurer.getProperty(property);
						if("all".equals(infoRun.getContent())){
							map.put(property, new String[]{value, currValue});	
						}
						else if(!value.equals(currValue)){
							map.put(property, new String[]{value, currValue});
						}					
					}
					catch(Exception e){}
				}
				for(String property : infoRun.getTables().keySet()){
					String value = infoRun.getTables().get(property);
					try{
						String currValue = "";
						currValue = generalTableNameMap.get(property);
						if("all".equals(infoRun.getContent())){
							map.put(property, new String[]{value, currValue});	
						}
						else if(!value.equals(currValue)){
							map.put(property, new String[]{value, currValue});
						}	
					}
					catch(Exception e){}
				}			
				ri.exportParameters(map, infoRun.getId(), infoRun.getMarkdiff(), infoRun.getDescription(), infoRun.getContent());
			}
		}
	}
	
	private int runStatistics_AVG_R2(String directory, int year) throws RCCustomException{
		throw new UnsupportedOperationException("Implement this module.");
	}	
	
}
