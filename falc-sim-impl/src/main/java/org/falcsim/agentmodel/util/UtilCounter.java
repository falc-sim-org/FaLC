package org.falcsim.agentmodel.util;

import org.springframework.stereotype.Component;
/** Provides counting functionality
 * 
 * @author  regioConcept AG
 *
 */
@Component
public class UtilCounter {
	
	Integer count1=0;
	Integer count2=0;
	Integer count3=0;
	
	Integer count4=0;
	Integer count5=0;
	Integer count6=0;
	Integer count7=0;
	
	public Integer getCount1() {
		return count1;
	}
	public void setCount1(Integer count1) {
		this.count1 = count1;
	}
	public Integer getCount2() {
		return count2;
	}
	public void setCount2(Integer count2) {
		this.count2 = count2;
	}
	public Integer getCount3() {
		return count3;
	}
	public void setCount3(Integer count3) {
		this.count3 = count3;
	}
	
	public void addCount1(Integer n){
		setCount1(count1+n);
	}
	
	public void addCount2(Integer n){
		setCount2(count2+n);
	}
	
	public void addCount3(Integer n){
		setCount3(count3+n);
	}
	
	public Integer getCount4() {
		return count4;
	}
	public void setCount4(Integer count4) {
		this.count4 = count4;
	}
	public Integer getCount5() {
		return count5;
	}
	public void setCount5(Integer count5) {
		this.count5 = count5;
	}
	public Integer getCount6() {
		return count6;
	}
	public void setCount6(Integer count6) {
		this.count6 = count6;
	}
	public Integer getCount7() {
		return count7;
	}
	public void setCount7(Integer count7) {
		this.count7 = count7;
	}
	
	public void reset(){
		setCount1(0);
		setCount2(0);
		setCount3(0);
		setCount4(0);
		setCount5(0);
		setCount6(0);
		setCount7(0);
	}
	
	
	

}
