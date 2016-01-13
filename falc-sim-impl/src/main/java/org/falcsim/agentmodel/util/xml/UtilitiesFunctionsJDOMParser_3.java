package org.falcsim.agentmodel.util.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.RCConstants;
import org.falcsim.agentmodel.dao.AbstractDao;
import org.falcsim.agentmodel.util.xml.domain.MasterChild;
import org.falcsim.agentmodel.util.xml.domain.MasterChildOverride;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.springframework.stereotype.Component;

/** JDOM parser for the utility functions configuration file
 * Used for in memory calculations with possibility to configure with xml
 * 
 * @author Peter Furtak
 * @version 0.5
 *
 */
@Component
public class UtilitiesFunctionsJDOMParser_3 {
		
		private static final String MOVE_PROB = UtilitiesXMLConstants.MOVE_PROB;
		private static final String MOVE_B = UtilitiesXMLConstants.MOVE_B;
		private static final String MOVE_H = UtilitiesXMLConstants.MOVE_H;
		private static final  String FUNCTIONS=UtilitiesXMLConstants.FUNCTIONS;
		private static final  String H_FUNCTIONS=UtilitiesXMLConstants.H_FUNCTIONS;
		private static final  String B_FUNCTIONS=UtilitiesXMLConstants.B_FUNCTIONS;
		private static final  String BUSINESSES=UtilitiesXMLConstants.BUSINESSES;
		private static final  String HOUSEHOLDS=UtilitiesXMLConstants.HOUSEHOLDS;
		private static final  String COEFF=UtilitiesXMLConstants.COEFFICIENT;
		private static final  String B_COEFFS=UtilitiesXMLConstants.B_COEFFICIENTS;
		private static final  String H_COEFFS=UtilitiesXMLConstants.H_COEFFICIENTS;
		
		private static final  String UTILITY=UtilitiesXMLConstants.UTILITY;
		private static final  String ALT_TYPE=UtilitiesXMLConstants.ALT_TYPE;
		private static final  String SECTOR=UtilitiesXMLConstants.SECTOR;
		private static final  String SEP=RCConstants.SEP;
		private static final  String UTIL_FUNCTION_H = UtilitiesXMLConstants.UTIL_FUNCTION_H;
		private static final  String UTIL_FUNCTION_B = UtilitiesXMLConstants.UTIL_FUNCTION_B;
		private static final  String ID = UtilitiesXMLConstants.ID;
		private static final  String VALUE = UtilitiesXMLConstants.VALUE;
		private static final  String VALUE_BASE ="value_base";
		private static final  String VALUE_STAY ="value_stay";
		private static final  String VALUE_MOVE ="value_move";
		private static final  String VALUE_MOVE_MIGRANTS ="value_move_migrants";		
		private static final  String X = UtilitiesXMLConstants.X;
		private static final  String STAY = UtilitiesXMLConstants.STAY;
		private static final  String MOVE = UtilitiesXMLConstants.MOVE;		
		
		private static final String RATE_FACTORS = "RATE_FACTORS";
		private static final  String RRFH = "h_relocation_rate_factors"; //for households
		private static final  String RRFB = "b_relocation_rate_factors"; //for businesses		
		private static final  String FACTOR = "factor";
		private static final  String OVERRIDE = "override";
		private static final  String MAXGROWTH = "relocation_max_growth_factors";
		private static final  String REFERENCE = "reference";
		
		public static Logger logger = Logger.getLogger(UtilitiesFunctionsJDOMParser_3.class);
		
		/** Parses the xml document containg the utility functions and returns a map with the values of the various values
		 * 
		 * @param filepath
		 * @return the map
		 */
		public Map<String, Map<String, String>> loadUtilFunctionsConfigMap (String filepath){
			JDOMUtil jdu = new JDOMUtil();
			Document document = jdu.loadJDom(filepath);	
			logger.info("loadUtilFunctionsConfigMap: " + filepath);
			return parseUtilitiesConfig(document);
		}
		
