package org.falcsim.agentmodel.service.methods.util;

/** Step methods interface
*
* @author regioConcept AG
* @version 0.5
* 
*/
public interface StepMethods {
	
	/** Sets the step corresponding to the current cycle
	 * 
	 * @param lastStep
	 */
	public  void setStep(Integer lastStep);
	/** Gets the step corresponding to the current cycle
	 * 
	 */
	public Integer getStep();
	public void init() throws Exception;

}
