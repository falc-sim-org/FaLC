package org.falcsim.agentmodel.service.methods.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.falcsim.agentmodel.accessiblevars.domain.AccessibleVarsParameters;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.app.FalcPropertyPlaceholderConfigurer;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.distances.domain.DistanceParameters;
import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.LocationEventsInfo;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RelocationInfo;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.service.methods.util.dao.RunIndicatorsDao;
import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsManager;
import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsParameters;
import org.falcsim.agentmodel.service.methods.util.io.CustomIndicator;
import org.falcsim.agentmodel.service.methods.util.io.CustomIndicatorHelper;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsBusinessesFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsBusinessesTypesFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsCommutersFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsFile.ExportLevel;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsGISFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsHouseholdFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsLocationsFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsOverviewFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsPersonsFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsR2analysesFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsRelocationFile;
import org.falcsim.agentmodel.service.methods.util.io.RunIndicatorsRunInfoFile;
import org.falcsim.agentmodel.utility.domain.RipisExportInfo;
import org.falcsim.agentmodel.utility.domain.RipisType;
import org.falcsim.agentmodel.utility.domain.UtilityParameters;
import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunIndicatorsMemorySplittedImpl implements RunIndicators {
		
		private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		
		@Autowired
		private UniverseServiceUtil usu;
		@Autowired 
		private RunParameters runParameters;
		@Autowired 
		private RunIndicatorsParameters riParams;
		@Autowired
		private ServiceParameters sp;
		@Autowired
		private RunIndicatorsDao riDao;
		@Autowired
		private RipisExportInfo ripisExp;
		@Autowired
		private RunIndicatorsManager riman;
		@Autowired
		private UtilityParameters up;
		@Autowired
		private DistanceParameters dp;
		@Autowired
		private AccessibleVarsParameters avp;		
				
		private String initTimestamp;
		
		private int currYear;	
		private int currRefRun;
		private String folder;	
		private String referenceSubfolder;
		private String description;
		
		private String fileprefix = "";
			
		public RunIndicatorsMemorySplittedImpl(){
			initTimestamp = sdf.format(new Date());
		}
		
		@Override
		public void init()
		{
			initTimestamp = sdf.format(new Date());
			currRefRun = 0;
			referenceSubfolder = "";
			try{ 
				exportSummaryLine();
				copyProperties();
			}catch(Exception e){
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.RUN_INDICATORS_EXPORTING_ERROR.getValue());
			}
		}
		
		@Override
		public String getReferenceSubfolder() {
			return referenceSubfolder;
		}
		@Override
		public void setReferenceSubfolder(String referenceSubfolder) {
			this.referenceSubfolder = referenceSubfolder;
		}

		private void copyProperties() throws IOException, URISyntaxException{
			if(riParams.getRun_indicators_active()){
				logger.info("Run indicators export - copy properties");
				
				folder = riParams.getRun_indicators_folder();
				description = riParams.getRun_indicators_description();
				File fdir = checkExportDir("");
				File dest = new File(fdir, "properties");
				
				//first access resources
				logger.info("    - Resource access");
				if (!dest.exists()) {
					dest.mkdir();
				}				
					
				if(runParameters.getProjectDir() == null || "".equals(runParameters.getProjectDir()) ){
					//copy only if not started from GUI, means no parameter found
					copyResource("/properties/falc.properties", fdir);
				}

				//if exist config files in JAR directory, overwrite resource copied
				//these files could be incomplete, because only changes to default files can be stored here... 
				logger.info("    - File access");
				dest = new File(fdir, "properties");
				
				File sourceDir = new File("properties");
				if(sourceDir != null && sourceDir.exists()){
					copyFolder(sourceDir, dest);
				}
				
				copyParamFile(dest, runParameters.getProjectfilePath());
				copyParamFile(dest, runParameters.getLocalProjectfilePath());
				copyParamFile(dest, up.getFilePathUtilitiesXml());
				copyParamFile(dest, dp.getSourceFile());
				copyParamFile(dest, runParameters.getRun_scenarios_xml_path());					
				copyParamFile(dest, runParameters.getDistances_evolution_xml_path());
				copyParamFile(dest, avp.getXmlpath());
				copyParamFile(dest, runParameters.getExportParameterInfoDefinitionPath());
			}
		}
		
		private void copyParamFile(File dest, String filename){
			try{
				File srcFile = new File(filename);				
				if( srcFile.exists()){
					File destFile = new File(dest, srcFile.getName());
					copyFile(srcFile, destFile);
				}				
			}
			catch(Exception e){
				logger.error(e);
			}			
		}
		
		public void copyResource(String src, File dest) throws IOException {
			try{
				InputStream in = getClass().getResourceAsStream(src);
				if(in != null){
					try{
						File destFile = new File(dest, src);
						
						OutputStream out = new FileOutputStream(destFile);
						try{
							byte[] buffer = new byte[1024];
					
							int length;
							// copy the file content in bytes
							while ((length = in.read(buffer)) > 0) {
								out.write(buffer, 0, length);
							}
						}
						finally{
							out.close();
						}
					}
					finally{
						in.close();
					}
				}
			}
			catch(IOException e){
				logger.error(e.getMessage(), e);
			}
		}
		
		public void copyFile(File src, File dest) throws IOException {
			logger.debug("    - file: " + src.getAbsolutePath());
			if (dest.exists()) {
				dest = new File(dest.getAbsolutePath() + ".updated");
			}
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			try{
				OutputStream out = new FileOutputStream(dest);
				try{
					byte[] buffer = new byte[1024];
		
					int length;
					// copy the file content in bytes
					while ((length = in.read(buffer)) > 0) {
						out.write(buffer, 0, length);
					}
				}
				finally{
					out.close();
				}
			}
			finally{
				in.close();
			}			
		}
		
		public void copyFolder(File src, File dest) throws IOException {

			if (src.isDirectory()) {
				logger.info("    - directory: " + src.getAbsolutePath());
				// if directory not exists, create it
				if (!dest.exists()) {
					dest.mkdir();
				}

				// list all the directory contents
				String files[] = src.list();

				for (String file : files) {
					// construct the src and dest file structure
					File srcFile = new File(src, file);
					File destFile = new File(dest, file);
					// recursive copy -  temporary deactivated	
					if (!srcFile.isDirectory()){
						copyFolder(srcFile, destFile);
					}
				}

			} else {
				copyFile(src, dest);
			}
		}

		@Override
		public void exportInitState() {
			try{ 
				currRefRun = sp.getCurrentReferenceRun() == null ? 0 : sp.getCurrentReferenceRun();
				currYear = sp.getCurrentYear(); //-1;
				//exportFnc();
				exportFncGeneral();
			}catch(Exception e){
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.RUN_INDICATORS_EXPORTING_ERROR.getValue());
			}
		}
		
		@Override
		public void export() {
			try{ 
				currRefRun = sp.getCurrentReferenceRun() == null ? 0 : sp.getCurrentReferenceRun();
				currYear = sp.getCurrentYear();
				exportFncGeneral();
			}catch(Exception e){
				logger.error(e.getMessage(), e);
				System.exit(ExitCodes.RUN_INDICATORS_EXPORTING_ERROR.getValue());
			}
		}
		
		@Override
		public File checkExportDir(String subfolder){
			File fdir = new File(folder, initTimestamp + "_" + description);
			if(!fdir.exists()) fdir.mkdirs();
			if(subfolder != null && !"".equals(subfolder)){
				fdir = new File(fdir.getAbsoluteFile(), subfolder);
				if(!fdir.exists()) fdir.mkdirs();
			}
			return fdir;
		}
		
		private void insertSummaryHeader(File f) throws IOException{
			File mainFolder = new File(f.getParent());
			if(!mainFolder.exists())
				mainFolder.mkdirs();
			if(!f.exists()){
			    FileWriter fw = new FileWriter(f, false);
			    try
			    {
			    	fw.write("\"ID_GEN\";\"Description\";\"Comment\";\"run_time\";\"start_year\";\"nb_cycles\";\"nb_locations\";\"app.systemQuery\"\r\n");
			    }
			    finally{
			    	fw.close();
			    }
			}
		}
		
		private int getMaxSummaryHeaderID(File f) throws IOException{
			int rval = 0;
			if(f.exists()){
			    FileReader fr = new FileReader(f);
			    try
			    {
			    	LineNumberReader lnr = new LineNumberReader(fr);
			    	try{
				        String line = "";           
				        while ((line = lnr.readLine()) != null) {
				            String[] cols = line.split(";");
				            if(cols.length > 0){
				            	String txtId = cols[0].replace("\"", "").replace("v","");
				            	if(txtId.indexOf("-") > -1){
				            		txtId = txtId.substring(0, txtId.indexOf("-"));
				            	}
					            try{
					            	if( Integer.parseInt(txtId) > rval ) rval = Integer.parseInt(txtId);
					            }
					            catch(Exception e){}
				            }
				        }
			    	}
			    	finally{
			    		lnr.close();
			    	}
			    }
			    finally{
			    	fr.close();
			    }
			}
			return ++rval;
		}
		
		private void exportSummaryLine() throws IOException{
			if(riParams.getRun_indicators_active()){
				logger.info("Run indicators summary export");
				folder = riParams.getRun_indicators_folder();	
				description = riParams.getRun_indicators_description();
				
				File fdir = new File(folder);
				File f = new File(fdir, "Run-indicators-summary.csv");

				insertSummaryHeader(f);
				
			    FileWriter fw = new FileWriter(f, true);
			    try
			    {		    	
			    	fw.write("\"v");
			    	fw.write( strUpdate(getMaxSummaryHeaderID(f), 5) );
			    	fw.write("-");
			    	fw.write( sp.getCurrentTimeMarker() != null ? strUpdate( sp.getCurrentTimeMarker(),2 ) : "");
			    	fw.write("\";\"");
			    	fw.write(description);
			    	fw.write("\";\"\";\"");		//empty comments
			    	fw.write(initTimestamp);
			    	fw.write("\";");
			    	fw.write(sp.getCurrentYear() != null ? String.valueOf( sp.getCurrentYear() ) : "");
			    	fw.write(";");
			    	fw.write( String.valueOf( runParameters.getService_cycles() ) );
			    	fw.write(";");
			    	fw.write(String.valueOf( usu.selectLocationIdsByCriterion(runParameters.getApp_systemQuery()).size() ) );
			    	fw.write(";\"");
			    	fw.write( runParameters.getApp_systemQuery() );
			    	fw.write("\"");	
			    	fw.write("\r\n");
			    }
			    finally{
			    	fw.close();
			    }
			}
		}

			
		private void writeHeaderLines(FileWriter fwallcal, boolean fileExistsCall ) throws IOException{
		    StringBuilder headerlineCal = new StringBuilder("\"ID_GEN\";\"CODE_LOC\";\"CYCLE\";\"NAME_LOC\";\"RES_T\";\"RES_00-19\";\"RES_20-64\";\"RES_65-XX\";\"HH_T\";");
		    
			headerlineCal.append("\"HH_P2-C0\";\"HH_P2-C1\";\"HH_P2-C2\";\"HH_P2-C3\";\"HH_P2-C4\";" + "\"HH_P2-C5\";\"HH_P2-CX\";\"HH_P1-C0\";\"HH_P1-C1\";\"HH_P1-C2\";" + 
					"\"HH_P1-C3\";\"HH_P1-C4\";\"HH_P1-C5\";\"HH_P1-CX\";\"HH_Paar\";" + "\"HH_Single\";\"HH_P0-C0\";\"HH_P0-CX\";\"HH_Error\";");			
			headerlineCal.append("\"FIRM_T\";");
							
			for(int i = 1; i <=10; i++){
				headerlineCal.append("\"FIRM_sec-" + strUpdate(i,2) + "\";");
			}
			headerlineCal.append("\"EMPL_T\";");
			for(int i = 1; i <= 10; i++){
				headerlineCal.append("\"EMPL_sec-" + strUpdate(i,2) + "\";");
			}
						
			for(int i = 1; i <= 5; i++){				
				headerlineCal.append("\"EDUC_lev_" + strUpdate(i,2) + "\";");
			}
			
			headerlineCal.append("\"FIRM_size-0\";\"FIRM_size-01\";\"FIRM_size-02-04\";\"FIRM_size-05-09\";\"FIRM_size-10-49\";\"FIRM_size-50-250\";\"FIRM_size-250-XX\";");
			
			for(int i = 0; i < 100; i = i+5){
				headerlineCal.append("\"COM_" + strUpdate(i,2) + "-"  + strUpdate(i+4,2) + "km\";");
			}
			headerlineCal.append("\"COM_100-XXkm\";");
			
			headerlineCal.append("\"MAXFA_RES_T\";\"MAXFA_FIRM_T\";\"OCCFA_FIRM_01\";\"OCCFA_FIRM_02\";\"OCCFA_FIRM_03\";\"OCCFA_FIRM_04\";\"OCCFA_FIRM_05\";\"OCCFA_FIRM_06\";\"OCCFA_FIRM_07\";\"OCCFA_FIRM_08\";\"OCCFA_FIRM_09\";\"OCCFA_FIRM_10\";\"OCCFA_RES_T\";\"OCCFA_FIRM_T\";\"MAXFA_T\";");

			headerlineCal.append("\"RES_HH_P2-C0\";\"RES_HH_P2-C1\";\"RES_HH_P2-C2\";\"RES_HH_P2-C3\";\"RES_HH_P2-C4\";" + 
								 "\"RES_HH_P2-C5\";\"RES_HH_P2-CX\";\"RES_HH_P1-C0\";\"RES_HH_P1-C1\";\"RES_HH_P1-C2\";" + 
								 "\"RES_HH_P1-C3\";\"RES_HH_P1-C4\";\"RES_HH_P1-C5\";\"RES_HH_P1-CX\";\"RES_HH_Paar\";" + 
								 "\"RES_HH_Single\";\"RES_HH_P0-C0\";\"RES_HH_P0-CX\";\"RES_HH_Error\";");
			
			headerlineCal.append("\"UNEMPL_T\";");
			
			headerlineCal.append("\"F_CLOSED\";\"J_CLOSED\";\"F_CREATE\";\"J_CREATE\";\"E_QUIT\";\"E_JOIN\";\"F_COMEIN\";\"E_COMEIN\";\"F_GOOUT\";\"E_GOOUT\";");
			headerlineCal.append("\"R_BIRTH\";\"R_DEATH\";\"R_IMMIGR\";\"H_KIDS_L\";\"H_DIVORS\";\"H_MERGED\";\"H_COMEIN\";\"R_COMEIN\";\"H_GOOUT\";\"R_GOOUT\";\"H_CLOSED\";\"F_EMPTY\";");
			headerlineCal.append("\"R_wse\";\"R_GTH_LMT\";\"E_GTH_LMT\";\"R_MFA_LMT\";\"E_MFA_LMT\";\"T_MFA_LMT\";\"H_Type0\";\"H_Type1\";\"H_Type2\";\"H_Type3\";\"H_Type4\"");

			headerlineCal.append(";\"" + initTimestamp + "_" + description + (referenceSubfolder != null ? "_" + referenceSubfolder : "") + "\"\r\n");
			
			if(!fileExistsCall){ 
				if(fwallcal != null ){
					fwallcal.write(headerlineCal.toString());
				}
			}
//			else{
//				if(fwallcal != null && fileExistsCall ) fwallcal.write(headerlineCal.toString());
//			}
		}

		private void writeReferenceHeaderLines(FileWriter fw ) throws IOException{
		    StringBuilder headerlineCal = new StringBuilder("\"RUN\";\"RES_T\";\"RES_00-19\";\"RES_20-64\";\"RES_65-XX\";\"HH_T\";");

			headerlineCal.append("\"HH_P2-C0\";\"HH_P2-C1\";\"HH_P2-C2\";\"HH_P2-C3\";\"HH_P2-C4\";" + "\"HH_P2-C5\";\"HH_P2-CX\";\"HH_P1-C0\";\"HH_P1-C1\";\"HH_P1-C2\";" + 
					"\"HH_P1-C3\";\"HH_P1-C4\";\"HH_P1-C5\";\"HH_P1-CX\";\"HH_Paar\";" + "\"HH_Single\";\"HH_P0-C0\";\"HH_P0-CX\";\"HH_Error\";");
			headerlineCal.append("\"FIRM_T\";");
							
			for(int i = 1; i <=10; i++){
				headerlineCal.append("\"FIRM_sec-" + strUpdate(i,2) + "\";");
			}
			headerlineCal.append("\"EMPL_T\";");
			for(int i = 1; i <= 10; i++){
				headerlineCal.append("\"EMPL_sec-" + strUpdate(i,2) + "\";");
			}
			
			for(int i = 1; i <= 5; i++){
				headerlineCal.append("\"EDUC_lev_" + strUpdate(i,2) + "\";");
			}

			headerlineCal.append("\"FIRM_size-0\";\"FIRM_size-01\";\"FIRM_size-02-04\";\"FIRM_size-05-09\";\"FIRM_size-10-49\";\"FIRM_size-50-250\";\"FIRM_size-250-XX\";");
			
			for(int i = 0; i < 100; i = i+5){
				headerlineCal.append("\"COM_" + strUpdate(i,2) + "-"  + strUpdate(i+4,2) + "km\";");
			}
			headerlineCal.append("\"COM_100-XXkm\";");
			
			headerlineCal.append("\"MAXFA_RES_T\";\"MAXFA_FIRM_T\";\"OCCFA_FIRM_01\";\"OCCFA_FIRM_02\";\"OCCFA_FIRM_03\";\"OCCFA_FIRM_04\";\"OCCFA_FIRM_05\";\"OCCFA_FIRM_06\";\"OCCFA_FIRM_07\";\"OCCFA_FIRM_08\";\"OCCFA_FIRM_09\";\"OCCFA_FIRM_10\";\"OCCFA_RES_T\";\"OCCFA_FIRM_T\";\"MAXFA_T\";");

			headerlineCal.append("\"RES_HH_P2-C0\";\"RES_HH_P2-C1\";\"RES_HH_P2-C2\";\"RES_HH_P2-C3\";\"RES_HH_P2-C4\";" + 
					 "\"RES_HH_P2-C5\";\"RES_HH_P2-CX\";\"RES_HH_P1-C0\";\"RES_HH_P1-C1\";\"RES_HH_P1-C2\";" + 
					 "\"RES_HH_P1-C3\";\"RES_HH_P1-C4\";\"RES_HH_P1-C5\";\"RES_HH_P1-CX\";\"RES_HH_Paar\";" + 
					 "\"RES_HH_Single\";\"RES_HH_P0-C0\";\"RES_HH_P0-CX\";\"RES_HH_Error\";");
			
			headerlineCal.append("\"UNEMPL_T\";\"R_wse\";\"R_GTH_LMT\";\"E_GTH_LMT\";\"R_MFA_LMT\";\"E_MFA_LMT\";\"T_MFA_LMT\";\"H_Type0\";\"H_Type1\";\"H_Type2\";\"H_Type3\";\"H_Type4\";\"RANK_RES\";\"RANK_EMPL\";\"RANK_SUM\"");
			headerlineCal.append(";\"" + initTimestamp + "_" + description + "_R2" + "\"\r\n");
			
			if(fw != null )fw.write(headerlineCal.toString());
		}

		private void exportFncGeneral() throws IOException {
			if(riParams.getRun_indicators_active()){
				logger.info("Run indicators export started");

				folder = riParams.getRun_indicators_folder();
				description = riParams.getRun_indicators_description();	
				String detailDesc = initTimestamp + "_" + description + (referenceSubfolder != null ? "_" + referenceSubfolder : "");
				
				File fdir = checkExportDir("");
				if(referenceSubfolder != null && !"".equals(referenceSubfolder)){
					fdir = checkExportDir(referenceSubfolder);
				}

				//export relocation ripis intervals
				if(riParams.getRun_indicators_export_level() >= RunIndicatorsParameters.run_indicators_export_level_FULL )
					export_indicators_csv_relocation_ripis_intervals(fdir);				
				
				//export locations_zones table
				if(riParams.getRun_indicators_LocationsZones_active() ){					
					RunIndicatorsLocationsFile riflz = new RunIndicatorsLocationsFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
							riParams.getRun_indicators_LocationsZones_location_filter() , riParams.getRun_indicators_LocationsZones_runs_filter(), riParams.getRun_indicators_LocationsZones_years_filter(), 
							riParams.getRun_indicators_LocationsZones_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
							riParams.getRun_indicators_extra_description());
					riflz.Open();
					try{
						riflz.Export( riDao.getRI_locations_table() );
					}
					finally{
						riflz.Close();
					}
				}
				//export run info
				if(riParams.getRun_indicators_RunInfo_active() ){					
					RunIndicatorsRunInfoFile rifri = new RunIndicatorsRunInfoFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
							riParams.getRun_indicators_RunInfo_location_filter() , riParams.getRun_indicators_RunInfo_runs_filter(), riParams.getRun_indicators_RunInfo_years_filter(), 
							riParams.getRun_indicators_RunInfo_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
							riParams.getRun_indicators_extra_description());
					rifri.Open();
					try{
						rifri.Export( riman.GetRunningTimeMap(currRefRun, currYear), currYear );
					}
					finally{
						rifri.Close();
					}
				}				
				
				logger.info("Start export Indicators" );
				try{
					//FIRST PREPARE ALL REQUESTED OUTPUT FILES
					List<RunIndicatorsFile> expFiles = new ArrayList<RunIndicatorsFile>();
					List<RunIndicatorsFile> expFilesreloc = new ArrayList<RunIndicatorsFile>();
					RunIndicatorsFile rif = null; 
					
					List<CustomIndicator> customIndicators = this.prepareCustomIndicators(currYear, currRefRun, fdir, detailDesc, riParams.getRun_indicators_extra_description());
					
					for(CustomIndicator ci : customIndicators){
						ci.Open();
						expFiles.add(ci);
					}
					
					//export R2 minimum info, always
					if( sp.getCurrentRun() == runParameters.getService_cycles()){
						RunIndicatorsR2analysesFile rifR2 = new RunIndicatorsR2analysesFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, "" , "", "", true, false, "","");
						rifR2.Open();
						expFiles.add(rifR2);
					}
					
					// OVERVIEW EXPORTS
					if(riParams.getRun_indicators_Overview_active() ){
						if(riParams.getRun_indicators_Overview_locations()){
							rif = new RunIndicatorsOverviewFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
									riParams.getRun_indicators_Overview_location_filter(), riParams.getRun_indicators_Overview_runs_filter(),  riParams.getRun_indicators_Overview_years_filter(), 
									riParams.getRun_indicators_Overview_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_Overview_municipalities() ){
							rif = new RunIndicatorsOverviewFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
									riParams.getRun_indicators_Overview_municipalities_filter(), riParams.getRun_indicators_Overview_runs_filter(),  riParams.getRun_indicators_Overview_years_filter(), 
									riParams.getRun_indicators_Overview_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_Overview_cantons() ){
							rif = new RunIndicatorsOverviewFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
									riParams.getRun_indicators_Overview_cantons_filter(), riParams.getRun_indicators_Overview_runs_filter(),  riParams.getRun_indicators_Overview_years_filter(), 
									riParams.getRun_indicators_Overview_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}						
					}
					
					// HOUSEHOLDS EXPORTS
					if(riParams.getRun_indicators_Households_active() ){
						if(riParams.getRun_indicators_Households_locations()){
							rif = new RunIndicatorsHouseholdFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
									riParams.getRun_indicators_Households_location_filter(), riParams.getRun_indicators_Households_runs_filter(),  riParams.getRun_indicators_Households_years_filter(), 
									riParams.getRun_indicators_Households_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_Households_municipalities() ){
							rif = new RunIndicatorsHouseholdFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
									riParams.getRun_indicators_Households_municipalities_filter(), riParams.getRun_indicators_Households_runs_filter(),  riParams.getRun_indicators_Households_years_filter(), 
									riParams.getRun_indicators_Households_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_Households_cantons() ){
							rif = new RunIndicatorsHouseholdFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
									riParams.getRun_indicators_Households_cantons_filter(), riParams.getRun_indicators_Households_runs_filter(),  riParams.getRun_indicators_Households_years_filter(), 
									riParams.getRun_indicators_Households_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}						
					}					
					
					// Businesses EXPORTS
					if(riParams.getRun_indicators_Businesses_active() ){
						if(riParams.getRun_indicators_Businesses_locations()){
							rif = new RunIndicatorsBusinessesFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
									riParams.getRun_indicators_Businesses_location_filter(), riParams.getRun_indicators_Businesses_runs_filter(),  riParams.getRun_indicators_Businesses_years_filter(), 
									riParams.getRun_indicators_Businesses_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_Businesses_municipalities() ){
							rif = new RunIndicatorsBusinessesFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
									riParams.getRun_indicators_Businesses_municipalities_filter(), riParams.getRun_indicators_Businesses_runs_filter(),  riParams.getRun_indicators_Businesses_years_filter(), 
									riParams.getRun_indicators_Businesses_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_Businesses_cantons() ){
							rif = new RunIndicatorsBusinessesFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
									riParams.getRun_indicators_Businesses_cantons_filter(), riParams.getRun_indicators_Businesses_runs_filter(),  riParams.getRun_indicators_Businesses_years_filter(), 
									riParams.getRun_indicators_Businesses_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}						
					}	
										
					// Businesses Types EXPORTS
					if(riParams.getRun_indicators_BusinessesTypes_active() ){
						if(riParams.getRun_indicators_BusinessesTypes_locations()){
							rif = new RunIndicatorsBusinessesTypesFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
									riParams.getRun_indicators_BusinessesTypes_location_filter(), riParams.getRun_indicators_BusinessesTypes_runs_filter(),  riParams.getRun_indicators_BusinessesTypes_years_filter(), 
									riParams.getRun_indicators_BusinessesTypes_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_BusinessesTypes_municipalities() ){
							rif = new RunIndicatorsBusinessesTypesFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
									riParams.getRun_indicators_BusinessesTypes_municipalities_filter(), riParams.getRun_indicators_BusinessesTypes_runs_filter(),  riParams.getRun_indicators_BusinessesTypes_years_filter(), 
									riParams.getRun_indicators_BusinessesTypes_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_BusinessesTypes_cantons() ){
							rif = new RunIndicatorsBusinessesTypesFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
									riParams.getRun_indicators_BusinessesTypes_cantons_filter(), riParams.getRun_indicators_BusinessesTypes_runs_filter(),  riParams.getRun_indicators_BusinessesTypes_years_filter(), 
									riParams.getRun_indicators_BusinessesTypes_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}						
					}	
					
					// Persons EXPORTS
					if(riParams.getRun_indicators_Persons_active() ){
						if(riParams.getRun_indicators_Persons_locations()){
							rif = new RunIndicatorsPersonsFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
									riParams.getRun_indicators_Persons_location_filter(), riParams.getRun_indicators_Persons_runs_filter(),  riParams.getRun_indicators_Persons_years_filter(), 
									riParams.getRun_indicators_Persons_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_Persons_municipalities() ){
							rif = new RunIndicatorsPersonsFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
									riParams.getRun_indicators_Persons_municipalities_filter(), riParams.getRun_indicators_Persons_runs_filter(),  riParams.getRun_indicators_Persons_years_filter(), 
									riParams.getRun_indicators_Persons_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_Persons_cantons() ){
							rif = new RunIndicatorsPersonsFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
									riParams.getRun_indicators_Persons_cantons_filter(), riParams.getRun_indicators_Persons_runs_filter(),  riParams.getRun_indicators_Persons_years_filter(), 
									riParams.getRun_indicators_Persons_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}						
					}						
					
					// GIS EXPORTS
					if(riParams.getRun_indicators_GIS_active() ){
						if(riParams.getRun_indicators_GIS_locations()){
							rif = new RunIndicatorsGISFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
									riParams.getRun_indicators_GIS_location_filter(), riParams.getRun_indicators_GIS_runs_filter(),  riParams.getRun_indicators_GIS_years_filter(), 
									riParams.getRun_indicators_GIS_all_year_single_file(), false, detailDesc, 
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_GIS_municipalities() ){
							rif = new RunIndicatorsGISFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
									riParams.getRun_indicators_GIS_municipalities_filter(), riParams.getRun_indicators_GIS_runs_filter(),  riParams.getRun_indicators_GIS_years_filter(), 
									riParams.getRun_indicators_GIS_all_year_single_file(), false, detailDesc, 
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}
						if(riParams.getRun_indicators_GIS_cantons() ){
							rif = new RunIndicatorsGISFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
									riParams.getRun_indicators_GIS_cantons_filter(), riParams.getRun_indicators_GIS_runs_filter(),  riParams.getRun_indicators_GIS_years_filter(), 
									riParams.getRun_indicators_GIS_all_year_single_file(), false, detailDesc, 
									riParams.getRun_indicators_extra_description());					
							rif.Open();
							expFiles.add(rif);
						}						

					}						
					
					//Prepare all_calibration.csv
					File fallcal = new File(fdir, fileprefix + "all_calibration.csv");
					logger.info("Indicators: " + fallcal.getAbsolutePath() );
					boolean fileExistsCall = fallcal.exists();
				    FileWriter fwallcal = riParams.getRun_indicators_export_level() >= RunIndicatorsParameters.run_indicators_export_level_CALIBRATION ? new FileWriter(fallcal, true) : null;
				    try
				    {
						writeHeaderLines(fwallcal, fileExistsCall );

						// WRITE DATE TO ALL export files FILES
						logger.info("Indicators: Locations");
						Map<Integer, Location> maplocGen = export_indicators_csv_general();
												
						// Relocation EXPORTS
						if(riParams.getRun_indicators_Relocation_active() ){
							if( !riParams.getRun_indicators_Relocation_cumulated() || 
								(riParams.getRun_indicators_Relocation_cumulated() && riParams.getRun_indicators_Relocation_years_filter().equals(String.valueOf(currYear)) )){
								if(riParams.getRun_indicators_Relocation_locations()){
									rif = new RunIndicatorsRelocationFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
											riParams.getRun_indicators_Relocation_location_filter(), riParams.getRun_indicators_Relocation_runs_filter(),  riParams.getRun_indicators_Relocation_years_filter(), 
											riParams.getRun_indicators_Relocation_all_year_single_file(), false, detailDesc, 
											riParams.getRun_indicators_extra_description(), maplocGen, riParams.getRun_indicators_Relocation_cumulated() );					
									rif.Open();
									expFilesreloc.add(rif);
								}
								if(riParams.getRun_indicators_Relocation_municipalities() ){
									rif = new RunIndicatorsRelocationFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
											riParams.getRun_indicators_Relocation_municipalities_filter(), riParams.getRun_indicators_Relocation_runs_filter(),  riParams.getRun_indicators_Relocation_years_filter(), 
											riParams.getRun_indicators_Relocation_all_year_single_file(), false, detailDesc, 
											riParams.getRun_indicators_extra_description(), maplocGen, riParams.getRun_indicators_Relocation_cumulated());					
									rif.Open();
									expFilesreloc.add(rif);
								}
								if(riParams.getRun_indicators_Relocation_cantons() ){
									rif = new RunIndicatorsRelocationFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
											riParams.getRun_indicators_Relocation_cantons_filter(), riParams.getRun_indicators_Relocation_runs_filter(),  riParams.getRun_indicators_Relocation_years_filter(), 
											riParams.getRun_indicators_Relocation_all_year_single_file(), false, detailDesc, 
											riParams.getRun_indicators_extra_description(), maplocGen, riParams.getRun_indicators_Relocation_cumulated());					
									rif.Open();
									expFilesreloc.add(rif);
								}	
							}
						}							
						
						for(Integer locid : maplocGen.keySet()){
							Location loc = maplocGen.get(locid);
							Municipality municipality = usu.selectMunicipalityById(loc.getMunicipalityId());		
							Canton canton = usu.selectCantonById(loc.getLocationId());
						
							RunIndicatorsRow rirow = riman.get(currYear, locid, currRefRun);

							if(riParams.getRun_indicators_Relocation_active() ){
								riman.put(loc.getRelocationMap(), currRefRun, currYear, locid, 
										riParams.getRun_indicators_Relocation_cumulated() ? "" : riParams.getRun_indicators_Relocation_years_filter(), "");   //TODO: location filter?
						    	if(riParams.getRun_indicators_Relocation_cumulated() ){
						    		riman.appendYear(loc.getRelocationMap(), locid, riParams.getRun_indicators_Relocation_location_filter() );
						    	}
							}
							
							if(rirow != null){
								
								StringBuilder sb = new StringBuilder();
								sb.append( rirow.getRowHeader() ); sb.append(";\""); sb.append( loc.getName() ); sb.append("\";");
								int[] tmpArray = rirow.getCalibrationArray();
								for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns; i++){
									sb.append( String.valueOf(tmpArray[i]) );
									//if(i < RunIndicatorsRow.numOfCalibrationColumns -1) 
									sb.append(";");
								}
								LocationEventsInfo lei = loc.getEventsStatus();
								if(lei != null){
									riman.append(lei,currYear, locid);
								
									sb.append( String.valueOf(lei.bb_closed) );sb.append(";");
									sb.append( String.valueOf(lei.bb_closed_jobs) );sb.append(";");
									sb.append( String.valueOf(lei.bb_created) );sb.append(";");
									sb.append( String.valueOf(lei.bb_created_jobs) );sb.append(";");
									sb.append( String.valueOf(lei.wrk_quited) );sb.append(";");
									sb.append( String.valueOf(lei.wrk_joined) );sb.append(";");
									
									sb.append( String.valueOf(lei.bb_come_in) );sb.append(";");
									sb.append( String.valueOf(lei.wrk_come_in) );sb.append(";");
									sb.append( String.valueOf(lei.bb_go_out) );sb.append(";");
									sb.append( String.valueOf(lei.wrk_go_out) );sb.append(";");	
									
									sb.append( String.valueOf(lei.res_birth) );sb.append(";");
									sb.append( String.valueOf(lei.res_death) );sb.append(";");
									sb.append( String.valueOf(lei.res_immigraton) );sb.append(";");
									sb.append( String.valueOf(lei.hh_kidsLeave) );sb.append(";");
									sb.append( String.valueOf(lei.hh_divorced) );sb.append(";");
									sb.append( String.valueOf(lei.hh_merged) );sb.append(";");
									
									sb.append( String.valueOf(lei.hh_come_in) );sb.append(";");
									sb.append( String.valueOf(lei.res_come_in) );sb.append(";");
									sb.append( String.valueOf(lei.hh_go_out) );sb.append(";");
									sb.append( String.valueOf(lei.res_go_out) );sb.append(";");	
									
									sb.append( String.valueOf(lei.hh_closed) );sb.append(";");
									sb.append( String.valueOf(lei.bb_closed_no_people) );sb.append(";");
								}
								sb.append("\r\n");
								if(fwallcal != null) fwallcal.write(sb.toString());	
								
							}
							else{
								if(fwallcal != null) fwallcal.write("\r\n");
							}
							
					    	for(RunIndicatorsFile riftmp : expFiles){
					    		riftmp.Export(rirow, loc, municipality, canton);
					    	}
					    	for(RunIndicatorsFile riftmp : expFilesreloc){
					    		if(riParams.getRun_indicators_Relocation_cumulated()){				    			
					    			loc.getRelocationMap().clear();
					    			loc.getRelocationMap().putAll( riman.calculateSUMrelocationRow(locid) );
					    			riftmp.Export(loc, municipality, canton);
					    		}
					    		else{
					    			riftmp.Export(rirow, loc, municipality, canton);
					    		}
					    	}					    	
						}
						
						
						if(riParams.getRun_indicators_commuters_active() ){
							RunIndicatorsCommutersFile rifComm = new RunIndicatorsCommutersFile(currYear, currRefRun, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
									riParams.getRun_indicators_commuters_location_filter(), riParams.getRun_indicators_commuters_runs_filter(),  riParams.getRun_indicators_commuters_years_filter(),
									true, false, "","");
							try{
								rifComm.Open();
								rifComm.writeCommData(riman.getCommutersMap());
							}
							finally{
								rifComm.Close();
							}							
						}						
				    }
				    finally{
				    	if(fwallcal != null) fwallcal.close();
				    	
				    	for(RunIndicatorsFile riftmp : expFiles){
				    		riftmp.Close();
				    	}
				    	for(RunIndicatorsFile riftmp : expFilesreloc){
				    		riftmp.Close();
				    	}				    	
				    }
		
				}
				catch(Exception e){
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
				
			}
			else{
				logger.info("Run indicators are disabled");
			}
		}
		
		@Override
		public void exportParameters(Map<String, String[]> map, String infoId, boolean markdiff, String desc, String content){
			File fdir = checkExportDir("");
			if(referenceSubfolder != null && !"".equals(referenceSubfolder)){
				fdir = checkExportDir(referenceSubfolder);
			}			
			File fparam = new File(fdir, "parameter.info-" + infoId + ".csv" );
			try{
				FileWriter fw = new FileWriter(fparam, false);
				try{
					fw.write("Info parameter file ID : " + infoId + "\r\n");
					fw.write("Description : " + desc + "\r\n");
					fw.write("Export parameters: content = " + content + " markdiff = " + String.valueOf(markdiff) + "\r\n\r\n");

					fw.write("\"\";\"Property/Table name\";\"Current value\";\"Reference value\"\r\n");
					for(String key : map.keySet()){
						if(markdiff && (!map.get(key)[0].equals(map.get(key)[1])) ) fw.write("\"*\";");
						else fw.write("\"\";");
						fw.write("\"" + key + "\";\"" + map.get(key)[0] + "\";\"" + map.get(key)[1] + "\"\r\n");	
					}
				}finally{
					fw.close();
				}				
			}catch(Exception e){
				logger.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void exportMovingProgress(Map<Integer, int[]> map, ExportType type){
			if(riParams.getRun_indicators_active() && riParams.getRun_indicators_export_level() >= RunIndicatorsParameters.run_indicators_export_level_FULL ){
				logger.info(" Save Moving progress file" );
				int expArrayLen = (type == ExportType.HouseholdRI ? 6 : 13);
				File fdir = checkExportDir("");
				if(referenceSubfolder != null && !"".equals(referenceSubfolder)){
					fdir = checkExportDir(referenceSubfolder);
				}
				String filename = "";
				if( type == ExportType.HouseholdRI ) filename = "hh_moving_" + String.valueOf(sp.getCurrentYear()) + ".csv";
				else if( type == ExportType.BusinessRI ) filename = "bb_moving_" + String.valueOf(sp.getCurrentYear()) + ".csv";
				
				File favgs = new File(fdir, filename );
				try{
					FileWriter fw = new FileWriter(favgs, false);
					try{
					    StringBuilder headerlineCal = new StringBuilder();
					    if( type == ExportType.HouseholdRI ) 
					    	headerlineCal.append("\"LOCID\";\"NAME\";\"HH_PASS_PROB\";\"HH_GO_AWAY\";\"RES_GO_AWAY\";\"HH_STAY\";\"HH_COME_IN\";\"RES_COME_IN\";\"OVER_LAND_LIMIT\";\"FINAL_HH_COUNT\";\"FINAL_RES_COUNT\";\"OVER_MAX_GROWTH\";\r\n");
					    else if( type == ExportType.BusinessRI ) {
					    	headerlineCal.append("\"LOCID\";\"NAME\";\"BB_PASS_PROB\";\"BB_GO_AWAY\";\"WRK_GO_AWAY\";\"BB_STAY\";\"BB_COME_IN\";\"WRK_COME_IN\";");
					    	headerlineCal.append("\"NEW_BB\";\"NEW_BB_GO_AWAY\";\"NEW_WRK_GO_AWAY\";\"NEW_BB_STAY\";\"NEW_BB_COME_IN\";\"NEW_WRK_COME_IN\";\"BB_CLOSED\";");
					    	headerlineCal.append("\"IS_OVER_LIMIT\";\"FINAL_BB_COUNT\";\"FINAL_WRK_COUNT\";\"OVER_MAX_GROWTH\";\r\n");
					    }
					    
					    fw.write(headerlineCal.toString());
						
					    Set<Integer> keys = new TreeSet<Integer>(map.keySet());
						for(Integer key : keys ){
							Location loc = usu.selectLocationById(key);
							int[] pole = map.get(key);
							String str = key.toString() + ";\"" + loc.getName() + "\";";
							for(int i = 0; i < expArrayLen; i ++){
								str = str + String.valueOf(pole[i]);
								str = str + ";"; 
							}
							if( type == ExportType.HouseholdRI ){
								str = str + String.valueOf(loc.getUsedFloorAreaRes() > loc.getMaxFloorAreaRes() || loc.getUsedFloorAreaAll() > loc.getMaxFloorAreaAll());
								str = str + ";" + String.valueOf(loc.getHouseholds().size());
								str = str + ";" + String.valueOf(loc.getActualResidents());
								str = str + ";" + String.valueOf(loc.getMG_overlimit_H());
							}
							else if( type == ExportType.BusinessRI ) {
								str = str + String.valueOf(loc.getUsedFloorAreaWrk() > loc.getMaxFloorAreaWrk() || loc.getUsedFloorAreaAll() > loc.getMaxFloorAreaAll());
								str = str + ";" + String.valueOf(loc.getBusinesses().size());
								str = str + ";" + String.valueOf(loc.getActualWorkers());
								str = str + ";" + String.valueOf(loc.getMG_overlimit_B());
							}
							fw.write(str + "\r\n");
						}

				    }
				    finally{
				    	fw.close();
				    }
				}
				catch(Exception e){
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}		
			}
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public int exportReferenceRuns_R2analysis(){
			int best_ref_run = 0;
			double maxRank = 0;
			double rank_diff = Integer.MAX_VALUE;
			if(riParams.getRun_indicators_active()){
				logger.info("Run indicators export reference runs R2 analysis");

				folder = riParams.getRun_indicators_folder();

				Map<Integer, double[]> map = (Map<Integer, double[]>)riman.calculateAVG(sp.getCurrentYear(), 
						runParameters.getUniverse_reference_runs_cycles(), runParameters.getUniverse_reference_runs_cycles_startfrom());
				
				logger.info(" Save AVG R2 file" );
				File fdir = checkExportDir("");
				File favgs = new File(fdir,  "analyses_R2_" + String.valueOf(sp.getCurrentYear()) + ".csv");
				try{
					FileWriter fw = new FileWriter(favgs, false);
					try{
						writeReferenceHeaderLines(fw);						
						for(Integer refrun : map.keySet()){
							double[] array = map.get(refrun);
							
							if(array[RunIndicatorsRow.numOfCalibrationColumns +2] > maxRank){
								maxRank = array[RunIndicatorsRow.numOfCalibrationColumns +2];
								rank_diff = Math.abs(array[RunIndicatorsRow.numOfCalibrationColumns] - array[RunIndicatorsRow.numOfCalibrationColumns +1]);
								best_ref_run = refrun;
							}
							else{
								if(array[RunIndicatorsRow.numOfCalibrationColumns +2] == maxRank){
									if(rank_diff > Math.abs(array[RunIndicatorsRow.numOfCalibrationColumns] - array[RunIndicatorsRow.numOfCalibrationColumns +1]) ){
										rank_diff = Math.abs(array[RunIndicatorsRow.numOfCalibrationColumns] - array[RunIndicatorsRow.numOfCalibrationColumns +1]);
										best_ref_run = refrun;
									}
								}
							}
								
							StringBuilder sb = new StringBuilder();
							sb.append(refrun);
							sb.append(";");
							//add values here
							for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns+3; i++){
								if(i < RunIndicatorsRow.numOfCalibrationColumns) sb.append( String.format("%.6f", array[i]) );
								else sb.append( String.format("%.0f", array[i]) );
								if(i < RunIndicatorsRow.numOfCalibrationColumns +2) sb.append(";");
							}
							sb.append("\r\n");
							fw.write(sb.toString());
						}
						logger.info("AVG Run indicators saved");
				    }
				    finally{
				    	fw.close();
				    }
				}
				catch(Exception e){
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
			}
			else{
				logger.info("Run indicators are disabled");
			}
			return best_ref_run;
		}
		
		@Override
		public void exportReferenceRunsAVG(){
			if(riParams.getRun_indicators_active()){
				File fdir = checkExportDir("");
				
				try{					
					logger.info("Run indicators export reference runs Average folder");
					referenceSubfolder = "run_average";
					fdir = checkExportDir("run_average");
					String detailDesc = initTimestamp + "_" + description + (referenceSubfolder != null ? "_" + referenceSubfolder : "");
					
					Map<Integer, Location> maplocGen = new TreeMap<Integer, Location>();
					for(Location loc : up.getLocs()) maplocGen.put(loc.getId(), loc );
					Map<Integer, Municipality> mapmunGen = new TreeMap<Integer, Municipality>();
					for(Municipality mun : up.getMunicipalities()) mapmunGen.put(mun.getId(), mun );					
					Map<Integer, Canton> mapcantGen = new TreeMap<Integer, Canton>();
					for(Canton cant : up.getCantons()) mapcantGen.put(cant.getLocationId(), cant );					
					
					riman.resetCycle(); //clear relocation cumulated years
					for(int j = 0; j <= runParameters.getService_cycles(); j++){
						int theYear = sp.getCurrentYear() - runParameters.getService_cycles() + j;
						
						//export AVG run info
						if(riParams.getRun_indicators_RunInfo_active() ){					
							RunIndicatorsRunInfoFile rifri = new RunIndicatorsRunInfoFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
									riParams.getRun_indicators_RunInfo_location_filter() , riParams.getRun_indicators_RunInfo_runs_filter(), riParams.getRun_indicators_RunInfo_years_filter(), 
									riParams.getRun_indicators_RunInfo_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc,
									riParams.getRun_indicators_extra_description());
							rifri.Open();
							try{
								rifri.Export( riman.calculateAVGruninfo(theYear), theYear );
							}
							finally{
								rifri.Close();
							}
						}						
						
						if(riman.containsYear(theYear)){

							//FIRST PREPARE ALL REQUESTED OUTPUT FILES
							List<RunIndicatorsFile> expFiles = new ArrayList<RunIndicatorsFile>();
							List<RunIndicatorsFile> expFilesreloc = new ArrayList<RunIndicatorsFile>();
							RunIndicatorsFile rif = null; 
							
							List<CustomIndicator> customIndicators = this.prepareCustomIndicators(theYear, 0, fdir, detailDesc, riParams.getRun_indicators_extra_description());
							
							for(CustomIndicator ci : customIndicators){
								ci.Open();
								expFiles.add(ci);
							}
							
							// OVERVIEW EXPORTS
							if(riParams.getRun_indicators_Overview_active() ){
								if(riParams.getRun_indicators_Overview_locations()){
									rif = new RunIndicatorsOverviewFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
											riParams.getRun_indicators_Overview_location_filter(), riParams.getRun_indicators_Overview_runs_filter(),  riParams.getRun_indicators_Overview_years_filter(), 
											riParams.getRun_indicators_Overview_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_Overview_municipalities() ){
									rif = new RunIndicatorsOverviewFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
											riParams.getRun_indicators_Overview_municipalities_filter(), riParams.getRun_indicators_Overview_runs_filter(),  riParams.getRun_indicators_Overview_years_filter(), 
											riParams.getRun_indicators_Overview_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_Overview_cantons() ){
									rif = new RunIndicatorsOverviewFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
											riParams.getRun_indicators_Overview_cantons_filter(), riParams.getRun_indicators_Overview_runs_filter(),  riParams.getRun_indicators_Overview_years_filter(), 
											riParams.getRun_indicators_Overview_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}						
							}
							
							// HOUSEHOLDS EXPORTS
							if(riParams.getRun_indicators_Households_active() ){
								if(riParams.getRun_indicators_Households_locations()){
									rif = new RunIndicatorsHouseholdFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
											riParams.getRun_indicators_Households_location_filter(), riParams.getRun_indicators_Households_runs_filter(),  riParams.getRun_indicators_Households_years_filter(), 
											riParams.getRun_indicators_Households_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_Households_municipalities() ){
									rif = new RunIndicatorsHouseholdFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
											riParams.getRun_indicators_Households_municipalities_filter(), riParams.getRun_indicators_Households_runs_filter(),  riParams.getRun_indicators_Households_years_filter(), 
											riParams.getRun_indicators_Households_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_Households_cantons() ){
									rif = new RunIndicatorsHouseholdFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
											riParams.getRun_indicators_Households_cantons_filter(), riParams.getRun_indicators_Households_runs_filter(),  riParams.getRun_indicators_Households_years_filter(), 
											riParams.getRun_indicators_Households_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}						
							}					
							
							// Businesses EXPORTS
							if(riParams.getRun_indicators_Businesses_active() ){
								if(riParams.getRun_indicators_Businesses_locations()){
									rif = new RunIndicatorsBusinessesFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
											riParams.getRun_indicators_Businesses_location_filter(), riParams.getRun_indicators_Businesses_runs_filter(),  riParams.getRun_indicators_Businesses_years_filter(), 
											riParams.getRun_indicators_Businesses_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_Businesses_municipalities() ){
									rif = new RunIndicatorsBusinessesFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
											riParams.getRun_indicators_Businesses_municipalities_filter(), riParams.getRun_indicators_Businesses_runs_filter(),  riParams.getRun_indicators_Businesses_years_filter(), 
											riParams.getRun_indicators_Businesses_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_Businesses_cantons() ){
									rif = new RunIndicatorsBusinessesFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
											riParams.getRun_indicators_Businesses_cantons_filter(), riParams.getRun_indicators_Businesses_runs_filter(),  riParams.getRun_indicators_Businesses_years_filter(), 
											riParams.getRun_indicators_Businesses_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}						
							}					

							// Businesses Types EXPORTS
							if(riParams.getRun_indicators_BusinessesTypes_active() ){
								if(riParams.getRun_indicators_BusinessesTypes_locations()){
									rif = new RunIndicatorsBusinessesTypesFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
											riParams.getRun_indicators_BusinessesTypes_location_filter(), riParams.getRun_indicators_BusinessesTypes_runs_filter(),  riParams.getRun_indicators_BusinessesTypes_years_filter(), 
											riParams.getRun_indicators_BusinessesTypes_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_BusinessesTypes_municipalities() ){
									rif = new RunIndicatorsBusinessesTypesFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
											riParams.getRun_indicators_BusinessesTypes_municipalities_filter(), riParams.getRun_indicators_BusinessesTypes_runs_filter(),  riParams.getRun_indicators_BusinessesTypes_years_filter(), 
											riParams.getRun_indicators_BusinessesTypes_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_BusinessesTypes_cantons() ){
									rif = new RunIndicatorsBusinessesTypesFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
											riParams.getRun_indicators_BusinessesTypes_cantons_filter(), riParams.getRun_indicators_BusinessesTypes_runs_filter(),  riParams.getRun_indicators_BusinessesTypes_years_filter(), 
											riParams.getRun_indicators_BusinessesTypes_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}						
							}							
							
							// Persons EXPORTS
							if(riParams.getRun_indicators_Persons_active() ){
								if(riParams.getRun_indicators_Persons_locations()){
									rif = new RunIndicatorsPersonsFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
											riParams.getRun_indicators_Persons_location_filter(), riParams.getRun_indicators_Persons_runs_filter(),  riParams.getRun_indicators_Persons_years_filter(), 
											riParams.getRun_indicators_Persons_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_Persons_municipalities() ){
									rif = new RunIndicatorsPersonsFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
											riParams.getRun_indicators_Persons_municipalities_filter(), riParams.getRun_indicators_Persons_runs_filter(),  riParams.getRun_indicators_Persons_years_filter(), 
											riParams.getRun_indicators_Persons_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_Persons_cantons() ){
									rif = new RunIndicatorsPersonsFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
											riParams.getRun_indicators_Persons_cantons_filter(), riParams.getRun_indicators_Persons_runs_filter(),  riParams.getRun_indicators_Persons_years_filter(), 
											riParams.getRun_indicators_Persons_all_year_single_file(), riParams.getRun_indicators_print_header_rows(), detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}						
							}									

							// GIS EXPORTS
							if(riParams.getRun_indicators_GIS_active() ){
								if(riParams.getRun_indicators_GIS_locations()){
									rif = new RunIndicatorsGISFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
											riParams.getRun_indicators_GIS_location_filter(), riParams.getRun_indicators_GIS_runs_filter(),  riParams.getRun_indicators_GIS_years_filter(), 
											riParams.getRun_indicators_GIS_all_year_single_file(), false, detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_GIS_municipalities() ){
									rif = new RunIndicatorsGISFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
											riParams.getRun_indicators_GIS_municipalities_filter(), riParams.getRun_indicators_GIS_runs_filter(),  riParams.getRun_indicators_GIS_years_filter(), 
											riParams.getRun_indicators_GIS_all_year_single_file(), false, detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}
								if(riParams.getRun_indicators_GIS_cantons() ){
									rif = new RunIndicatorsGISFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
											riParams.getRun_indicators_GIS_cantons_filter(), riParams.getRun_indicators_GIS_runs_filter(),  riParams.getRun_indicators_GIS_years_filter(), 
											riParams.getRun_indicators_GIS_all_year_single_file(), false, detailDesc, 
											riParams.getRun_indicators_extra_description());					
									rif.Open();
									expFiles.add(rif);
								}						
							}	
							
							// Relocation EXPORTS
							if(riParams.getRun_indicators_Relocation_active() ){
								if( !riParams.getRun_indicators_Relocation_cumulated() || 
										(riParams.getRun_indicators_Relocation_cumulated() && riParams.getRun_indicators_Relocation_years_filter().equals(String.valueOf(currYear)) )){
									if(riParams.getRun_indicators_Relocation_locations()){
										rif = new RunIndicatorsRelocationFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.LOCATION, 
												riParams.getRun_indicators_Relocation_location_filter(), "",  riParams.getRun_indicators_Relocation_years_filter(), 
												riParams.getRun_indicators_Relocation_all_year_single_file(), false, detailDesc, 
												riParams.getRun_indicators_extra_description(), maplocGen, riParams.getRun_indicators_Relocation_cumulated() );					
										rif.Open();
										expFilesreloc.add(rif);
									}
									if(riParams.getRun_indicators_Relocation_municipalities() ){
										rif = new RunIndicatorsRelocationFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.MUNICIPALITY, 
												riParams.getRun_indicators_Relocation_municipalities_filter(), "",  riParams.getRun_indicators_Relocation_years_filter(), 
												riParams.getRun_indicators_Relocation_all_year_single_file(), false, detailDesc, 
												riParams.getRun_indicators_extra_description(), maplocGen, riParams.getRun_indicators_Relocation_cumulated());					
										rif.Open();
										expFilesreloc.add(rif);
									}
									if(riParams.getRun_indicators_Relocation_cantons() ){
										rif = new RunIndicatorsRelocationFile(theYear, 0, fdir, RunIndicatorsFile.ExportLevel.CANTON, 
												riParams.getRun_indicators_Relocation_cantons_filter(), "",  riParams.getRun_indicators_Relocation_years_filter(), 
												riParams.getRun_indicators_Relocation_all_year_single_file(), false, detailDesc, 
												riParams.getRun_indicators_extra_description(), maplocGen, riParams.getRun_indicators_Relocation_cumulated());					
										rif.Open();
										expFilesreloc.add(rif);
									}
								}
							}
							
							File fallcal = new File(fdir, fileprefix + "all_calibration.csv");
							boolean fileExistsCall = fallcal.exists();
							
						    FileWriter fwallcal = new FileWriter(fallcal, true);
						    try
						    {
								writeHeaderLines(fwallcal, fileExistsCall );
		
								for(Integer locid : maplocGen.keySet()){
									Location loc = maplocGen.get(locid);
									Municipality municipality = mapmunGen.get(loc.getMunicipalityId());		
									Canton canton = mapcantGen.get(loc.getLocationId());								

									RunIndicatorsAVGRow rirow = riman.calculateAVGdetailRow(theYear, loc.getId(), 
											runParameters.getUniverse_reference_runs_cycles() - runParameters.getUniverse_reference_runs_cycles_startfrom() +1);									
									
									if(riParams.getRun_indicators_Relocation_active() ){
										Map<Integer, RelocationInfo> relocmap = riman.calculateAVGrelocationRow(theYear, locid, 
												runParameters.getUniverse_reference_runs_cycles() - runParameters.getUniverse_reference_runs_cycles_startfrom() +1);										
										loc.getRelocationMap().clear();
										loc.getRelocationMap().putAll(relocmap);	
										
								    	if(riParams.getRun_indicators_Relocation_cumulated() ){
								    		riman.appendYear(loc.getRelocationMap(), locid, riParams.getRun_indicators_Relocation_location_filter() );
								    	}
									}									
																		
									if(rirow != null){
										
										StringBuilder sb = new StringBuilder();
										sb.append( rirow.getRowHeader() ); sb.append(";\""); sb.append( loc.getName() ); sb.append("\";");
										double[] tmpArray = rirow.getCalibrationArray();
										for(int i = 0; i < RunIndicatorsRow.numOfCalibrationColumns; i++){
											sb.append( String.format("%.2f",tmpArray[i]) );
											//if(i < RunIndicatorsRow.numOfCalibrationColumns -1) 
											sb.append(";");
										}
										LocationEventsInfo lei = riman.calculateAVGLocationEventsInfo(theYear, loc.getId(), 
												runParameters.getUniverse_reference_runs_cycles() - runParameters.getUniverse_reference_runs_cycles_startfrom() +1);
										loc.getEventsStatus().clear();
										loc.getEventsStatus().Add(lei);
										if(lei != null){
											sb.append( String.valueOf(lei.bb_closed) );sb.append(";");
											sb.append( String.valueOf(lei.bb_closed_jobs) );sb.append(";");
											sb.append( String.valueOf(lei.bb_created) );sb.append(";");
											sb.append( String.valueOf(lei.bb_created_jobs) );sb.append(";");
											sb.append( String.valueOf(lei.wrk_quited) );sb.append(";");
											sb.append( String.valueOf(lei.wrk_joined) );sb.append(";");
											
											sb.append( String.valueOf(lei.bb_come_in) );sb.append(";");
											sb.append( String.valueOf(lei.wrk_come_in) );sb.append(";");
											sb.append( String.valueOf(lei.bb_go_out) );sb.append(";");
											sb.append( String.valueOf(lei.wrk_go_out) );sb.append(";");	
											
											sb.append( String.valueOf(lei.res_birth) );sb.append(";");
											sb.append( String.valueOf(lei.res_death) );sb.append(";");
											sb.append( String.valueOf(lei.res_immigraton) );sb.append(";");
											sb.append( String.valueOf(lei.hh_kidsLeave) );sb.append(";");
											sb.append( String.valueOf(lei.hh_divorced) );sb.append(";");
											sb.append( String.valueOf(lei.hh_merged) );sb.append(";");
											
											sb.append( String.valueOf(lei.hh_come_in) );sb.append(";");
											sb.append( String.valueOf(lei.res_come_in) );sb.append(";");
											sb.append( String.valueOf(lei.hh_go_out) );sb.append(";");
											sb.append( String.valueOf(lei.res_go_out) );sb.append(";");	
											
											sb.append( String.valueOf(lei.hh_closed) );sb.append(";");
											sb.append( String.valueOf(lei.bb_closed_no_people) );sb.append(";");
										}
										sb.append("\r\n");
										if(fwallcal != null ) fwallcal.write(sb.toString());
										
								    	for(RunIndicatorsFile riftmp : expFiles){
								    		riftmp.Export(rirow, loc, municipality, canton);
								    	}	
								    	for(RunIndicatorsFile riftmp : expFilesreloc){
								    		if(riParams.getRun_indicators_Relocation_cumulated()){								    			
								    			loc.getRelocationMap().clear();
								    			loc.getRelocationMap().putAll( riman.calculateSUMrelocationRow(locid) );
								    			riftmp.Export(loc, municipality, canton); //rif
								    		}
								    		else{
								    			riftmp.Export(rirow, loc, municipality, canton);
								    		}
								    	}								    	
									}
									else{
										if(fwallcal != null ) fwallcal.write("\r\n");
									}
								}
						    }
						    finally{
						    	if(fwallcal != null ) fwallcal.close();
						    	
						    	for(RunIndicatorsFile riftmp : expFiles){
						    		riftmp.Close();
						    	}
						    	for(RunIndicatorsFile riftmp : expFilesreloc){
						    		riftmp.Close();
						    	}						    	
						    }
						}
					}

					logger.info("Run indicators export reference runs done");
				}
				catch(Exception e){
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
			}
			else{
				logger.info("Run indicators are disabled");
			}
		}
		

		private String strUpdate(int num, int count){
			String st = String.valueOf(num);
			if(st.length() < count){
				int rep = count - st.length();
				for(int i = 0; i < rep; i++) st = "0" + st;
			}
			return st;
		}
		
	//////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
		private Map<Integer, Location> export_indicators_csv_general() throws IOException{
			return riDao.getRI_General(currYear, currRefRun);
		}
		
		private void export_indicators_csv_relocation_ripis_intervals(File fdir) throws IOException{
			
			logger.info("Export Relocation Ripis intervals...");
			
			List<Integer> exploclist = riParams.getExportRipisLocationsSet();
			
			if(riParams.getRun_indicators_export_ripis_households()){
				File fh = new File(fdir, "relocation_ripis_h_" + String.valueOf(currYear) + ".csv");
				logger.info("Indicators: " + fh.getAbsolutePath() );
				
				BufferedWriter writerh = null;
				try
				{
				    writerh = new BufferedWriter( new FileWriter(fh));
				    writerh.write("\"sourcelocid\";\"businessid\";\"sector\";\"type\";\"is_new_b\";\"destlocid\";\"ripis list(locid, interval_size)\"\r\n");
				    		
					ripisExp.SortLocationList(0);
					for(Integer locid : ripisExp.GetSortedKeys(RipisType.HouseholdRipis) ){
						if(exploclist.size() == 0 || exploclist.contains(locid)){
							List<?> list = ripisExp.exportmapH.get(locid);
							for(Object obj : list){
								writerh.write( String.valueOf(locid) + "; " + obj.toString() + "\r\n");
							}
						}
					}
				}
				finally
				{
				    try
				    {
				        if ( writerh != null)
				        writerh.close( );
				    }
				    catch ( IOException e)
				    {
				    }
				}
			}
			
			if(riParams.getRun_indicators_export_ripis_business()){
				File fb = new File(fdir, "relocation_ripis_b_" + String.valueOf(currYear) + ".csv");
				logger.info("Indicators: " + fb.getAbsolutePath() );
				
				BufferedWriter writerb = null;
				try
				{
				    writerb = new BufferedWriter( new FileWriter(fb));
				    writerb.write("\"sourcelocid\";\"businessid\";\"sector\";\"type\";\"is_new_b\";\"destlocid\";\"ripis list(locid, interval_size)\"\r\n");
				    		
					ripisExp.SortLocationList(0);
					for(Integer locid : ripisExp.GetSortedKeys(RipisType.BusinessRipis) ){
						if(exploclist.size() == 0 || exploclist.contains(locid)){
							List<?> list = ripisExp.exportmapB.get(locid);
							for(Object obj : list){
								writerb.write( String.valueOf(locid) + "; " + obj.toString() + "\r\n");
							}
						}
					}
				}
				finally
				{
				    try
				    {
				        if ( writerb != null)
				        writerb.close( );
				    }
				    catch ( IOException e)
				    {
				    }
				}
			}
			logger.info("Done...");
		}	
		
		public List<CustomIndicator> prepareCustomIndicators(int year, int run, File dir, String description, String extraDesctiption){
			Map<String, CustomIndicatorHelper> customIndicators = AppCtxProvider.getApplicationContext().getBean(FalcPropertyPlaceholderConfigurer.class).getCustomRIProperties();
			List<CustomIndicator> retVal = new ArrayList<CustomIndicator>(customIndicators.size());
			for(String indicatorName : customIndicators.keySet()){
				CustomIndicatorHelper ciHelper = customIndicators.get(indicatorName);
				if(ciHelper.cantonsActive)
					retVal.add(new CustomIndicator(year, run, dir, ExportLevel.CANTON, 
							ciHelper.cantons, "", ciHelper.years, false, true, 
							description, extraDesctiption, ciHelper.name, ciHelper.selected));
				if(ciHelper.locationActive)
					retVal.add(new CustomIndicator(year, run, dir, ExportLevel.LOCATION, 
							ciHelper.locations, "", ciHelper.years, false, true, 
							description, extraDesctiption, ciHelper.name, ciHelper.selected));
				if(ciHelper.municipalityActive)
					retVal.add(new CustomIndicator(year, run, dir, ExportLevel.MUNICIPALITY, 
							ciHelper.municipalities, "", ciHelper.years, false, true, 
							description, extraDesctiption, ciHelper.name, ciHelper.selected));
			}
			return retVal;
		}
	}
