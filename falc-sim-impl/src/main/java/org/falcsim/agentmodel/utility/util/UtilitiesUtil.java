package org.falcsim.agentmodel.utility.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.csv.GeneralCsv;
import org.falcsim.agentmodel.dao.jdbc.DynamicEntityTableMapper;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Utility method to manipulate strings for the utility module
 * 
* @author regioConcept AG
* @version 0.5
 *
 */

@Component
public class UtilitiesUtil {
	

	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private DynamicEntityTableMapper tableMapper;
	
	private static final String SEP = RCConstants.SEP;
	private static final String tot_emp= "bsch";
	
	
	/** 
	 * 
	 * @param codeString
	 * @return 1 for Alt_1, 0 for alt_x
	 */
	public Integer extractCode(String codeString){
		Integer codeInt = 0;
		String code = codeString.split("_")[1];
		try {
			codeInt = new Integer(code);
		} catch (Exception e){}
		return codeInt;
		
	}
	
	/**The method loads the normalized number of employees by sector  divided by the total number of employees into a map.
	 * This quantity replaces the  the parameter bs_glbr_q in the utility query
	 * The normalization values are taken from from 03a BIOGEME Beschrieb HR-138.csv
	 */
	//FIXME:not used method
	public Map<String, Double> loadSameSectorEmployeesMap(List <Location> locs){
		
		double norm_val_sub=-0.061;
		double norm_val_div = 0.043;
		double checkZeroDivide = 0.0001;
		String personsTable = generalDao.getPersonsTableName();
		String businessesTable = generalDao.getBusinessesTableName();
		String locationssTable = generalDao.getLocationsTableName();
		String locsIdString = " and  foo.location_id in "+StringUtil.packageLocsString(locs);
		String selectSameSectorEmployees = "select  foo.location_id, foo.type_1, l."+tot_emp+" , count(*) from "+personsTable+" p,"
				+businessesTable+"  b, "+locationssTable+" l ,(select distinct on (location_id, type_1) location_id, type_1 " 
				+"from "+businessesTable+" b) as foo where p.business_id=b.business_id and b.type_1=foo.type_1 and b.location_id=foo.location_id and foo.location_id=l.locid"
				+locsIdString
				+"group by foo.location_id, foo.type_1, l."+tot_emp;
		Map<String, Double> map = new HashMap<>();
		List<Object[]> lo = generalDao.executeQuery2(selectSameSectorEmployees);
		for (Object[] o : lo){
			double d =  (((Long) o[3]).doubleValue()/(((Integer) o[2]).doubleValue()+checkZeroDivide));
			map.put(o[0].toString()+SEP+o[1].toString(), (d-norm_val_sub)/norm_val_div);
		}
		
		return map;
	}

	//FIXME:not used method
	public Map<Integer, Map<String, Double>> loadRelocationProbData(List <Location> locs, List<String> columns){
		String locationssTable = generalDao.getLocationsTableName();
		String locsIdString = " where locid in " + StringUtil.packageLocsString(locs);
		
		StringBuilder getLocationInfoValues = new StringBuilder("select locid");
		for(String col : columns) getLocationInfoValues.append(", " + col);
		getLocationInfoValues.append("  from " + locationssTable);
		getLocationInfoValues.append(locsIdString);
		
		Map<Integer, Map<String, Double>> map = new HashMap<Integer, Map<String, Double>>();
		List<Object[]> lo = generalDao.executeQuery2(getLocationInfoValues.toString());
		for (Object[] o : lo){
			Integer locid = (Integer)o[0];
			
			Map<String, Double> list = new HashMap<String, Double>();
			map.put(locid,list);
			
			int i = 1;
			for(String col : columns){
				list.put(col, ((Number)o[i++]).doubleValue());
			}
		}
		return map;
	}
	
