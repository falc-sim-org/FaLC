package org.falcsim.agentmodel.hist.dao;

/** 
* Removes entities closed in current year of universe evolution
*  
* Formerly used for:
* Data access methods for the historisation of service changes,
*  i.e, for keeping track and reconstructing the population state at any given run.
* 
* @author regioConcept AG
* @version 0.5
* 
*/
public interface HistoryDao {
	
	/** records changes of data
	 * creating history data
	 */
	 public void make_history();
	 
		 
	/** removes dead objects from the population tables
	 */
	 public void remove_corpses();
}
