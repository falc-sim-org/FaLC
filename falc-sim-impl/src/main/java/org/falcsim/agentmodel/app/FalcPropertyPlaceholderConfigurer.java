package org.falcsim.agentmodel.app;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.falcsim.agentmodel.service.methods.util.domain.RunIndicatorsParameters;
import org.falcsim.agentmodel.service.methods.util.io.CustomIndicatorHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;

/** 
* Custom property Placeholder. Resets and setup application properties from project.xml file as well as before each scenario
*
* @author regioConcept AG
* @version 1.0
* @since   0.5 
* 
*/
public class FalcPropertyPlaceholderConfigurer extends	PropertyPlaceholderConfigurer {
	
	private static Map<Class, Map<String, String>> configuredPropertiesMap;
    private static Map<String, String> propertiesMap;
    private Map<String, CustomIndicatorHelper> customRIProperties;
    // Default as in PropertyPlaceholderConfigurer
    private int springSystemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;
    
    private ConfigurableListableBeanFactory beanFactory;

	private List<OldPropertyValue> updPropMap = new ArrayList<OldPropertyValue>();
	
    @Override
    public void setSystemPropertiesMode(int systemPropertiesMode) {
        super.setSystemPropertiesMode(systemPropertiesMode);
        springSystemPropertiesMode = systemPropertiesMode;
    }

    private String getStringValue (Object value) {
    	if (value instanceof TypedStringValue) {
			TypedStringValue typedStringValue = (TypedStringValue) value;
			String stringValue = typedStringValue.getValue();
			if (stringValue != null) {
				return stringValue;
			}
		}
    	else if (value instanceof String) {
			return (String)value;
		}
		return "";
	}
    
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
    	this.beanFactory = beanFactory;
    	configuredPropertiesMap = new HashMap<Class, Map<String, String>>();
    	customRIProperties = null;
    	
    	try{
			String[] beanNames = beanFactory.getBeanDefinitionNames();
			for (String curName : beanNames) {
				BeanDefinition beanDefinition = beanFactory.getBeanDefinition(curName);
				MutablePropertyValues pvs = beanDefinition.getPropertyValues();
				PropertyValue[] pvArray = pvs.getPropertyValues();
				for (PropertyValue pv : pvArray) {
					String stringValue = getStringValue(pv.getValue());
					if( stringValue.matches("\\$\\{.*\\}") ){
						Class clazz = beanFactory.getType(curName);
						Map<String, String> propMap = configuredPropertiesMap.containsKey(clazz) ? configuredPropertiesMap.get(clazz) : null;
						if( propMap == null){
							propMap = new HashMap<String, String>();
							configuredPropertiesMap.put(clazz, propMap);
						}
						propMap.put(pv.getName(), stringValue );
					}
				}
			}
    	}
    	catch(Exception e){
    		logger.error(e.getMessage(), e);
    	}
    	
        super.processProperties(beanFactory, props);

        propertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String valueStr = resolvePlaceholder(keyStr, props, springSystemPropertiesMode);
            propertiesMap.put(keyStr, valueStr);
        }
    }

    public static String getProperty(String name) {
        return propertiesMap.get(name).toString();
    }
    
    public String getOldConfiguration(String property) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	for(OldPropertyValue opv : updPropMap){
    		if(opv.bean instanceof org.falcsim.agentmodel.synthese.domain.SyntheseParameters){
    			return opv.value.toString();
    		}
    	}
    	return "";
    }
    
    public void reset() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	for(OldPropertyValue opv : updPropMap){
    		opv.setter.invoke(opv.bean, new Object[]{opv.value});
    	}
    	updPropMap.clear();
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void updateProperty(String propName, String propValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	
    	if(propName.startsWith("run.indicators.custom")){
    		propertiesMap.put(propName, propValue);
    		return;
    	}
    	
	   	for(Class clazz : configuredPropertiesMap.keySet()){
    		Map<String, String> propMap = configuredPropertiesMap.get(clazz);
    		for(String pname : propMap.keySet()){
    			if(propMap.get(pname).equals("${" + propName + "}")){
    				Object bean = beanFactory.getBean(clazz);
	                PropertyDescriptor property = BeanUtils.getPropertyDescriptor(clazz, pname);
	                Method setter = property.getWriteMethod();
	                Method getter = property.getReadMethod();
	                
	                Class typeClazz = property.getPropertyType();
	                Object objValue = null;
	                if(typeClazz.equals(Boolean.class)) objValue = Boolean.parseBoolean(propValue);
	                else if(typeClazz.equals(boolean.class)) objValue = Boolean.parseBoolean(propValue);
	                else if(typeClazz.equals(Integer.class)) objValue = Integer.parseInt(propValue);
	                else if(typeClazz.equals(int.class)) objValue = Integer.parseInt(propValue);
	                else if(typeClazz.equals(Long.class)) objValue = Long.parseLong(propValue);
	                else if(typeClazz.equals(long.class)) objValue = Long.parseLong(propValue);
	                else if(typeClazz.equals(Double.class)) objValue = Double.parseDouble(propValue);
	                else if(typeClazz.equals(double.class)) objValue = Double.parseDouble(propValue);
	                else if(typeClazz.equals(Float.class)) objValue = Float.parseFloat(propValue);
	                else if(typeClazz.equals(float.class)) objValue = Float.parseFloat(propValue);
	                else objValue = propValue;
	                Object oldValue = getter.invoke(bean);
	                setter.invoke(bean, new Object[]{objValue});
	                updPropMap.add( new OldPropertyValue(bean, setter, oldValue) );
	                propertiesMap.put(propName, propValue);
	                return;
    			}
    		}
    	}

    }
    
    public void setupCustomRunIndicators(){
    	if(customRIProperties == null){
    		this.customRIProperties = new HashMap<String, CustomIndicatorHelper>();
    		if(propertiesMap.get("run.indicators.custom.names") != null){
    			String[] cIndicators = propertiesMap.get("run.indicators.custom.names").split(",");
    			for(String entry : cIndicators){
    				CustomIndicatorHelper helper = findCustomIndicators(entry.trim());
    				if(helper != null)
    					this.customRIProperties.put(entry.trim(), helper);
    			}
    		}
    	}
    }
    
    private CustomIndicatorHelper findCustomIndicators(String name){
    	CustomIndicatorHelper cih = null;
    	String propertyName = "run.indicators.custom." + name + ".selected";
		if(propertiesMap.get(propertyName) != null){
			cih = new CustomIndicatorHelper();
			cih.name = name;
			cih.selected = propertiesMap.get(propertyName);
			
			String property = "run.indicators.custom." + name + ".cantons";
			if(propertiesMap.get(property) != null){
				cih.cantonsActive = Boolean.parseBoolean(propertiesMap.get(property));
				property = "run.indicators.custom." + name + ".cantons.filter";
				if(propertiesMap.get(property) != null){
					cih.cantons = propertiesMap.get(property);
				}
			}
			
			property = "run.indicators.custom." + name + ".municipalities";
			if(propertiesMap.get(property) != null){
				cih.municipalityActive = Boolean.parseBoolean(propertiesMap.get(property));
				property = "run.indicators.custom." + name + ".municipalities.filter";
				if(propertiesMap.get(property) != null){
					cih.municipalities = propertiesMap.get(property);
				}
			}
			
			property = "run.indicators.custom." + name + ".locations";
			if(propertiesMap.get(property) != null){
				cih.locationActive = Boolean.parseBoolean(propertiesMap.get(property));
				property = "run.indicators.custom." + name + ".locations.filter";
				if(propertiesMap.get(property) != null){
					cih.locations = propertiesMap.get(property);
				}
			}
			
			property = "run.indicators.custom." + name + ".years";
			cih.years = propertiesMap.get(property);
		}
		return cih;
    }

	public Map<String, CustomIndicatorHelper> getCustomRIProperties() {
		return customRIProperties;
	}
}

class OldPropertyValue{
	public Object bean;
	public Method setter;
	public Object value;
	public OldPropertyValue(Object bean, Method setter, Object value){
		this.bean = bean;
		this.setter = setter;
		this.value = value;
	}
}
