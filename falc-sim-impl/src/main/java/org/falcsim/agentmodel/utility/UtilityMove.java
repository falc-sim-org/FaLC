package org.falcsim.agentmodel.utility;

import java.util.List;

import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.exceptions.RCCustomException;




/** Key interface of the dynamic module. 
 * Moves business and households from one location to another.
 * 
 * @author regioConcept AG
 *
 */
public interface UtilityMove {
	
	
	/** moves households and businesses
	 * 
	 * @param loc - the chosen location from which the businesses/households are moved
	 * @param locs -  locs: the target locations, among which the destination is chosen.
	 */
	public void moveHB (Location loc, List<Location> locs) throws RCCustomException;
	/** moves households 
	 * 
	 * @param loc - the chosen location from which the businesses/households are moved
	 * @param locs -  locs: the target locations, among which the destination is chosen.
	 */
	public void moveH (Location loc, List<Location> locs) throws RCCustomException;
	/** moves businesses 
	 * 
	 * @param loc - the chosen location from which the businesses/households are moved
	 * @param locs -  locs: the target locations, among which the destination is chosen.
	 */
	public void moveB (Location loc, List<Location> locs) throws RCCustomException;
	
	/** moves households with building of random list of all households
	 * 
	 * @param locs -  locs: the target locations, among which the destination is chosen.
	 */
	public void moveHTAllLocations(List<Location> locs) throws RCCustomException;
	public void moveSelectedHouseholdsIntoSelectedLocationsThreads(List<Household> hhs, List<Location> locs) throws RCCustomException;
	
	
	/** moves businesses with building of random list of all businesses
	 * 
	 * @param locs -  locs: the target locations, among which the destination is chosen.
	 */
	public void moveBTAllLocations(List<Location> locs) throws RCCustomException;
	
	/**
	 * Initialization 
	 */
	public void init ();
	
	
}
