package org.falcsim.agentmodel.utility.util;

import java.util.Map;

import parsii.tokenizer.ParseException;

/**
* Mathematics formula evaluator.
* Formula is stored as XML file with static and dynamic parameters.
* Dynamic parameters are evaluated on the fly during universe evolution
*
* @author regioConcept AG
* @version 1.0
*/
public interface MathExpressionParser {
	
	public void init(String expression, String[] parameters)  throws ParseException;
	public double evaluate(Map<String, Double> params);
}
