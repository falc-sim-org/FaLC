package org.falcsim.agentmodel.service.methods;

import java.util.List;

import org.falcsim.agentmodel.app.domain.AppParameters;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.service.Service;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.service.methods.EWMethods.MigrantionType;
import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsManager;
import org.falcsim.agentmodel.util.UtilCounter;
import org.falcsim.agentmodel.utility.UtilityMove;
import org.falcsim.agentmodel.utility.domain.UtilityGrowthChecker;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.falcsim.agentmodel.utility.landusage.LandUsage;
import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


@Component
public class ServicePandTImpl implements ServiceMethod {
	
	@Autowired
	private Service service;
	@Autowired
	private AppParameters appPar;
	@Autowired
	private UtilityParameters up;
	@Autowired
	private EWMethods ewm;
	@Autowired
	private HHFormationMethods hhfm;
	@Autowired
	private HHSeparationMethods hhsm;
	@Autowired
	private LDMethods ldm;
	@Autowired
	private UtilCounter uc;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private UtilityMove umsw;
	@Autowired
	private UniverseServiceUtil userviceutil;
	@Autowired
	private LandUsage landLimits;
	@Autowired
	private UtilityGrowthChecker ugch;
	@Autowired
	private RunIndicatorsManager riman;
	@Autowired
	private BusinessEconomicDevRev1 becdrev1;
	@Autowired
	private BusinessRiseAndFall brf;
	@Autowired
	private BusinessQuittingEmp bqe;
	@Autowired
	private BusinessJoiningEmp bje;
	//@Autowired
	//private EducationChange edu;
	
	private StopWatch stopWatch;
		
	@Override
	public void proceed() {
		stopWatch = new StopWatch();
		
		stopWatch.start("initialization");
		inizialize();
		stopWatch.stop();
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.SINIT, stopWatch.getLastTaskTimeMillis() );	
		
		int count = 0;
		int size = up.getLocs().size();		
		for(Location loc : up.getLocs()){
			count++;
			logger.info("Processing demography in location "+count+"-th of "+size+" locations. ("+loc.getId()+ " - "+loc.getName() + ")");
			handle_hh(loc);
			userviceutil.updateLocation(loc); //because new households and business are not in universe index...
		}		
		
		//Migration model is independent, because it work with regions, not only locations
		//A = Immigrants
		handle_migration_a();
		//relocate households
		handle_relocation_households();
		//B = Migrants	
		handle_migration_b();
		if(appPar.getControl_totals_residentsOn() || appPar.getMigrationOn()){
			//just test output
			//ewm.writeCSVindicators();
		}		
		
