package org.falcsim.agentmodel.utility.util;

import java.util.HashMap;
import java.util.Map;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;
import parsii.tokenizer.ParseException;

public class MathExpressionParserImplParsii implements MathExpressionParser {

	private Scope scope;
	private Map<String, Variable> paramMap;
	private parsii.eval.Expression mathexpr;
	
	@Override
	public void init(String expression, String[] parameters) throws ParseException {
		//we need correction of expression string, _name is not allowed and does not pass parser...
		scope = Scope.create();
		paramMap = new HashMap<String, Variable>();
		for(String paramname : parameters){
			Variable var = scope.getVariable(paramname);
			paramMap.put(paramname, var);
		}
		mathexpr = Parser.parse(expression, scope);
	}

	@Override
	public double evaluate(Map<String, Double> params) {
		for(String paramname : params.keySet()){
			paramMap.get(paramname).setValue(params.get(paramname));
		}
		return mathexpr.evaluate();
	}

}
