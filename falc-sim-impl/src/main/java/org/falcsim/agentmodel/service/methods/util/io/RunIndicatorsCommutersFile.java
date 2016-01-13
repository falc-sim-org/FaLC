package org.falcsim.agentmodel.service.methods.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.NotImplementedException;
import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RelocationInfo;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsFile.ExportLevel;

public class RunIndicatorsCommutersFile extends RunIndicatorsFile {

	public RunIndicatorsCommutersFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
	}	
	
	@Override
	protected String getFileName() {
		return "run_indicators_commuters_" + String.valueOf(getCurrYear()) + ".csv";
	}

	@Override
	protected void writeHeader(FileWriter fw) throws IOException {
		StringBuilder headerline = new StringBuilder();
		headerline.append("\"FROMLOC\";\"TOLOC\";\"COMMUTERS\";");	
		headerline.append("\r\n");
		fw.write(headerline.toString());
	}

	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow,
			Location loc, Municipality municipality, Canton canton)
			throws IOException {
		throw new NotImplementedException();
	}

	@Override
	protected void writeData(FileWriter fw, RunIndicatorsAVGRow rirow,
			Location loc, Municipality municipality, Canton canton)
			throws IOException {
		throw new NotImplementedException();
	}

	public void writeCommData(Map<Integer,Map<Integer,Integer>> commuters ) throws IOException{
		if(Active()){
			FileWriter fw = getWriter();
			StringBuilder sb = new StringBuilder();	
			Set<Integer> mainset = new TreeSet<Integer>(commuters.keySet());
			for(int fromid : mainset){			
				Map<Integer,Integer> map = commuters.get(fromid);
				Set<Integer> set = new TreeSet<Integer>(map.keySet());			
				for(int toid : set){
					if(checkSiteFilter(fromid) || checkSiteFilter(toid)){ 
						sb.setLength(0);
						int value = map.get(toid);
						sb.append(fromid);sb.append(";");
						sb.append(toid);sb.append(";");
						sb.append(value);
						sb.append("\r\n");
						if( fw != null) getWriter().write(sb.toString());
					}
				}				
			}
		}
	}	
	
}
