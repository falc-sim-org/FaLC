package org.falcsim.agentmodel.domain.idgenerators;

import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.idgenerators.dao.IdGenerationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * class responsible for generation of unique business id's
 * 
 * @author regioConcept AG
 * @version 0.5
 */
@Component
public class BusinessIdGenerator implements IdGenerator {

	private static int id;
	private static IdGenerationDao igm;

	@Override
	@Autowired
	public void setIdGenerationMethods(IdGenerationDao an_igm) {
		BusinessIdGenerator.igm = an_igm;
	}

	public synchronized static int nextId() {
		return ++id;
	}

	private synchronized static void setId(int an_id) {
		id = an_id;
	}

	public synchronized static int getId() {
		return id;
	}

	//@PostConstruct
	protected void initialize() {
		setId(igm.getMaxIdFromTable(Business.class));
	}

	public void initializeFromUniverse(int an_id) {
		id = an_id;
	}	
	
}
