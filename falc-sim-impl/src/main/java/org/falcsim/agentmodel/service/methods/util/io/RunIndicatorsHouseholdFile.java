package org.falcsim.agentmodel.service.methods.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.LocationEventsInfo;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsFile.ExportLevel;

public class RunIndicatorsHouseholdFile extends RunIndicatorsFile {

	public RunIndicatorsHouseholdFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
	}
		
	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton ) throws IOException{
		if(rirow != null){			
			StringBuilder sb = new StringBuilder(generateRowBaseData(rirow, loc, municipality, canton));						
			int[] tmpArray = rirow.getCalibrationArray();
			
			storeArray(tmpArray, 4, 20, sb); sb.append(";");	//hh
			storeArray(tmpArray, 94, 19, sb); sb.append(";");	//hh res
			int r_t = 0;
			for(int i = 94; i < 94+19; i++) r_t += tmpArray[i];
			sb.append( r_t ); sb.append(";");
			storeArray(tmpArray, 120, 5, sb); 					//hh reloc types
			
//			LocationEventsInfo lei = loc.getEventsStatus();
//			if(lei != null){
//				sb.append(";");
//				sb.append( String.valueOf(lei.hh_kidsLeave) );sb.append(";");
//				sb.append( String.valueOf(lei.hh_divorced) );sb.append(";");
//				sb.append( String.valueOf(lei.hh_merged) );sb.append(";");				
//				sb.append( String.valueOf(lei.hh_come_in) );sb.append(";");
//				sb.append( String.valueOf(lei.res_come_in) );sb.append(";");
//				sb.append( String.valueOf(lei.hh_go_out) );sb.append(";");
//				sb.append( String.valueOf(lei.res_go_out) );sb.append(";");	
//			}			
			
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
			
			storeArray(tmpArray, 4, 20, sb); sb.append(";");	//hh
			storeArray(tmpArray, 94, 19, sb); sb.append(";");	//hh res
			double r_t = 0;
			for(int i = 94; i < 94+19; i++) r_t += tmpArray[i];
			sb.append( String.format("%.2f",r_t) ); sb.append(";");
			storeArray(tmpArray, 120, 5, sb); 					//hh reloc types
			
			sb.append("\r\n");
			if(getWriter() != null) getWriter().write(sb.toString());				
		}
		else{
			if(getWriter() != null) getWriter().write("\r\n");
		}	
	}
	
	@Override
	protected String getFileName() {
		String name = "run_indicators_hh_loc";
		if(getExportLevel() == ExportLevel.MUNICIPALITY) name = "run_indicators_hh_mun";
		else if(getExportLevel() == ExportLevel.CANTON) name = "run_indicators_hh_can";
		
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
		appendHeaderStringHouseholds(headerline);
//		headerline.append("\"H_KIDS_L\";\"H_DIVORS\";\"H_MERGED\";\"H_COMEIN\";\"R_COMEIN\";\"H_GOOUT\";\"R_GOOUT\";");
		headerline.append("\r\n");
		fw.write(headerline.toString());
	}


}
