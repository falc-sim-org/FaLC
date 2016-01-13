package org.falcsim.agentmodel.service.methods.util.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.service.domain.FirmographyParameters;
import org.falcsim.agentmodel.service.methods.util.domain.AgeBySexGroup;
import org.falcsim.agentmodel.service.methods.util.domain.HouseholdGroup;
import org.falcsim.agentmodel.service.methods.util.domain.JobGroup;
import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsManager;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.falcsim.exit.ExitCodes;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunIndicatorsDaoImpl implements RunIndicatorsDao {
	private static Logger logger = Logger.getLogger(RunIndicatorsDaoImpl.class);
	
	@Autowired
	private RunParameters runParameters;
	@Autowired
	private FirmographyParameters fps;
	@Autowired
	private UniverseServiceUtil universeservice;
	@Autowired
	private UtilityParameters up;
	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private RunIndicatorsManager riman;
	
	private static final String  public_schema_name = "assumptions";
	private static final String  fa_m2_per_person_table_name = "landusage_m2_per_person";
	private static final String  fa_m2_per_person_column_name = "sector";
	private static final String  fa_m2_per_person_column_prefix = "u";
	
	private static final int  resident_start_column = 0;
	private static final int  households_start_column = 44;
	private static final int  business_start_column = 63;
	private static final int  commuting_start_column = 83;
	private static final int  education_start_column = 104;
	private static final int  businessjobs_start_column = 109;
	private static final int  businesssize_start_column = 129;
	private static final int  landusage_start_column = 136;
	private static final int  households_RES_start_column = 151;
	private static final int  unemployedpersons_start_column = 170;
	private static final int  location_res_workers = 171;
	private static final int  location_overlimits = 172;
	private static final int  households_reloc_types = 177;
	
	private static final int OrderOfHHcolumnPaar = 14;
	private static final int OrderOfHHcolumnSingle = 15;
	private static final int OrderOfHHcolumnError = 18;
	
	
	@Override
	public Map<Integer,String> getRI_sublocations_table(){
		logger.info("Processing RI_locations_table...");
		Map<Integer,String> map = new TreeMap<Integer,String>();
    
		for(Location loc : universeservice.selectAllLocations()){
			StringBuilder sb = new StringBuilder();
			sb.append(loc.getId()); //locid
			sb.append(";\"");
			sb.append(loc.getName() == null ? "" : loc.getName()); //bezeichnung
			sb.append("\";\"");
			sb.append(loc.getLocationSubsetH() == null ? "" : loc.getLocationSubsetH()); //sublocH
			sb.append("\";\"");
			sb.append(loc.getLocationSubsetB() == null ? "" : loc.getLocationSubsetB()); //sublocB
			sb.append("\"");
			map.put(loc.getId(), sb.toString());
		}
		return map;
	}	
				
	@Override
	public Map<Integer,String> getRI_locations_table(){
		logger.info("Processing RI_locations_table...");
		Map<Integer,String> map = new TreeMap<Integer,String>();
    
		for(Location loc : universeservice.selectAllLocations()){
			StringBuilder sb = new StringBuilder();
			sb.append(loc.getId()); //locid
			sb.append(";\"");
			sb.append(loc.getName() == null ? "" : loc.getName()); //bezeichnung
			sb.append("\";");
			
			Municipality municipality = universeservice.selectMunicipalityById(loc.getMunicipalityId());
			sb.append(loc.getMunicipalityId()); //locid
			sb.append(";\"");
			sb.append(municipality.getName() == null ? "" : municipality.getName()); //bezeichnung
			sb.append("\";");			
			Canton canton = universeservice.selectCantonById(loc.getLocationId());
			sb.append(loc.getLocationId() == null ? "" : loc.getLocationId().toString()); //kantonsnr
			sb.append(";\"");
			sb.append(canton.getShortname() == null ? "" : canton.getShortname().toString()); //kantonsnr
			sb.append("\";");
			
			sb.append(loc.getRun() == null ? "" : loc.getRun().toString()); //run
			sb.append(";");
			sb.append(loc.getActualResidents() == null ? "" : loc.getActualResidents().toString());	//vzto		
			sb.append(";");
			sb.append(loc.getActualWorkers() == null ? "" : loc.getActualWorkers().toString());	//bsch
			sb.append(";");
			sb.append(loc.getSettlement_area() == null ? "" : loc.getSettlement_area().toString());	//fl_sdl
			sb.append(";");
			sb.append(loc.getFlat_rent() == null ? "" : loc.getFlat_rent().toString());	//mia_4
			sb.append(";");
			sb.append(loc.getUrban_centre() == null ? "" : loc.getUrban_centre().toString());	//gt_gmz			
			sb.append(";");
			sb.append(loc.getDiversity_sector() == null ? "" : loc.getDiversity_sector().toString());	//bst_dbr	
			sb.append(";");
			sb.append(loc.getMotorway_access() == null ? "" : loc.getMotorway_access().toString());	//se_aansl
			sb.append(";");
			sb.append(loc.getRailway_access() == null ? "" : loc.getRailway_access().toString());	//se_bahn
			sb.append(";");
			sb.append(loc.getResident_landprice_n() == null ? "" : loc.getResident_landprice_n().toString());	//lp_wohn_norm
			sb.append(";");
			sb.append(loc.getLanduse_density_n() == null ? "" : loc.getLanduse_density_n().toString());	//bz_totnd_norm
			sb.append(";");
			sb.append(loc.getUniversity_degree_n() == null ? "" : loc.getUniversity_degree_n().toString());	//wb_hsabs_q_norm
			sb.append(";");
			sb.append(loc.getDiversity_sector_n() == null ? "" : loc.getDiversity_sector_n().toString());	//bst_dbr_norm
			sb.append(";");
			sb.append(loc.getTax_holdingcomp_n() == null ? "" : loc.getTax_holdingcomp_n().toString());	//st_hg_k_norm
			sb.append(";");
			sb.append(loc.getTax_partnership_n() == null ? "" : loc.getTax_partnership_n().toString());	//st_pg_e_norm
			sb.append(";");
			sb.append(loc.getTax_companies_n() == null ? "" : loc.getTax_companies_n().toString());	//st_kg_g_norm
			sb.append(";");
			sb.append(loc.getAccessibility_t_n() == null ? "" : loc.getAccessibility_t_n().toString());	//se_ac_at_norm
			sb.append(";");
			sb.append(loc.getBus_dev_cant_n() == null ? "" : loc.getBus_dev_cant_n().toString());	//se_wfk_norm
			sb.append(";");
			sb.append(loc.getAccessibility_res() == null ? "" : loc.getAccessibility_res().toString());	//se_ac_wt
			sb.append(";");
			sb.append(loc.getBus_dev_mun_n() == null ? "" : loc.getBus_dev_mun_n().toString());	//se_wfr_vf_norm			
			sb.append(";");
			sb.append(loc.getAv_1() == null ? "" : loc.getAv_1().toString());	//av1	
			sb.append(";");
			sb.append(loc.getAv_2() == null ? "" : loc.getAv_2().toString());	//av2				
			sb.append(";");
			sb.append(loc.getAv_3() == null ? "" : loc.getAv_3().toString());	//av3				
			sb.append(";");
			sb.append(loc.getAv_4() == null ? "" : loc.getAv_4().toString());	//av4				
			sb.append(";");
			sb.append(loc.getAv_5() == null ? "" : loc.getAv_5().toString());	//av5			
			sb.append(";");
			sb.append(loc.getAv_6() == null ? "" : loc.getAv_6().toString());	//av6	
			sb.append(";");
			sb.append(loc.getAv_7() == null ? "" : loc.getAv_7().toString());	//av1	
			sb.append(";");
			sb.append(loc.getAv_8() == null ? "" : loc.getAv_8().toString());	//av2				
			sb.append(";");
			sb.append(loc.getAv_9() == null ? "" : loc.getAv_9().toString());	//av3				
			sb.append(";");
			sb.append(loc.getAv_10() == null ? "" : loc.getAv_10().toString());	//av4				
			sb.append(";");
			sb.append(loc.getAv_11() == null ? "" : loc.getAv_11().toString());	//av5			
			sb.append(";");
			sb.append(loc.getAv_12() == null ? "" : loc.getAv_12().toString());	//av6	
			sb.append(";");			
			sb.append(loc.getLandtype() == null ? "" : loc.getLandtype().toString());	//landtype	
			sb.append(";");
			sb.append(loc.getMaxFloorAreaRes() == null ? "" : loc.getMaxFloorAreaRes().toString());	//maxfloorareares
			sb.append(";");
			sb.append(loc.getUsedFloorAreaRes() == null ? "" : loc.getUsedFloorAreaRes().toString());	//usedfloorareares	
			sb.append(";");
			sb.append(loc.getMaxFloorAreaWrk() == null ? "" : loc.getMaxFloorAreaWrk().toString());	//maxfloorareawrk	
			sb.append(";");
			sb.append(loc.getUsedFloorAreaWrk() == null ? "" : loc.getUsedFloorAreaWrk().toString());	//usedfloorareawrk	
			sb.append(";");
			sb.append(loc.getMaxFloorAreaAll() == null ? "" : loc.getMaxFloorAreaAll().toString());	//maxfloorareaall	
			sb.append(";");
			sb.append(loc.getUsedFloorAreaAll() == null ? "" : loc.getUsedFloorAreaAll().toString());	//usedfloorareaall	
/*			
			sb.append(";");
			sb.append(loc.getUsedFloorAreaResDemography() == null ? "" : loc.getUsedFloorAreaResDemography().toString());		
			sb.append(";");
			sb.append(loc.getUsedFloorAreaWrkDemography() == null ? "" : loc.getUsedFloorAreaWrkDemography().toString());		
			sb.append(";");
			sb.append(loc.getUsedFloorAreaAllDemography() == null ? "" : loc.getUsedFloorAreaAllDemography().toString());					
			sb.append(";");
			sb.append(loc.getUsedFloorAreaResMoveHH() == null ? "" : loc.getUsedFloorAreaResMoveHH().toString());		
			sb.append(";");
			sb.append(loc.getUsedFloorAreaWrkMoveHH() == null ? "" : loc.getUsedFloorAreaWrkMoveHH().toString());		
			sb.append(";");
			sb.append(loc.getUsedFloorAreaAllMoveHH() == null ? "" : loc.getUsedFloorAreaAllMoveHH().toString());	
*/
			
			sb.append(";");
			sb.append(loc.getVar1() == null ? "" : loc.getVar1().toString());	//variable1	
			sb.append(";");
			sb.append(loc.getVar2() == null ? "" : loc.getVar2().toString());	//variable2				
			sb.append(";");
			sb.append(loc.getVar3() == null ? "" : loc.getVar3().toString());	//variable3				
			sb.append(";");
			sb.append(loc.getVar4() == null ? "" : loc.getVar4().toString());	//variable4				
			sb.append(";");
			sb.append(loc.getVar5() == null ? "" : loc.getVar5().toString());	//variable5			
			sb.append(";");
			sb.append(loc.getVar6() == null ? "" : loc.getVar6().toString());	//variable6	
			sb.append(";");
			sb.append(loc.getVar7() == null ? "" : loc.getVar7().toString());	//variable7				
			sb.append(";");
			sb.append(loc.getVar8() == null ? "" : loc.getVar8().toString());	//variable8				
			sb.append(";");
			sb.append(loc.getVar9() == null ? "" : loc.getVar9().toString());	//variable9			
			sb.append(";");
			sb.append(loc.getVar10() == null ? "" : loc.getVar10().toString());	//variable10
			
			map.put(loc.getId(), sb.toString());
		}
		
		return map;
	}
	
	@Override
	public Map<Integer,String> getRI_locations(int currYear){
		logger.info("Processing RI locations...");
		Map<Integer,String> map = new HashMap<Integer,String>();
		for(Location loc : universeservice.selectAllLocations()){
			String line = "" + currYear + strUpdate(loc.getId(), 8) + ";" + String.valueOf(loc.getId()) + ";" + currYear + ";\"" + loc.getName() + "\"";
			map.put(loc.getId(), line);
		}
		return map;
	}
	
	@Override
	public Map<Integer,?> getRI_persons(int currYear){
		logger.info("Processing RI persons...");
		Map<Integer, Map<Integer, AgeBySexGroup>> map = new HashMap<Integer, Map<Integer, AgeBySexGroup>>();
		for(Location loc : universeservice.selectAllLocations()){
			Map<Integer, AgeBySexGroup> locmap = new HashMap<Integer, AgeBySexGroup>();
			map.put(loc.getId(), locmap);
			for(Household h : loc.getHouseholds()){
				for(Person p : h.getPersons()){
					DateTime dBirth = new DateTime(p.getDbirth());
					int age = currYear - dBirth.getYear();
					AgeBySexGroup ageGroup = null;
					if(locmap.containsKey(age)) ageGroup = locmap.get(age);
					else locmap.put(age, ageGroup = new AgeBySexGroup());
					
					if(p.getSex() == RCConstants.MALE) ageGroup.mancount++;
					else if(p.getSex() == RCConstants.FEMALE) ageGroup.womancount++;
				}
			}
		}
		return map;	
	}
	
	@Override
	public Map<Integer,?> getRI_education(){
		logger.info("Processing RI education...");
		Map<Integer, Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();

		for(Location loc : universeservice.selectAllLocations()){
			Map<Integer, Integer> locmap = new HashMap<Integer, Integer>();
			map.put(loc.getId(), locmap);
			for(Household h : loc.getHouseholds()){
				for(Person p : h.getPersons()){
					if(p.getEducation() != null){
						int edu = p.getEducation();
						if(!locmap.containsKey(edu)) locmap.put(edu,1);						
						else {
							locmap.put(edu, locmap.get(edu)+1);
						}
					}
				}
			}
		}
		return map;		
	}
	
	@Override
	public Map<Integer,?> getRI_households(){
		logger.info("Processing RI households...");
		Map<Integer, Map<HouseholdGroup, Integer>> map = new HashMap<Integer, Map<HouseholdGroup, Integer>>();
		
		for(Location loc : universeservice.selectAllLocations()){
			Map<HouseholdGroup, Integer> locmap = new HashMap<HouseholdGroup, Integer>();
			map.put(loc.getId(), locmap);
			for(Household h : loc.getHouseholds()){
				int nr_of_parents = 0;
				int nr_of_children = 0;
				int nr_of_adult_children = 0;
				for(Person p : h.getPersons()){
					if(p.getPosition_in_hh() == null) nr_of_adult_children++;
					else{
						//if(p.getPosition_in_hh() == RCConstants.M_P || p.getPosition_in_hh() == RCConstants.F_P || p.getPosition_in_hh() == RCConstants.HP_P) nr_of_parents++;
						if(p.getPosition_in_hh() == RCConstants.H_Partner) nr_of_parents++;
						else{
							if(p.getPosition_in_hh() == RCConstants.KID) nr_of_children++;
						}
					}
				}
				HouseholdGroup hg = new HouseholdGroup(nr_of_parents, nr_of_children, nr_of_adult_children);
				if(!locmap.containsKey(hg)) locmap.put(hg, 1);
				else{
					int count = locmap.get(hg) + 1;
					locmap.put(hg, count);
				}
			}
		}
		return map;		
	}
	
	@Override
	public Map<Integer,?> getRI_business(){
		logger.info("Processing RI businesses...");
		Map<Integer, Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();

		for(Location loc : universeservice.selectAllLocations()){
			Map<Integer, Integer> locmap = new HashMap<Integer, Integer>();
			map.put(loc.getId(), locmap);
			for(Business b : loc.getBusinesses()){
				int sector = b.getType_1();
				if(!locmap.containsKey(sector)) locmap.put(sector, 1);						
				else {
					locmap.put(sector, locmap.get(sector) + 1);
				}
			}
		}
		return map;	
	}
	
	@Override
	public Map<Integer,?> getRI_workplaces(){
		logger.info("Processing RI workplaces...");
		Map<Integer, Map<Integer, JobGroup>> map = new HashMap<Integer, Map<Integer, JobGroup>>();
		
		for(Location loc : universeservice.selectAllLocations()){
			Map<Integer, JobGroup> locmap = new HashMap<Integer, JobGroup>();
			map.put(loc.getId(), locmap);
			for(Business b : loc.getBusinesses()){
				int sector = b.getType_1();
				JobGroup jg;
				if(locmap.containsKey(sector)) jg = locmap.get(sector);						
				else {
					locmap.put(sector, jg = new JobGroup());
				}
				jg.jobcount += b.getNr_of_jobs();
				jg.freejobcount += b.getPersons().size();
			}
			for(JobGroup jg : locmap.values()){
				jg.freejobcount = jg.jobcount - jg.freejobcount;
			}
		}
		
		return map;	
	}
	
	@Override
	public Map<Integer,?> getRI_bussizes(){
		logger.info("Processing RI businesses sizes...");
		Map<Integer, Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();
		
		for(Location loc : universeservice.selectAllLocations()){
			Map<Integer, Integer> locmap = new HashMap<Integer, Integer>();
			map.put(loc.getId(), locmap);
			for(Business b : loc.getBusinesses()){
				int bussize = b.getNr_of_jobs();
				int bussizegroup = 0;
				if(bussize == 0 ) bussizegroup = 1;	
				if(bussize == 1 ) bussizegroup = 2;
				if(bussize >= 2 &&  bussize <= 4) bussizegroup = 3;	
				if(bussize >= 5 &&  bussize <= 9) bussizegroup = 4;	
				if(bussize >= 10 &&  bussize <= 49) bussizegroup = 5;
				if(bussize >= 50 &&  bussize <= 250) bussizegroup = 6;	
				if(bussize > 250) bussizegroup = 7;	
				if(!locmap.containsKey(bussizegroup)) locmap.put(bussizegroup, 1);						
				else {
					locmap.put(bussizegroup, locmap.get(bussizegroup) + 1);
				}
			}
		}

		return map;		
	}
	
	@Override
	public Map<Integer,?> getRI_workers(){
		logger.info("Processing RI workers...");
		Map<Integer, Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();

		for(Location loc : universeservice.selectAllLocations()){
			Map<Integer, Integer> locmap = new HashMap<Integer, Integer>();
			map.put(loc.getId(), locmap);
			for(Business b : loc.getBusinesses()){
				int sector = b.getType_1();
				int workers = b.getPersons().size();
				if(!locmap.containsKey(sector)) locmap.put(sector, workers);						
				else {
					locmap.put(sector, locmap.get(sector) + workers);
				}
			}
		}
		return map;
	}
	
	@Override
	public Map<Integer,?> getRI_commuting(){
		logger.info("Processing RI commuting...");
		Map<Integer, Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();
				
		for(Location loc : universeservice.selectAllLocations()){
			Map<Integer, Integer> locmap = new HashMap<Integer, Integer>();
			map.put(loc.getId(), locmap);
			for(Household h : loc.getHouseholds()){
				for(Person p : h.getPersons()){
					if(p.getBusinessId() != null ){
						Business b = universeservice.selectBusinessById(p.getBusinessId());
						double dist = up.getDistance(h.getLocationId(), b.getLocationId());
						int distgroup = ((int) (dist / 5.00));
						if(distgroup > 20) distgroup = 20;
						if(locmap.containsKey(distgroup)){
							Integer val = locmap.get(distgroup) + 1;
							locmap.put(distgroup, val);
						}
						else locmap.put(distgroup, 1);
					}
				}
			}
		}
		return map;
	}
	
	@Override
	public Map<Integer,?> getRI_lanusage(int currYear){
		logger.info("Processing RI landusage...");
		Map<Integer, Double[]> map = new HashMap<Integer, Double[]>();
		
		Map<Integer, Map<String, ?>> fa_m2_per_person = null;
		try {
			fa_m2_per_person = loadLandUsageMap(currYear);
		} catch (RCCustomException e) {
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.RUN_INDICATORS_LOADING_ERROR.getValue());
		}
		
		if(fa_m2_per_person != null){
			String column = fa_m2_per_person_column_prefix + String.valueOf(currYear);
			for(Location loc : universeservice.selectAllLocations()){
				Double[] tmparr = new Double[14];
				for(int i = 0; i < 14; i++) tmparr[i] = 0.;
				map.put(loc.getId(), tmparr);
				tmparr[0] = loc.getMaxFloorAreaRes() != null ? loc.getMaxFloorAreaRes() : 0;
				tmparr[1] = loc.getMaxFloorAreaWrk() != null ? loc.getMaxFloorAreaWrk() : 0;
				tmparr[12] = loc.getUsedFloorAreaRes() != null ? loc.getUsedFloorAreaRes() : 0;
				tmparr[13] = loc.getMaxFloorAreaAll() != null ? loc.getMaxFloorAreaAll() : 0;
	
				for(Business b : loc.getBusinesses()){
					int sector = b.getType_1();
					int workers = b.getPersons().size();
					tmparr[1+sector] += workers;
				}
				for(int i = 1; i <= 10; i++){
					Object m2 = fa_m2_per_person.get(i).get(column);
					tmparr[i+1] = tmparr[i+1] * (Double)m2;
				}
			}
		}
		return map;
	}
	
	@Override
	public Map<Integer, Location> getRI_General(int currYear, int refRun)
	{
		Map<Integer, Location> map = new TreeMap<Integer, Location>();
		logger.info("Processing RI General calc...");
		
		Map<Integer, Map<String, ?>> fa_m2_per_person = null;
		try {
			fa_m2_per_person = loadLandUsageMap(currYear);
		} catch (RCCustomException e) {
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.RUN_INDICATORS_LOADING_ERROR.getValue());
		}
		List<Location> loclist = universeservice.selectAllLocations();
		Map<Integer, Business> busmap = new HashMap<Integer, Business>();
		for(Location loc : loclist){
			logger.info("      processing Location: " + String.valueOf(loc.getId()) + " ( " + loc.getName() + " )");
			//Map<Integer, Double> locDistMap = up.getDistanceMap(loc.getId());
			RunIndicatorsRow rirow = new RunIndicatorsRow(loc.getId(), refRun, currYear);
			riman.put(rirow);
			
			 //landlimits
			rirow.setValue(landusage_start_column	  , loc.getMaxFloorAreaRes() != null ? loc.getMaxFloorAreaRes().intValue() : 0);
			rirow.setValue(landusage_start_column + 1 , loc.getMaxFloorAreaWrk() != null ? loc.getMaxFloorAreaWrk().intValue() : 0);
			rirow.setValue(landusage_start_column + 12, loc.getUsedFloorAreaRes() != null ? loc.getUsedFloorAreaRes().intValue() : 0);
			rirow.setValue(landusage_start_column + 13, loc.getUsedFloorAreaWrk() != null ? loc.getUsedFloorAreaWrk().intValue() : 0);
			rirow.setValue(landusage_start_column + 14, loc.getMaxFloorAreaAll() != null ? loc.getMaxFloorAreaAll().intValue() : 0);	
			
			rirow.setValue(location_overlimits    , loc.getMG_overlimit_H() != null ? 1 : 0);
			rirow.setValue(location_overlimits + 1, loc.getMG_overlimit_B() != null ? 1 : 0);
			rirow.setValue(location_overlimits + 2, loc.getLU_overlimit_H() != null ? 1 : 0);
			rirow.setValue(location_overlimits + 3, loc.getLU_overlimit_B() != null ? 1 : 0);
			rirow.setValue(location_overlimits + 4, loc.getLU_overlimit_T() != null ? 1 : 0);

			//businesses
			for(Business b : loc.getBusinesses()){
				busmap.put(b.getId(), b);			//save it for thread usage
				int sector = b.getType_1();
				int comptype = b.getType_2();
				
				int workers = b.getPersons().size();
				int bussize = b.getNr_of_jobs();
				int bussizegroup = 0;
				if(bussize == 0 ) bussizegroup = 1;	
				else if(bussize == 1 ) bussizegroup = 2;
				else if(bussize >= 2 &&  bussize <= 4) bussizegroup = 3;	
				else if(bussize >= 5 &&  bussize <= 9) bussizegroup = 4;	
				else if(bussize >= 10 &&  bussize <= 49) bussizegroup = 5;
				else if(bussize >= 50 &&  bussize <= 250) bussizegroup = 6;	
				else if(bussize > 250) bussizegroup = 7;	
				else  bussizegroup = 1; //maybe exception, but this could not happend
					
				rirow.appendValue(business_start_column + sector -1, 1);  				//businesses
				rirow.appendValue(business_start_column + sector -1 + 10, workers);  	//workers
				rirow.appendValue(businessjobs_start_column + (sector -1)*2, bussize);  				//work places
				rirow.appendValue(businessjobs_start_column + (sector -1)*2+1, bussize - workers);  	//free places
				rirow.appendValue(businesssize_start_column + bussizegroup -1, 1);  					//business size group
				
				rirow.appendValueCompType((sector -1)*3 + (comptype -1), workers);
				rirow.appendValueCompType((sector -1)*3 + (comptype -1) + 30, 1);
			}
			 //landlimits - finalize businesses
			if(fa_m2_per_person != null){
				String column = fa_m2_per_person_column_prefix + String.valueOf(currYear);
				for(int sector = 1; sector <= 10; sector++){
					Object m2 = fa_m2_per_person.get(sector).get(column);
					Double landsector = rirow.getValue(business_start_column + sector -1) * (Double)m2;
					rirow.setValue(landusage_start_column + 1 + sector, landsector.intValue() );
				}
			}
			
			//households
			for(Household h : loc.getHouseholds()){
				int nr_of_parents = 0;
				int nr_of_children = 0;
				int nr_of_adult_children = 0;
				//int nr_of_undef = 0;
				
				rirow.appendValue(households_reloc_types + h.getType_2(), 1);	//hh reloc type
				
				for(Person p : h.getPersons()){
					if(p.getEducation() != null && p.getEducation() > 0){
						int edu_lvl = p.getEducation();
						rirow.appendValue(education_start_column + edu_lvl -1, 1);		//education
					}
						
					DateTime dBirth = new DateTime(p.getDbirth());
					int age = currYear - dBirth.getYear();
					int age_group = age >= 100 ? 26 : age/5;
					age_group = age_group*2;
					if(age >= 18 ) age_group += 2;		
					if(RCConstants.FEMALE.equals( p.getSex() )) age_group++;
					
					rirow.appendValue(resident_start_column + age_group, 1);	//persons

					if(p.getPosition_in_hh() == null) nr_of_adult_children++;
					else{
						//if(p.getPosition_in_hh() == RCConstants.M_P || p.getPosition_in_hh() == RCConstants.F_P || p.getPosition_in_hh() == RCConstants.HP_P) nr_of_parents++;
						if(p.getPosition_in_hh() == RCConstants.H_Partner) nr_of_parents++;
						else{
							if(p.getPosition_in_hh() == RCConstants.KID) nr_of_children++;
							//else nr_of_undef++;
						}
					}
					
					if(p.getDdeath() == null && p.getBusinessId() == null){
						if(age >= fps.getMinimumJobAge() && age <= fps.getMaximumJobAge()){
							rirow.appendValue(unemployedpersons_start_column, 1);
						}
					}
					
					//count residents-workers
					if(p.getDdeath() == null && p.getBusinessId() != null){
						rirow.appendValue(location_res_workers, 1);
					}					
					
				}
				putIntoHHmap(rirow, nr_of_parents, nr_of_children, nr_of_adult_children, h.getPersons().size());		//households
			}
			map.put(loc.getId(), loc );
		}
		
		logger.info("RI - commuting calculation");
		int maxThreadCount = runParameters.getService_maximum_threads() > 0 ? runParameters.getService_maximum_threads() : 4;
		int lSize = loclist.size(); 
		int perThread = lSize / maxThreadCount; 
		int modThread = lSize % maxThreadCount; 
													
		CountDownLatch latch = new CountDownLatch(maxThreadCount); 
																	
		Thread thread = null;
		CommutingThread ricommThread = null;
		List<CommutingThread> threadCache = new ArrayList<>(maxThreadCount);
		
		// create threads
		int current = 0;
		int step = perThread + 1;
		for (int i = 0; i < maxThreadCount; i++) {
			if (step != perThread) {
				if (modThread != 0) {
					modThread--;
				} else {
					step = perThread;
				}
			}
			ricommThread = new CommutingThread(currYear, refRun, commuting_start_column, loclist.subList(current, current + step), up, busmap, riman, latch);
			threadCache.add(ricommThread);
			thread = new Thread(ricommThread);
			thread.start();
			current += step;
		}
		logger.info("RI - commuting threads started");
		
		try { // wait till all threads finish
			latch.await();
		} catch(InterruptedException e) {
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.THREAD_ERROR.getValue());
		}
		
		logger.info("RI - update commuting map from threads");		
		
		for(CommutingThread th : threadCache){
			riman.getCommutersMap().putAll( th.getCommuters() );
		}
		
		logger.info("RI - commuting threads finished");
		
		return map;
	}
	
	private void putIntoHHmap(RunIndicatorsRow rirow, int nr_of_parents, int nr_of_children, int nr_of_adult_children, int hhsize){
		int mapKey = -1;
		
		//2 parents
		if( nr_of_parents == 2 && nr_of_adult_children == 0 && nr_of_children == 0 ){
			mapKey = OrderOfHHcolumnPaar;	
		}
		else if( nr_of_parents == 2 && nr_of_adult_children == 0 && nr_of_children > 0 ){
			mapKey = nr_of_children;
			if(mapKey > 5) mapKey = 6;	
		}
		else if( nr_of_parents == 2 && nr_of_adult_children > 0 && nr_of_children > 0 ){
			mapKey = nr_of_children;
			if(mapKey > 5) mapKey = 6;	
		}
		else if( nr_of_parents == 2 && nr_of_adult_children > 0 && nr_of_children == 0 ){
			mapKey = 0;	
		}
		//1 parents
		else if( nr_of_parents == 1 && nr_of_adult_children == 0 && nr_of_children == 0 ){
			mapKey = OrderOfHHcolumnSingle;	
		}		
		else if( nr_of_parents == 1 && nr_of_adult_children == 0 && nr_of_children > 0 ){
			mapKey = nr_of_children;
			if(mapKey > 5) mapKey = 6;
			mapKey += 7;
		}	
		else if( nr_of_parents == 1 && nr_of_adult_children > 0 && nr_of_children > 0 ){
			mapKey = nr_of_children;
			if(mapKey > 5) mapKey = 6;
			mapKey += 7;
		}
		else if( nr_of_parents == 1 && nr_of_adult_children > 0 && nr_of_children == 0 ){
			mapKey = 7;
		}		
		//0 parents
		else if( nr_of_parents == 0 && nr_of_adult_children > 0 && nr_of_children == 0 ){
			mapKey = OrderOfHHcolumnSingle;	
		}	
		else if( nr_of_parents == 0 && nr_of_adult_children > 0 && nr_of_children > 0 ){
			mapKey = 16;	
		}	
		else if( nr_of_parents == 0 && nr_of_adult_children == 0 && nr_of_children > 0 ){
			mapKey = 17;	
		}			
		else mapKey = OrderOfHHcolumnError;
		
		if(mapKey > -1){
			rirow.appendValue(households_start_column + mapKey, 1);								//households
			rirow.appendValue(households_RES_start_column + mapKey, hhsize);
		}
	}
	
	private Map<Integer, Map<String, ?>> loadLandUsageMap(int currYear) throws RCCustomException{	
		List<String> lsColNames = new ArrayList<>();
		lsColNames.add(fa_m2_per_person_column_name);
		lsColNames.add(fa_m2_per_person_column_prefix + String.valueOf(currYear));

		Map<Integer, Map<String, ?>> fa_m2_per_person = generalDao.loadFieldsIntoAMap(fa_m2_per_person_column_name, "", public_schema_name, fa_m2_per_person_table_name, lsColNames);
				
		return fa_m2_per_person;
	}
	
	private String strUpdate(int num, int count){
		String st = String.valueOf(num);
		if(st.length() < count){
			int rep = count - st.length();
			for(int i = 0; i < rep; i++) st = "0" + st;
		}
		return st;
	}
}