	//FIXME:not used method
	public Map<Integer, List<Double>> loadHHrelocationProbData(List <Location> locs){
		String locationssTable = generalDao.getLocationsTableName();
		String locsIdString = " where locid in " + StringUtil.packageLocsString(locs);
		
		String getLocationInfoValues = "select locid, (CASE WHEN vzto+bsch = 0 THEN 0 ELSE ln(vzto+bsch) END) n1, (-5.501)*(mia_4) n2, (0.41)*ln(av_3+av_4) + (0.1)*ln(av_1+av_2) + (-0.015)*(vzto+bsch)/(fl_sdl*100) as n3  from " + locationssTable;
		Map<Integer, List<Double>> map = new TreeMap<Integer, List<Double>>();
		List<Object[]> lo = generalDao.executeQuery2(getLocationInfoValues + locsIdString);
		for (Object[] o : lo){
			Integer locid = (Integer)o[0];
			List<Double> list = new ArrayList<Double>();
			map.put(locid,list);
			list.add(((Number)o[1]).doubleValue());
			list.add(((Number)o[2]).doubleValue());
			list.add(((Number)o[3]).doubleValue());
		}
		return map;
	}

	//FIXME:not used method
	public Map<Integer, List<Double>> loadBBrelocationProbData(List <Location> locs){
		String locationssTable = generalDao.getLocationsTableName();
		String locsIdString = " where locid in " + StringUtil.packageLocsString(locs);
		
		String getLocationInfoValues = "select locid, (-0.014)*lp_wohn_norm +(-0.025)*bz_totnd_norm +(0.056)*wb_hsabs_q_norm +(0.060)*bst_dbr_norm + (0.049)*se_aansl +(0.061)*se_bahn " +
				" +(0.033)*se_ac_at_norm     +(-0.021)*se_wfr_vf_norm     +(0.119)*se_wfk_norm +(0.194)*gt_gmz as util,st_hg_k_norm, st_pg_e_norm, st_kg_g_norm   from " + locationssTable;
		Map<Integer, List<Double>> map = new TreeMap<Integer, List<Double>>();
		List<Object[]> lo = generalDao.executeQuery2(getLocationInfoValues + locsIdString);
		for (Object[] o : lo){
			Integer locid = (Integer)o[0];
			List<Double> list = new ArrayList<Double>();
			map.put(locid,list);
			list.add(((Number)o[1]).doubleValue());
			list.add(((Number)o[2]).doubleValue());
			list.add(((Number)o[3]).doubleValue());
			list.add(((Number)o[4]).doubleValue());
		}
		return map;
	}
	
	public Map<Integer, Map<Integer, Double>> loadBuildingZoneRegulationData(List <Location> locs) throws RCCustomException{
		String defaultSchema = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getDefaultSchema();
		String schemaDelimiter = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getSchemaDelimiter();
		
		String locsIdString = "locid in " + StringUtil.packageLocsString(locs);
		GeneralCsv csv = new GeneralCsv();
		
		
		Map<Integer, Map<Integer, Double>> map = new HashMap<Integer, Map<Integer, Double>>();
		List<Object[]> lo = csv.loadTableGeneral(
				tableMapper.getEntityTable(null, defaultSchema + schemaDelimiter + "landusage_building_zone_regulation"), 
				Arrays.asList(new String[]{"locid", "code", "surface_m2"}), 
				locsIdString
			);
		for (Object[] o : lo){
			int locId = (Integer)o[0];
			int code = (Integer)o[1];
			double m2 = ((Number)o[2]).doubleValue();
			
			Map<Integer, Double> onelocmap = null;
			if(!map.containsKey(locId) ){
				onelocmap = new HashMap<Integer, Double>();
				map.put(locId, onelocmap);
			}
			else onelocmap = map.get(locId);
			
			if(!onelocmap.containsKey(code) ){
				onelocmap.put(code, m2 );
			}
			else{
				throw new RCCustomException("Loading building_zone_regulation_table: duplicated locid + code");
			}
		}
		return map;
	}
}
