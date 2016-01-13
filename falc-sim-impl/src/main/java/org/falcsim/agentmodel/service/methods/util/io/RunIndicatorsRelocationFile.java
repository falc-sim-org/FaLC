package org.falcsim.agentmodel.service.methods.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RelocationInfo;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;

public class RunIndicatorsRelocationFile extends RunIndicatorsFile {

	private Map<Integer, Map<Integer,RelocationInfo>> relocmap = new TreeMap<Integer, Map<Integer,RelocationInfo>>();
	private Map<Integer, Location> maploc;
	private boolean cumulated;
	
	public RunIndicatorsRelocationFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription, 
			Map<Integer, Location> maploc, boolean cumulated){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
		this.cumulated = cumulated;
	}
	
	@Override
	protected String generateRowBaseData(RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton ){
		return "";
	}
	
	protected void writeData(FileWriter fw, int id, Map<Integer, RelocationInfo> relocmap ) throws IOException{
		StringBuilder sb = new StringBuilder();	
		for(int toid : relocmap.keySet()){
			sb.setLength(0);
			if(checkSiteFilter(id) || checkSiteFilter(toid) ){
				RelocationInfo rinfo = relocmap.get(toid);
	
				//if(isSingleFile()){ }
				sb.append( getCurrYear() ); sb.append(";");
				
				sb.append(id);sb.append(";");
				sb.append(toid);sb.append(";");
				
				if( rinfo.numofRefRuns > 0 ){
					sb.append(String.format("%.2f", (double)rinfo.residents/(double)rinfo.numofRefRuns) );sb.append(";");
					sb.append(String.format("%.2f", (double)rinfo.households/(double)rinfo.numofRefRuns) );sb.append(";");
					
					sb.append(String.format("%.2f", (double)rinfo.workers/(double)rinfo.numofRefRuns) );sb.append(";");
					sb.append(String.format("%.2f", (double)rinfo.businesses/(double)rinfo.numofRefRuns) );sb.append(";");
					sb.append(rinfo.jobs);sb.append(";");
					for(int i = 0; i < 10; i++){
						sb.append(String.format("%.2f", (double)rinfo.workersbysector[i]/(double)rinfo.numofRefRuns) );sb.append(";");
					}
					for(int i = 0; i < 10; i++){
						sb.append(String.format("%.2f", (double)rinfo.businessesbysector[i]/(double)rinfo.numofRefRuns) );sb.append(";");
					}						
					
				}
				else{
					sb.append(rinfo.residents);sb.append(";");
					sb.append(rinfo.households);sb.append(";");
					
					sb.append(rinfo.workers);sb.append(";");
					sb.append(rinfo.businesses);sb.append(";");
					sb.append(rinfo.jobs);sb.append(";");
					for(int i = 0; i < 10; i++){
						sb.append(rinfo.workersbysector[i]);sb.append(";");
					}
					for(int i = 0; i < 10; i++){
						sb.append(rinfo.businessesbysector[i]);sb.append(";");
					}		
				}		
				sb.append("\r\n");
				if( fw != null) getWriter().write(sb.toString());
			}
		}
	}
	
	private boolean checkDestinationSite(Location loc){
		boolean val = true;
		if(getExportFilter() != null && !"".equals(getExportFilter())){
			val = false;
			Map<Integer,RelocationInfo> relocmap = loc.getRelocationMap();
			for(int toid : relocmap.keySet()){
				if(checkSiteFilter(toid)){
					val = true;
					break;
				}
			}
		}
		return val;
	}
	
	@Override
	protected void writeData(FileWriter fw, RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton ) throws IOException{
	
		Map<Integer,RelocationInfo> relocmap = loc.getRelocationMap();
		writeData(fw, loc.getId(), relocmap);
	}
	@Override
	protected void writeData(FileWriter fw, RunIndicatorsAVGRow rirow,
			Location loc, Municipality municipality, Canton canton)
			throws IOException {
		Map<Integer,RelocationInfo> relocmap = loc.getRelocationMap();
		writeData(fw, loc.getId(), relocmap);
	}
	
	@Override
	public void Export(RunIndicatorsRow rirow, Location loc,
			Municipality municipality, Canton canton) throws IOException {

		if(getExportLevel() == ExportLevel.LOCATION ){	
			if(checkSiteFilter(loc.getId()) || checkDestinationSite(loc) ) {
				writeData(getWriter(), rirow, loc, municipality, canton);				
			}
		}
		else{
			Map<Integer,RelocationInfo> relocmap = loc.getRelocationMap();
			if(getExportLevel() == ExportLevel.MUNICIPALITY){
				if(checkSiteFilter(municipality.getId()))  AddIndicators(municipality.getId(), relocmap);
			}
			if(getExportLevel() == ExportLevel.CANTON ){
				if(checkSiteFilter(canton.getLocationId())) AddIndicators(canton.getLocationId(), relocmap);
			}				
		}
	}

	@Override
	public void Export(RunIndicatorsAVGRow rirow, Location loc,
			Municipality municipality, Canton canton) throws IOException {

		if(getExportLevel() == ExportLevel.LOCATION ){	
			if(checkSiteFilter(loc.getId()) || checkDestinationSite(loc) ) {
				writeData(getWriter(), rirow, loc, municipality, canton);				
			}
		}
		else{
			Map<Integer,RelocationInfo> relocmap = loc.getRelocationMap();
			if(getExportLevel() == ExportLevel.MUNICIPALITY){
				if(checkSiteFilter(municipality.getId()))  AddIndicators(municipality.getId(), relocmap);
			}
			if(getExportLevel() == ExportLevel.CANTON ){
				if(checkSiteFilter(canton.getLocationId())) AddIndicators(canton.getLocationId(), relocmap);
			}				
		}
	}
	
	@Override
	public void Close() throws IOException {
		if(relocmap.size() > 0 ){
			for(int id : relocmap.keySet()){
				Map<Integer,RelocationInfo> areamap = relocmap.get(id);
				writeData(getWriter(), id, areamap );
			}
		}
		super.Close();
	}	
	
	protected void AddIndicators(int id, Map<Integer, RelocationInfo> map ){
		
		Map<Integer, RelocationInfo> areamap = null;
		if(relocmap.containsKey(id)) areamap = relocmap.get(id);
		else{
			areamap = new TreeMap<Integer, RelocationInfo>();
			relocmap.put(id,areamap);
		}
		for(int key : map.keySet()){
			int areaID = 0;
			Location tmploc = maploc.get(key);
			if(getExportLevel() == ExportLevel.MUNICIPALITY){
				if(checkSiteFilter(tmploc.getMunicipalityId())){
					areaID = tmploc.getMunicipalityId(); 	
				}
			}
			if(getExportLevel() == ExportLevel.CANTON ){
				if(checkSiteFilter(tmploc.getLocationId())){
					areaID = tmploc.getLocationId(); 
				}
			}
			RelocationInfo areainfo = null;
			if(areaID != 0){
				RelocationInfo info = map.get(key);
				if(areamap.containsKey(areaID)) areainfo = areamap.get(areaID);
				else{
					areainfo = new RelocationInfo(10);
					areamap.put(areaID, areainfo);
				}
				areainfo.Add(info);
			}
		}
	}
	
	@Override
	protected void AddIndicators(int area, RunIndicatorsRow row, Location loc, Municipality municipality, Canton canton ){
	}

	@Override
	protected void AddIndicators(int area, RunIndicatorsAVGRow row, Location loc, Municipality municipality, Canton canton ){
	}
	
	@Override
	protected String getFileName() {
		String name = "relocation_loc";
		if(getExportLevel() == ExportLevel.MUNICIPALITY) name = "relocation_mun";
		else if(getExportLevel() == ExportLevel.CANTON) name = "relocation_can";
		
		if(!isSingleFile()) name += ("_" + getCurrYear());
		if(cumulated) name += ("_cum");
		name += ".csv";
		return name;
	}

	@Override
	protected void writeHeader(FileWriter fw) throws IOException {
		StringBuilder headerline = new StringBuilder();
		if(isPrintHeader()){
			headerline.append( getHeadingInfoLines() );
		}
		
		appendHeaderStringRelocation(headerline);
		headerline.append("\r\n");
		fw.write(headerline.toString());
	}


}
