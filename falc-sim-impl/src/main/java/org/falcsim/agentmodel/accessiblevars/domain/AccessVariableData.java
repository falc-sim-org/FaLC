package org.falcsim.agentmodel.accessiblevars.domain;

import java.util.HashMap;
import java.util.Map;

import org.falcsim.agentmodel.distances.domain.DistanceRecord;

/**
 * Accessibility variables generator data class
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */

public class AccessVariableData {
	private long limit;
	private double beta;
	private int[] tii;
	private int[] tiiLimit;
	private Map<Integer, LocationOpportunity> opportunities = new HashMap<Integer, LocationOpportunity>();
	private boolean mathLog = false;
	
	private String tablename;
	private double minvalue;
	private String type;

	public AccessVariableData(long limit, double beta, String tiiIntervals,	boolean mathLog, double minval) {
		this.limit = limit;
		this.beta = beta;
		this.minvalue = minval;
		if(tiiIntervals != null){
			// parse tii limits
			String[] limits = tiiIntervals.split(";");
			tii = new int[limits.length];
			tiiLimit = new int[limits.length];
			for (int i = 0; i < limits.length; i++) {
				String[] values = limits[i].split(":");
				tiiLimit[i] = Integer.parseInt(values[0]);
				tii[i] = Integer.parseInt(values[1]);
			}
		}
		this.mathLog = mathLog;
	}

	/**
	 * get distance limit
	 * 
	 * @return parameter value
	 */
	public long getLimit() {
		return limit;
	}

	/**
	 * get beta constant
	 * 
	 * @return parameter value
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * get Tii parameter by settings
	 * 
	 * @param numResidents
	 * @return travel time
	 */
	public int getTii(long numResidents) {
		int idx = tii.length - 1;
		while (numResidents < tiiLimit[idx]) {
			idx--;
		}
		return tii[idx];
	}

	public LocationOpportunity getOpportunity(Integer idLoc) {
		LocationOpportunity locOpp = null;
		if (opportunities.containsKey(idLoc)) {
			locOpp = opportunities.get(idLoc);
		} else {
			locOpp = new LocationOpportunity(0, 0);
		}
		return locOpp;
	}

	public void addOpportunity(Integer idLoc, long numResidents,
			long numEmployees) {
		opportunities.put(idLoc, new LocationOpportunity(numResidents,
				numEmployees));
	}

	/**
	 * normalize value (simple / log)
	 * 
	 * @param value
	 * @return normalized value
	 */
	public double norm(double value) {
		return mathLog ? Math.log(value) : value;
	}
	
	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}	
	
	public double getMinvalue() {
		return minvalue;
	}

	public void setMinvalue(double minvalue) {
		this.minvalue = minvalue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

