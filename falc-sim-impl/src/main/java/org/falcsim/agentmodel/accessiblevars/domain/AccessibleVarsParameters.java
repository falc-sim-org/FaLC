package org.falcsim.agentmodel.accessiblevars.domain;

/**
 * Accessibility variables generator parameters from properties
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5
 * 
 */

public class AccessibleVarsParameters {

	/**
	 * limit for distance in access variable computation
	 */
	private int accessLimit;
	/**
	 * constant Beta for access variable computation
	 */
	private double accessBeta;
	/**
	 * Tii intervals definition
	 */
	private String accessTiiIntervals;
	/**
	 * evaluate log of sums
	 */
	private boolean accessMathLog;
	/**
	 * minimal accessible variable value
	 */
	private double accessMinValue;
	
	/**
	 * path to config XML file for AVs
	 */	
	private String xmlpath;
	
	/**
	 * get value
	 * 
	 * @return parameter value
	 */
	public int getAccessLimit() {
		return accessLimit;
	}

	/**
	 * set value
	 * 
	 * @param accessLimit
	 */
	public void setAccessLimit(int accessLimit) {
		this.accessLimit = accessLimit;
	}

	/**
	 * get value
	 * 
	 * @return parameter value
	 */
	public double getAccessBeta() {
		return accessBeta;
	}

	/**
	 * set value
	 * 
	 * @param accessBeta
	 */
	public void setAccessBeta(double accessBeta) {
		this.accessBeta = accessBeta;
	}

	/**
	 * get value
	 * 
	 * @return parameter value
	 */
	public String getAccessTiiIntervals() {
		return accessTiiIntervals;
	}

	/**
	 * set value
	 * 
	 * @param accessTiiIntervals
	 */
	public void setAccessTiiIntervals(String accessTiiIntervals) {
		this.accessTiiIntervals = accessTiiIntervals;
	}

	/**
	 * get value
	 * 
	 * @return parameter value
	 */
	public boolean isAccessMathLog() {
		return accessMathLog;
	}

	/**
	 * set value
	 * 
	 * @param accessMathLog
	 */
	public void setAccessMathLog(boolean accessMathLog) {
		this.accessMathLog = accessMathLog;
	}

	/**
	 * get value
	 * 
	 * @return parameter value
	 */		
	public double getAccessMinValue() {
		return accessMinValue;
	}

	/**
	 * set value
	 * 
	 * @param accessMinValue
	 */	
	public void setAccessMinValue(double accessMinValue) {
		this.accessMinValue = accessMinValue;
	}
	
	public String getXmlpath() {
		return xmlpath;
	}

	public void setXmlpath(String xmlpath) {
		this.xmlpath = xmlpath;
	}
	
}
