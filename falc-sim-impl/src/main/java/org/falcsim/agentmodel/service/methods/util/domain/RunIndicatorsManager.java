package org.falcsim.agentmodel.service.methods.util.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.falcsim.agentmodel.dao.GeneralDao;
import org.falcsim.agentmodel.domain.LocationEventsInfo;
import org.falcsim.agentmodel.domain.RelocationInfo;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manages multiple run indicators (locations, years, reference runs) 
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */
@Component
public class RunIndicatorsManager {

	public static String APPINIT = "Application Initialization";
	public static String AV = "Accessibility vars";
	public static String SINIT = "Service Initialization";
	public static String DCH = "Demographic Change";
	public static String IMMIGR = "Migration";
	public static String HHSEP = "Households separation";
	public static String HHFORM = "Households formation";
	public static String ECDEV = "Economic development";
	public static String FCH = "Firmographics Change";
	public static String QUITEMP = "Quitting employees";
	public static String JOINEMP = "Joining employees";
	public static String RELOC = "Relocation"; 
	public static String SAVEUN = "Save universe"; 
		
	@Autowired
	private GeneralDao generalDao;
	
	@Autowired
	private RunIndicatorsParameters ripar;
	
	private Map<Integer, Map<Integer,List<RunIndicatorsRow>>> indicators; //YEAR, LOCID, LISTof REFERENCE_RUN
	private Map<Integer, Map<Integer, Map<Integer,RelocationInfo> >> relocationIndicators; //YEAR, LOCID, MAP<LocationID, RelocInfo>
	private Map<Integer, Map<Integer,RelocationInfo>> relocationIndicatorsCummYear;
	
	
	private Map<Integer, Map<Integer, Map<String, Long> >> runinfo; //YEAR, REFRUN, MAP<MODULE, RUNTIME>
	private Map<Integer, Map<Integer,LocationEventsInfo>> eventsInfo; //YEAR, MAP<LocationID, LocationEventsInfo>
	
	private Map<Integer,Map<Integer,Integer>> commuters;
	
	public Map<Integer, Map<Integer, Integer>> getCommutersMap() {
		return commuters;
	}

	public RunIndicatorsManager(){
		indicators = new HashMap<Integer, Map<Integer,List<RunIndicatorsRow>>>();
		relocationIndicators = new HashMap<Integer, Map<Integer, Map<Integer,RelocationInfo>> >();
		relocationIndicatorsCummYear = new HashMap<Integer, Map<Integer,RelocationInfo>>();
		runinfo = new HashMap<Integer, Map<Integer, Map<String, Long> >>();
		eventsInfo = new HashMap<Integer, Map<Integer,LocationEventsInfo>>();
		commuters = new HashMap<Integer,Map<Integer,Integer>>();
	}
	
	public void reset(){		
		indicators.clear();
		relocationIndicators.clear();
		relocationIndicatorsCummYear.clear();
		runinfo.clear();	
		eventsInfo.clear();
		commuters.clear();
		//generalDao.truncateTable( RunIndicatorsRow.class );
		//CsvTrasaction.commitAll();
	}
	
	public void resetCycle(){
		relocationIndicatorsCummYear.clear();
		commuters.clear();
	}

	public void put(int refrun, int year, String module, long time){
		Map<Integer, Map<String, Long> > map = null;
		
		if( runinfo.containsKey(year) ) map = runinfo.get(year);
		else runinfo.put(year, map = new HashMap<Integer, Map<String, Long> >() );
		
		if(map != null){
			Map<String, Long> singlemap = null;
			if( map.containsKey(refrun )) singlemap = map.get(refrun);
			else map.put(refrun, singlemap = new HashMap<String, Long>() );
			
			if(singlemap != null){
				Long totaltime = time;
				if( singlemap.containsKey(module)) totaltime += singlemap.get(module); 
				singlemap.put(module, totaltime);
			}
		}	
	}

	public Map<String, Long> GetRunningTimeMap(Integer refrun, Integer year){		
		if(runinfo.containsKey(year)){
			return runinfo.get(year).get(refrun);
		}
		return null;
	}
	
	
	public void put(RunIndicatorsRow rirow){
		Map<Integer,List<RunIndicatorsRow>> map = null;
	
		if( indicators.containsKey(rirow.getYear()) ) 
			map = indicators.get(rirow.getYear());
		else 
			indicators.put(rirow.getYear(), map = new HashMap<Integer,List<RunIndicatorsRow>>() );
		
		if(map != null){
			List<RunIndicatorsRow> list = null;
			if( map.containsKey(rirow.getLocid()) ) 
				list = map.get(rirow.getLocid());
			else 
				map.put(rirow.getLocid(), list = new ArrayList<RunIndicatorsRow>() );
			
			if(list != null){
				list.add(rirow);
			}
		}
	}

