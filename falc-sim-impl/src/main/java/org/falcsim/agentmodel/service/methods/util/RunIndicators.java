package org.falcsim.agentmodel.service.methods.util;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Generates run indicators for single year 
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */
public interface RunIndicators {
	
	public static enum ExportType {HouseholdRI, BusinessRI}
	
	public String getReferenceSubfolder();
	public void setReferenceSubfolder(String referenceSubfolder);
	
	public void init();
	public File checkExportDir(String subfolder);
	
	public void exportInitState();
	public void export();
	public int exportReferenceRuns_R2analysis();
	public void exportReferenceRunsAVG();
	
	public void exportMovingProgress(Map<Integer, int[]> map, ExportType type);
	public void exportParameters(Map<String, String[]> map, String infoId, boolean markdiff, String desc, String content);
	
	public static Logger logger =  Logger.getLogger(RunIndicators.class);
}
