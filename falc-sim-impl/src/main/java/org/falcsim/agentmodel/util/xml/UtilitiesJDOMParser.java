package org.falcsim.agentmodel.util.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.springframework.stereotype.Component;

/** Provides JDOM methods to parse the to parse the xml file for utility
 *  models read the parameter values into the utility functions
*
* @author regioConcept AG
* @version 0.5
* 
*/
@Component
public class UtilitiesJDOMParser {
	
	private static final  String VALUE="value";
	private static final  String MODEL="model";
	private static final  String NAME="name";
	private Document document;
	private Map<String, Map<String, String>> allParsedUtilitiesMap = new HashMap<String, Map<String, String>>();
	
	
	
	public Map<String, Map<String, String>> loadUtilitiesMap(String filepath, List<String> chosenModelsList){
		JDOMUtil jdu = new JDOMUtil();
		document = jdu.loadJDom(filepath);
		parseUtilitesFillInCoefficients(chosenModelsList);
		return allParsedUtilitiesMap;
	}
	
	public Map<String, Map<String, Map<String, String>>> loadConfigMap(String filepath){
		JDOMUtil jdu = new JDOMUtil();
		document = jdu.loadJDom(filepath);
		return parseConfig();
	}
	
    public List<String> getChosenModelsNames(Map<String, Map<String, Map<String, String>>> parsedConfig){
    	List<String> chosenModelsNames = new ArrayList<String>();
    	chosenModelsNames.add(parsedConfig.get(UtilitiesXMLConstants.HOUSEHOLDS).keySet().iterator().next());
    	chosenModelsNames.add(parsedConfig.get(UtilitiesXMLConstants.BUSINESSES).keySet().iterator().next());
    	return chosenModelsNames;
    }
	
	public List<String> getModelNames(String filepath){
		
		List<String> modelNames = new ArrayList<String>();
		JDOMUtil jdu = new JDOMUtil();
		document = jdu.loadJDom(filepath);
		Element rootNode = document.getRootElement();
		List<?> modelist = (List<?>) rootNode.getChildren(MODEL);
		for (Object mod : modelist){
			modelNames.add(((Element) mod).getAttributeValue(NAME));
		}
		return modelNames;
	}
	
	@SuppressWarnings("unchecked") //Parsing the config files which assigns models to business sectors and households filling it into a map 
	private Map<String, Map<String, Map<String, String>>> parseConfig(){
		Map<String, Map<String, Map<String, String>>> allMap = new HashMap<String, Map<String, Map<String, String>>>();
		Element rootNode = document.getRootElement();
		Element bnode = rootNode.getChild(UtilitiesXMLConstants.BUSINESSES);
		Element mbnode = bnode.getChild(MODEL); //the businesses model
		Iterator<Element> sectorIterator = bnode.getDescendants(new ElementFilter(UtilitiesXMLConstants.SECTOR));
		Map<String , Map<String, String>> modelBusMap = new HashMap<String , Map<String, String>> ();
		Map<String, String> sectorMap = new HashMap<String, String>();
		while (sectorIterator.hasNext()){
			Element sectorEl = sectorIterator.next();
			//assigns the alternative to the sector
			sectorMap.put(sectorEl.getAttributeValue(UtilitiesXMLConstants.ID), sectorEl.getAttributeValue(UtilitiesXMLConstants.ALTERNATIVE));		
		}
		modelBusMap.put(mbnode.getAttributeValue(NAME), sectorMap); //a sector map for the model, with the model name as key
		allMap.put(UtilitiesXMLConstants.BUSINESSES, modelBusMap); // the key is the businesses keyword , the value is the sector map 
		Element hnode = rootNode.getChild(UtilitiesXMLConstants.HOUSEHOLDS);
		Element mhnode = hnode.getChild(MODEL); //the households model
		Iterator<Element> typeIterator = bnode.getDescendants(new ElementFilter(UtilitiesXMLConstants.SECTOR));
		Map<String , Map<String, String>> modelHousMap = new HashMap<String , Map<String, String>> ();
		Map<String, String> typeMap = new HashMap<String, String>();
		while (typeIterator.hasNext()){
			Element typeEl = typeIterator.next();
			//assigns the alternative to the type
			typeMap.put(typeEl.getAttributeValue(UtilitiesXMLConstants.ID), typeEl.getAttributeValue(UtilitiesXMLConstants.ALTERNATIVE));		
		}
		modelHousMap.put(mhnode.getAttributeValue(NAME), sectorMap); //a type map for the model, with the model name as key
		allMap.put(UtilitiesXMLConstants.HOUSEHOLDS, modelHousMap);// the key is the households keyword , the value is the types' map 
		return allMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void parseUtilitesFillInCoefficients(List<String> chosenModelsList){
		String modName ="";
		Element rootNode = document.getRootElement();
		Element pnode = rootNode.getChild(UtilitiesXMLConstants.PARAMETERS); //the parameters
		Map<String , Map<String, String>> paramMapExt = new HashMap<String , Map<String, String>> ();
		Map<String, String> paramMapInt = new HashMap<String, String>();
		Iterator<Element> paramIterator = pnode.getDescendants(new ElementFilter(UtilitiesXMLConstants.PARAMETER));
		while (paramIterator.hasNext()){
			Element paramEl = paramIterator.next();
			//assigns the value to the param name
			paramMapInt.put(paramEl.getAttributeValue(NAME), paramEl.getAttributeValue(VALUE));		
		}
		allParsedUtilitiesMap.put(UtilitiesXMLConstants.PARAMETERS, paramMapInt); // the key is the parameter keyword 
		List modelist = rootNode.getChildren(MODEL);//The list of the models in the xml file
		for (Object mod : modelist){
			modName = ((Element) mod).getAttributeValue(NAME);
			for (String chosenModelName : chosenModelsList){
				if (chosenModelName.toLowerCase().trim().matches(modName.toLowerCase().trim())){
					Iterator<Element> utilsIterator = ((Element) mod).getDescendants(new ElementFilter(UtilitiesXMLConstants.ALTERNATIVE));
					Map<String, String> utilMap = new HashMap<String, String>();
					while (utilsIterator.hasNext()){
						Element utilEl = utilsIterator.next();
						String altUtil = utilEl.getAttributeValue(NAME);
						String editedUtil = utilEl.getAttributeValue(VALUE);
						Iterator<Element> coeffIterator = ((Element) mod).getDescendants(new ElementFilter(UtilitiesXMLConstants.COEFFICIENT));
						while (coeffIterator.hasNext()){
							Element coeffEl = coeffIterator.next();
							String coeffName = coeffEl.getAttributeValue(NAME);
							String coeffValue = coeffEl.getAttributeValue(VALUE);
							editedUtil = editedUtil.replace(coeffName, coeffValue);
						}
						utilMap.put(altUtil, editedUtil);//putting the parsed utilities into a map with the alternative's name as key
					}
					allParsedUtilitiesMap.put(modName, utilMap);//putting the map with the parsed utilities in the return map with the model name as key
				}
			}
		}
		
	}
	
	
						
	

}
