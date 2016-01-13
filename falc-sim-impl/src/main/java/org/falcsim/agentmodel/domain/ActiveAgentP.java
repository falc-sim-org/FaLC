package org.falcsim.agentmodel.domain;

import java.util.List;

/** Interface for persons-endowed ActiveAgent.
*
* @author regioConcept AG
* @version 0.5
* 
*/

public interface ActiveAgentP extends ActiveAgent {
	
	public List<Person> getPersons();
	public void setPersons(List<Person> lp);

}
