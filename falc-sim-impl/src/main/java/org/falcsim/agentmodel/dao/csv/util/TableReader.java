package org.falcsim.agentmodel.dao.csv.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.AppCtxProvider;
import org.falcsim.agentmodel.dao.csv.CsvProperties;
import org.falcsim.agentmodel.dao.jdbc.ClassDescriptor;
import org.falcsim.agentmodel.dao.jdbc.FieldDescriptor;
import org.falcsim.agentmodel.dao.util.ClassStringUtil;

/**
 * CSV file reader
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   1.0 
 */
public class TableReader {
	private static final String ENCODING = AppCtxProvider.getApplicationContext().getBean(CsvProperties.class).getCharset();
	
	private class CIHashmap<T> extends HashMap<String, T>{
		@Override
		public T put(String key, T value) {
			// TODO Auto-generated method stub
			return super.put(key.toLowerCase().replaceAll("\"", ""), value);
		}
		
		@Override
		public T get(Object key) {
			return super.get(((String)key).toLowerCase().replaceAll("\"", ""));
		}
	}
	
	private static final Logger _log = Logger.getLogger(TableReader.class);
	private static final String AND_OR = " AND | OR ";
	
	private BufferedReader bufferedReader;
	
	private Map<String, ColumnInfo> columnMap;
	
	private String columns;
	private String[] columnsArray;
	
	private String[] typesArray;
	private Class<?>[] types;
	
	private List<Condition> conditions;
	private List<Logic> logics;
	
	private void makeMap(String[] array){
		this.columnMap = new CIHashmap<ColumnInfo>();
		this.columnsArray = new String[array.length]; 
		
		for(int i = 0; i < array.length; i++){
			array[i] = array[i].replaceAll("\"", "");
			ColumnInfo ci = new ColumnInfo();
			ci.columnIndex = i;
			ci.type = this.types[i];
			this.columnsArray[i] = array[i]; 
			this.columnMap.put(array[i], ci);
		}
	}
		
