package org.falcsim.agentmodel.domain;

import java.util.List;

import org.springframework.stereotype.Component;

/** Container class for the FaLC service Module, accessing all relevant objects
*
* @author regioConcept AG
* @version 0.5
* 
*/
@Component
public class Universe {

	private List<Location> locations;

	public List<Location> getLocations() {
		return locations;
	}
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	
}
