package org.falcsim.agentmodel.dao.csv.util;

/**
 * Columns filter
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class FilterCsv {
	public static int column(String[] columns, String column){
		for(int i = 0; i < columns.length; i++){
			if(column.equalsIgnoreCase(columns[i]))
				return i;
		}
		return -1;
	}
}
