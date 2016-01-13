package org.falcsim.agentmodel.distances.domain;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;
import org.falcsim.agentmodel.domain.ICSVWrite;


public class SimpleDistanceRecord implements ICSVWrite {
	public double km = -1;
	public double min = -1;
	
	@Override
	public String getRowString(String separator, Object... others) {
		StringBuilder sb = new StringBuilder();
		// adds the 2 ids
		sb.append(others[0] + separator);
		sb.append(others[1] + separator);
		// add others
		sb.append(ClassStringUtil.formatDouble(this.km) + separator);
		sb.append(ClassStringUtil.formatDouble(this.min));
		return sb.toString();
	}
}