class CommutingThread implements Runnable {
	private int currYear;
	private int currRefRun;
	private int commuting_start_column;
	private List<Location> locs;
	private Map<Integer, Business> busmap;
	private RunIndicatorsManager riman;
	private UtilityParameters up;
	
	private final CountDownLatch latch;
	private final Logger logger = Logger.getLogger(CommutingThread.class);
	
	private Map<Integer,Map<Integer,Integer>> commuters = new HashMap<Integer,Map<Integer,Integer>>();
	
	public Map<Integer, Map<Integer, Integer>> getCommuters() {
		return commuters;
	}

	public CommutingThread(int currYear, int currRefRun, int commuting_start_column, List<Location> locs, UtilityParameters up, Map<Integer, Business> busmap, 
			RunIndicatorsManager riman, CountDownLatch latch) {
		this.currYear = currYear;
		this.currRefRun = currRefRun;
		this.commuting_start_column = commuting_start_column;
		this.latch = latch;
		this.locs = locs;
		this.up = up;
		this.busmap = busmap;
		this.riman = riman;
	}
	
	@Override
	public void run() {
		try {
			for(Location loc : locs){
				Map<Integer,Integer> loccumm = null;
				if(!commuters.containsKey(loc.getId())){
					commuters.put(loc.getId(), loccumm = new HashMap<Integer,Integer>());
				}
				else loccumm = commuters.get(loc.getId());
				
				Map<Integer, Double> locDistMap = up.getDistanceMap(loc.getId());
				RunIndicatorsRow rirow = riman.get(currYear, loc.getId(), currRefRun);
				if(rirow != null){
					for(Household h : loc.getHouseholds()){
						for(Person p : h.getPersons()){
							//commuting
							if(p.getBusinessId() != null ){
								Business b = busmap.get(p.getBusinessId());
								double dist = locDistMap.get(b.getLocationId());
								int distgroup = ((int) (dist / 5.00));
								if(distgroup > 20) distgroup = 20;
								rirow.appendValue(commuting_start_column + distgroup, 1);				//commuting
								//commuters
								if(loc.getId() != b.getLocationId().intValue()){
									if(!loccumm.containsKey(b.getLocationId())){
										loccumm.put(b.getLocationId(),1);
									}
									else{
										int comms = loccumm.get(b.getLocationId()) + 1;
										loccumm.put(b.getLocationId(), comms);
									}
								}
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		latch.countDown();
	}
		
}
