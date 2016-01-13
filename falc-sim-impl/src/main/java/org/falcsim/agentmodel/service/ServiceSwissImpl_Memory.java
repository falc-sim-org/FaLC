package org.falcsim.agentmodel.service;

import org.falcsim.agentmodel.accessiblevars.generator.GenerateAccessibleVars;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.app.util.CommunicationClass;
import org.falcsim.agentmodel.domain.Universe;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.hist.dao.HistoryDao;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.service.methods.ServiceMethod;
import org.falcsim.agentmodel.service.methods.util.RunIndicators;
import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsManager;
import org.falcsim.agentmodel.util.dao.UpdateLocationsData;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.falcsim.enums.ReportType;
import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;

/** Service(Universe evolution) module implementation
*
* @author regioConcept AG
* @version 1.0
* 
*/
public class ServiceSwissImpl_Memory implements Service {

	/**
	 * universe functionality
	 */
	@Autowired
	private UniverseServiceUtil universeService;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private UtilityParameters up;
	@Autowired
	private RunParameters rp;
	@Autowired
	private ServiceMethod sMethod;

	@Autowired
	UpdateLocationsData uld;
	@Autowired
	private RunIndicators ri;
	@Autowired
	private RunIndicatorsManager riman;	
	
	/**
	 * Accessible variables generator
	 */	
	@Autowired
	private GenerateAccessibleVars gAV;
	@Autowired
	private HistoryDao historyDao;
	
	private boolean firstYearRun = true;
	
	public void setUniverse(){

		universeService.loadUniverse();
	}
	
	public Universe getUniverse(){
		
		return universeService.getUniverse();
	}
	
	public void saveUniverse(){
		
		universeService.saveOrUpdateUniverse( universeService.getUniverse() );
	}
	
	public void addHistory(){
		logger.info("Cleaning universe history ...");
		//TODO: originally called thru universe, but there is exception if cached universe data != null
		historyDao.make_history();
		logger.info("Cleaning universe history done...");
		
		logger.info("Cleaning universe indexes...");
		universeService.resetUniverseIndexes();
		logger.info("Finished processing universe ...");
		
	}
	
	public void processUniverse(){
		//XXX 
		CommunicationClass.writeUniverseYear(sp.getCurrentYear() + 1);
		Long start = System.currentTimeMillis();
		if(getUniverse() == null ){
			setUniverse();
		}
		if(firstYearRun) firstYearRun = false;
		logger.info("Increase universe year ...");
		sp.update();
		logger.info("");
		logger.info("");
		logger.info("================================");	
		logger.info("Current universe year      : " + String.valueOf(sp.getCurrentYear()));
		logger.info("Current universe timemarker: " + String.valueOf(sp.getCurrentTimeMarker()));	
		logger.info("Current universe ref_runs  : " + String.valueOf(sp.getCurrentReferenceRun()));
		logger.info("================================");
		logger.info("");
		
		logger.info("Processing universe ...");		
		processExtensions();
		processCore();
		Long end = System.currentTimeMillis();
		logger.info("");
		logger.info("********************************");
		logger.info("Calculation took: " + String.valueOf(end - start) + "ms");
		logger.info("********************************");
		logger.info("");
		CommunicationClass.writeDiffMessage(start, end);
		CommunicationClass.reportProgress();
	}

	private void processExtensions(){
		logger.info("Universe extensions ...");			
		try{
			//calculate accessible variables			
			if(rp.getRun_yearly_accessible_variables()){
				logger.info("Yearly accessible variables...");				
				gAV.generate(sp.getCurrentYear(), true);
			}
			else{
				logger.info("Yearly accessible variables...by XML settings");				
				gAV.generate(sp.getCurrentYear(), false);				
			}
			up.initDistanceMap( universeService.getUniverse().getLocations() );			
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.AV_GENERAL_ERROR.getValue());
		}
		uld.update(true);	//update locations_zones tables	
	}
	
	private void processCore(){
		logger.info("Universe set. Proceeding ...");
		logger.info("Running universe year: " + String.valueOf(sp.getCurrentYear()));
		
		sMethod.proceed();
		
		logger.info("Universe without save...");
	}

	@Override
	public void finalize(){
		ri.export();
		riman.reset();
		
		if(sp.getCurrentRun() == rp.getService_cycles() && rp.getService_final_save() ){
			logger.info("Saving universe ...");
			saveUniverse();
			logger.info("Universe saved...");
		}
		else{
			logger.info("Universe not saved in DB this year...");	
		}
	}
	
	@Override
	public void init() {		
		firstYearRun = true;
		setUniverse();

		up.init( universeService.getUniverse().getLocations() );
		
		//save starting state of run indicators..
		ri.exportInitState();
		riman.reset();
	}
}
