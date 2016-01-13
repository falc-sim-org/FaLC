package org.falcsim.agentmodel.app;

import java.util.List;

import org.falcsim.agentmodel.app.domain.ScenarioParameters;
import org.falcsim.agentmodel.exceptions.RCCustomException;

/** Synthesis / Universe manager interface
*
* @author regioConcept AG
* @version 1.0
* @since   0.5  
* 
*/
public interface UniverseManager {

	public int getCurrentReferenceRun();	
	
	public String getCurrentDistanceTable();
	public void setCurrentDistanceTable(String distanceTableName);
	
	public String getEntityTable(String defaultTable);
	
	public void copyUniverse(int refRunID);
	public void runSynthese() throws RCCustomException;
	public void runUniverse() throws Exception;
	public void setSessionDirectory(String date);
	
	public void init(String falcProjectDirPath) throws Exception;
	
	public List<String> GetActiveScenarios();
	public void SetScenario(String scenario);

}
