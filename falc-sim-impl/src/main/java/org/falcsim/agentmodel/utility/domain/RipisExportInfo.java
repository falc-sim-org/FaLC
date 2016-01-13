package org.falcsim.agentmodel.utility.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;
import org.springframework.stereotype.Component;


/** 
* A probability interval, export info
* @author regioConcept AG
* @version 1.0
* 
*/

@Component
public class RipisExportInfo {

	
	public Map<Integer, List<RipisEntityExportInfo>> exportmapH;
	public Map<Integer, List<RipisEntityExportInfo>> exportmapB;
	
	public RipisExportInfo(){
		exportmapH = new HashMap<Integer, List<RipisEntityExportInfo>>();
		exportmapB = new HashMap<Integer, List<RipisEntityExportInfo>>();
	}
	
	public void add(RipisType ripistype, int sourcelocid, int id, int sector, int type, int destlocid, List<RealIdProbInterval> ripis){
		List<RipisEntityExportInfo> list = null;
		if(ripistype == RipisType.HouseholdRipis){
			if(exportmapH.containsKey(sourcelocid)) list = exportmapH.get(sourcelocid);
			else{
				list = new ArrayList<RipisEntityExportInfo>();
				exportmapH.put(sourcelocid, list);
			}
		}
		else if(ripistype == RipisType.BusinessRipis){
			if(exportmapB.containsKey(sourcelocid)) list = exportmapB.get(sourcelocid);
			else{
				list = new ArrayList<RipisEntityExportInfo>();
				exportmapB.put(sourcelocid, list);
			}
		}
		if(list != null) list.add(new RipisEntityExportInfo(id, sector, type, destlocid, ripis) );
	}
	
	@Override
	public String toString() {
		return "";
	}
	
	public void Clear(){
		for(int key : exportmapH.keySet()){
			List<RipisEntityExportInfo> list = exportmapH.get(key);
			list.clear();
		}
		exportmapH.clear();
		for(int key : exportmapB.keySet()){
			List<RipisEntityExportInfo> list = exportmapB.get(key);
			list.clear();
		}
		exportmapB.clear();
	}
	
	public SortedSet<Integer> GetSortedKeys(RipisType ripistype){
		SortedSet<Integer> keys = null;
		if(ripistype == RipisType.HouseholdRipis) keys = new TreeSet<Integer>(exportmapH.keySet());	
		else if(ripistype == RipisType.BusinessRipis) keys = new TreeSet<Integer>(exportmapB.keySet());	
		return keys;
	}
	
	public void SortLocationList(int locid){
		if(locid > 0){
			if(exportmapH.containsKey(locid)){
				List<RipisEntityExportInfo> list = exportmapH.get(locid);
				Collections.sort(list, new RipisExportInfoComparator() );
			}
			List<RipisEntityExportInfo> list = null;
			if(exportmapB.containsKey(locid)){
				list = exportmapB.get(locid);
				Collections.sort(list, new RipisExportInfoComparator() );
			}
		}
		else{
			for(int key : exportmapH.keySet()){
				List<RipisEntityExportInfo> list = exportmapH.get(key);
				Collections.sort(list, new RipisExportInfoComparator() );
			}
			for(int key : exportmapB.keySet()){
				List<RipisEntityExportInfo> list = exportmapB.get(key);
				Collections.sort(list, new RipisExportInfoComparator() );
			}
		}
	}
	
	public static void mergeRipisExportInfo(RipisType ripistype, RipisExportInfo merged, RipisExportInfo source){
		if(ripistype == RipisType.HouseholdRipis){
			Map<Integer, List<RipisEntityExportInfo>> exportmap = merged.exportmapH;
			for(int key : source.exportmapH.keySet()){
				List<RipisEntityExportInfo> list = source.exportmapH.get(key);
				
				List<RipisEntityExportInfo> mergedlist = null;
				if(exportmap.containsKey(key)) mergedlist = exportmap.get(key);
				else{
					mergedlist = new ArrayList<RipisEntityExportInfo>();
					exportmap.put(key, mergedlist);
				}
				mergedlist.addAll(list);
			}
		}
		else if(ripistype == RipisType.BusinessRipis){
			Map<Integer, List<RipisEntityExportInfo>> exportmap = merged.exportmapB;
			for(int key : source.exportmapB.keySet()){
				List<RipisEntityExportInfo> list = source.exportmapB.get(key);
				
				List<RipisEntityExportInfo> mergedlist = null;
				if(exportmap.containsKey(key)) mergedlist = exportmap.get(key);
				else{
					mergedlist = new ArrayList<RipisEntityExportInfo>();
					exportmap.put(key, mergedlist);
				}
				mergedlist.addAll(list);
			}
		}
	}
	
}