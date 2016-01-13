package org.falcsim.agentmodel.service.methods.util.domain;

/**
 * Holds counts of all and free jobs
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */
public class JobGroup {
	public int jobcount;
	public int freejobcount;
	public JobGroup(){
		this.jobcount = 0;
		this.freejobcount = 0;
	}
}
