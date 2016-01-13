package org.falcsim.agentmodel.distances.domain;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;
import org.falcsim.agentmodel.domain.ICSVWrite;

/**
 * Generalized distances info between two locations
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public class DistanceRecord implements ICSVWrite{
	public double distance = -1;
	public double carTime = -1;
	public double publicTime = -1;
	public double bicycleTime = -1;
	
	public double publicDistance = -1;
	public double bicycleDistance = -1;
	
	@Override
	public String getRowString(String separator, Object... others) {
		StringBuilder sb = new StringBuilder();
		// adds the 2 ids
		sb.append(others[0] + separator);
		sb.append(others[1] + separator);
		// add others
		sb.append(ClassStringUtil.formatDouble(this.distance) + separator);
		sb.append(ClassStringUtil.formatDouble(this.carTime) + separator);
		sb.append(ClassStringUtil.formatDouble(this.publicDistance) + separator);
		sb.append(ClassStringUtil.formatDouble(this.publicTime) + separator);
		sb.append(ClassStringUtil.formatDouble(this.bicycleDistance) + separator);
		sb.append(ClassStringUtil.formatDouble(this.bicycleTime) + separator);
		sb.append(0 + separator);
		sb.append(0);
		return sb.toString();
	}

}
