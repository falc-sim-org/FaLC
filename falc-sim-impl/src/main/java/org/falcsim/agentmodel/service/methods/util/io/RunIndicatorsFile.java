package org.falcsim.agentmodel.service.methods.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.RunIndicatorsRow;
import org.falcsim.agentmodel.domain.statistics.RunIndicatorsAVGRow;

/** Saves corresponding set of run indicators into file 
*
* @author regioConcept AG
* @version 1.0
* 
*/
public abstract class RunIndicatorsFile {

	protected abstract String getFileName();
	protected abstract void writeHeader(FileWriter fw) throws IOException;	
	protected abstract void writeData(FileWriter fw, RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton )throws IOException;
	protected abstract void writeData(FileWriter fw, RunIndicatorsAVGRow rirow, Location loc, Municipality municipality, Canton canton )throws IOException;
	
	public enum ExportLevel {
		 LOCATION, MUNICIPALITY, CANTON; 
	}
	
	private boolean activated;
	private int currYear;
	private int currRun;
	private ExportLevel expLevel;
	private String exportFilter;
	private String runFilter;
	private String yearFilter;
	private boolean singleFile;
	private boolean printheader;
	private String description;
	private String extraDescription;
	
	private File f;
	private File riDir;	
	private FileWriter fw;

	public boolean Active() {
		return activated;
	}	
	
	public File getFile() {
		return f;
	}
	
	protected void setFile(File f){
		this.f = f;
	}

	public File getRiDir() {
		return riDir;
	}
	
	public ExportLevel getExportLevel() {
		return expLevel;
	}
	
	public String getExportFilter() {
		return exportFilter;
	}
	
	public String getRun() {
		return runFilter;
	}
	
	public String getYearFilter() {
		return yearFilter;
	}
	
	public boolean isSingleFile() {
		return singleFile;
	}
	
