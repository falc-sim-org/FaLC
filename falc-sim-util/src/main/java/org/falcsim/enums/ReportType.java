package org.falcsim.enums;

public enum ReportType {
	START("@start "),
	FINISH("@finish "),
	U_RUN("@u_run "),
	U_YEAR("@u_year "),
	STAGE("@stage "),
	MAX_PROGRESS("@max_progress "),
	DIFF("@diff "),
	GENERAL("@general "),
	STAT("@stat "),
	SEPARATOR("@sepa "),
	DELIMITER("@delimiter "),
	ETTBF("@ettbf ");
	
	
	private String value;
	
	ReportType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	
	public static ReportType getType(String value){
		for(ReportType rt : ReportType.values()){
			if(value.startsWith(rt.getValue()))
				return rt;
		}
		return null;
	}
}
