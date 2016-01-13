package org.falcsim.agentmodel.util.xml.domain;

public class MasterChildOverride extends MasterChild {
	
	String type;
	String id;
	
	public MasterChildOverride(String master, String child, String type, String id){
		this.master = master;
		this.child = child;
		this.type = type;	
		this.id = id;	
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
}