		public Map<String, Map<String, String>> loadUtilitiesFunctionsMap(String filepath){
			JDOMUtil jdu = new JDOMUtil();
			Document document = jdu.loadJDom(filepath);
			logger.info("loadUtilitiesFunctionsMap: " + filepath);
			return parseUtilitiesFunctions(document);
		}
		
		@SuppressWarnings("unchecked")
		private  Map<String, Map<String, String>> parseUtilitiesConfig(Document document){
			Map<String , Map<String, String>> utMap = new HashMap<String , Map<String, String>> ();
			Element rootNode = document.getRootElement();
			Element bnode = rootNode.getChild(BUSINESSES);
			Iterator<Element> businessIterator = bnode.getDescendants(new ElementFilter(UTILITY));
			Map<String, String> bMap = new HashMap<String, String>();
			while (businessIterator.hasNext()){
				Element bEl = businessIterator.next();
				//assigns the utility function to the combination of business sector and location type
				bMap.put(bEl.getAttributeValue(ALT_TYPE) +SEP+ bEl.getAttributeValue(SECTOR), bEl.getAttributeValue(UTIL_FUNCTION_B));		
			}
			utMap.put(BUSINESSES, bMap );
			Element hnode = rootNode.getChild(HOUSEHOLDS);
			Iterator<Element> householdIterator = hnode.getDescendants(new ElementFilter(UTILITY));
			Map<String, String> hMap = new HashMap<String, String>();
			while (householdIterator.hasNext()){
				Element hEl = householdIterator.next();
				//assigns the utility function to the combination of household and location type
				hMap.put(hEl.getAttributeValue(ALT_TYPE) +SEP+ hEl.getAttributeValue(UtilitiesXMLConstants.TYPE), hEl.getAttributeValue(UTIL_FUNCTION_H));		
			}
			utMap.put(HOUSEHOLDS, hMap );
			return utMap;
		}
		
