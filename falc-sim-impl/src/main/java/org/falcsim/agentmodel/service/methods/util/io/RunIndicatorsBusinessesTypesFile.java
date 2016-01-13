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

public class RunIndicatorsBusinessesTypesFile extends RunIndicatorsFile {

	public RunIndicatorsBusinessesTypesFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription ){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
	}
	
	@Override
	protected String getFileName() {
		String name = "run_indicators_firm_types_loc";
		if(getExportLevel() == ExportLevel.MUNICIPALITY) name = "run_indicators_firm_types_mun";
		else if(getExportLevel() == ExportLevel.CANTON) name = "run_indicators_firm_types_can";
		
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
		headerline.append("\"E_T\";\"F_T\";");
		headerline.append("\"E_S01_01\";\"E_S01_02\";\"E_S01_03\";\"E_S02_01\";\"E_S02_02\";\"E_S02_03\";\"E_S03_01\";\"E_S03_02\";\"E_S03_03\";");
		headerline.append("\"E_S04_01\";\"E_S04_02\";\"E_S04_03\";\"E_S05_01\";\"E_S05_02\";\"E_S05_03\";\"E_S06_01\";\"E_S06_02\";\"E_S06_03\";");
		headerline.append("\"E_S07_01\";\"E_S07_02\";\"E_S07_03\";\"E_S08_01\";\"E_S08_02\";\"E_S08_03\";\"E_S09_01\";\"E_S09_02\";\"E_S09_03\";");
		headerline.append("\"E_S10_01\";\"E_S10_02\";\"E_S10_03\";");		
		headerline.append("\"F_S01_01\";\"F_S01_02\";\"F_S01_03\";\"F_S02_01\";\"F_S02_02\";\"F_S02_03\";\"F_S03_01\";\"F_S03_02\";\"F_S03_03\";");
		headerline.append("\"F_S04_01\";\"F_S04_02\";\"F_S04_03\";\"F_S05_01\";\"F_S05_02\";\"F_S05_03\";\"F_S06_01\";\"F_S06_02\";\"F_S06_03\";");
		headerline.append("\"F_S07_01\";\"F_S07_02\";\"F_S07_03\";\"F_S08_01\";\"F_S08_02\";\"F_S08_03\";\"F_S09_01\";\"F_S09_02\";\"F_S09_03\";");
		headerline.append("\"F_S10_01\";\"F_S10_02\";\"F_S10_03\";");
		headerline.append("\r\n");
		fw.write(headerline.toString());
	}

	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow,
			Location loc, Municipality municipality, Canton canton)
			throws IOException {
		
		if(rirow != null){			
			StringBuilder sb = new StringBuilder(generateRowBaseData(rirow, loc, municipality, canton));						
			int[] tmpArray = rirow.getCalibrationArray();
			int[] tmpCompTypesArray = rirow.getCompTypesArray();
			
			storeArray(tmpArray, 35, 1, sb); sb.append(";");	//empl_tot
			storeArray(tmpArray, 24, 1, sb); sb.append(";");	//firms+tot
			
			storeArray(tmpCompTypesArray, 0, 60, sb); sb.append(";");	//firm_sizes

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
			double[] tmpCompTypesArray = rirow.getCompTypesArray();
			
			storeArray(tmpArray, 35, 1, sb); sb.append(";");	//empl_tot
			storeArray(tmpArray, 24, 1, sb); sb.append(";");	//firms+tot
			
			storeArray(tmpCompTypesArray, 0, 60, sb); sb.append(";");	//firm_sizes

			sb.append("\r\n");
			if(getWriter() != null) getWriter().write(sb.toString());				
		}
		else{
			if(getWriter() != null) getWriter().write("\r\n");
		}
		
	}

}
