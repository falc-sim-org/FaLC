package org.falcsim.agentmodel.hist.dao;

/** 
* Data access methods for the current run in the db.
* 
* @author regioConcept AG
* @version 0.5
* 
*/
public interface StepDao {
	
	/**
	 * 
	 * @return the last run in the db
	 */
	public Integer lastStep();
	/**
	 * set the current run in the db
	 */
	public void updateSteps();

	/**
	 * set the defined run in the db
	 */
	public void updateSteps(int run, int year);
}
