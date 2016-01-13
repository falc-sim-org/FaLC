package org.falcsim.agentmodel.util.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.csv.GeneralCsv;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.service.domain.RelocationParameters;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.sublocations.domain.LocationSubset;
import org.falcsim.agentmodel.util.StringUtil;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * Implementation does not update locations table directly, only universe loaded in memory
 * 
 * @author regioConcept AG
 * @version 1
 *
 */
@Repository
public class UpdateLocationsDataMemoryImpl implements UpdateLocationsData {

	@Autowired 
	private ServiceParameters sp;
	@Autowired
	private RelocationParameters relocParameters;
	/**
	 * universe functionality
	 */
	@Autowired
	private UniverseServiceUtil universeService;
	
	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private UtilityParameters up;
	@Autowired
	private RunParameters rp;
	
	private boolean updateCounts = true;
	
	private static final String public_schema = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDefaultSchema();
	private static final String schema_delimiter = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getSchemaDelimiter();
	
	private static final String devel2000_2050 = public_schema + schema_delimiter + "location_attributes_development";	
	private static final String location_basedata = public_schema + schema_delimiter + "base_data_synthetic_population";	
	private static final Logger logger = Logger.getLogger(UpdateLocationsDataMemoryImpl.class);
	
	@Override
	public void update(boolean withSubsets) {
		
		logger.info("Updating locations data in loaded universe... ");
		int limit_gt_gmz = relocParameters.getLimitGtGmz();

		for(Location loc : universeService.selectAllLocations() ){
			loc.getRelocationMap().clear();
			loc.getEventsStatus().clear();
			loc.setMG_overlimit_H(null);
			loc.setMG_overlimit_B(null);
			loc.setLU_overlimit_H(null);
			loc.setLU_overlimit_B(null);
			loc.setLU_overlimit_T(null);
		}
		
		int yearSuffix = sp.getCurrentYear() - 2000;
		String suf = ( yearSuffix < 10 ? "0" + yearSuffix : yearSuffix + "");

		String[] columns = new String[]{ 
				"locid", 
				"st_hold_" + suf, 
				"ste_h_" + suf,  
				"st_gh_" + suf, 
				"aansch_" + suf, 
				"bhf_" + suf 
		};		
		String[] columnsEd = new String[]{ 
				"locid", "ED_18_xx_02", "ED_18_xx_03", "ED_18_xx_04", "ED_18_xx_05"
		};		
		
		List<String> cols = Arrays.asList(columns);
		String tableName = generalDao.getTableName(devel2000_2050);
		String condition = "locid in " + StringUtil.packageIds(universeService.selectLocationIdsByCriterion(rp.getApp_systemQuery()));
		
		GeneralCsv csv = new GeneralCsv();
		List<Object[]>  zones = csv.loadTableGeneral(tableName, cols, condition);
		
		tableName = generalDao.getTableName(location_basedata);
		cols = Arrays.asList(columnsEd);
		List<Object[]>  parametersEd = csv.loadTableGeneral(tableName, cols, condition);		
		
		Map<Integer, Object[]> eduMap = new HashMap<Integer, Object[]>();
		for(Object[] objlst : parametersEd){
			Integer locid = (Integer)objlst[0];
			eduMap.put(locid, objlst);
		}		
		
		for(Object[] objlst : zones){
			Integer locid = (Integer)objlst[0];
			Location loc = universeService.selectLocationById(locid);
			Object[] edulist = eduMap.get(locid);
			
			if(updateCounts){
				int pcount = 0;
				for(Household h : loc.getHouseholds()){
					pcount += h.getPersons().size();
				}
				loc.setActualResidents(pcount); //vzto
				
				Set<Integer> distinct_sectors = new HashSet<Integer>();
				pcount = 0;
				for(Business b : loc.getBusinesses()){
					pcount += b.getPersons().size();
					if(!distinct_sectors.contains(b.getType_1())) distinct_sectors.add(b.getType_1());
				}
				loc.setActualWorkers(pcount); //bsch	
				loc.setDiversity_sector(2 * distinct_sectors.size());  //field_10+" = 2*(select count(*) from (select distinct  type_1 from "+generalDao.getBusinessesTableName()+" b where b.location_id=l.locid) as foo ) ";
				
				double diversity_sector_n = ( loc.getDiversity_sector().doubleValue() - 16.908) / 3.061;
				loc.setDiversity_sector_n(diversity_sector_n);
			}
			
			double value = (Integer)objlst[1];
			loc.setTax_holdingcomp_n( (value - 1904.93)/1120.745 ); //st_hg_k_norm = (n.st_hold_+suf-1904.93)/1120.745
			value = (Integer)objlst[2];
			loc.setTax_partnership_n( (value - 107722.421)/15452.995 ); //st_pg_e_norm = (n.ste_h_+suf-107722.421)/15452.995		
			value = (Integer)objlst[3];
			loc.setTax_companies_n( (value - 247198.242)/30923.248 ); //st_kg_g_norm = (n.st_gh_+suf-247198.242)/30923.248	
			
			Integer ivalue = (Integer)objlst[4];		
			loc.setMotorway_access(ivalue); 		//se_aansl =  n.aansch_+suf 
			ivalue = (Integer)objlst[5];		
			loc.setRailway_access(ivalue);			//se_aansl =  n.bhf_+suf 	
			//FIXME -- zero value gives infinity
			if(loc.getSettlement_area() == 0){
				loc.setLanduse_density_n(0D);
			} else {
				loc.setLanduse_density_n( ((double)(loc.getActualResidents() + loc.getActualWorkers())/(loc.getSettlement_area()*100) -0.456 )/0.124  ); //(("+tot_pop+"+"+tot_emp+")/("+field_9+"*100)-0.456)/0.124 ,"
			}
			loc.setAccessibility_t_n( (loc.getAv_1() + loc.getAv_2() + loc.getAv_3() + loc.getAv_4() + 
					loc.getAv_5() + loc.getAv_6() -11929.133)/10971.748 ); //(av_1+av_2+av_3+av_4+av_5+av_6-11929.133)/10971.748 ,"
			
			if(loc.getUrban_centre() == 0){
				loc.setUrban_centre( loc.getActualResidents() + loc.getActualWorkers() < limit_gt_gmz ? 0 : 1 );  //gt_gmz =  CASE WHEN "+tot_pop+"<"+limit_gt_gmz+" THEN 0 ELSE 1 END "
				if( loc.getId() > 10000) loc.setUrban_centre(1);
			}

			Integer ed_XX_01 = (int)Math.round(loc.getActualResidents() * (1 - ( (Double)edulist[1] + (Double)edulist[2] + (Double)edulist[3] + (Double)edulist[4])));
			Integer ed_XX_02 = (int)Math.round(loc.getActualResidents() * (Double)edulist[1]);
			Integer ed_XX_03 = (int)Math.round(loc.getActualResidents() * (Double)edulist[2]);
			Integer ed_XX_04 = (int)Math.round(loc.getActualResidents() * (Double)edulist[3]);
			Integer ed_XX_05 = (int)Math.round(loc.getActualResidents() * (Double)edulist[4]);
			
			double university_degree_n_part1 = ed_XX_01 + ed_XX_02 + ed_XX_03 + ed_XX_04 + ed_XX_05;
			if(university_degree_n_part1 == 0) loc.setUniversity_degree_n(0.);
			else{
				double university_degree_n_part2 = ed_XX_05 / university_degree_n_part1;
				double university_degree_n = (university_degree_n_part2 - 0.055 ) / 0.027;			
				loc.setUniversity_degree_n(university_degree_n);
			}
			
		}
				
		if(withSubsets){
			columns = new String[]{
				"locid", 
				"locsubseth_"+suf,
				"locsubsetb_"+suf
			};
			cols = Arrays.asList(columns);
			tableName = generalDao.getTableName(ClassDescriptor.getClassDescriptor(LocationSubset.class).getTableName());
			condition = "locid in " + StringUtil.packageIds(universeService.selectLocationIdsByCriterion(rp.getApp_systemQuery()));
			
			List<Object[]> locations = csv.loadTableGeneral(tableName, cols, condition);
			
			for(Object[] objlst : locations){
				Integer locid = (Integer)objlst[0];
				Location loc = universeService.selectLocationById(locid);
	
				String svalue = objlst[1].toString();
				loc.setLocationSubsetH(svalue);
				svalue = objlst[2].toString();
				loc.setLocationSubsetB(svalue);
			}
		}
		logger.info("Locations data updated. ");
	}
	
}
