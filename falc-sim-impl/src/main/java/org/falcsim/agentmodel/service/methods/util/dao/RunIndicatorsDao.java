package org.falcsim.agentmodel.service.methods.util.dao;

import java.util.Map;

import org.falcsim.agentmodel.domain.Location;

/**
 * Data extractor class for run indicators
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */
public interface RunIndicatorsDao {
	public Map<Integer,String> getRI_locations_table();
	public Map<Integer,String> getRI_sublocations_table();
	
	public Map<Integer,String> getRI_locations(int currYear);
	
	public Map<Integer,?> getRI_persons(int currYear);
	public Map<Integer,?> getRI_education();
	public Map<Integer,?> getRI_households();
	
	public Map<Integer,?> getRI_business();
	public Map<Integer,?> getRI_workplaces();
	public Map<Integer,?> getRI_bussizes();
	public Map<Integer,?> getRI_workers();
	
	public Map<Integer,?> getRI_commuting();
	
	public Map<Integer,?> getRI_lanusage(int currYear);
	
	public Map<Integer, Location> getRI_General(int currYear, int refRun);
}