		@SuppressWarnings("unchecked")
		private Map<String, Map<String, String>> parseUtilitiesFunctions (Document document){
			Map<String, Map<String, String>> overridesMap = new HashMap<String, Map<String, String>>();
			
			Map<String, Map<String, String>> outMap = new HashMap<String, Map<String, String>>();
			Element rootNode = document.getRootElement();
			Element hnode = rootNode.getChild(H_COEFFS);

			List<MasterChild> coeffListH = new ArrayList<MasterChild>();
			List<MasterChildOverride> coeffListHoverrides = new ArrayList<MasterChildOverride>();
			
			Iterator<Element> coeffsIteratorH = hnode.getDescendants(new ElementFilter(COEFF));
			while (coeffsIteratorH.hasNext()){
				Element hEl = coeffsIteratorH.next();
				//assigns the utility function to the combination of business sector and location type
				coeffListH.add(new MasterChild(hEl.getAttributeValue(ID), hEl.getAttributeValue(VALUE)));
				CheckCoefsOverrides(hEl, coeffListHoverrides);
			}
			
			Element param_util_node = rootNode.getChild(UtilitiesXMLConstants.PARAMETERS);
			Map<String, String> paramMap = new HashMap<String, String>();
			Iterator<Element> paramIterator = param_util_node.getDescendants(new ElementFilter(UtilitiesXMLConstants.PARAMETER));
			while (paramIterator.hasNext()){
				Element pEl = paramIterator.next();
				paramMap.put(pEl.getAttributeValue(ID), "");		
			}
					
			Element h_util_node = rootNode.getChild(FUNCTIONS).getChild(UTIL_FUNCTION_H);
			Map<String, String> h_funcMap = new HashMap<String, String>();
			//h_funcMap.put(X, fillInCoefficients(h_util_node.getAttributeValue(VALUE), coeffListH));	
			h_funcMap.put("BASE" +SEP+ X, fillInCoefficients(h_util_node.getAttributeValue(VALUE_BASE), coeffListH));
			h_funcMap.put("MOVE" +SEP+ X, fillInCoefficients(h_util_node.getAttributeValue(VALUE_MOVE), coeffListH));
			h_funcMap.put(UtilitiesXMLConstants.MOVE_MIGR +SEP+ X, fillInCoefficients(h_util_node.getAttributeValue(VALUE_MOVE_MIGRANTS), coeffListH));
						
			Map<String, String> b_funcMap = new HashMap<String, String>();
			Element function_util_node = rootNode.getChild(FUNCTIONS);
			Iterator<Element> bFunctionsIterator = function_util_node.getDescendants(new ElementFilter(UTIL_FUNCTION_B));
			while (bFunctionsIterator.hasNext()){
				Element b_util_node = bFunctionsIterator.next();

				String base = b_util_node.getAttributeValue(VALUE_BASE);
				String move = b_util_node.getAttributeValue(VALUE_MOVE);
				
				Element bnode = rootNode.getChild(B_COEFFS);
				Iterator<Element> sectorIteratorB = bnode.getDescendants(new ElementFilter(SECTOR));
				while (sectorIteratorB.hasNext()){
					Element sEl = sectorIteratorB.next();
					List<MasterChild> coeffListB = new ArrayList<MasterChild>();
					Iterator<Element> coeffsIteratorB = bnode.getDescendants(new ElementFilter(COEFF));
					while (coeffsIteratorB.hasNext()){
						Element bEl = coeffsIteratorB.next();
						coeffListB.add(new MasterChild(bEl.getAttributeValue(ID), bEl.getAttributeValue(VALUE)));	
						//CheckOverrides(B_COEFFS +"-" + b_util_node.getAttributeValue(ID), bEl, overridesMap);
					}
					
					String base_fill= fillInCoefficients(base, coeffListB);
					b_funcMap.put("BASE" +SEP+ b_util_node.getAttributeValue(ID) +SEP+ sEl.getAttributeValue(ID), base_fill );
					b_funcMap.put("MOVE" +SEP+ b_util_node.getAttributeValue(ID) +SEP+ sEl.getAttributeValue(ID), fillInCoefficients(move, coeffListB) );
				}
			}

			Map<String, String> moveBMap = new HashMap<String, String>();
			Iterator<Element> moveBIterator = rootNode.getDescendants(new ElementFilter(MOVE_B));
			while (moveBIterator.hasNext()){
				Element uEl = moveBIterator.next();
				//assigns the utility function to the combination of business sector and location type
				moveBMap.put(uEl.getAttributeValue(SECTOR), formatProb(uEl.getAttributeValue(MOVE_PROB)));		
			}
			
			Map<String, String> moveHMap = new HashMap<String, String>();
			Iterator<Element> moveHIterator = rootNode.getDescendants(new ElementFilter(MOVE_H));
			while (moveHIterator.hasNext()){
				Element uEl = moveHIterator.next();
				moveHMap.put(uEl.getAttributeValue(UtilitiesXMLConstants.TYPE), formatProb(uEl.getAttributeValue(MOVE_PROB)));		
			}
			
			//Relocation rate factor
			Map<String, String> ratefactorMap = new HashMap<String, String>();
			Element rnode = rootNode.getChild(RRFH);

			ratefactorMap.put("RRF_H_REFERENCE", rnode.getAttributeValue(REFERENCE) );
			
			Iterator<Element> factorIterator = rnode.getDescendants(new ElementFilter(FACTOR));
			while (factorIterator.hasNext()){
				Element uEl = factorIterator.next();
				ratefactorMap.put("RRF_H_" + uEl.getAttributeValue(SECTOR), uEl.getAttributeValue(VALUE));
			}
			rnode = rootNode.getChild(RRFB);
			ratefactorMap.put("RRF_B_REFERENCE", rnode.getAttributeValue(REFERENCE) );
			factorIterator = rnode.getDescendants(new ElementFilter(FACTOR));
			while (factorIterator.hasNext()){
				Element uEl = factorIterator.next();
				ratefactorMap.put("RRF_B_" + uEl.getAttributeValue(SECTOR), uEl.getAttributeValue(VALUE));
			}
			
			Map<String, String> maxGrowthMap = new HashMap<String, String>();
			Element mgnode = rootNode.getChild(MAXGROWTH);
			Iterator<Element> gfactorIterator = mgnode.getDescendants(new ElementFilter(FACTOR));
			while (gfactorIterator.hasNext()){
				Element uEl = gfactorIterator.next();
				maxGrowthMap.put(uEl.getAttributeValue(ID), uEl.getAttributeValue(VALUE));				
				CheckOverrides(MAXGROWTH, uEl, overridesMap);
			}
			
			outMap.put(UtilitiesXMLConstants.PARAMETERS, paramMap);
			outMap.put(MOVE_H, moveHMap);
			outMap.put(MOVE_B, moveBMap);
			outMap.put(H_FUNCTIONS, h_funcMap);
			outMap.put(B_FUNCTIONS, b_funcMap);
			outMap.put(RATE_FACTORS, ratefactorMap);
			outMap.put(MAXGROWTH, maxGrowthMap);
						
			for(String key : overridesMap.keySet()){
				outMap.put(OVERRIDE + "-" + key , overridesMap.get(key) );
			}
			return outMap;
		}
		
