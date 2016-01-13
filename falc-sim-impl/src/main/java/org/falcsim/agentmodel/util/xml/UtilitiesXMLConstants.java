package org.falcsim.agentmodel.util.xml;

import java.util.ArrayList;

public class UtilitiesXMLConstants {
	public static final String MOVE_PROBS = "move_probabilities";
	public static final String MOVE_PROB = "move_probability";
	public static final String MOVE_B = "move_b";
	public static final String MOVE_H = "move_h";
	public static final String H_COEFFICIENTS = "h_coefficients";
	public static final String B_COEFFICIENTS = "b_coefficients";
	public static final String COEFFICIENTS = "coefficients";
	public static final String COEFFICIENT = "coefficient";
	public static final String UTILITIES = "utilities";
	public static final String UTILITY = "utility";
	public static final String TYPE = "type";
	public static final String X = "X";
	public static final String LOC_TYPE = "loctype";
	public static final String ALT_TYPE = "alttype";
	public static final String B_SECTOR = "bsector";
	public static final String H_TYPE = "htype";
	public static final String UTIL_FUNCTION = "utfunction";
	public static final String UTIL_FUNCTION_H = "utfunction_h";
	public static final String UTIL_FUNCTION_B = "utfunction_b";
	public static final String FUNCTIONS="functions";
	public static final String H_FUNCTIONS="h_functions";
	public static final String B_FUNCTIONS="b_functions";
	public static final String ALTERNATIVE = "alternative";
	public static final String BUSINESSES = "businesses";
	public static final String HOUSEHOLDS = "households";
	public static final String PARAMETERS = "params";
	public static final String SECTOR = "sector";
	public static final String PARAMETER = "param";
	public static final String ID = "id";
	public static final String VALUE = "value";
	public static final String STAY = "STAY";
	public static final String MOVE = "MOVE";
	public static final String MOVE_MIGR = "MOVE_MIGR";
		
	public static final ArrayList<String[]> propertiesList = new ArrayList<String[]>() {
		private static final long serialVersionUID = 1L;
		{
			add(new String[] {COEFFICIENTS, COEFFICIENT});
			add(new String[] {UTILITIES, ALTERNATIVE});
		   }
		};

}
