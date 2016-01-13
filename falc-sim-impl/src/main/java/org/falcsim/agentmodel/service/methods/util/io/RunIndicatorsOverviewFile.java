package org.falcsim.agentmodel.service.methods.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsFile.ExportLevel;

public class RunIndicatorsOverviewFile extends RunIndicatorsFile {
	
	public RunIndicatorsOverviewFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
	}
		
	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton ) throws IOException{
		StringBuilder sb = new StringBuilder(generateRowBaseData(rirow, loc, municipality, canton));
		if(rirow != null){			
			int[] tmpArray = rirow.getCalibrationArray();					
			storeArray(tmpArray, 0, 4, sb); sb.append(";");		//res
			storeArray(tmpArray, 35, 11, sb); sb.append(";");	//empl
			
			storeArray(tmpArray, 93, 1, sb); sb.append(";");	
			storeArray(tmpArray, 79, 1, sb); sb.append(";");
			storeArray(tmpArray, 80, 1, sb); sb.append(";");
			
			sb.append(tmpArray[91] + tmpArray[92]); sb.append(";");
			storeArray(tmpArray, 91, 1, sb); sb.append(";");
			storeArray(tmpArray, 92, 1, sb); sb.append(";");
			storeArray(tmpArray, 115, 5, sb);					//overlimits			
			sb.append("\r\n");
			if(fw != null) fw.write(sb.toString());				
		}
	}
	
	@Override
	protected void writeData(FileWriter fw, RunIndicatorsAVGRow rirow,
			Location loc, Municipality municipality, Canton canton)
			throws IOException {
		StringBuilder sb = new StringBuilder(generateRowBaseData(rirow, loc, municipality, canton));
		if(rirow != null){			
			double[] tmpArray = rirow.getCalibrationArray();					
			storeArray(tmpArray, 0, 4, sb); sb.append(";");		//res
			storeArray(tmpArray, 35, 11, sb); sb.append(";");	//empl
			
			storeArray(tmpArray, 93, 1, sb); sb.append(";");	
			storeArray(tmpArray, 79, 1, sb); sb.append(";");
			storeArray(tmpArray, 80, 1, sb); sb.append(";");
			
			sb.append(tmpArray[91] + tmpArray[92]); sb.append(";");
			storeArray(tmpArray, 91, 1, sb); sb.append(";");
			storeArray(tmpArray, 92, 1, sb); sb.append(";");
			storeArray(tmpArray, 115, 5, sb);					//overlimits
			sb.append("\r\n");
			if(fw != null) fw.write(sb.toString());				
		}
		
	}
	
	@Override
	protected String getFileName() {
		String name = "run_indicators_overview_loc";
		if(getExportLevel() == ExportLevel.MUNICIPALITY) name = "run_indicators_overview_mun";
		else if(getExportLevel() == ExportLevel.CANTON) name = "run_indicators_overview_can";
		
		if(!isSingleFile()) name += ("_" + getCurrYear());
		name += ".csv";
		return name;
	}

	@Override
	protected void writeHeader(FileWriter fw) throws IOException {
		StringBuilder headerline = new StringBuilder();
		if(isPrintHeader()){
			headerline.append( getHeadingInfoLines() );
		}
		
		appendHeaderStringBase(headerline );
		appendHeaderStringResTotal(headerline);
		appendHeaderStringEmployees(headerline);
		appendHeaderStringLandLimits(headerline);
		appendHeaderStringOverLimits(headerline);
		headerline.append("\r\n");
		fw.write(headerline.toString());
	}


}
