package org.falcsim.agentmodel.service.methods.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;
import org.falcsim.agentmodel.service.methods.util.dao.RunIndicatorsDao;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsFile.ExportLevel;
import org.springframework.beans.factory.annotation.Autowired;

public class RunIndicatorsLocationsFile extends RunIndicatorsFile {
	
	public RunIndicatorsLocationsFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription){
		super(currYear, currRun, riDir, expLevel, exportFilter, run, yearFilter, singleFile, printheader, description, extraDescription);
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

	public void Export(Map<Integer, String> map) throws IOException {
		if(getWriter() != null && map != null){			
			for (Integer locid : map.keySet())
			{	
				if( checkSiteFilter(locid)){
					String line = map.get(locid);
					getWriter().write(line + "\r\n");
				}
			}
		}
	}
	
	@Override	
	protected String getFileName() {
		String name = "locations_zones";
		name += ("_" + getCurrYear());
		name += ".csv";
		return name;
	}

	@Override
	protected void writeHeader(FileWriter fw) throws IOException {
		StringBuilder headerline = new StringBuilder();
		if(isPrintHeader()){
			headerline.append( getHeadingInfoLines() );
		}
		
		headerline.append("\"locid\";\"denot\";\"code_mun\";\"name_mun\";\"subarea_nr\";\"name_can\";\"run\";\"res_tot\";\"emp_tot\";\"settlement_area\";\"flat_rent\";"+
				"\"urban_centre\";\"diversity_sector\";\"motorway_access\";\"railway_access\";\"resident_landprice_n\";\"landuse_density_n\";"+
				"\"university_degree_n\";\"diversity_sector_n\";\"tax_holdingcomp_n\";\"tax_partnership_n\";\"tax_companies_n\";"+
				"\"accessibility_t_n\";\"bus_dev_cant_n\";\"accessibility_res_n\";\"bus_dev_mun_n\";" +
				"\"av_1\";\"av_2\";\"av_3\";\"av_4\";\"av_5\";\"av_6\";\"av_7\";\"av_8\";\"av_9\";\"av_10\";\"av_11\";\"av_12\";" +
				"\"landtype\";\"maxfloorareares\";\"usedfloorareares\";\"maxfloorareawrk\";\"usedfloorareawrk\";\"maxfloorareaall\";\"usedfloorareaall\";" +
				"\"var1\";\"var2\";\"var3\";\"var4\";\"var5\";\"var6\";\"var7\";\"var8\";\"var9\";\"var10\"" +
				"\r\n");				
		fw.write(headerline.toString());
	}


}