	public void preReadTable(String fileName, String separator)
			throws RuntimeException {
		File file = new File(fileName);
		_log.info("Reading file " + file.getName());
		if(!file.exists()){
			_log.error("The file " + file.getPath() + " does not exists!");
			throw new RuntimeException("The file " + file.getPath() + " does not exists!");
		}
		
		try{
			this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING));
			this.columns = this.bufferedReader.readLine();
			this.makeMap(columns.split(separator));
			_log.info("The first line in " + file + " has columns " + columns);
		} catch (IOException ioe) {
			_log.error(ioe.getMessage(), ioe);
			throw new RuntimeException(ioe);
		}
	}
	
	public void typesRead(String fileName, String separator)
			throws RuntimeException{
		
		File file = new File(fileName);
		_log.info("Reading file " + file.getName());
		if(!file.exists()){
			_log.error("The file " + file.getPath() + " does not exists!");
			throw new RuntimeException("The file " + file.getPath() + " does not exists!");
		}
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODING))){
			this.typesArray = br.readLine().split(separator);
			this.types = new Class<?>[this.typesArray.length];
			
			for(int i = 0; i < this.typesArray.length; i++){
				switch(this.typesArray[i].replaceAll(" ", "")){
					case "Integer":
						this.types[i] = Integer.class;
						break;
					case "Double":
						this.types[i] = Double.class;
						break;
					case "Date":
						this.types[i] = Date.class;
						break;
					case "String":
						this.types[i] = String.class;
						break;
					default: 
						this.types[i] = Object.class;
						break;
				}
			}
		} catch (IOException ioe){
			_log.error(ioe.getMessage(), ioe);
			throw new RuntimeException(ioe);
		}
	}
	
	public String readNextLine() throws RuntimeException {
		try {
			return this.bufferedReader.readLine();
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public void postReadTable() throws RuntimeException{
		try {
			this.bufferedReader.close();
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public BufferedReader getBufferedReader() {
		return this.bufferedReader;
	}


	public String getColumns() {
		return this.columns;
	}
	
	public void prepareStatement(String input, Map<String, ColumnInfo> columns){		
		this.conditions = new ArrayList<Condition>();
		this.logics = new ArrayList<Logic>();
		
		if(input == "")
			return;
		
		Pattern p = Pattern.compile(AND_OR);
		Matcher m = p.matcher(input);
		while(m.find()){
			if(m.group().equalsIgnoreCase(" and "))
				this.logics.add(Logic.AND);
			else
				this.logics.add(Logic.OR);
		}
		
		
		String[] splitted = input.split(AND_OR);
		for(int i = 0; i < splitted.length; i++){
			Condition c = new Condition();
			int operationIndex = 0;
			int startValueIndex = 0;
			
			if(splitted[i].contains(" == ")){
				operationIndex = splitted[i].indexOf(" == ");
				startValueIndex = operationIndex + 4;
				c.operation = Operation.EQ;
			} else if(splitted[i].contains(" < ")) {
				operationIndex = splitted[i].indexOf(" < ");
				startValueIndex = operationIndex + 3;
				c.operation = Operation.LT;
			} else if(splitted[i].contains(" <= ")) {
				operationIndex = splitted[i].indexOf(" <= ");
				startValueIndex = operationIndex + 4;
				c.operation = Operation.LEQ;
			} else if(splitted[i].contains(" > ")) {
				operationIndex = splitted[i].indexOf(" > ");
				startValueIndex = operationIndex + 3;
				c.operation = Operation.GT;
			} else if(splitted[i].contains(" >= ")) {
				operationIndex = splitted[i].indexOf(" >= ");
				startValueIndex = operationIndex + 4;
				c.operation = Operation.GEQ;
			} else if(splitted[i].contains(" in ")) {
				operationIndex = splitted[i].indexOf(" in ");
				startValueIndex = operationIndex + 4;
				c.operation = Operation.IN;
			}
			
			c.columnName = splitted[i].substring(0, operationIndex);
			String valueS = splitted[i].substring(startValueIndex, splitted[i].length());
			if(columns.get(c.columnName).type == (Double.class) && c.operation != Operation.IN){
				c.value = Double.valueOf(valueS.replaceAll(" ", ""));
			} else if(columns.get(c.columnName).type == (Integer.class) && c.operation != Operation.IN){
				c.value = Integer.valueOf(valueS.replaceAll(" ", ""));
			} else if(c.operation == Operation.IN){ 
				c.value = CommonUtil.stringToSet(valueS, columns.get(c.columnName).type);
			} else {
				c.value = valueS.replaceAll(" ", "");
			}
			
			this.conditions.add(c);
		}
	}
	
	public Map<String, Method> prepareGetters(ClassDescriptor cd){
		Map<String, Method> methods = new HashMap<String, Method>();
		for(int i = 0; i < this.conditions.size(); i++){
			for(FieldDescriptor fd : cd.getFields()){
				if(fd.getColumnName().equalsIgnoreCase(this.conditions.get(i).columnName)){
					methods.put(this.conditions.get(i).columnName, fd.getGetMethod());
					break;
				}
			}
		}
		return methods;
	}
	
	public Set<Integer> prepareColumnIndexes(){
		Set<Integer> columns = new HashSet<Integer>();
		for(Condition c : this.conditions){
			ColumnInfo ci = this.columnMap.get(c.columnName);
			if(ci != null){
				columns.add(ci.columnIndex);
			}
		}
		return columns;
	}
	
	public boolean fits(Object object, Map<String, Method> methods) throws Exception {
		if(conditions.size() == 0){
			return true;
		}
		if(logics.size() == 0){
			Object value = methods.get(conditions.get(0).columnName).invoke(object);
			return Condition.matches(conditions.get(0), value);
		} else {
			Object valueA = methods.get(conditions.get(0).columnName).invoke(object);
			Object valueB = methods.get(conditions.get(1).columnName).invoke(object);
			Boolean b1 = Condition.matches(conditions.get(0), valueA);
			Boolean b2 = Condition.matches(conditions.get(1), valueB);
			
			Boolean last = Condition.applyLogic(logics.get(0), b1, b2);
			for(int i = 1; i < logics.size(); i++){
				Object valueC = methods.get(conditions.get(0).columnName).invoke(object);
				Boolean rightSide = Condition.matches(conditions.get(i + 1), valueC);
				last = Condition.applyLogic(logics.get(i), last, rightSide);
			}
			return last;
		}
	}
	
	public boolean fits(Object[] value){
		if(conditions.size() == 0)
			return true;
		
		if(logics.size() == 0){
			Integer index = this.columnMap.get(this.conditions.get(0).columnName).columnIndex;
			return Condition.matches(conditions.get(0), value[index]);
		} else {
			Integer index1 = this.columnMap.get(this.conditions.get(0).columnName).columnIndex;
			Integer index2 = this.columnMap.get(this.conditions.get(1).columnName).columnIndex;
			
			Boolean b1 = Condition.matches(conditions.get(0), value[index1]);
			Boolean b2 = Condition.matches(conditions.get(1), value[index2]);
			Boolean last = Condition.applyLogic(logics.get(0), b1, b2);
			
			for(int i = 1; i < logics.size(); i++){
				Integer index = this.columnMap.get(this.conditions.get(i + 1).columnName).columnIndex;
				Boolean rightSide = Condition.matches(conditions.get(i + 1), value[index]);
				last = Condition.applyLogic(logics.get(i), last, rightSide);
			}
			return last;
		}
	}
	
	public Object cast(String input, Class<?> type){
		if(type == Integer.class){
			return ClassStringUtil.parseInt(input);
		} else if (type == Double.class) {
			return ClassStringUtil.parseDouble(input);
		} else if (type == Date.class) {
			return ClassStringUtil.parseDate(input);
		} else {
			return input;
		}
	}

	public Map<String, ColumnInfo> getColumnsMap() {
		return this.columnMap;
	}
	
	public String[] getColumnsNames(){
		return this.columnsArray;
	}
}
