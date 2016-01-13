package org.falcsim.agentmodel.domain.idgenerators;

import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.idgenerators.dao.IdGenerationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * class responsible for generation of unique households id's
 * 
 * @author regioConcept AG
 * @version 0.5
 */
@Component
public class HouseholdIdGenerator implements IdGenerator{
	
	private static Integer  id;
	private static IdGenerationDao igm;
	
	@Autowired
	@Override
	public  void setIdGenerationMethods(IdGenerationDao an_igm){
		HouseholdIdGenerator.igm=an_igm;
	}
	
	public static Integer createId() {
		id++;
		setId(id);
		return id;
	}
	
	private static void setId(Integer an_id){
		id=an_id;
	}
	
	
	public static Integer getId(){
		return id;
	}
	
	
	//@PostConstruct
	protected void initialize() {
		setId(igm.getMaxIdFromTable(Household.class));
	}
	
	public void initializeFromUniverse(int an_id) {
		id = an_id;
	}	
}
