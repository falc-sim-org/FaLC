package org.falcsim.agentmodel.app.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Scenario specific tables, properties and parameters
*
* @author regioConcept AG
* @version 1.0
* @since   0.5  
*/
public class ScenarioParameters {

	private static final String PROPERTY = "property";
	private static final String TABLE = "table";
	
	private boolean active = false;
	private String id;
	private String description;
	
	private String content;
	private boolean markdiff = true;
	
	private Map<String,String> properties;
	private Map<String,String> tables;
	
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	public Map<String, String> getTables() {
		return tables;
	}
	public void setTables(Map<String, String> tables) {
		this.tables = tables;
	}
	
	public ScenarioParameters(boolean active, String id, String description){
		this.active = active;
		this.id = id;
		this.description = description;
		
		properties = new HashMap<String,String>();
		tables = new HashMap<String,String>();
	}
	
	public void AddSettings(String id, String key, String value){
		Map<String,String> map = null;
		if(PROPERTY.equals(id.trim().toLowerCase())){
			map = properties;
		}
		if(TABLE.equals(id.trim().toLowerCase())){
			map = tables;
		}		
		if(map != null){
			map.put(key, value);
		}
	}
	
	public static boolean exists(List<ScenarioParameters> params, String id){
		for(ScenarioParameters sp : params){
			if(sp.active && sp.id == id)return true;
		}
		return false;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean getMarkdiff() {
		return markdiff;
	}
	public void setMarkdiff(boolean markdiff) {
		this.markdiff = markdiff;
	}	
	
}
