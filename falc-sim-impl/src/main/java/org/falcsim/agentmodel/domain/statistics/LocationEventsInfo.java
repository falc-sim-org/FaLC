package org.falcsim.agentmodel.domain.statistics;

/**
 * Supplementary class for universe events duration statistics
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */
public class LocationEventsInfo {
	public int res_death = 0;
	public int res_birth = 0;
	public int res_immigraton = 0;
	public int hh_closed = 0;
	public int hh_kidsLeave = 0;
	public int hh_divorced = 0;
	public int hh_merged = 0;	
	public int wrk_quited = 0;
	public int wrk_joined = 0;	
	public int bb_closed = 0;
	public int bb_closed_no_people = 0;
	public int bb_closed_jobs = 0;
	public int bb_created = 0;	
	public int bb_created_jobs = 0;	
	
	public int res_come_in = 0;	
	public int wrk_come_in = 0;
	public int hh_come_in = 0;
	public int bb_come_in = 0;
	public int res_go_out = 0;	
	public int wrk_go_out = 0;
	public int hh_go_out = 0;
	public int bb_go_out = 0;
	
	public int[] businessesbysector_come_in;
	public int[] workersbysector_come_in;
	public int[] businessesbysector_go_out;
	public int[] workersbysector_go_out;	
	
	public void clear(){
		res_death = 0;
		res_birth = 0;
		res_immigraton = 0;
		hh_closed = 0;
		hh_kidsLeave = 0;
		hh_divorced = 0;
		hh_merged = 0;	
		wrk_quited = 0;
		wrk_joined = 0;	
		bb_closed = 0;
		bb_closed_no_people = 0;
		bb_closed_jobs = 0;
		bb_created = 0;
		bb_created_jobs = 0;
		 
		res_come_in = 0;	
		wrk_come_in = 0;
		hh_come_in = 0;
		bb_come_in = 0;
		res_go_out = 0;	
		wrk_go_out = 0;
		hh_go_out = 0;
		bb_go_out = 0;
	}
	
	public void Add(LocationEventsInfo info){
		res_death += info.res_death;
		res_birth += info.res_birth;
		res_immigraton += info.res_immigraton;
		hh_closed += info.hh_closed;
		hh_kidsLeave += info.hh_kidsLeave;
		hh_divorced += info.hh_divorced;
		hh_merged += info.hh_merged;	
		wrk_quited += info.wrk_quited;
		wrk_joined += info.wrk_joined;	
		bb_closed += info.bb_closed;
		bb_closed_no_people += info.bb_closed_no_people;
		bb_closed_jobs += info.bb_closed_jobs;
		bb_created += info.bb_created;
		bb_created_jobs += info.bb_created_jobs;
		 
		res_come_in += info.res_come_in;	
		wrk_come_in += info.wrk_come_in;
		hh_come_in += info.hh_come_in;
		bb_come_in += info.bb_come_in;
		res_go_out += info.res_go_out;	
		wrk_go_out += info.wrk_go_out;
		hh_go_out += info.hh_go_out;
		bb_go_out += info.bb_go_out;		
	}
}
