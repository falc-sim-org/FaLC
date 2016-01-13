package org.falcsim.agentmodel.statistics;

import org.springframework.stereotype.Component;

@Component
public class StatisticsParameter {
	private String inDirectory;
	private Integer inYear;	
	
	private Integer bestRun;
	private String errorMessage;
	
	private Boolean runAVG;
	private Boolean runMIN;
	private Boolean runMAX;
	private Boolean runMEDIAN;
	private Boolean runPERC;
	private Boolean runR2;
	private Boolean runR2Median;
	private String runPercString;
	
	public StatisticsParameter(){
		this.inDirectory = "";
		this.inYear = 0;
		this.bestRun = -1;
		this.errorMessage = "";
		this.runAVG = false;
		this.runMIN = false;
		this.runMAX = false;
		this.runMEDIAN = false;
		this.runR2 = false;
	}
	
	public String getInDirectory() {
		return inDirectory;
	}
	public void setInDirectory(String inDirectory) {
		this.inDirectory = inDirectory;
	}
	public Integer getBestRun() {
		return bestRun;
	}
	public void setBestRun(Integer bestRun) {
		this.bestRun = bestRun;
	}
	public Integer getInYear() {
		return inYear;
	}
	public void setInYear(Integer inYear) {
		this.inYear = inYear;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public Boolean getRunAVG() {
		return runAVG;
	}

	public void setRunAVG(Boolean runAVG) {
		this.runAVG = runAVG;
	}

	public Boolean getRunMIN() {
		return runMIN;
	}

	public void setRunMIN(Boolean runMIN) {
		this.runMIN = runMIN;
	}

	public Boolean getRunMAX() {
		return runMAX;
	}

	public void setRunMAX(Boolean runMAX) {
		this.runMAX = runMAX;
	}

	public Boolean getRunMEDIAN() {
		return runMEDIAN;
	}

	public void setRunMEDIAN(Boolean runMEDIAN) {
		this.runMEDIAN = runMEDIAN;
	}

	public Boolean getRunR2() {
		return runR2;
	}

	public void setRunR2(Boolean runR2) {
		this.runR2 = runR2;
	}
	
	public Boolean getRunPERC() {
		return runPERC;
	}

	public void setRunPERC(Boolean runPERC) {
		this.runPERC = runPERC;
	}

	public Boolean getRunR2Median() {
		return runR2Median;
	}

	public void setRunR2Median(Boolean runR2Median) {
		this.runR2Median = runR2Median;
	}
	
	public String getRunPercString() {
		return runPercString;
	}

	public void setRunPercString(String runPercString) {
		this.runPercString = runPercString;
	}	
}
