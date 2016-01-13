package org.falcsim.agentmodel.util.xml.domain;

/** Utility class for parsing and encoding the tavle sequence
 * 
 * @author regioConcept AG
 * @version 0.5
 *
 */
public class MasterChild {
	
	String master;
	String child;
	
	public MasterChild(){}
	public MasterChild(String master, String child){
		this.master = master;
		this.child = child;		
	}
	
	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public String getChild() {
		return child;
	}
	public void setChild(String child) {
		this.child = child;
	}

}
