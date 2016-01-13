package org.falcsim.agentmodel.service.methods.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;
import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsManager;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsFile.ExportLevel;

public class RunIndicatorsRunInfoFile extends RunIndicatorsFile {

	private List<String> list;
	
	public RunIndicatorsRunInfoFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription ){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
		
		list = new ArrayList<String>();
		//list.add(RunIndicatorsManager.APPINIT);
		//list.add(RunIndicatorsManager.AV);
		list.add(RunIndicatorsManager.SINIT);
		list.add(RunIndicatorsManager.DCH);
		list.add(RunIndicatorsManager.IMMIGR);
		list.add(RunIndicatorsManager.HHSEP);
		list.add(RunIndicatorsManager.HHFORM);		
		list.add(RunIndicatorsManager.ECDEV);
		list.add(RunIndicatorsManager.FCH);
		list.add(RunIndicatorsManager.QUITEMP);
		list.add(RunIndicatorsManager.JOINEMP);		
		list.add(RunIndicatorsManager.RELOC);
		//list.add(RunIndicatorsManager.SAVEUN);	
	}
	
	@Override
	protected String getFileName() {
		String name = "runinfo";
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
		headerline.append("Values represent time in miliseconds spent by each module and year\r\n");
		headerline.append("\"cycle\";");
		for(String str : list){
			headerline.append("\"" + str + "\";");
		}
		headerline.append("\r\n");				
		fw.write(headerline.toString());
	}

	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton ) throws IOException{
		
	}
	
	@Override
	protected void writeData(FileWriter fw, RunIndicatorsAVGRow rirow,
			Location loc, Municipality municipality, Canton canton)
			throws IOException {
	}
	
	@Override
	public void Export(RunIndicatorsRow rirow, Location loc,
			Municipality municipality, Canton canton) throws IOException {
	}

	@Override
	public void Export(Location loc,
			Municipality municipality, Canton canton) throws IOException {
	}
	
	public void Export(Map<String, Long> map, int cycle) throws IOException {
		StringBuilder sb = new StringBuilder(cycle + ";");
		if(getWriter() != null && map != null){	
			for(String str : list){
				sb.append(map.containsKey(str) ? map.get(str) : ""); //TODO: format number to min:sec,milis. on 1 dec. place
				sb.append(";");
			}
			getWriter().write(sb + "\r\n");
		}
	}


}
