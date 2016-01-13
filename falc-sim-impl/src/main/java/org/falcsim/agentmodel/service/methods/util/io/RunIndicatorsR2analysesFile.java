package org.falcsim.agentmodel.service.methods.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.NotImplementedException;
import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsFile.ExportLevel;

public class RunIndicatorsR2analysesFile extends RunIndicatorsFile {

	public RunIndicatorsR2analysesFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
	}
	
	@Override
	protected String getFileName() {
		return "run_indicators_R2_loc.csv";
	}

	@Override
	protected void writeHeader(FileWriter fw) throws IOException {
		StringBuilder headerline = new StringBuilder();
		headerline.append("\"ID_GEN\";\"R_T\";\"E_T\";\"H_T\";\"F_T\";");	
		headerline.append("\r\n");
		fw.write(headerline.toString());
	}

	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow,
			Location loc, Municipality municipality, Canton canton)		throws IOException {
		if(rirow != null){			
			StringBuilder sb = new StringBuilder( rirow.getRowID() ); sb.append(";");						
			int[] tmpArray = rirow.getCalibrationArray();
			
			storeArray(tmpArray, 0, 1, sb); sb.append(";");		//res_tot
			storeArray(tmpArray, 35, 1, sb); sb.append(";");	//empl_tot
			storeArray(tmpArray, 4, 1, sb); sb.append(";");		//hh_tot
			storeArray(tmpArray, 24, 1, sb); sb.append(";");	//firsms_tot	
			
			sb.append("\r\n");
			if(getWriter() != null) getWriter().write(sb.toString());				
		}
		else{
			if(getWriter() != null) getWriter().write("\r\n");
		}	
	}

	@Override
	protected void writeData(FileWriter fw, RunIndicatorsAVGRow rirow,
			Location loc, Municipality municipality, Canton canton)		throws IOException {
		throw new NotImplementedException();
	}

}
