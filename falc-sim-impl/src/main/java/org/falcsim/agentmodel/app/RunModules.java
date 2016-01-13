package org.falcsim.agentmodel.app;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.accessiblevars.generator.GenerateAccessibleVars;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.app.util.CommunicationClass;

import org.falcsim.agentmodel.service.Service;
import org.falcsim.agentmodel.sublocations.GenerateLocationsSubset;

import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Depending on the parameters in the run.properties file the corresponding
 * modules are loaded from the context and run. Note that the service module may
 * be run iteratively.
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */
@Component
public class RunModules {

	@Autowired
	private RunParameters runParameters;
	@Autowired
	private Service service;
	@Autowired
	private UniverseManager um;
	@Autowired
	private GenerateAccessibleVars gAV;
	@Autowired
	private GenerateLocationsSubset gLS;
	
	private static Logger logger = Logger.getLogger(RunModules.class);
	private String projectName = "";

	public void setProjectName(String projectName){
		this.projectName = projectName;
	}
	
	public void process() {		
		logger.info("START");		
		try {
			logger.info("Initialize Universe manager");
			um.init(this.projectName);			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.FALC_PROJECT_LOAD_ERROR.getValue());
		}		
		
		if(um.GetActiveScenarios() == null){
			logger.info("Single run");
			processModules();
		} else{
			if(um.GetActiveScenarios().size() == 0) 
				logger.warn("No active scenarios to proceed...");
			else{
				
				for(int i = 0; i < um.GetActiveScenarios().size(); i++){
					String scenario = um.GetActiveScenarios().get(i);
					StringBuilder message = new StringBuilder("  Starting scenario: ");
					message.append(scenario).append(", ").append(String.valueOf(i + 1));
					message.append("/").append(String.valueOf(um.GetActiveScenarios().size()));
					CommunicationClass.writeDelimiter();CommunicationClass.writeDelimiter();
					CommunicationClass.writeSimpleMessage(message.toString());
					CommunicationClass.writeDelimiter();CommunicationClass.writeDelimiter();
					logger.info("RUN scenario:" + scenario);
					um.SetScenario(scenario);
					processModules();
					logger.info("Scenario finished:" + scenario);
				}
			}
		}
		logger.info("FINISH");
	}

	private void processModules(){
		//once per FaLC run?
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		um.setSessionDirectory(sdf.format(new Date()));

		if (runParameters.getRun_populateDistances()) {
			throw new UnsupportedOperationException("Implement this module.");
		}
		
		//TODO: if synthesis and reset = TRUE, it generates data tables, copy then to history tables, delete them and copy history tables to them
		//run.universe.history=false ... this prevent Falc to use history tables and save time...
		if (runParameters.getRun_synthese()) {
			throw new UnsupportedOperationException("Implement this module.");
		} 

		if(runParameters.getRun_sublocations()){
			try{
				logger.info("Generate locations subset...");
				
				CommunicationClass.writeModuleStart("Generating locations subset");
				gLS.generate();
				
				CommunicationClass.writeModuleStart("Generating locations subset");
			}
			catch(Exception e){
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.LOCATION_SUBSET_GENERAL_ERROR.getValue());
			}
		}
		
		if (runParameters.getRun_universe()) {
			try{
				
				CommunicationClass.writeModuleStart("Universe");
				um.runUniverse(); //universe manager
				
				CommunicationClass.writeModuleStart("Universe");
			}
			catch(Exception e){
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.UNIVERSE_GENERAL_ERROR.getValue());
			}
		}		
	}
}
