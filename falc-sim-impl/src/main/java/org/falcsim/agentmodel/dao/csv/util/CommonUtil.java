package org.falcsim.agentmodel.dao.csv.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Array transformation
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class CommonUtil {	
	public static <T> Set<Integer> intersection(List<T> array, Set<T> set){
		Set<Integer> ret = new HashSet<Integer>();
		if(array == null || array.size() == 0){
			for(int i = 0; i < set.size(); i++)
				ret.add(i);
			return ret;
		}
		
		for(int i = 0; i < array.size(); i++){
			if(set.contains(array.get(i)))
				ret.add(i);
		}
		return ret;
	}
	
	public static <T> Set<T> stringToSet(String value, Class<?> type){
		value = value.replaceAll("\\(", "");
		value = value.replaceAll("\\)", "");
		String[] splitted = value.split(",");
		Set<T> ret = new HashSet<T>();
		
		if(splitted.length == 0)
			return ret;
		
		for(String s : splitted){
			if(s.equalsIgnoreCase("") || s.length() == 0)
				continue;
			if(type == Integer.class){
				Integer i = Integer.valueOf(s.replaceAll(" ", ""));
				ret.add((T)i);
			} else if(type == Double.class){
				Double d = Double.valueOf(s.replaceAll(" ", ""));
				ret.add((T)d);
			}
		}
		
		return ret;
	}
}
