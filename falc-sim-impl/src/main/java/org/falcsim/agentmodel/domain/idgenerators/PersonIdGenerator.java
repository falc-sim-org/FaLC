package org.falcsim.agentmodel.domain.idgenerators;

import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.idgenerators.dao.IdGenerationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * class responsible for generation of unique person id's
 * 
 * @author regioConcept AG
 * @version 0.5
 */
@Component
public class PersonIdGenerator implements IdGenerator {

	/**
	 * current id
	 */
	private static int id;
	/**
	 * database methods
	 */
	private static IdGenerationDao igm;

	/**
	 * initialize database methods
	 */
	@Override
	@Autowired
	public void setIdGenerationMethods(IdGenerationDao an_igm) {
		PersonIdGenerator.igm = an_igm;
	}

	/**
	 * return next unique id
	 * 
	 * @return next Id
	 */
	public synchronized static int nextId() {
		return ++id;
	}

	/**
	 * initialize counter
	 * 
	 * @param an_id
	 */
	private synchronized static void setId(int an_id) {
		id = an_id;
	}

	/**
	 * return last created id
	 * 
	 * @return current Id
	 */
	public synchronized static int getId() {
		return id;
	}

	//@PostConstruct
	protected void initialize() {
		setId(igm.getMaxIdFromTable(Person.class));
	}

	public void initializeFromUniverse(int an_id) {
		id = an_id;
	}	
	
}
