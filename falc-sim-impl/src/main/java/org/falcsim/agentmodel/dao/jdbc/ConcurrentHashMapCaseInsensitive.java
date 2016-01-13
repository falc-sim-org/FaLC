package org.falcsim.agentmodel.dao.jdbc;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Case insensitive concurrent HashMap 
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class ConcurrentHashMapCaseInsensitive<T> extends ConcurrentHashMap<String, T>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public T put(String key, T value){
		return super.put(key, value);
	}
	
	public boolean containsKey(String key){
		return super.containsKey(key);
	}
	
	public T get(String key){
		return super.get(key);
	}
}