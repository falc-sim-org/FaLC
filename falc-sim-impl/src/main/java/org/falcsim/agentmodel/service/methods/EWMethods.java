package org.falcsim.agentmodel.service.methods;

import java.util.List;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;

/** Models migration of population and control(corects) total size of residents  
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public interface EWMethods {
	
	public enum MigrantionType {
		 MIGRANTS, CONTROL_TOTALS_RESIDENTS; 
	}
	
	public void migrate(Location loc);
	public void migrate(List<Location> locs, MigrantionType type) throws IllegalArgumentException;
	public void init();
	public void writeCSVindicators();
	
	public static Logger logger =  Logger.getLogger(EWMethods.class);

}