		@SuppressWarnings("unchecked")
		private void CheckCoefsOverrides(Element el, List<MasterChildOverride> coeffListHoverrides){

			String mainID = el.getAttributeValue(ID);
			Iterator<Element> overridesIterator = el.getDescendants(new ElementFilter(OVERRIDE));
			while (overridesIterator.hasNext()){
				Element subEl = overridesIterator.next();
				String type = subEl.getAttributeValue(UtilitiesXMLConstants.TYPE);
				String id = subEl.getAttributeValue(ID);
				String value = subEl.getAttributeValue(VALUE);
				
				coeffListHoverrides.add(new MasterChildOverride(mainID, value, type, id) );
			}
		}
		
		@SuppressWarnings("unchecked")
		private void CheckOverrides(String kind, Element el, Map<String, Map<String, String>> overridesMap){
						
			String mainID = el.getAttributeValue(ID);
			mainID = mainID != null ? mainID.trim().toUpperCase() : "";
			
			Iterator<Element> overridesIterator = el.getDescendants(new ElementFilter(OVERRIDE));
			while (overridesIterator.hasNext()){
				Element subEl = overridesIterator.next();
				String type = subEl.getAttributeValue(UtilitiesXMLConstants.TYPE);
				type = type != null ? kind + "-" + type.trim().toUpperCase() : "";
				String id = subEl.getAttributeValue(ID);
				
				id = id != null ? mainID + "-" + id.trim().toUpperCase() : "";
				String value = subEl.getAttributeValue(VALUE);
				value = value != null ? value.trim().toUpperCase() : "";
				
				Map<String, String> map = null;
				if(overridesMap.containsKey(type)){
					map = overridesMap.get(type);
				}
				else{
					map = new HashMap<String, String>();
					overridesMap.put(type, map);
				}
				map.put(id, value);
			}			
		}
		
		//Filling coefficients into the utility String
		private String fillInCoefficients(String editString, List<MasterChild> coeffs){
			for (MasterChild coeff : coeffs){
				editString = editString.replace(coeff.getMaster(), coeff.getChild());
			}
			return editString;
		}
		
		private String fillInCoefficientsFromOverrides(String editString, List<MasterChildOverride> coeffs){
			for (MasterChildOverride coeff : coeffs){
				editString = editString.replace(coeff.getMaster(), coeff.getChild());
			}
			return editString;
		}		
		
		private String formatProb(String probStr){
			Double prob = new Double (probStr.replaceFirst("%", ""))/100;
			return prob.toString();
		}

}
