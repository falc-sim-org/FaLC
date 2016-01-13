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

public class RunIndicatorsPersonsFile extends RunIndicatorsFile {

	public RunIndicatorsPersonsFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
	}
	
	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton ) throws IOException{
		if(rirow != null){			
			StringBuilder sb = new StringBuilder(generateRowBaseData(rirow, loc, municipality, canton));						
			int[] tmpArray = rirow.getCalibrationArray();
			
			storeArray(tmpArray, 0, 4, sb); sb.append(";");		//res
			sb.append( rirow.toString(0, 44) );	sb.append(";");	//all_res
			storeArray(tmpArray, 35, 11, sb); sb.append(";");	//empl
			storeArray(tmpArray, 113, 1, sb); sb.append(";");	//unempl
			storeArray(tmpArray, 58, 21, sb); sb.append(";");	//comm
			storeArray(tmpArray, 114, 1, sb); sb.append(";");	//res-wrk
			sb.append("\r\n");
			if(getWriter() != null) getWriter().write(sb.toString());				
		}
		else{
			if(getWriter() != null) getWriter().write("\r\n");
		}
	}


	@Override
	protected void writeData(FileWriter fw, RunIndicatorsAVGRow rirow,
			Location loc, Municipality municipality, Canton canton)
			throws IOException {
		if(rirow != null){			
			StringBuilder sb = new StringBuilder(generateRowBaseData(rirow, loc, municipality, canton));						
			double[] tmpArray = rirow.getCalibrationArray();
			
			storeArray(tmpArray, 0, 4, sb); sb.append(";");		//res
			sb.append( rirow.toString(0, 44) );	sb.append(";");	//all_res
			storeArray(tmpArray, 35, 11, sb); sb.append(";");	//empl
			storeArray(tmpArray, 113, 1, sb); sb.append(";");	//unempl
			storeArray(tmpArray, 58, 21, sb); sb.append(";");	//comm
			storeArray(tmpArray, 114, 1, sb); sb.append(";");	//res-wrk
			sb.append("\r\n");
			if(getWriter() != null) getWriter().write(sb.toString());				
		}
		else{
			if(getWriter() != null) getWriter().write("\r\n");
		}
		
	}
	
	@Override
	protected String getFileName() {
		String name = "run_indicators_persons_loc";
		if(getExportLevel() == ExportLevel.MUNICIPALITY) name = "run_indicators_persons_mun";
		else if(getExportLevel() == ExportLevel.CANTON) name = "run_indicators_persons_can";
		
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
		appendHeaderStringResidents(headerline);
		appendHeaderStringEmployees(headerline);
		headerline.append("\"E_UE_T\";");
		appendHeaderStringCommuting(headerline);
		appendHeaderStringRes_Workers(headerline);
		headerline.append("\r\n");
		fw.write(headerline.toString());
	}


}
