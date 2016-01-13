package org.falcsim.agentmodel.dao.jdbc;

import org.falcsim.agentmodel.app.UniverseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Universe managed dynamic entity table mapper implementation
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
@Service(value="dynamicEntityTableMapper")
public class DynamicEntityTableMapperMultiverseImpl implements
		DynamicEntityTableMapper {

	@Autowired
	private UniverseManager um;
	
	@Override
	public <T> String getEntityTable(Class<T> clazz, String defaultTable) {

		return um.getEntityTable(defaultTable);
	}

}
