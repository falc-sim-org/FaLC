package org.falcsim.agentmodel.domain.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.csv.GeneralCsv;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Universe;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.util.StringUtil;
import org.falcsim.agentmodel.util.dao.UpdateLocationsData;
import org.falcsim.agentmodel.utility.landusage.LandUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Fills locations attributes not saved in population table
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
@Repository
public class UniverseServiceUtilities {

	@Autowired
	private UniverseServiceUtil universeService;	
	@Autowired
	private GeneralDao generalDao;	
	@Autowired
	private RunParameters rp;
	@Autowired
	private LandUsage landLimits;
	@Autowired
	private UpdateLocationsData uld;
	
	private static final String public_schema = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDefaultSchema();
	private static final String schema_delimiter = 
			AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getSchemaDelimiter();
	
	private static final String location_attributes = public_schema + schema_delimiter + "location_attributes";
	private static final String location_basedata = public_schema + schema_delimiter + "base_data_synthetic_population";	
	
	private static Logger logger =  Logger.getLogger(UniverseServiceUtilities.class);
	
	public void updateLocationsTableInUniverse(Universe universe){
				
		List<Integer> list = universeService.selectLocationIdsByCriterion(rp.getApp_systemQuery());
		if(list.size() > 0){		
			
			//fill universe after load		
			String[] columns = new String[]{ 
							"locid", "denot", "municipality", "subarea_nr",
							"sales_FA", "sales_FA_centr", "Area_tot", "Area_settl", "Area_cultivable",
							"Rent_4flat", "LP_res",	"LP_mix", "LP_ind",	"Bus_dev_index", 							
							"urban_centre",	"loc_type", "var1", "var2", "var3", "var4", "var5", "var6", "var7", "var8", "var9", "var10"
					};
			String[] columnsEd = new String[]{ 
					"locid", "ED_18_xx_02", "ED_18_xx_03", "ED_18_xx_04", "ED_18_xx_05"
			};		
			
			List<String> cols = Arrays.asList(columns);
			String tableName = generalDao.getTableName(location_attributes);
			String condition = "locid in " + StringUtil.packageIds(list);	
			
			GeneralCsv csv = new GeneralCsv();
			List<Object[]>  parameters = csv.loadTableGeneral(tableName, cols, condition);		
			
			tableName = generalDao.getTableName(location_basedata);
			cols = Arrays.asList(columnsEd);
			List<Object[]>  parametersEd = csv.loadTableGeneral(tableName, cols, condition);	
			
			fillLocationsParameters(universe.getLocations(), parameters, list, parametersEd);	
					
			//fill yearly data, universe should be set to first year
			//this will fill RES + WRK also
			uld.update(false);
			
			//land usage
			try{
				logger.info("Initial calculation of land usage ...");
				landLimits.init(universe.getLocations(), true);
			}
			catch(Exception e){
				logger.error(e.getMessage(), e);
				System.exit(-1);
			}
			finally{
				landLimits.reset();
			}
		}
		//accessibility variables...,maybe
	}
	
	private void fillLocationsParameters(List<Location> locs, List<Object[]>  parameters, List<Integer> list, List<Object[]>  eduParameters){
		
		Map<Integer, Object[]> eduMap = new HashMap<Integer, Object[]>();
		for(Object[] objlst : eduParameters){
			Integer locid = (Integer)objlst[0];
			eduMap.put(locid, objlst);
		}
		
		for(Object[] objlst : parameters){
			Integer locid = (Integer)objlst[0];
			Location loc = universeService.selectLocationById(locid);
			list.remove(locid);
			Object[] edulist = eduMap.get(locid);
			
			loc.setAv_1(0.);
			loc.setAv_2(0.);
			loc.setAv_3(0.);
			loc.setAv_4(0.);
			loc.setAv_5(0.);
			loc.setAv_6(0.);
			loc.setAv_7(0.);
			loc.setAv_8(0.);
			loc.setAv_9(0.);
			loc.setAv_10(0.);
			loc.setAv_11(0.);
			loc.setAv_12(0.);		
			
			loc.setMunicipalityId( (Integer)objlst[2] );
			loc.setLocationId( (Integer)objlst[3] );
			loc.setUrban_centre( (Integer)objlst[14] );
			loc.setLandtype( (Integer)objlst[15] );
			//loc.setMotorway_access( (Integer)objlst[21] );
			
			loc.setVar1((Double)objlst[16]);
			loc.setVar2((Double)objlst[17]);
			loc.setVar3((Double)objlst[18]);
			loc.setVar4((Double)objlst[19]);
			loc.setVar5((Double)objlst[20]);
			loc.setVar6((Double)objlst[21]);
			loc.setVar7((Double)objlst[22]);
			loc.setVar8((Double)objlst[23]);
			loc.setVar9((Double)objlst[24]);
			loc.setVar10((Double)objlst[25]);		
			
			Object bus_dev_cant_n = objlst[13];
			Object flat_rent = objlst[9];
			Object fl_sdl = objlst[7];
			
			Integer lp_wwe_00 = (Integer)objlst[10];
			double resident_landprice_n = ( lp_wwe_00.doubleValue() - 302.022) / 118.727;
					
			loc.setBus_dev_cant_n((Double)bus_dev_cant_n);
			loc.setFlat_rent((Integer)flat_rent);	
			loc.setSettlement_area((Integer)fl_sdl);			
			loc.setResident_landprice_n(resident_landprice_n);
			
			int pcount = 0;
			for(Household h : loc.getHouseholds()){
				pcount += h.getPersons().size();
			}
			loc.setActualResidents(pcount);			
			
			Integer ed_XX_01 = (int)Math.round(pcount * (1 - ( (Double)edulist[1] + (Double)edulist[2] + (Double)edulist[3] + (Double)edulist[4])));
			Integer ed_XX_02 = (int)Math.round(pcount * (Double)edulist[1]);
			Integer ed_XX_03 = (int)Math.round(pcount * (Double)edulist[2]);
			Integer ed_XX_04 = (int)Math.round(pcount * (Double)edulist[3]);
			Integer ed_XX_05 = (int)Math.round(pcount * (Double)edulist[4]);
			
			double university_degree_n_part1 = ed_XX_01 + ed_XX_02 + ed_XX_03 + ed_XX_04 + ed_XX_05;
			if(university_degree_n_part1 == 0) loc.setUniversity_degree_n(0.);
			else{
				double university_degree_n_part2 = ed_XX_05 / university_degree_n_part1;
				double university_degree_n = (university_degree_n_part2 - 0.055 ) / 0.027;			
				loc.setUniversity_degree_n(university_degree_n);
			}							
		}	

	}	
	
}
