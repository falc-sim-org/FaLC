package org.falcsim.agentmodel.app.util;

import java.util.Date;

import org.apache.log4j.Logger;

import org.falcsim.enums.ReportType;

/**
 * Sends progress information to GUI
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 * 
 */
public class CommunicationClass {
	private static final Logger _log = Logger.getLogger(CommunicationClass.class);
	private static Long progress;
	
	private static void writeMessage(final String message, final ReportType type){
		_log.info((new Date()).getTime() +  "|" + type.toString() + message);
	}
	
	public static void writeModuleStart(final String moduleName){
		
		 writeModule(moduleName, false);
	}
	
	public static void writeModuleFinish(final String moduleName){
		
		 writeModule(moduleName, true);
	}
	
	public static void writeModule(final String moduleName, boolean finish){
		if(!finish)
			progress = 0L;
		writeMessage(moduleName, (!finish) ? ReportType.START : ReportType.FINISH);
	}
	
	public static void reportProgress(){
		writeMessage(String.valueOf(progress++), ReportType.STAGE);
	}
	
	public static void setMaxProgress(final String progress){
		writeMessage(progress, ReportType.MAX_PROGRESS);
	}
	
	public static void writeSeparator(){
		writeMessage("", ReportType.SEPARATOR);
	}
	
	public static void writeDelimiter(){
		writeMessage("a", ReportType.DELIMITER);
	}
	
	private static void writeRunStats(String text, long millis){
		writeMessage(text.concat("|").concat(String.valueOf(millis)), ReportType.STAT);
	}
	
	public static void writeRunStats(long millis, int runIndex){
		writeRunStats("Run ..." + runIndex + " took ", millis);
	}
	
	public static void writeAverageTimeStats(Double averageRun){
		writeRunStats("average time per run", averageRun.longValue());
	}
	
	public static void writeEstimateForRuns(Long time, Integer runs){
		writeRunStats("estimated total time for ..." + runs + " runs", time);
	}
	
	public static void writeEstimateRemaining(Long time){
		writeRunStats("estimated remaining time", time);
	}
	
	public static void writeEstimateFinish(Date date){
		writeMessage("estimated time to be finished".concat("|").concat(String.valueOf(date.getTime())), ReportType.ETTBF);
	}
	
	
	public static void writeSimpleMessage(String text){
		writeMessage(text, ReportType.GENERAL);
	}
	
	public static void writeUniverseYear(Integer year){
		writeMessage("Universe year " + year,ReportType.U_YEAR);
	}
	
	public static void writeDiffMessage(Long start, Long end){
		writeMessage(String.valueOf(end - start), ReportType.DIFF);
	}
	
	public static void writeRunMessage(Integer runIndex){
		writeMessage("Start run: " + runIndex, ReportType.U_RUN);
	}
}
