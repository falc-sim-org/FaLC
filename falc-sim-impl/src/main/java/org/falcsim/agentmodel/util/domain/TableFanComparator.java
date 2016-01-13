package org.falcsim.agentmodel.util.domain;

import java.util.Comparator;

/**  TableFan comparator for a table validity span.
 * 
 * 
 * @author regioConcept AG
 *
 */
public class TableFanComparator implements Comparator<TableFan>{

	@Override
	public int compare(TableFan o1, TableFan o2) {
			return o1.compareTo(o2);
	}	
}
