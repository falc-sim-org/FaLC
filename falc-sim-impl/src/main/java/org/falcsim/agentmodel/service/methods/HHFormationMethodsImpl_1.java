package org.falcsim.agentmodel.service.methods;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.demography.methods.DemographyMethods;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.service.domain.HhFormationParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.statistics.RandDevice;
import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HHFormationMethodsImpl_1 implements HHFormationMethods{

	@Autowired
	private HhFormationParameters hfp;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private UtilityParameters up;
	@Autowired
	private DemographyMethods demogMs;
	@Autowired
	private RandDevice randDevice;
	@Autowired
	private RCRandomGenerator rg;		
	
	private Map<Integer, List<Household>> partnersExMap;
	private static final double minDist = 2; //original value = 2000, but now we are using KM
	private int currYear;
	private int run;
	private int refrun;
	private Map<Integer, Integer> logMap;
	private boolean log = false;
	
	
	public void init() {
		
		run = sp.getCurrentTimeMarker();
		currYear = sp.getCurrentYear();	
		refrun = sp.getCurrentReferenceRun();
		partnersExMap = createExclusionListMap();
		if (!up.isDistanceMapInitialized()){
			up.initDistanceMap(up.getLocs());
		}
		if (log){
			initLogMap();
		}
	}

	@Override
	public void peopleJoin(Location loc) {
		try{
			List<Household> newCouples = new ArrayList<Household>();
			List<RealIdProbInterval> matingRipisForLoc = prepareRipisForMating(loc.getId());
			for(Household h : loc.getHouseholds()){
				//if(h.getPersons().size() == 1){
				for(Person p : h.getPersons()){
						
				//		if((p.getPosition_in_hh() == null || 
				//				(p.getPosition_in_hh() == RCConstants.H_Partner && p.getPartnership_since() == null)) && 
				//				p.getSex().equals(RCConstants.MALE) ){
				if(validatePartnerInHouseholds(h, p, RCConstants.MALE)){
							try {
								if(randDevice.assignRandomWeightedBoolean(hfp.getHhformationProbability(), p, sp.getCurrentYear(), sp.getCurrentReferenceRun()) ){
									mate(p, h, matingRipisForLoc, newCouples );							
								}
								break;
							} catch (RCCustomException e) {
								logger.error(e.getMessage(), e);
								System.exit(ExitCodes.MODELS_HH_FORMATION_ERROR.getValue());
							}
						//}
					}
				}
			}
			logger.info(newCouples.size()+" new couples in location "+loc.getId()+" "+loc.getName());
			loc.getEventsStatus().hh_merged = newCouples.size();
			loc.getHouseholds().addAll(newCouples);
			if (log){
				displayLogMap();
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.MODELS_HH_FORMATION_ERROR.getValue());
		}
	}

	private boolean validatePartnerInHouseholds(Household h, Person p, int gender){
		boolean valid = false;		
		int ageMale = demogMs.getAgeOfPerson(p, currYear);
		if(p.getSex().equals(gender) && ageMale >= hfp.getHhformationMinage().intValue()){
			if( p.getPosition_in_hh() == null ||
			   (p.getPosition_in_hh() == RCConstants.H_Partner && p.getPartnership_since() == null))
			{
				valid = true;
				if(h.getPersons().size() > 1){
					for(Person tmp_p : h.getPersons()){
						if(tmp_p != p ){
							if( p.getPosition_in_hh() == null || p.getPosition_in_hh() != RCConstants.KID){
								valid = false;
								break;
							}
						}
					}
				}
			}
		}
		return valid;
	}
	
	private List<RealIdProbInterval> prepareRipisForMating(Integer locId){

		double tot;
		double totOld;

		List<RealIdProbInterval> ripis = new ArrayList<>();
		tot=0;
		totOld=0;
		for (Location lic : up.getLocs() ){
			RealIdProbInterval ripi = new RealIdProbInterval();
			ripi.setId(lic.getId());
			totOld=tot;
			tot += (lic.getHouseholds().size()/Math.max(minDist, up.getDistance(locId, lic.getId())));
			ripi.setLower(totOld);
			ripi.setUpper(tot);
			ripis.add(ripi);
		}
		return ripis;
	}

	private void mate(Person p_1, Household h_1, List<RealIdProbInterval> matingRipisForLoc, List<Household> newCouples) throws RCCustomException{
		Household h_2; 
		Household h_new = new Household(true); 
		int count = 0;
		boolean assigned = false;
		while (!assigned ){			
			Integer locTargetId = up.getLocationChoiceManagedByID() ? 
					  randDevice.assignWeightedRandomId(rg, matingRipisForLoc, p_1, currYear, refrun * (count+1) )
					: randDevice.assignWeightedRandomId(matingRipisForLoc);
			Iterator<Household> itr = partnersExMap.get(locTargetId).iterator();
			count++;
			while (itr.hasNext()){
				h_2 = itr.next();
				if (h_2.getId() == h_1.getId()){
					itr.remove(); //We don't want to marry people to themselves, another Male cannot select this Male, neither later
					continue;
				}
				List<Person> ps =  h_2.getPersons();
				for (Person p_2 :  ps){
					//if( ( p_2.getPosition_in_hh() == null || (p_2.getPosition_in_hh() == RCConstants.H_Partner && p_2.getPartnership_since() == null)) && p_2.getSex().equals(RCConstants.FEMALE)){
																	
					if(validatePartnerInHouseholds(h_2, p_2, RCConstants.FEMALE)){
						
						Date fdate = demogMs.assignDateOnYear(currYear);
						h_new.setDfoundation(fdate);
						h_new.setLocationId(h_1.getLocationId());
						h_new.setRun(run);
						p_1.setHouseholdId(h_new.getId());
						//p_1.setPosition_in_hh(M_P);
						p_1.setPosition_in_hh(RCConstants.H_Partner);
						p_1.setPartnership_since(currYear);
						p_1.setRun(run);	//maybe this was means as no change, but than it was not written to history...
						//p_2.setPosition_in_hh(F_P);
						p_2.setPosition_in_hh(RCConstants.H_Partner);
						p_2.setPartnership_since(currYear);
						for (Person p : h_2.getPersons()){
							p.setHouseholdId(h_new.getId());
							p.setRun(run);
						}
						if (log){
							fillLogMap(h_1.getLocationId(), locTargetId , h_2.getPersons().size());
						}
						h_new.getPersons().addAll(h_2.getPersons());
						h_new.getPersons().add(p_1);
						h_1.setDclosing(fdate);
						h_2.setDclosing(fdate);
						h_1.setRun(run);
						h_2.setRun(run);
						h_1.getPersons().clear();
						h_2.getPersons().clear();
						assigned = true;
						break;
					}
				}
				itr.remove();
				if (assigned){
					break;
				}
			}
			
			if (assigned || count>1000){
				break;
			}
		}
		if (!assigned){
			logger.warn("Could not assign a mate to " + p_1.getId());
		} else {
			newCouples.add(h_new);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<Integer, List<Household>> createExclusionListMap() {
		logger.info("      - createExclusionListMap ... ");
		Map<Integer, List<Household>> map = new HashMap<>();
		for (Location loc: up.getLocs()){
			ArrayList<Household> hArr = new ArrayList<>();
			hArr.addAll(loc.getHouseholds());
			map.put(loc.getId(), (List<Household>) (hArr).clone());
		}
		logger.info("        Done ... ");
		return map;
	}
	
	private void initLogMap(){
		logMap = new HashMap<>();
		for(Location loc: up.getLocs()){
			logMap.put(loc.getId(), 0);
		}		
	}
	
	private void fillLogMap(Integer locFromId, Integer locToId , int size){
		logMap.put(locFromId, logMap.get(locFromId) -size);
		logMap.put(locToId, logMap.get(locToId) +size);			
	}
	
	private void displayLogMap(){
		for (Map.Entry<Integer, Integer> entry : logMap.entrySet())
		{
		    System.out.println(entry.getValue() +" balance for location "+entry.getKey());
		}
	}

}
