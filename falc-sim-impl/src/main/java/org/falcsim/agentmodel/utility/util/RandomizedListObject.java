package org.falcsim.agentmodel.utility.util;

import org.falcsim.agentmodel.domain.ActiveAgent;
import org.falcsim.agentmodel.domain.Location;
/**
* Helper class for Randomize list of universe entities
* 
*
* @author regioConcept AG
* @version 1.0
*/
public class RandomizedListObject {
	public Location loc;
	public ActiveAgent a;
	public RandomizedListObject(Location loc, ActiveAgent a){
		this.loc = loc;
		this.a = a;
	}
}