		count = 0;	
		logger.info("Processing firmography economic development.");
		stopWatch.start("economicdev");
		becdrev1.calculateEconomicVariables(up.getLocs());
		stopWatch.stop();
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.ECDEV, stopWatch.getLastTaskTimeMillis());
		
		for(Location loc : up.getLocs()){
			count++;
			logger.info("Processing firmography in location "+count+"-th of "+size+" locations. ("+loc.getId()+ " - "+loc.getName() + ")");
			handle_bb(loc);	
			userviceutil.updateLocation(loc); //because new households and business are not in universe index...
		}
		
		stopWatch.start("joinEmployees");
		if(appPar.getJoiningEmplOn()){
			bje.joinEmployees(up.getLocs());
		}
		stopWatch.stop();
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.JOINEMP, stopWatch.getLastTaskTimeMillis());
		
		//relocate businesses
		handle_relocation_businesses();
			
		fillNumbersOfPopulatedPersons();
		
		logger.info(" run "+(sp.getCurrentTimeMarker())+": "+size+ " locations, "+uc.getCount3()+ " immigrants, "+uc.getCount1()+ " births and "+uc.getCount2()+ " deaths");
		logger.info(" run "+(sp.getCurrentTimeMarker())+": "+up.getLocs().size()+ " locations, "+uc.getCount4()+ " businesses created, "+
				uc.getCount5()+ " businesses closed, "+uc.getCount6()+ " employers quit job, "+uc.getCount7()+ " employers join job ");
	}
	
	/** 
	 * Update location with information about generated numbers of residents and workers
	 * 
	 */
	private void fillNumbersOfPopulatedPersons(){
		for(Location loc : up.getLocs() ){
			int numOfResidents = 0;
			int numOfWorkers = 0;
			for(Household h : loc.getHouseholds()){
				//if(h.getDclosing() == null ) 
				numOfResidents += getLivePersons( h.getPersons() );
			}
			for(Business b : loc.getBusinesses()){
				if(b.getDclosing() == null ) numOfWorkers += getLivePersons( b.getPersons() );
			}
			loc.setActualResidents(numOfResidents);
			loc.setActualWorkers(numOfWorkers);
		}
	}
	
	private int getLivePersons(List<Person> list){
		int count = 0;
		for(Person p : list){
			if(p != null && p.getDdeath() == null )count++;
		}
		return count;
	}
		
	private void inizialize(){
		up.setLocs(service.getUniverse().getLocations());
		up.setMunicipalities(userviceutil.selectAllMunicipalities());
		up.setCantons(userviceutil.selectAllCantons());
		
		uc.reset();
		logger.info("Initializing ... ");
		
		if (appPar.getUtilitiesHouseholdsOn() || appPar.getUtilitiesBusinessesOn() ){
			logger.info("Initializing utilities ... ");
			umsw.init();
			logger.info("Utilities initialized. ");
			ugch.init(service.getUniverse().getLocations());
		}
		
		init_hh();
		init_bb();
				
		try{
			logger.info("Initialize Lang Usage Limits...");
			landLimits.init(service.getUniverse().getLocations(), false);
			logger.info("Initialize Lang Usage Limits Done...");
		}
		catch(RCCustomException e){
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.MODELS_LAND_USAGE_ERROR.getValue());
		}
	}
	
	private void handle_bb(Location loc){

		stopWatch.start("FirmographyChanges");
		if(appPar.getFirmographyOn()){
			brf.process(loc);
		}
		stopWatch.stop(); 
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.FCH, stopWatch.getLastTaskTimeMillis());
		
		stopWatch.start("quitEmployees");
		if(appPar.getQuittingEmplOn()){
			bqe.quitEmployees(loc);
		}			
		stopWatch.stop(); 
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.QUITEMP, stopWatch.getLastTaskTimeMillis());
	}
	
	private void init_bb(){
		
		if(appPar.getFirmographyOn()){
			logger.info("Initializing firmography ... ");
			becdrev1.init();
			brf.init();
			logger.info("Firmography initialized. ");
		}
		
		if(appPar.getQuittingEmplOn()){
			logger.info("Initializing quitting employees ... ");
			bqe.init();
			logger.info("Quitting employees initialized. ");
		}
		
		if(appPar.getJoiningEmplOn()){
			logger.info("Initializing joining employees ... ");
			bje.init();
			logger.info("Joining employees initialized. ");
		}		
				
	}
	
	private void init_hh(){
		
		if(appPar.getDemographyOn()){
			logger.info("Initializing demography ... ");
			ldm.init();
			//edu.init();
			logger.info("Demography initialized. ");
		}
		if(appPar.getMigrationOn()){
			logger.info("Initializing immigration ... ");
			ewm.init();
			logger.info("Immigratio initialized. ");			
		}
		if (appPar.getHhSeparationOn()){
			logger.info("Initializing household separation ... ");
			hhsm.init();
			logger.info("Household separation initialized. ");
		}
		if (appPar.getHhFormationOn()){
			logger.info("Initializing household formation ... ");
			hhfm.init();
			logger.info("Household formation initialized. ");
		}
	}
	
	private void handle_migration_a(){  
		stopWatch.start("Migration");
		if(appPar.getMigrationOn()){
			logger.debug("Starting Migration A.");
			try {
				ewm.migrate(up.getLocs(), MigrantionType.MIGRANTS);
			} catch (IllegalArgumentException e) {
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.MODELS_IMMIGRATION_ERROR.getValue());
			}
			logger.debug("Migration completed A.");
		}
		stopWatch.stop(); 
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.IMMIGR, stopWatch.getLastTaskTimeMillis());		
	}
	
	private void handle_migration_b(){  
		stopWatch.start("ControlTotalResidents");
		if(appPar.getControl_totals_residentsOn()){
			logger.debug("Starting Migration B.");
			try {
				ewm.migrate(up.getLocs(), MigrantionType.CONTROL_TOTALS_RESIDENTS);
			} catch (IllegalArgumentException e) {
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.MODELS_MIGRATION_ERROR.getValue());
			}
			logger.debug("Migration completed B.");
		}
		stopWatch.stop(); 
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.IMMIGR, stopWatch.getLastTaskTimeMillis());		
	}
	
	private void handle_hh(Location loc){		
		stopWatch.start("DemographicChange");
		if(appPar.getDemographyOn()){
			logger.debug("Starting demographic change.");
			ldm.beBornAndDie(loc);
			logger.debug("Demographic change completed.");
			logger.debug("Starting education.");
			//edu.process(loc);
			logger.debug("Education completed.");
		}
		stopWatch.stop(); 
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.DCH, stopWatch.getLastTaskTimeMillis());
		
		//OLD implementation
