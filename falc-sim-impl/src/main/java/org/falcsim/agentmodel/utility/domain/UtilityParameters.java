package org.falcsim.agentmodel.utility.domain;

import java.util.List;
import java.util.Map;
import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.util.xml.UtilitiesFunctionsJDOMParser_3;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Conveniency interface for parameters exchange and loading
 *
* @author regioConcept AG
* @version 0.5
* 
*/
public abstract class UtilityParameters {
	
	@Autowired
	private UtilitiesFunctionsJDOMParser_3 ujdp; //UtilitiesFunctionsJDOMParser_2
	
	private Map<String, Map<String, String>> utilMap;
	private String filePathUtilitiesXml;
	private List<Location> locs;
	private List<Municipality> muns;
	private List<Canton> cants;
	//private Map<String, Float> distanceMap;
	protected Map<Integer, Map<Integer,Double>> newDistanceMap;
	protected Map<Integer, Map<Integer,Double>> newDistanceTimeMap;
	
	private Boolean locationChoiceManagedByID;
	
	public boolean getLocationChoiceManagedByID() {
		return locationChoiceManagedByID;
	}

	public void setLocationChoiceManagedByID(boolean locationChoiceManagedByID) {
		this.locationChoiceManagedByID = locationChoiceManagedByID;
	}	
	
	public List<Location> getLocs() {
		return locs;
	}
	
	public void setLocs(List<Location> locs) {
		this.locs = locs;
	}
	
	public List<Canton> getCantons() {
		return cants;
	}
	
	public void setCantons(List<Canton> cants) {
		this.cants = cants;
	}
	
	public List<Municipality> getMunicipalities() {
		return muns;
	}
	
	public void setMunicipalities(List<Municipality> muns) {
		this.muns = muns;
	}
	
	public Map<String, Map<String, String>> getUtilMap() {
		return utilMap;
	}

	public void setUtilMap(Map<String, Map<String, String>> utilMap) {
		this.utilMap = utilMap;
	}
	
	public void initUtilMap(){
		this.utilMap = ujdp.loadUtilitiesFunctionsMap(filePathUtilitiesXml);
	}
	
	public boolean isDistanceMapInitialized(){
		return newDistanceMap != null;
	}
	
	public void resetDistanceMap(){
		newDistanceMap = null;
	}
	
	public double getDistance(int locidA, int locidB){
		return newDistanceMap.get(locidA).get(locidB); //.floatValue();
	}
	
	public Map<Integer,Double> getDistanceMap(int locidA){
		return newDistanceMap.get(locidA);
	}
	
	public double getTime(int locidA, int locidB){
		return newDistanceTimeMap.get(locidA).get(locidB); //.floatValue();
	}
	
	public Map<Integer,Double> getTimeMap(int locidA){
		return newDistanceTimeMap.get(locidA);
	}	
	
	public void initDistanceMap(List<Location> locs){	
	}
	
	public String getFilePathUtilitiesXml() {
		return filePathUtilitiesXml;
	}
	
	public void setFilePathUtilitiesXml(String filePathUtilitiesXml) {
		this.filePathUtilitiesXml = filePathUtilitiesXml;
	}
	
	public abstract void init(List<Location> locs);

}