	public boolean isPrintHeader() {
		return printheader;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getExtraDescription() {
		return extraDescription;
	}	
	
	public int getCurrYear() {
		return currYear;
	}

	public int getCurrRun() {
		return currRun;
	}
	
	protected FileWriter getWriter() {
		return fw;
	}
	protected void setWriter(FileWriter fw) {
		this.fw = fw;
	}		
	
	private Map<Integer, RunIndicatorsFileHelper> areamap = new TreeMap<Integer, RunIndicatorsFileHelper>();
	
	
	public RunIndicatorsFile(int currYear, int currRun, File riDir, ExportLevel expLevel, String exportFilter, String run, String yearFilter, 
			boolean singleFile, boolean printheader, String description, String extraDescription){
		activated = false;
		
		this.currYear = currYear;
		this.currRun = currRun;
		this.riDir = riDir;		
		this.expLevel = expLevel;
		this.exportFilter = exportFilter;
		this.runFilter = run;
		this.yearFilter = yearFilter;
		this.singleFile = singleFile;
		this.printheader = printheader;
		this.description = description;
		this.extraDescription = extraDescription;
		f = new File(riDir, getFileName());
		
		if( checkYearFilter(currYear) && checkRunFilter(currRun) ){
			activated = true;
		}
	}
	
	public void Open() throws IOException {
		boolean exists = getFile().exists();
		if( activated){
			FileWriter fw = new FileWriter(getFile(), true);
			if(!exists) writeHeader(fw);
			setWriter(fw);
		}
	}
	
	public void Close() throws IOException {
		if(areamap.size() > 0 ){
			for(int id : areamap.keySet()){
				RunIndicatorsFileHelper localRow = areamap.get(id);
				if(localRow.row != null){
					writeData(fw, localRow.row, localRow.loc, localRow.municipality, localRow.canton );
				}
				else if(localRow.avgrow != null){
					writeData(fw, localRow.avgrow, localRow.loc, localRow.municipality, localRow.canton );	
				}
			}
		}
		if(getWriter() != null) getWriter().close();
		areamap.clear();
	}	
		
	public void Export( Location loc, Municipality municipality, Canton canton )  throws IOException {		
	}
	
	public void Export(RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton )  throws IOException {		
		if(rirow != null){			
			if(getExportLevel() == ExportLevel.LOCATION ){	
				if(checkSiteFilter(loc.getId())) writeData(getWriter(), rirow, loc, municipality, canton);				
			}
			else{
				if(getExportLevel() == ExportLevel.MUNICIPALITY){
					if(checkSiteFilter(municipality.getId()))  AddIndicators(municipality.getId(), rirow, loc, municipality, canton);
				}
				if(getExportLevel() == ExportLevel.CANTON ){
					if(checkSiteFilter(municipality.getId())) AddIndicators(canton.getLocationId(), rirow, loc, municipality, canton);
				}				
			}
		}
	}
	
	public void Export(RunIndicatorsAVGRow rirow, Location loc, Municipality municipality, Canton canton )  throws IOException {		
		if(rirow != null){			
			if(getExportLevel() == ExportLevel.LOCATION ){	
				if(checkSiteFilter(loc.getId())) writeData(getWriter(), rirow, loc, municipality, canton);				
			}
			else{
				if(getExportLevel() == ExportLevel.MUNICIPALITY){
					if(checkSiteFilter(municipality.getId()))  AddIndicators(municipality.getId(), rirow, loc, municipality, canton);
				}
				if(getExportLevel() == ExportLevel.CANTON ){
					if(checkSiteFilter(municipality.getId())) AddIndicators(canton.getLocationId(), rirow, loc, municipality, canton);
				}				
			}
		}
	}
	
	protected void AddIndicators(int area, RunIndicatorsRow row, Location loc, Municipality municipality, Canton canton ){
		RunIndicatorsFileHelper localRow = null;
		if(areamap.containsKey(area))localRow = areamap.get(area);
		else{
			localRow = new RunIndicatorsFileHelper();
			localRow.loc = loc;
			localRow.municipality = municipality;
			localRow.canton = canton;
			localRow.row = new RunIndicatorsRow(area, row.getReferencerun(), row.getYear());
			areamap.put(area, localRow);
		}
		for(int i = 0; i < RunIndicatorsRow.numOfColumns; i ++){
			localRow.row.appendValue(i, row.getValue(i));
		}
		int[] tmparray = row.getCompTypesArray();
		if(tmparray != null){
			for(int i = 0; i < tmparray.length; i ++){
				localRow.row.appendValueCompType(i, tmparray[i]);
			}
		}
	}
	
	protected void AddIndicators(int area, RunIndicatorsAVGRow row, Location loc, Municipality municipality, Canton canton ){
		RunIndicatorsFileHelper localRow = null;
		if(areamap.containsKey(area))localRow = areamap.get(area);
		else{
			localRow = new RunIndicatorsFileHelper();
			localRow.loc = loc;
			localRow.municipality = municipality;
			localRow.canton = canton;
			localRow.avgrow = new RunIndicatorsAVGRow(area, row.getReferencerun(), row.getYear());
			areamap.put(area, localRow);
		}
		for(int i = 0; i < RunIndicatorsRow.numOfColumns; i ++){
			localRow.avgrow.appendValue(i, row.getValue(i));
		}
		double[] tmparray = row.getCompTypesArray();
		if(tmparray != null){
			for(int i = 0; i < tmparray.length; i ++){
				localRow.avgrow.appendValueCompType(i, tmparray[i]);
			}
		}
	}
	
	protected String generateRowBaseData(RunIndicatorsRow rirow, Location loc, Municipality municipality, Canton canton ){
		StringBuilder sb = new StringBuilder();
		sb.append( rirow.getRowID() ); sb.append(";"); 
		if(getExportLevel() == ExportLevel.LOCATION){
			sb.append( loc.getId() ); sb.append(";\"");	sb.append( loc.getName() ); sb.append("\";");
			sb.append( loc.getMunicipalityId() ); sb.append(";\"");	
			sb.append( municipality == null ? "" : municipality.getName() ); sb.append("\";");
		}		
		if(getExportLevel() == ExportLevel.MUNICIPALITY){
			sb.append( loc.getMunicipalityId() ); sb.append(";\"");	
			sb.append( municipality == null ? "" : municipality.getName() ); sb.append("\";");
		}		
		sb.append( loc.getLocationId() ); sb.append(";\"");
		sb.append( canton == null ? "" : canton.getShortname() ); sb.append("\";");

		//if(isSingleFile()){
			sb.append( rirow.getYear() ); sb.append(";");
		//}
		return sb.toString();
	}
	
	protected String generateRowBaseData(RunIndicatorsAVGRow rirow, Location loc, Municipality municipality, Canton canton ){
		StringBuilder sb = new StringBuilder();
		sb.append( rirow.getRowID() ); sb.append(";"); 
		if(getExportLevel() == ExportLevel.LOCATION){
			sb.append( loc.getId() ); sb.append(";\"");	sb.append( loc.getName() ); sb.append("\";");
			sb.append( loc.getMunicipalityId() ); sb.append(";\"");	
			sb.append( municipality == null ? "" : municipality.getName() ); sb.append("\";");
		}		
		if(getExportLevel() == ExportLevel.MUNICIPALITY){
			sb.append( loc.getMunicipalityId() ); sb.append(";\"");	
			sb.append( municipality == null ? "" : municipality.getName() ); sb.append("\";");
		}		
		sb.append( loc.getLocationId() ); sb.append(";\"");
		sb.append( canton == null ? "" : canton.getShortname() ); sb.append("\";");

		//if(isSingleFile()){
			sb.append( rirow.getYear() ); sb.append(";");
		//}
		return sb.toString();
	}
	
	private boolean checkFilter(int value, String filter){
		boolean val = true;
		if(filter != null && !"".equals(filter)){
			String[] list = filter.split(",");
			if( list.length > 0 ){
				val = false;			
				for(String str : list){
					try{
						if(Integer.parseInt(str) == value){
							val = true;
							break;
						}
					}
					catch(NumberFormatException e){						
					}
				}
			}					
		}
		return val;	
	}
	
	protected boolean checkSiteFilter(int id){
		return checkFilter(id, exportFilter);
	}
	
	protected boolean checkYearFilter(int year){
		return checkFilter(year, yearFilter);
	}
	
	protected boolean checkRunFilter(int run){
		return run == 0 || checkFilter(run, runFilter);
	}
	
	protected void storeArray(int[] tmpArray, int from, int count, StringBuilder sb){
		for(int i = from; i < from + count; i++){
			sb.append( String.valueOf(tmpArray[i]) );
			if(i < from + count -1) sb.append(";");
		}								
	}
		
	protected void storeArray(double[] tmpArray, int from, int count, StringBuilder sb){
		for(int i = from; i < from + count; i++){
			sb.append( String.format("%.2f",tmpArray[i]) );
			if(i < from + count -1) sb.append(";");
		}								
	}
	
	protected void appendHeaderStringBase(StringBuilder headerline){
		headerline.append("\"ID_GEN\";");
		
		if(expLevel == ExportLevel.LOCATION){
			headerline.append("\"CODE_LOC\";\"NAME_LOC\";");
			headerline.append("\"CODE_MUN\";\"NAME_MUN\";");
		}		
		if(getExportLevel() == ExportLevel.MUNICIPALITY){
			headerline.append("\"CODE_MUN\";\"NAME_MUN\";");
		}		
		headerline.append("\"CODE_CAN\";\"NAME_CAN\";");

		//if(isSingleFile()){
			headerline.append("\"CYCLE\";");
		//}
	}
	
	protected void appendHeaderStringRelocation(StringBuilder headerline){
		//if(isSingleFile()){
			headerline.append("\"CYCLE\";");
		//}
		headerline.append("\"FROM\";\"TO\";\"R_T\";\"H_T\";\"E_T\";\"F_T\";\"J_T\";");		
		headerline.append("\"E_S01\";\"E_S02\";\"E_S03\";\"E_S04\";\"E_S05\";\"E_S06\";\"E_S07\";\"E_S08\";\"E_S09\";\"E_S10\";");
		headerline.append("\"F_S01\";\"F_S02\";\"F_S03\";\"F_S04\";\"F_S05\";\"F_S06\";\"F_S07\";\"F_S08\";\"F_S09\";\"F_S10\";");
	}	

	protected void appendHeaderStringResTotal(StringBuilder headerline){
		headerline.append("\"R_T\";\"R_00_19\";\"R_20_64\";\"R_65_XX\";");
	}
	protected void appendHeaderStringResidents(StringBuilder headerline){
		for(int i = 0; i < 100; i = i+5){
			if(i==15){
				headerline.append("\"R_" + strUpdate(i,2) + "_"  + strUpdate(i+2,2) + "m\";\"R_" + strUpdate(i,2) + "_"  + strUpdate(i+2,2) + "w\";");
				headerline.append("\"R_" + strUpdate(i+3,2) + "_"  + strUpdate(i+4,2) + "m\";\"R_" + strUpdate(i+3,2) + "_"  + strUpdate(i+4,2) + "w\";");
			}
			else{
				headerline.append("\"R_" + strUpdate(i,2) + "_"  + strUpdate(i+4,2) + "m\";\"R_" + strUpdate(i,2) + "_"  + strUpdate(i+4,2) + "w\";");
			}
		}
		headerline.append("\"R_100-XXm\";\"R_100-XXw\";");
	}
	protected void appendHeaderStringEmployees(StringBuilder headerline){
		headerline.append("\"E_T\";\"E_S01\";\"E_S02\";\"E_S03\";\"E_S04\";\"E_S05\";\"E_S06\";\"E_S07\";\"E_S08\";\"E_S09\";\"E_S10\";");
	}
	protected void appendHeaderStringCompanies(StringBuilder headerline){
		headerline.append("\"E_T\";\"F_T\";\"F_S01\";\"F_S02\";\"F_S03\";\"F_S04\";\"F_S05\";\"F_S06\";\"F_S07\";\"F_S08\";\"F_S09\";\"F_S10\";");
		headerline.append("\"F_Z00\";\"F_Z01\";\"F_Z02_04\";\"F_Z05_09\";\"F_Z10_49\";\"F_Z50250\";\"F_Z250\";");
	}		
	protected void appendHeaderStringGIS(StringBuilder headerline){
		headerline.append("\"R_T\";\"E_T\";\"H_T\";\"F_T\";\"E_S01\";\"E_S02\";\"E_S03\";\"E_S04\";\"E_S05\";\"E_S06\";\"E_S07\";\"E_S08\";\"E_S09\";\"E_S10\";");
	}		
	protected void appendHeaderStringLandLimits(StringBuilder headerline){
		headerline.append("\"FA_MAX_T\";\"FA_MAX_R\";\"FA_MAX_F\";\"FA_OCC_T\";\"FA_OCC_R\";\"FA_OCC_F\";");
	}
	protected void appendHeaderStringOverLimits(StringBuilder headerline){
		headerline.append("\"R_GTH_LMT\";\"E_GTH_LMT\";\"R_MFA_LMT\";\"E_MFA_LMT\";\"T_MFA_LMT\";");
	}		
	protected void appendHeaderStringHouseholds(StringBuilder headerline){
		headerline.append("\"H_T\";\"H_P2-C0\";\"H_P2-C1\";\"H_P2-C2\";\"H_P2-C3\";\"H_P2-C4\";\"H_P2-C5\";\"H_P2-CX\";");			
		headerline.append("\"H_P1-C0\";\"H_P1-C1\";\"H_P1-C2\";\"H_P1-C3\";\"H_P1-C4\";\"H_P1-C5\";\"H_P1-CX\";");
		headerline.append("\"H_Pair\";\"H_Sing\";\"H_P0-C0\";\"H_P0-CX\";\"H_Error\";");			
		headerline.append("\"R_HP2-C0\";\"R_HP2-C1\";\"R_HP2-C2\";\"R_HP2-C3\";\"R_HP2-C4\";\"R_HP2-C5\";\"R_HP2-CX\";");
		headerline.append("\"R_HP1-C0\";\"R_HP1-C1\";\"R_HP1-C2\";\"R_HP1-C3\";\"R_HP1-C4\";\"R_HP1-C5\";\"R_HP1-CX\";");
		headerline.append("\"R_HPair\";\"R_Hsing\";\"R_HP0-C0\";\"R_HP0-CX\";\"R_HError\";\"R_T\";");
		
		headerline.append("\"H_Type0\";\"H_Type1\";\"H_Type2\";\"H_Type3\";\"H_Type4\";");
	}	
	protected void appendHeaderStringCommuting(StringBuilder headerline){
		for(int i = 0; i < 100; i = i+5){
			headerline.append("\"E_D" + strUpdate(i,2) + "_"  + strUpdate(i+4,2) + "\";");
		}
		headerline.append("\"E_D100_X\";");
	}		

	protected void appendHeaderStringRes_Workers(StringBuilder headerline){
		headerline.append("\"R_wse\";");
	}		
	
	protected void appendHeaderStringR2rank(StringBuilder headerline){
		headerline.append("\"RANK_RES\";\"RANK_EMPL\";\"RANK_SUM\";");
	}
	
	protected String strUpdate(int num, int count){
		String st = String.valueOf(num);
		if(st.length() < count){
			int rep = count - st.length();
			for(int i = 0; i < rep; i++) st = "0" + st;
		}
		return st;
	}
	
	protected String getHeadingInfoLines() throws IOException{
		return getHeadingInfoLines(description);
	}
	
	protected String getHeadingInfoLines(String description) throws IOException{
		StringBuilder headerline = new StringBuilder(f.getName());
		headerline.append("\r\n");
		headerline.append(description == null ? "" : description);
		headerline.append("\r\n");
		headerline.append(extraDescription == null ? "" : extraDescription);
		headerline.append("\r\n");
		headerline.append(f.getAbsolutePath());			
		headerline.append("\r\n");
		headerline.append("\r\n");
		return headerline.toString();
	}
	
}

class RunIndicatorsFileHelper {
	public RunIndicatorsRow row;
	public RunIndicatorsAVGRow avgrow;
	public Location loc;
	public Municipality municipality;
	public Canton canton; 
}
