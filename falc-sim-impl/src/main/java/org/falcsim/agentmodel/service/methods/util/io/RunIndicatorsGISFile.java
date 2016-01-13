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

public class RunIndicatorsGISFile extends RunIndicatorsFile {

	public RunIndicatorsGISFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
	}
	
	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton ) throws IOException{
		if(rirow != null){			
			StringBuilder sb = new StringBuilder(generateRowBaseData(rirow, loc, municipality, canton));						
			int[] tmpArray = rirow.getCalibrationArray();
			
			storeArray(tmpArray, 0, 1, sb); sb.append(";");		//res_tot
			storeArray(tmpArray, 35, 1, sb); sb.append(";");	//empl_tot
			storeArray(tmpArray, 4, 1, sb); sb.append(";");		//hh_tot
			storeArray(tmpArray, 24, 1, sb); sb.append(";");	//firsms_tot
			storeArray(tmpArray, 36, 10, sb); sb.append(";");	//empl_tot

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
			
			storeArray(tmpArray, 0, 1, sb); sb.append(";");		//res_tot
			storeArray(tmpArray, 35, 1, sb); sb.append(";");	//empl_tot
			storeArray(tmpArray, 4, 1, sb); sb.append(";");		//hh_tot
			storeArray(tmpArray, 24, 1, sb); sb.append(";");	//firsms_tot
			storeArray(tmpArray, 36, 10, sb); sb.append(";");	//empl_tot

			sb.append("\r\n");
			if(getWriter() != null) getWriter().write(sb.toString());				
		}
		else{
			if(getWriter() != null) getWriter().write("\r\n");
		}	
		
	}
	
	@Override
	protected String getFileName() {
		String name = "run_indicators_gis_loc";
		if(getExportLevel() == ExportLevel.MUNICIPALITY) name = "run_indicators_gis_mun";
		else if(getExportLevel() == ExportLevel.CANTON) name = "run_indicators_gis_can";
		
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
		appendHeaderStringGIS(headerline);
		headerline.append("\r\n");
		fw.write(headerline.toString());
		//file was created, create CSVT also
		try{
			File f = new File(getRiDir(), getFileName() + "t");
			FileWriter fwcsvt = new FileWriter(f, false);
			try{
				if(getExportLevel() == ExportLevel.LOCATION){
					fwcsvt.write("Integer,Integer,String,Integer,String,Integer,String,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real\r\n");
				}
				if(getExportLevel() == ExportLevel.MUNICIPALITY){
					fwcsvt.write("Integer,Integer,String,Integer,String,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real,Real\r\n");
				}				
			}
			finally{
				fwcsvt.close();	
			}
		}
		catch(Exception e){
			
		}
	}


}
