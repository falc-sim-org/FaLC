package org.falcsim.agentmodel.util.domain;

/**  TableFan encodes a table name and its start of validity.
 * 
 * 
 * @author regioConcept AG
 *
 */
public class TableFan implements Comparable<Object>{
	
	String tableName;
	Integer start;
	Boolean active;
	
	public TableFan(){}

	public TableFan(String tableName, Integer start) {
        this.tableName = tableName;
        this.start = start;
    }
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	
	public int compareTo(Object tbCompare) {
		Integer cmpStart = ((TableFan) tbCompare).getStart();
		return start-cmpStart;
	}

	

}
