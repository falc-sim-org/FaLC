package org.falcsim.agentmodel.domain.statistics;

/**
 * Supplementary class for universe relocation statistics
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
public class RelocationInfo {	
	public int residents = 0;
	public int workers = 0;
	public int households = 0;
	public int businesses = 0;
	public int jobs = 0;
	
	public int[] businessesbysector;
	public int[] workersbysector;
	
	public int numofRefRuns = 0;
	private int count = 0;
		
	public RelocationInfo(int count){
		this.count = count;
		businessesbysector = new int[count];
		workersbysector = new int[count];
		for(int i = 0; i < count; i++){
			businessesbysector[i] = 0;
			workersbysector[i] = 0;
		}		
	}
	
	public void Add(RelocationInfo info){
		residents += info.residents;
		households += info.households;
		workers += info.workers;
		businesses += info.businesses;
		jobs += info.jobs;
		for(int i = 0; i < count; i++){
			workersbysector[i] += info.workersbysector[i];
			businessesbysector[i] += info.businessesbysector[i];
		}
				
		numofRefRuns = info.numofRefRuns;
	}
	
}
