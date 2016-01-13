package org.falcsim.agentmodel.dao.csv.util;

import java.util.Set;

/**
 * Process query conditions
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class Condition {
	
	public String columnName;
	public Operation operation;
	public Object value;
	
	
	private static <T> boolean eq(final T read, final T required){
		if(read instanceof Integer)
			return ((Integer)read == (Integer)required);
		if(read instanceof Double)
			return ((Double)read == (Double)required);
		if(read instanceof String)
			return ((String)read).compareToIgnoreCase((String)required) == 0 ? true : false;
		
		return false;
	}
	
	private static <T> boolean lt(final T read, final T required){
		if(read instanceof Integer)
			return ((Integer)read < (Integer)required);
		if(read instanceof Double)
			return ((Double)read < (Double)required);
		
		return false;
	}
	
	private static <T> boolean leq(final T read, final T required){
		if(read instanceof Integer)
			return ((Integer)read <= (Integer)required);
		if(read instanceof Double)
			return ((Double)read <= (Double)required);
		
		return false;
	}
	
	private static <T> boolean gt(final T read, final T required){
		if(read instanceof Integer)
			return ((Integer)read > (Integer)required);
		if(read instanceof Double)
			return ((Double)read > (Double)required);
		
		return false;
	}
	
	private static <T> boolean geq(final T read, final T required){
		if(read instanceof Integer)
			return ((Integer)read > (Integer)required);
		if(read instanceof Double)
			return ((Double)read > (Double)required);
		
		return false;
	}
	
	public static boolean applyLogic(Logic logic, Boolean b1, Boolean b2){
		return (logic == Logic.AND) ? b1 && b2 : b1 || b2;
	}
	
	private static <T> boolean inSet(final Object input, final Set<T> value2){
		return value2.contains(input);
	}
	
	public static boolean matches(Condition condition, Object input){
		switch(condition.operation){
			case EQ:
				return Condition.eq(input, condition.value);
			case LT:
				return Condition.lt(input, condition.value);
			case LEQ:
				return Condition.leq(input, condition.value);
			case GT:
				return Condition.gt(input, condition.value);
			case GEQ:
				return Condition.geq(input, condition.value);
			case IN:
				return Condition.inSet(input, (Set)condition.value);
			default:
				return true;
		}
	}
}