//		stopWatch.start("Immigration");
//		if(appPar.getImmigrationOn()){
//			logger.debug("Starting immigration.");
//			ewm.migrate(loc);
//			logger.debug("Immigration completed.");
//		}
//		stopWatch.stop(); 
//		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.IMMIGR, stopWatch.getLastTaskTimeMillis());
		
		stopWatch.start("HouseholdSeparation");
		if (appPar.getHhSeparationOn()){
			logger.debug("Starting household separation.");
			hhsm.peoplePart(loc);
			logger.debug("Household separation completed.");
		}
		stopWatch.stop(); 
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.HHSEP, stopWatch.getLastTaskTimeMillis());
		
		stopWatch.start("HouseholdFormation");
		if (appPar.getHhFormationOn()){
			logger.debug("Starting household formation.");
			hhfm.peopleJoin(loc);
			logger.debug("Household formation completed.");
		}
		stopWatch.stop();
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.HHFORM, stopWatch.getLastTaskTimeMillis());
	}

	private void relocation_update_locations_status(){
		stopWatch.start("RelocationUpdate");
		logger.info("Calculate Lang Usage Limits...");
		landLimits.calculate(service.getUniverse().getLocations());
		for(Location loc : service.getUniverse().getLocations() ){
			loc.setUsedFloorAreaResDemography(loc.getUsedFloorAreaRes());
			loc.setUsedFloorAreaWrkDemography(loc.getUsedFloorAreaWrk());
			loc.setUsedFloorAreaAllDemography(loc.getUsedFloorAreaAll());
		}
		logger.info("Calculate Lang Usage Limits Done...");
		
		
		logger.info("Update relocation locations status.");
		ugch.updateLocationStatus(up.getLocs());
		stopWatch.stop(); 
		riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.RELOC, stopWatch.getLastTaskTimeMillis());
	}
	
	private void handle_relocation_households(){
		if (appPar.getUtilitiesHouseholdsOn() ){
			
			relocation_update_locations_status();
			try {
				stopWatch.start("HouseholdRelocation");
				umsw.moveHTAllLocations(up.getLocs());
				stopWatch.stop();
				riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.RELOC, stopWatch.getLastTaskTimeMillis());
				
				for(Location loc : service.getUniverse().getLocations() ){
					loc.setUsedFloorAreaResMoveHH(loc.getUsedFloorAreaRes());
					loc.setUsedFloorAreaWrkMoveHH(loc.getUsedFloorAreaWrk());
					loc.setUsedFloorAreaAllMoveHH(loc.getUsedFloorAreaAll());
				}
			} catch (RCCustomException e) {
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.MODELS_HH_RELOCATION_ERROR.getValue());
			}
		}		
	}
	
	private void handle_relocation_businesses(){
		if (appPar.getUtilitiesBusinessesOn() ){
			
			relocation_update_locations_status();			
			try {				
				stopWatch.start("BusinessesRelocation");
				umsw.moveBTAllLocations(up.getLocs());
				stopWatch.stop();
				riman.put(sp.getCurrentReferenceRun(), sp.getCurrentYear(), RunIndicatorsManager.RELOC, stopWatch.getLastTaskTimeMillis());
			} catch (RCCustomException e) {
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.MODELS_BB_RELOCATION_ERROR.getValue());
			}
		}		
	}	
}
