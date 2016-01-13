package org.falcsim.agentmodel.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.falcsim.agentmodel.util.StringUtil;


/**
 * Generalized distance class
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
public class GeneralizedLocationDistanceSuperClass /* implements Serializable */{

	/*@Id
	@GeneratedValue(generator = "gld_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "GLD_ID")*/
	@Transient
	private Integer id;

	@Column(name = "LOCATION_A_ID")
	private Integer locationAId;

	@Column(name = "LOCATION_B_ID")
	private Integer locationBId;

	@Column(name = "DISTANCE_1")
	private Float distance_A;

	@Column(name = "DISTANCE_2")
	private Float distance_B;

	@Column(name = "DISTANCE_3")
	private Float distance_C;

	@Column(name = "DISTANCE_4")
	private Float distance_D;

	@Column(name = "DISTANCE_5")
	private Float distance_E;

	@Column(name = "DISTANCE_6")
	private Float distance_F;

	@Column(name = "DISTANCE_7")
	private Float distance_G;

	@Column(name = "DISTANCE_8")
	private Float distance_H;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLocationAId() {
		return locationAId;
	}

	public void setLocationAId(Integer locationAId) {
		this.locationAId = locationAId;
	}

	public Integer getLocationBId() {
		return locationBId;
	}

	public void setLocationBId(Integer locationBId) {
		this.locationBId = locationBId;
	}

	public Float getDistance_A() {
		return distance_A;
	}

	public void setDistance_A(Float distance_A) {
		this.distance_A = distance_A;
	}

	public Float getDistance_B() {
		return distance_B;
	}

	public void setDistance_B(Float distance_B) {
		this.distance_B = distance_B;
	}

	public Float getDistance_C() {
		return distance_C;
	}

	public void setDistance_C(Float distance_C) {
		this.distance_C = distance_C;
	}

	public Float getDistance_D() {
		return distance_D;
	}

	public void setDistance_D(Float distance_D) {
		this.distance_D = distance_D;
	}

	public Float getDistance_E() {
		return distance_E;
	}

	public void setDistance_E(Float distance_E) {
		this.distance_E = distance_E;
	}

	public Float getDistance_F() {
		return distance_F;
	}

	public void setDistance_F(Float distance_F) {
		this.distance_F = distance_F;
	}

	public Float getDistance_G() {
		return distance_G;
	}

	public void setDistance_G(Float distance_G) {
		this.distance_G = distance_G;
	}

	public Float getDistance_H() {
		return distance_H;
	}

	public void setDistance_H(Float distance_H) {
		this.distance_H = distance_H;
	}

	@Override
	public String toString() {
		return "locationAId = " + StringUtil.fix(locationAId)
				+ " locationBId =" + StringUtil.fix(locationBId)
				+ " distance_A = " + StringUtil.fix(distance_A)
				+ " distance_B = " + StringUtil.fix(distance_B)
				+ " distance_C = " + StringUtil.fix(distance_C)
				+ " distance_D = " + StringUtil.fix(distance_D)
				+ " distance_E = " + StringUtil.fix(distance_E)
				+ " distance_F = " + StringUtil.fix(distance_F)
				+ " distance_G = " + StringUtil.fix(distance_G)
				+ " distance_H = " + StringUtil.fix(distance_H);
	}

}
