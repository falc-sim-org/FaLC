package org.falcsim.agentmodel.dao.jdbc;

/**
 * Dynamic entity table mapper interface
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public interface DynamicEntityTableMapper {
	
	/**
	 * return table name for the given class of the entity
	 * @param clazz
	 * @param defaultTable
	 * @return currently used table name
	 */
	public <T> String getEntityTable(Class<T> clazz, String defaultTable);
	
}
