package org.falcsim.agentmodel.hist.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.dao.AbstractDao;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.dao.jdbc.JDBCApproach;
import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.service.UniverseServiceUtil;
import org.falcsim.agentmodel.service.domain.ServiceParameters;
import org.falcsim.agentmodel.service.methods.util.StepMethods;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class HistoryDaoMemoryImpl extends AbstractDao implements HistoryDao {

	@Autowired
	private RunParameters rp;
	@Autowired
	private StepMethods sm;
	@Autowired
	private ServiceParameters sp;
	@Autowired
	private UniverseServiceUtil universeService;
	
	private static Logger logger =  Logger.getLogger(HistoryDaoMemoryImpl.class);
	private List<String> tnls;
		
	@PostConstruct
	void init(){
		tnls = loadTableNames();
	}
	
	@Override
	public void make_history() {
		remove_corpses();
		sm.setStep(sm.getStep()+1);
		logger.info("Closed objects removed.");
	}
	
	public void remove_corpses(){
	
		logger.info("In memory - Remove closed objects.");
		int currYear = sp.getCurrentYear();	
		
		int hcount = 0;
		int pcount = 0;
		int bcount = 0;
		int pcountb = 0;
		
		List<Location> locs = universeService.selectAllLocations();
		
		for(Location loc : locs){
			//HOUSEHOLDS
			List<Household> pListHHDel = new ArrayList<Household>();
			for(Household h : loc.getHouseholds()){
				//check death persons first
				List<Person> pList = h.getPersons();
				List<Person> pListDel = new ArrayList<Person>();
				for(Person p : pList){
					if(p.getDdeath() != null){
						p.setHouseholdId(null);
						p.setPosition_in_hh(null);
						pListDel.add(p);
					}
					else{
						if(!p.getHouseholdId().equals(h.getId())){
							p.setHouseholdId(h.getId());
						}
					}
				}
				//remove death persons
				if(pListDel.size() > 0){
					h.getPersons().removeAll(pListDel);
					pcount += pListDel.size();
				}
				//check household
				if(h.getPersons().size() == 0){
					pListHHDel.add(h);	
					if(h.getDclosing() == null){
						DateTime fDate = new DateTime(currYear, 12, 31, 0, 0, 0);
						h.setDclosing(fDate.toDate());
					}
				}
				else{
					if(h.getDclosing() != null){
						h.setDclosing(null);
					}
				}
			}
			//remove closed households
			loc.getEventsStatus().hh_closed = pListHHDel.size();
			if(pListHHDel.size() > 0){
				loc.getHouseholds().removeAll(pListHHDel);
				hcount += pListHHDel.size();
			}
			pListHHDel.clear();
			
			//BUSINESSES
			List<Business> pListBBDel = new ArrayList<Business>();
			for(Business b : loc.getBusinesses()){

				//check business
				if(b.getDclosing() != null){
					pListBBDel.add(b);
					if(b.getPersons().size() > 0){
						for(Person p : b.getPersons()){
							p.setBusinessId(null);
							p.setPosition_in_bus(null);
							p.setFinance_1(0);
						}
						b.getPersons().clear();
					}
				}
				else{
					if(b.getPersons().size() != b.getNr_of_jobs()){
						logger.debug("In memory - check : Not filled business: bid=" + String.valueOf(b.getId()) + ", lid=" + String.valueOf(b.getLocationId()) 
								 + ", status=" + String.valueOf(b.getPersons().size()) + " / " +  String.valueOf(b.getNr_of_jobs()) );
					}
					//check death persons if company not closed
					List<Person> pList = b.getPersons();
					List<Person> pListDel = new ArrayList<Person>();
					for(Person p : pList){
						if(p.getDdeath() != null){
							pListDel.add(p);
							p.setBusinessId(null);
							p.setPosition_in_bus(null);
							p.setFinance_1(0);
						}
						else{
							if(!p.getBusinessId().equals(b.getId())){
								//fix person
								p.setBusinessId(b.getId());
							}
						}
					}
					//remove death persons
					if(pListDel.size() > 0){
						b.getPersons().removeAll(pListDel);
						pcountb += pListDel.size();
					}
					
					if(b.getPersons().size() == 0 && b.getDclosing() == null){
						loc.getEventsStatus().bb_closed_no_people ++;
						pListBBDel.add(b);
						logger.warn("In memory - Removing : Opened business without persons: " + String.valueOf(b.getId()) );
					}
				}
			}
			
			//remove closed businesses
			if(pListBBDel.size() > 0){
				loc.getBusinesses().removeAll(pListBBDel);
				bcount += pListBBDel.size();
			}
			pListBBDel.clear();
			
			updateCounts(loc);
			universeService.saveOrUpdateLocation(loc);
		}
		
		logger.info("In memory - Removed: H: " + String.valueOf(hcount) + ", P: " + String.valueOf(pcount) + ", B: " + String.valueOf(bcount) + ", PinB: " + String.valueOf(pcountb));
	}
	
	private void updateCounts(Location loc){
		int pcount = 0;
		for(Household h : loc.getHouseholds()){
			pcount += h.getPersons().size();
		}
		loc.setActualResidents(pcount); //vzto
		
		Set<Integer> distinct_sectors = new HashSet<Integer>();
		pcount = 0;
		for(Business b : loc.getBusinesses()){
			pcount += b.getPersons().size();
			if(!distinct_sectors.contains(b.getType_1())) distinct_sectors.add(b.getType_1());
		}
		loc.setActualWorkers(pcount); //bsch
	}

	private  List<String> loadTableNames(){
		List<String> tableNames = new ArrayList<String>();
		tableNames.add(Person.class.getName());
		tableNames.add(Household.class.getName());
		tableNames.add(Business.class.getName());
		return tableNames;
	}

}
