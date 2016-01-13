package org.falcsim.agentmodel.service.methods;

import java.util.List;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Location;

/** Economic development methods models percentage growth per company sector
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public interface BusinessEconomicDevRev1 {
	public static int num_of_workplaces_per_company = 4;
	
	public Double getGrowth_businesses_perc();
	public int getNumOfNewBusinesses(Location loc, int sector);
	
	public void calculateEconomicVariables(List<Location> locs);
	public static Logger logger =  Logger.getLogger(BusinessEconomicDevRev1.class);
	public void init();
}
