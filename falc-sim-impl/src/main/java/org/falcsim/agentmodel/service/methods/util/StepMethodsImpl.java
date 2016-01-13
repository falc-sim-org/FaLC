package org.falcsim.agentmodel.service.methods.util;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/** Initializes the step
 * 
 * @author regioConcept AG
 *
 */
@Component
public class StepMethodsImpl implements StepMethods {
	
	private Integer lastStep;
	
	public  void setStep(Integer lastStep){
		this.lastStep = lastStep;
	}
	
	public Integer getStep(){
		return lastStep;
	}

	@PostConstruct
	public void init() throws Exception {
		this.lastStep = 0;
	}

}
