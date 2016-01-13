package org.falcsim.agentmodel.utility.domain;

import java.text.DecimalFormat;

import org.falcsim.agentmodel.util.StringUtil;

public class RipisIntervalInfo {
	public int id;
	public double lower;
	public double upper;
	
	public RipisIntervalInfo(int id, double lower, double upper){
		this.id = id;
		this.lower = lower;
		this.upper = upper;
	}
	
	@Override
	public String toString() {
		//return StringUtil.fix(id) +	"; " + String.format("%.6g%n", lower) +	"; " + String.format("%.6g%n", upper);
		
		DecimalFormat df = new DecimalFormat("###.#######");
		return StringUtil.fix(id) +	"; " + df.format( (upper - lower) );
	}
}
