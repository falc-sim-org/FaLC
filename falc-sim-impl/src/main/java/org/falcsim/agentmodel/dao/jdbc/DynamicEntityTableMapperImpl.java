package org.falcsim.agentmodel.dao.jdbc;

/**
 * Default dynamic entity table mapper implementation
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class DynamicEntityTableMapperImpl implements DynamicEntityTableMapper {

	@Override
	public <T> String getEntityTable(Class<T> clazz, String defaultTable) {
		return defaultTable;
	}

}
