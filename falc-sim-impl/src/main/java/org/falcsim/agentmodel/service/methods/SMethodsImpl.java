package org.falcsim.agentmodel.service.methods;

import org.falcsim.agentmodel.domain.ActiveAgent;
import org.springframework.stereotype.Component;

@Component("SMethods")
public class SMethodsImpl implements SMethods{
	
	public void move(ActiveAgent ag, Integer locId){
		ag.setLocationId(locId);
	}
		
}
