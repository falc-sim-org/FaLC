package org.falcsim.agentmodel.dao.jdbc;

import java.lang.reflect.Method;

/**
 * This class will hold reflection information of the field
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class FieldDescriptor {
	/**
	 * class of the field
	 */
	@SuppressWarnings("rawtypes")
	private Class fieldClass;

	/**
	 * name of the column
	 */
	private String columnName;

	/**
	 * name of the field
	 */
	private String fieldName;

	/**
	 * name of get method
	 */
	private String getMethodName;

	/**
	 * name of set method
	 */
	private String setMethodName;

	/**
	 * get method
	 */
	private Method getMethod;

	/**
	 * set method
	 */
	private Method setMethod;

	@SuppressWarnings("rawtypes")
	public Class getFieldClass() {
		return fieldClass;
	}

	@SuppressWarnings("rawtypes")
	public void setFieldClass(Class fieldClass) {
		this.fieldClass = fieldClass;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getGetMethodName() {
		return getMethodName;
	}

	public void setGetMethodName(String getMethodName) {
		this.getMethodName = getMethodName;
	}

	public String getSetMethodName() {
		return setMethodName;
	}

	public void setSetMethodName(String setMethodName) {
		this.setMethodName = setMethodName;
	}

	public Method getGetMethod() {
		return getMethod;
	}

	public void setGetMethod(Method getMethod) {
		this.getMethod = getMethod;
	}

	public Method getSetMethod() {
		return setMethod;
	}

	public void setSetMethod(Method setMethod) {
		this.setMethod = setMethod;
	}
}