	public void append(LocationEventsInfo locEventsInfo, int year, int locid){
		Map<Integer,LocationEventsInfo> yearmap = null;
		if(locEventsInfo != null){
			if( eventsInfo.containsKey(year) ) yearmap = eventsInfo.get( year);
			else eventsInfo.put(year, yearmap = new HashMap<Integer,LocationEventsInfo>() );			
			if(yearmap != null){
				LocationEventsInfo locnfo = null;
				if( yearmap.containsKey(locid) ) locnfo = yearmap.get( locid);
				else yearmap.put(locid, locnfo = new LocationEventsInfo() );
				if(locnfo != null){
					locnfo.Add(locEventsInfo);
				}
			}
		}
	}
	
	public void put(Map<Integer, RelocationInfo> relocmap, int refrun, int year, int locid, String yearFilter, String locationFilter){
		Map<Integer, Map<Integer,RelocationInfo>> yearmap = null;
		if(relocmap != null){
			if( ("".equals(yearFilter) || yearFilter.contains(String.valueOf(year))) && 
				(checkFilter(locid,locationFilter) || checkDestinationSite(relocmap, locationFilter)) ){
				
				if( relocationIndicators.containsKey(year) ) yearmap = relocationIndicators.get( year);
				else relocationIndicators.put(year, yearmap = new HashMap<Integer, Map<Integer,RelocationInfo>>() );
				
				if(yearmap != null){
					Map<Integer,RelocationInfo> locmap = null;
					
					if( yearmap.containsKey( locid ) ) locmap = yearmap.get(locid);
					else yearmap.put(locid, locmap = new HashMap<Integer,RelocationInfo>() );
					
					if(locmap != null){
						for(int destlocid : relocmap.keySet()){
							if("".equals(locationFilter) || locationFilter.contains(String.valueOf(destlocid))) {
								RelocationInfo dr_info = relocmap.get(destlocid);
								RelocationInfo ri_info = null;
								
								if( locmap.containsKey( destlocid ) ) ri_info = locmap.get(destlocid);
								else locmap.put(destlocid, ri_info = new RelocationInfo(10) );
								if(ri_info != null){
									ri_info.Add(dr_info);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private boolean checkFilter(int value, String filter){
		boolean val = true;
		if(filter != null && !"".equals(filter)){
			String[] list = filter.split(",");
			if( list.length > 0 ){
				val = false;			
				for(String str : list){
					try{
						if(Integer.parseInt(str) == value){
							val = true;
							break;
						}
					}
					catch(NumberFormatException e){						
					}
				}
			}					
		}
		return val;	
	}
	
	private boolean checkDestinationSite(Map<Integer, RelocationInfo> relocmap, String locationFilter){
		boolean val = true;
		if(locationFilter != null && !"".equals(locationFilter)){
			val = false;
			for(int toid : relocmap.keySet()){
				if(checkFilter(toid, locationFilter)){
					val = true;
					break;
				}
			}
		}
		return val;
	}
	
	public void appendYear(Map<Integer, RelocationInfo> relocmap, int locid, String locationFilter){
		if(relocmap != null){
			if(checkFilter(locid,locationFilter) || checkDestinationSite(relocmap, locationFilter) ) { 				
				Map<Integer,RelocationInfo> locmap = null;
				
				if( relocationIndicatorsCummYear.containsKey( locid ) ) locmap = relocationIndicatorsCummYear.get(locid);
				else relocationIndicatorsCummYear.put(locid, locmap = new HashMap<Integer,RelocationInfo>() );
				if(locmap != null){	
					for(int destlocid : relocmap.keySet()){
						RelocationInfo dr_info = relocmap.get(destlocid);
						RelocationInfo ri_info = null;
						
						if( locmap.containsKey( destlocid ) ) ri_info = locmap.get(destlocid);
						else locmap.put(destlocid, ri_info = new RelocationInfo(10) );
						if(ri_info != null){
							ri_info.Add(dr_info);
						}
					}
				}
			}
		}
	}	
	
	public Map<String, Long> calculateAVGruninfo(int year){
		Map<String, Long> refmap = new HashMap<String, Long>();
		
		if(runinfo.containsKey(year)){
			Map<Integer, Map<String, Long> > yearmap = runinfo.get(year);

			double refruns = 0;
			for(Integer rr : yearmap.keySet()){
				refruns++;
				Map<String, Long> map = yearmap.get(rr);
				for(String module : map.keySet()){
					Long totaltime = map.get(module);;
					if( refmap.containsKey(module)) totaltime += refmap.get(module);
					refmap.put(module, totaltime);
				}
			}
			for(String module : refmap.keySet()){
				double val = refmap.get(module);
				val = Math.round( val / refruns );
				refmap.put(module, (long)val);
			}
		}
		return refmap;
	}
	
	public Map<Integer, RelocationInfo> calculateSUMrelocationRow(int locId){
		Map<Integer, RelocationInfo> relocmap = new HashMap<Integer, RelocationInfo>();
		
		if(relocationIndicatorsCummYear.containsKey(locId)){
			Map<Integer,RelocationInfo> locmap = relocationIndicatorsCummYear.get(locId);
			for(int destlocid : locmap.keySet()){
				RelocationInfo dr_info = locmap.get(destlocid);	
				
				RelocationInfo ri_info = new RelocationInfo(10);
				ri_info.residents = dr_info.residents;
				ri_info.households = dr_info.households;
				ri_info.workers = dr_info.workers;
				ri_info.businesses = dr_info.businesses;
				ri_info.jobs = dr_info.jobs;
				for(int i = 0; i < 10; i++){
					ri_info.workersbysector[i] = dr_info.workersbysector[i];
					ri_info.businessesbysector[i] = dr_info.businessesbysector[i];
				}
				ri_info.numofRefRuns = dr_info.numofRefRuns;
				relocmap.put(destlocid, ri_info );
			}
		}
		return relocmap;
	}	
	
	public LocationEventsInfo calculateAVGLocationEventsInfo(int year, int locid, int numofruns){
		LocationEventsInfo avg = new LocationEventsInfo();
		Map<Integer,LocationEventsInfo> yearmap = null;
		if( eventsInfo.containsKey(year) ) yearmap = eventsInfo.get( year);			
		if(yearmap != null){
			LocationEventsInfo info = null;
			if( yearmap.containsKey(locid) ) info = yearmap.get( locid);
			if(info != null){
				avg.res_death = (int)Math.round( (double)info.res_death / (double)numofruns);
				avg.res_birth = (int)Math.round((double)info.res_birth / (double)numofruns);
				avg.res_immigraton = (int)Math.round((double)info.res_immigraton / (double)numofruns);
				avg.hh_closed = (int)Math.round((double)info.hh_closed / (double)numofruns);
				avg.hh_kidsLeave = (int)Math.round((double)info.hh_kidsLeave / (double)numofruns);
				avg.hh_divorced = (int)Math.round((double)info.hh_divorced / (double)numofruns);
				avg.hh_merged = (int)Math.round((double)info.hh_merged / (double)numofruns);
				avg.wrk_quited = (int)Math.round((double)info.wrk_quited / (double)numofruns);
				avg.wrk_joined = (int)Math.round((double)info.wrk_joined / (double)numofruns);
				avg.bb_closed = (int)Math.round((double)info.bb_closed / (double)numofruns);
				avg.bb_closed_jobs = (int)Math.round((double)info.bb_closed_jobs / (double)numofruns);
				avg.bb_created = (int)Math.round((double)info.bb_created / (double)numofruns);
				avg.bb_created_jobs = (int)Math.round((double)info.bb_created_jobs / (double)numofruns);
				 
				avg.res_come_in = (int)Math.round((double)info.res_come_in / (double)numofruns);
				avg.wrk_come_in = (int)Math.round((double)info.wrk_come_in / (double)numofruns);
				avg.hh_come_in = (int)Math.round((double)info.hh_come_in / (double)numofruns);
				avg.bb_come_in = (int)Math.round((double)info.bb_come_in / (double)numofruns);
				avg.res_go_out = (int)Math.round((double)info.res_go_out / (double)numofruns);
				avg.wrk_go_out = (int)Math.round((double)info.wrk_go_out / (double)numofruns);
				avg.hh_go_out = (int)Math.round((double)info.hh_go_out / (double)numofruns);
				avg.bb_go_out = (int)Math.round((double)info.bb_go_out / (double)numofruns);
			}
		}
		return avg;
	}	
	
	public Map<Integer, RelocationInfo> calculateAVGrelocationRow(int year, int locId, int numofruns){
		Map<Integer, RelocationInfo> relocmap = new HashMap<Integer, RelocationInfo>();
		
		if(relocationIndicators.containsKey(year)){
			Map<Integer, Map<Integer,RelocationInfo>> yearmap = relocationIndicators.get(year);
			if(yearmap.containsKey(locId)){
				Map<Integer,RelocationInfo> locmap = yearmap.get(locId);
				for(int destlocid : locmap.keySet()){
					RelocationInfo dr_info = locmap.get(destlocid);	
					
					RelocationInfo ri_info = new RelocationInfo(10);
					ri_info.numofRefRuns = numofruns; 
					double tmpnumofruns = 1; //do not divide! because we have too small values for integers
					
					ri_info.residents = (int)Math.round( (double)dr_info.residents / (double)tmpnumofruns );
					ri_info.households = (int)Math.round( (double)dr_info.households / (double)tmpnumofruns );
					ri_info.workers = (int)Math.round( (double)dr_info.workers / (double)tmpnumofruns );
					ri_info.businesses = (int)Math.round( (double)dr_info.businesses / (double)tmpnumofruns );
					ri_info.jobs = (int)Math.round( (double)dr_info.jobs / (double)tmpnumofruns );
					for(int i = 0; i < 10; i++){
						ri_info.workersbysector[i] = (int)Math.round( (double)dr_info.workersbysector[i] / (double)tmpnumofruns );
						ri_info.businessesbysector[i] = (int)Math.round( (double)dr_info.businessesbysector[i] / (double)tmpnumofruns );
					}
					relocmap.put(destlocid, ri_info );
				}
			}
		}
		return relocmap;
	}
	
	
	public boolean containsYear(int year){
		return indicators.containsKey(year);
	}

	public List<RunIndicatorsRow> get(int year, int locid ){
		if( indicators.containsKey(year) ){
			Map<Integer,List<RunIndicatorsRow>> map = indicators.get(year);
			if( map.containsKey(locid) ) return map.get(locid);
		}	
		return null; 
	}
	
	public RunIndicatorsRow get(int year, int locid, int refrun ){
		List<RunIndicatorsRow> list = get(year, locid);
		if(list != null){
			int index = Collections.binarySearch(list, new RunIndicatorsRow( locid, refrun, year ), new RIRowListReferenceRunComparator());
			if(index > -1) return list.get(index);
		}
		return null;
	}	
	
	public RunIndicatorsAVGRow calculateAVGdetailRow(int year, int locId, int numofruns){
		RunIndicatorsAVGRow rirow = new RunIndicatorsAVGRow(locId,0, year);
		
		if(indicators.containsKey(year)){
			List<RunIndicatorsRow> list = indicators.get(year).get(locId);
			int size = list.size();
			for(RunIndicatorsRow row : list){
				for(int i = 0; i < RunIndicatorsRow.numOfColumns; i++ ){
					rirow.setValue(i, rirow.getValue(i) + row.getValue(i) );
				}
				for(int i = 0; i < 60; i++ ){
					rirow.appendValueCompType(i, row.getCompTypesArray()[i]);
				}
			}
			for(int i = 0; i < RunIndicatorsRow.numOfColumns; i++ ){
				rirow.setValue(i, rirow.getValue(i) / size );
			}
			for(int i = 0; i < 60; i++ ){
				rirow.setValueCompType(i, rirow.getCompTypesArray()[i] / size );
			}			
		}
		return rirow;
	}
	
	public Map<Integer, ?> calculateAVG(int year, int numofruns, int startrun){
		Map<Integer, List<RunIndicatorsRow>> map = indicators.get(year);
		int locsize = map.keySet().size();
		int finalnumofruns = numofruns - startrun + 1;
		double[][] runsAVGs = new double[RunIndicatorsRow.numOfCalibrationColumns][finalnumofruns];	//will hold AVG for each run
		double[] alllocavg = new double[RunIndicatorsRow.numOfCalibrationColumns];				//will hold all AVGs
		
		for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns; i++ ){
			alllocavg[i] = 0;
			for(int j = 0; j < finalnumofruns; j++ ){
				runsAVGs[i][j] = 0;
			}
		}
		
		Map<Integer, double[]> locavgmap = new HashMap<Integer, double[]>();		//AVG for each location
		
		//first calculate averages for each attribute
		for(int locid : map.keySet()){
			double[] tmploc = new double[RunIndicatorsRow.numOfCalibrationColumns];
			for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns; i++ )tmploc[i] = 0;
			locavgmap.put(locid, tmploc);
			
			List<RunIndicatorsRow> list = map.get(locid);							//list of all runs for one year, one location
			for(RunIndicatorsRow row : list){
				int[] tmpArray = row.getCalibrationArray();
				for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns; i++ ){
					tmploc[i] += tmpArray[i]; //row.getValue(i);
					
					runsAVGs[i][row.getReferencerun() - startrun] += tmpArray[i];
				}
			}
			for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns; i++ ){
				tmploc[i] = tmploc[i] / list.size();
				alllocavg[i] += tmploc[i];
			}
		}
		for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns; i++ ){
			alllocavg[i] = alllocavg[i] / locsize;								//average of all averages for each RI column
			for(int j = 0; j < finalnumofruns; j++ ){
				runsAVGs[i][j] = runsAVGs[i][j] / locsize;						//average for each ref run for each RI column
			}
		}
		
		//calculate R2 values		
		Map<Integer, double[]> r2map = new TreeMap<Integer, double[]>(); 			//run, attributes
		for(int refrun = 0; refrun < finalnumofruns; refrun++){
			
			double[] tmploc = new double[RunIndicatorsRow.numOfCalibrationColumns+3];				//3 columns for Ranking
			for(int j = 0; j < RunIndicatorsRow.numOfCalibrationColumns+3; j++ )tmploc[j] = 0;
			r2map.put(refrun+startrun, tmploc);
			
			double[] tmploc2 = new double[RunIndicatorsRow.numOfCalibrationColumns];
			for(int j = 0; j < RunIndicatorsRow.numOfCalibrationColumns; j++ )tmploc2[j] = 0;
			
			for(int locid : map.keySet()){
				List<RunIndicatorsRow> list = map.get(locid);	
				RunIndicatorsRow row = list.get(refrun);
				int[] tmpArray = row.getCalibrationArray();
				double[] avgloc = locavgmap.get(locid);
				
				for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns; i++ ){
					double val = tmpArray[i] /*row.getValue(i)*/ - avgloc[i];			//loc_1 - AVG(loc1)
					val = val * val;									//[loc_1 - AVG(loc1)]2
					tmploc[i] += val; 									//SUM
					
					//val = avgloc[i] - alllocavg[i];
					val = tmpArray[i] - runsAVGs[i][refrun]; 	//fixed Balz - use AVG for run instead of all AVG...
					
					val = val * val;									//[loc_1 - AVG(run)]2
					tmploc2[i] += val; 	
				}
			}
			for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns; i++ ){
				double res = 1 - (tmploc2[i] == 0 ? 0 : (tmploc[i] / tmploc2[i]));
				tmploc[i] = res;
			}
		}
		//here add rankings and sum of rankings
		rankColumn(r2map, 0, RunIndicatorsRow.numOfCalibrationColumns, numofruns, startrun);
		rankColumn(r2map, 35, RunIndicatorsRow.numOfCalibrationColumns+1, numofruns, startrun);
		for(int refrun = 0; refrun < finalnumofruns; refrun++){
			double[] tmploc = r2map.get(refrun+startrun);
			tmploc[RunIndicatorsRow.numOfCalibrationColumns+2] = tmploc[RunIndicatorsRow.numOfCalibrationColumns] + tmploc[RunIndicatorsRow.numOfCalibrationColumns+1];
		}
		return r2map;
	}
	
	private void rankColumn(Map<Integer, double[]> r2map, int columnToRank, int destColumn, int numofruns, int startrun){
		List<RefRunColumnValue> list = new ArrayList<RefRunColumnValue>();
		int finalnumofruns = numofruns - startrun + 1;
		for(int refrun = 0; refrun < finalnumofruns; refrun++){
			double[] tmploc = r2map.get(refrun+startrun);
			list.add(new RefRunColumnValue(refrun+startrun, tmploc[columnToRank]));
		}
		Collections.sort(list, new RefRunColumnValueComparator());
		int rank = 1;
		for(RefRunColumnValue rrv : list){
			double[] tmploc = r2map.get(rrv.refrun);
			tmploc[destColumn] = rank++;
		}
	}
}

class RIRowListReferenceRunComparator implements Comparator<RunIndicatorsRow>
{
	public int compare(RunIndicatorsRow a, RunIndicatorsRow b)
	{
	    return (a.getReferencerun().compareTo(b.getReferencerun()));
	}
}

class RefRunColumnValue
{
	public int refrun;
	public double val;
	public RefRunColumnValue(int refrun, double val)
	{
	    this.refrun = refrun;
	    this.val = val;
	}
}

class RefRunColumnValueComparator implements Comparator<RefRunColumnValue>
{
	public int compare(RefRunColumnValue a, RefRunColumnValue b)
	{
	    return Double.compare(a.val, b.val);
	}
}