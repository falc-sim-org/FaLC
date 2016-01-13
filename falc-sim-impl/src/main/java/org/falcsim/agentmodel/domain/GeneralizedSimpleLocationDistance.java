package org.falcsim.agentmodel.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;

@Entity
@Table(name="distances")
public class GeneralizedSimpleLocationDistance implements ICSVRead {
	@Column(name="from")
	private int locationA;
	@Column(name="to")
	private int locationB;
	@Column(name="km")
	private double distance;
	@Column(name="min")
	private double time;
	
	@Override
	public void getRowValues(String[] values, Object... objects) {
		
		locationA = ClassStringUtil.parseInt(values[0]);
		locationB = ClassStringUtil.parseInt(values[1]);
		distance = ClassStringUtil.parseDouble(values[2]);
		time = ClassStringUtil.parseDouble(values[3]);
	}

	public int getLocationA() {
		return locationA;
	}

	public void setLocationA(int locationA) {
		this.locationA = locationA;
	}

	public int getLocationB() {
		return locationB;
	}

	public void setLocationB(int locationB) {
		this.locationB = locationB;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
}
