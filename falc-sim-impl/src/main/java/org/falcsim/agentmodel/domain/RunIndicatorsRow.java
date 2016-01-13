package org.falcsim.agentmodel.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Extension of the core RunIndicatorValueSuperClass class
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Table(name = "run_indicators", schema = "assumptions")
public class RunIndicatorsRow extends RunIndicatorsRowSuperClass {

	@Transient
	public final static int numOfColumns = 182;
	@Transient
	public final static int numOfCalibrationColumns = 125;
	
	@Transient
	private int[] attrset;

	@Transient
	private int[] companyTypesAttrset;	
	
	private void initarray(){
		attrset = new int[numOfColumns];
		for(int i = 0; i < numOfColumns; i++) attrset[i] = 0;
		companyTypesAttrset = new int[60]; 
		for(int i = 0; i < 60; i++) companyTypesAttrset[i] = 0;
	}
	
	private String strUpdate(int num, int count){
		String st = String.valueOf(num);
		if(st.length() < count){
			int rep = count - st.length();
			for(int i = 0; i < rep; i++) st = "0" + st;
		}
		return st;
	}
	
	public RunIndicatorsRow(){
		initarray();
	}
	
	public RunIndicatorsRow(int locid, int referencerun, int year){
		setLocid(locid);
		setReferencerun(referencerun);
		setYear(year);
		initarray();
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < numOfColumns; i++){
			sb.append(attrset[i]);
			if(i < numOfColumns -1) sb.append(";");
		}
		return sb.toString();
	}

	public String toString(int from, int count){
		StringBuilder sb = new StringBuilder();
		int to = (from+count < numOfColumns ? from+count : numOfColumns);
		for(int i = from; i < to; i++){
			sb.append(attrset[i]);
			if(i < to -1) sb.append(";");
		}
		return sb.toString();
	}
	
	public String getRowID(){
		StringBuilder sb = new StringBuilder();
		sb.append(getYear());
		sb.append(strUpdate(getLocid(), 8));
		return sb.toString();
	}	
	
	public String getRowHeader(){
		StringBuilder sb = new StringBuilder();
		sb.append(getYear());
		sb.append(strUpdate(getLocid(), 8));
		sb.append(";");
		sb.append(getLocid());
		sb.append(";");
		sb.append(getYear());
		return sb.toString();
	}
	
	public void setValue(int id, int val){
		attrset[id] = val;
	}
	
	public void appendValue(int id, int val){
		attrset[id] += val;
	}
	
	public int getValue(int id){
		return attrset[id];
	}

	public void setValueCompType(int id, int val){
		companyTypesAttrset[id] = val;
	}
	
	public void appendValueCompType(int id, int val){
		companyTypesAttrset[id] += val;
	}
	
	public int getSumValue(int from, int count){
		return getSumValue(from, count, 1);
	}
	
	public int getSumValue(int from, int count, int step){
		int result = 0;
		for(int i = from; i < (from+count < numOfColumns ? from+count : numOfColumns); i = i+step){
			result += attrset[i];
		}
		return result;
	}
	
	public int[] getCompTypesArray(){
		return companyTypesAttrset;
	}
	
	public int[] getCalibrationArray(){
		int[] result = new int[numOfCalibrationColumns];
		result[0] = getSumValue(0, 44);			//residents
		result[1] = getSumValue(0, 10);
		result[2] = getSumValue(10, 18);
		result[3] = getSumValue(28, 16);
		
		result[4] = getSumValue(44, 19);	//households
		copyTo( 44, 19, result, 5);

		result[24] = getSumValue(63, 10);	//businesses
		copyTo( 63, 10, result, 25);

		result[35] = getSumValue(73, 10);	//workers
		copyTo( 73, 10, result, 36);
		
		copyTo( 104, 5, result, 46);		//education
		copyTo( 129, 7, result, 51);		//business sizes group
		copyTo( 83, 21, result, 58);		//commuting
		copyTo( 136, 15, result, 79);		//landusage
		copyTo( 151, 19, result, 94);		//households group residents
		copyTo( 170, 1, result, 113);		//unemployed
		
		copyTo( 171, 1, result, 114);		//residents-workers
		copyTo( 172, 5, result, 115);		//overlimits
		copyTo( 177, 5, result, 120);		//HH reloc types	
		return result;
	}
	
	private void copyTo( int from, int count, int[] array, int index){
		for(int i = 0; i < count; i++){
			array[index + i] = attrset[from + i];
		}
	}

}
