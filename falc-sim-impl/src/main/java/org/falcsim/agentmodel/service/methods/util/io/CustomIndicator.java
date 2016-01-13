package org.falcsim.agentmodel.service.methods.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;
import org.falcsim.enums.AllCalibration;

public class CustomIndicator extends RunIndicatorsFile {

	private String name;
	private List<AllCalibration> selectedIndicators;
		
	public CustomIndicator(int currYear, int currRun, File riDir,
			ExportLevel expLevel, String exportFilter, String run,
			String yearFilter, boolean singleFile, boolean printheader,
			String description, String extraDescription, String name, 
			String selectedIndicators) {

		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter,
				singleFile, printheader, description, extraDescription);
		this.name = name;
		this.setFile(new File(riDir, this.getFileName()));
		
		this.selectedIndicators = new ArrayList<AllCalibration>();
		String[] selected = selectedIndicators.split(",");
		for(String s : selected){
			if(AllCalibration.valueOf(s) != null)
				this.selectedIndicators.add(AllCalibration.valueOf(s));
		}
	}

	@Override
	protected String getFileName() {
		String name = "run_indicators_" + this.name + "_loc";
		if(getExportLevel() == ExportLevel.MUNICIPALITY) 
			name = "run_indicators_" + this.name + "_mun";
		else if(getExportLevel() == ExportLevel.CANTON) 
			name = "run_indicators_" + this.name + "_can";
		
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
		
		StringBuilder custom = new StringBuilder();
		for(AllCalibration ac : this.selectedIndicators){
			custom.append(ac.toString() + ";");
		}
		
		headerline.append(custom);
		
		headerline.append("\r\n");
		fw.write(headerline.toString());
	}

	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow,
			Location loc, Municipality municipality, Canton canton)
			throws IOException {
		StringBuilder sb = new StringBuilder(generateRowBaseData(rirow, loc, municipality, canton));
		if(rirow != null){			
			int[] tmpArray = rirow.getCalibrationArray();
			int[] usedArray = new int[tmpArray.length + 4];
			for(int i = 4; i < usedArray.length; i++){
				usedArray[i] = tmpArray[i - 4]; 
			}
			
			for(int i = 0; i < this.selectedIndicators.size() - 1; i++){
				sb.append(usedArray[this.selectedIndicators.get(i).ordinal()]).append(";");
			}
			int last = this.selectedIndicators.size() - 1;
			sb.append(usedArray[this.selectedIndicators.get(last).ordinal()]);
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
			double[] usedArray = new double[tmpArray.length + 4];
			for(int i = 3; i < usedArray.length; i++){
				usedArray[i] = tmpArray[i - 3]; 
			}
			
			for(int i = 0; i < this.selectedIndicators.size() - 1; i++){
				sb.append(usedArray[this.selectedIndicators.get(i).ordinal()]).append(";");
			}
			int last = this.selectedIndicators.size() - 1;
			sb.append(usedArray[this.selectedIndicators.get(last).ordinal()]);
			
			sb.append("\r\n");
			if(fw != null) fw.write(sb.toString());	
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
