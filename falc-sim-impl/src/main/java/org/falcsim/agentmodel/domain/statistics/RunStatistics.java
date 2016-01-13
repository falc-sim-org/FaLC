package org.falcsim.agentmodel.domain.statistics;

import java.util.Map;
import java.util.TreeMap;

import org.falcsim.agentmodel.domain.Location;

/**
 * Supplementary class run indicators
 * 
 * @author regioConcept AG
 * @version 0.1
 * 
 */
public class RunStatistics {

	private Integer locid;
	
	public Integer getLocid() {
		return locid;
	}

	public RunIndicatorsRow getRirow() {
		return rirow;
	}

	public RunIndicatorsRow getBtrow() {
		return btrow;
	}

	public LocationEventsInfo getEventsStatus() {
		return eventsStatus;
	}

	public Map<Integer, RelocationInfo> getRelocmap() {
		return relocmap;
	}

	private RunIndicatorsRow rirow;
	private RunIndicatorsRow btrow;
	private LocationEventsInfo eventsStatus;
	private Map<Integer, RelocationInfo> relocmap = new TreeMap<Integer, RelocationInfo>();
	
	public RunStatistics(Location loc){
		this.locid = loc.getId();
		rirow = new RunIndicatorsRow(locid, 0, 0, RunIndicatorsRow.numOfStatisticsColumns);
		btrow = new RunIndicatorsRow(locid, 0, 0, RunIndicatorsRow.numOfBusinessTypesColumns);
	}
	
	public void clear(){
		rirow.clear();
		btrow.clear();
		eventsStatus.clear();
		relocmap.clear();
	}
}
