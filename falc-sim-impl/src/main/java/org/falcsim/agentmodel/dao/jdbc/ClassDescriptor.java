package org.falcsim.agentmodel.dao.jdbc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.springframework.beans.factory.BeanFactory;

/**
 * transform JPA/Hibernate annotations to JDBC helper class
 * 
 * @author regioConcept AG
 * 
 */
public class ClassDescriptor {
	
	/**
	 * delimeter between scema and table
	 */
	private static String schemaDelimiter = ((CsvProperties)(AppCtxProvider.getApplicationContext().getBean(CsvProperties.class))).getSchemaDelimiter();
	
	public static String getSchemaDelimiter() {
		return schemaDelimiter;
	}

	public static void setSchemaDelimiter(String schemaDelimiter) {
		ClassDescriptor.schemaDelimiter = schemaDelimiter;
	}

	/**
	 * list of fields
	 */
	private List<FieldDescriptor> fields = new ArrayList<FieldDescriptor>();
	
	/**
	 * list of target entities
	 */
	private static Set<Class<?>> targetEntities = Collections.synchronizedSet(new HashSet<Class<?>>());
	
	/**
	 * primary key
	 */
	private FieldDescriptor primaryKey;
	/**
	 * name of the table
	 */
	private String tableName;

	/**
	 * holder of already used class descriptors
	 */
	@SuppressWarnings("rawtypes")
	private static Map<Class, ClassDescriptor> classDescriptors = Collections
			.synchronizedMap(new HashMap<Class, ClassDescriptor>());

	/**
	 * holder of the class
	 */
	@SuppressWarnings("rawtypes")
	private Class clazz;

	/**
	 * constructor - build for given class
	 * 
	 * @param clazz
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	@SuppressWarnings({ "rawtypes" })
	public ClassDescriptor(Class clazz) {
		this.clazz = clazz;
		applyClass(clazz);
		// process also parent if not standard
		while(!clazz.getName().equals(Object.class.getName())){
			clazz = clazz.getSuperclass();
			if (!clazz.getName().equals(Object.class.getName())) {
				applyClass(clazz);
			}
		}
	}

	/**
	 * process annotations for given class including one parent level
	 * 
	 * @param clazz
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void applyClass(Class clazz) {
		try {
			StringBuilder sb = new StringBuilder();
			Annotation[] annotations = null;
			Column column;

			// class
			annotations = clazz.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Table) {
					Table table = (Table) annotation;
					if(this.tableName == null || "".equals(this.tableName.length()) ){
						this.tableName = table.name();
						if(table.schema() != null && !"".equals(table.schema())){
							this.tableName = table.schema() + schemaDelimiter + table.name();
						}
					}
					break;
				}
			}

			// fields
			FieldDescriptor descriptor;
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				if (field.getType() == int.class
						|| field.getType() == Integer.class
						|| field.getType() == Date.class
						|| field.getType() == String.class
						|| field.getType() == BigDecimal.class
						|| field.getType() == Boolean.class
						|| field.getType() == boolean.class
						|| field.getType() == Double.class
						|| field.getType() == double.class
						|| field.getType() == Float.class
						|| field.getType() == float.class) {

					descriptor = new FieldDescriptor();
					descriptor.setFieldClass(field.getType());
					descriptor.setFieldName(field.getName());

					annotations = field.getAnnotations();

					// by default any field can be stored
					descriptor.setColumnName(field.getName());
					sb.setLength(0);
					sb.append(field.getName());
					sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
					sb.insert(0, "set");
					descriptor.setSetMethodName(sb.toString());
					sb.replace(0, 1, "g");
					descriptor.setGetMethodName(sb.toString());
					descriptor.setSetMethod(clazz.getMethod(
							descriptor.getSetMethodName(), field.getType()));
					descriptor.setGetMethod(clazz.getMethod(descriptor
							.getGetMethodName()));

					for (Annotation annotation : annotations) {
						if (annotation instanceof Column) {
							column = (Column) annotation;
							if(column.name() != null && !"".equals(column.name())){
								descriptor.setColumnName(column.name());
							}
							continue;
						}
						if (annotation instanceof Id) {
							primaryKey = descriptor;
							continue;
						}
						if (annotation instanceof Transient) {
							descriptor.setColumnName(null);
							break;
						}
					}

					if (descriptor.getColumnName() != null) {
						this.fields.add(descriptor);
					}
				}else{
					//other field types check for target entity
					annotations = field.getAnnotations();
					for (Annotation annotation : annotations) {
						if (annotation instanceof OneToOne) {
							OneToOne oneToOne = (OneToOne)annotation;
							if(oneToOne.targetEntity() != null){
								targetEntities.add(oneToOne.targetEntity());
							}
							continue;
						}
						if (annotation instanceof OneToMany) {
							OneToMany oneToMany = (OneToMany)annotation;
							if(oneToMany.targetEntity() != null){
								targetEntities.add(oneToMany.targetEntity());
							}
							continue;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * get list of fields
	 * 
	 * @return field descriptors
	 */
	public List<FieldDescriptor> getFields() {
		return fields;
	}

	/**
	 * set list of fields
	 * 
	 * @param fields
	 */
	public void setFields(List<FieldDescriptor> fields) {
		this.fields = fields;
	}

	/**
	 * get primary key
	 * 
	 * @return primary key field
	 */
	public FieldDescriptor getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * set primary key
	 * 
	 * @param primaryKey
	 */
	public void setPrimaryKey(FieldDescriptor primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * get table name
	 * 
	 * @return actual table name
	 */
	@SuppressWarnings("unchecked")
	public String getTableName() {
		BeanFactory factory = (BeanFactory) AppCtxProvider
				.getApplicationContext();
		DynamicEntityTableMapper tableMapper = factory.getBean(DynamicEntityTableMapper.class);
		return tableMapper.getEntityTable(clazz, tableName);
	}

	/**
	 * set return name
	 * 
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * transform HSQL command to SQL command
	 * 
	 * @param hsql
	 * @return transformed SQL command
	 */
	public String transformHSQLtoSQL(String hsql) {
		if (hsql != null) {
			// replace all occurencies
			hsql = hsql.replace(clazz.getName(), getTableName());
			for (FieldDescriptor field : getFields()) {
				// replace just full word
				hsql = hsql.replaceAll("\\b" + field.getFieldName() + "\\b",
						field.getColumnName());
			}
		}
		return hsql;
	}

	/**
	 * return class specific descriptor from local cache
	 * 
	 * @param clazz
	 * @return class descriptor
	 * @throws Exception
	 * @throws SecurityException
	 */
	public static <T> ClassDescriptor getClassDescriptor(Class<T> clazz) {
		ClassDescriptor classDescriptor = null;
		synchronized (classDescriptors) {
			classDescriptor = classDescriptors.get(clazz);
			if (classDescriptor == null) {
				classDescriptor = new ClassDescriptor(clazz);
				classDescriptors.put(clazz, classDescriptor);
			}
		}
		return classDescriptor;
	}
	
	/**
	 * return class descriptor for given list of available implementations
	 * @param classes
	 * @return class descriptor
	 */
	public static <T> ClassDescriptor getClassDescriptorForImpl(Set<Class<T>> classes){
		Class<T> clazz = null;
		for(Class<T> obj : classes){
			if(targetEntities.contains(obj)){
				clazz = obj;
				break;
			}
		}
		if(clazz == null){
			throw new RuntimeException("No such implementation available for " + classes.toString());
		}
		return getClassDescriptor(clazz);
	}
}
