package org.falcsim.agentmodel.synthese.domain;

/** Parameters for the synthesis module
*
* @author regioConcept AG
* @version 0.5
* 
*/
public class SyntheseParameters {
	
    String dataTables;
    String locidStr;
	
	public String getLocidStr() {
		return locidStr;
	}
	public void setLocidStr(String locidStr) {
		this.locidStr = locidStr;
	}
	public String getDataTables() {
		return dataTables;
	}
	public void setDataTables(String dataTables) {
		this.dataTables = dataTables;
	}
}
