package org.falcsim.agentmodel.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.falcsim.agentmodel.RCConstants;

/**
 * Core class of FaLC's conceptual module
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class LocationSuperClass implements ActiveAgent {

	@Id
	@Column(name = "LOCID")
	protected int id;

	@Column(name = "MUNICIPALITY_NR")
	private Integer municipalityId;// id of a municipality
	
	@Column(name = "SUBAREA_NR")
	private Integer locationId;// id of a superentity, e.g. Kanton for Gemeinden

//	@Transient
//	private Integer locationId_A;// other location pointer

//	@Transient
//	private Integer locationId_B;// other location pointer

	@Transient
	private Integer population_1;

	@Transient
	private Integer population_2;

	@Transient
	private Integer population_3;

	@Transient
	private Integer population_4;

	@Transient
	private Integer type_1;

//	@Transient
//	private Integer finance_1;

	@Column(name = "DENOT")
	private String name;

	@Column(name = "RUN")
	private Integer run;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "LOCATION_ID", insertable = true, updatable = true)
	private List<Business> businesses = new ArrayList<Business>(); //Collections.synchronizedList(  )

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "LOCATION_ID", insertable = true, updatable = true)
	private List<Household> households = new ArrayList<Household>(); //Collections.synchronizedList(  )

//	@Transient
//	private Map<String, ?> map;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "location_a_id", insertable = false, updatable = false)
	@MapKey(name = "locationBId")
	private Map<Integer, GeneralizedLocationDistance> distances;
	
	/**
	 * these values are updated at the begining of each running year
	 */
	@Column(name = "RES_TOT")
	private Integer actualResidents;

	@Column(name = "EMP_TOT")
	private Integer actualWorkers;

	@Column(name = "FLAT_RENT")
	private Integer flat_rent;
	
	@Column(name = "tax_holdingcomp_n")
	private Double tax_holdingcomp_n;
	
	@Column(name = "tax_partnership_n")
	private Double tax_partnership_n;
	
	@Column(name = "tax_companies_n")
	private Double tax_companies_n;
	
	@Column(name = "resident_landprice_n")
	private Double resident_landprice_n;

	@Column(name = "motorway_access")
	private Integer motorway_access;
	
	@Column(name = "railway_access")
	private Integer railway_access;
	
	@Column(name = "landuse_density_n")
	private Double landuse_density_n;
	
	@Column(name = "university_degree_n")
	private Double university_degree_n;

	@Column(name = "diversity_sector_n")
	private Double diversity_sector_n;

	@Column(name = "accessibility_t_n")
	private Double accessibility_t_n;
	
	@Column(name = "bus_dev_mun_n")
	private Double bus_dev_mun_n;
	
	@Column(name = "bus_dev_cant_n")
	private Double bus_dev_cant_n;

	@Column(name = "urban_centre")
	private Integer urban_centre;
	
	@Column(name = "SETTLEMENT_AREA")
	private Integer settlement_area;
	
	@Column(name = "diversity_sector")
	private Integer diversity_sector;
	
	@Column(name = "accessibility_res")
	private Integer accessibility_res;
	
	public Integer getActualResidents() {
		return actualResidents;
	}

	public void setActualResidents(Integer act_residents) {
		this.actualResidents = act_residents;
	}

	public Integer getActualWorkers() {
		return actualWorkers;
	}

	public void setActualWorkers(Integer act_workers) {
		this.actualWorkers = act_workers;
	}

	/**
	 * access variable car residents
	 */
	@Column(name = "av_1")
	private Double av_1;
	/**
	 * access variable car employees
	 */
	@Column(name = "av_2")
	private Double av_2;
	/**
	 * access variable public transport residents
	 */
	@Column(name = "av_3")
	private Double av_3;
	/**
	 * access variable public transport employees
	 */
	@Column(name = "av_4")
	private Double av_4;
	/**
	 * access variable bicycle residents
	 */
	@Column(name = "av_5")
	private Double av_5;
	/**
	 * access variable bicycle employees
	 */
	@Column(name = "av_6")
	private Double av_6;

	@Column(name = "av_7")
	private Double av_7;
	@Column(name = "av_8")
	private Double av_8;
	@Column(name = "av_9")
	private Double av_9;
	@Column(name = "av_10")
	private Double av_10;
	@Column(name = "av_11")
	private Double av_11;
	@Column(name = "av_12")
	private Double av_12;	
	
	
	@Column(name = "LANDTYPE")
	private Integer landtype;	

	@Column(name = "maxFloorAreaRes")
	private Double maxFloorAreaRes;
	
	@Column(name = "usedFloorAreaRes")
	private Double usedFloorAreaRes;
	
	@Column(name = "maxFloorAreaWrk")
	private Double maxFloorAreaWrk;
	
	@Column(name = "usedFloorAreaWrk")
	private Double usedFloorAreaWrk;
	
	@Column(name = "maxFloorAreaAll")
	private Double maxFloorAreaAll;
	
	@Column(name = "usedFloorAreaAll")
	private Double usedFloorAreaAll;
	
	@Column(name = "locSubsetH")
	private String locationSubsetH;
	
	@Column(name = "locSubsetB")
	private String locationSubsetB;

	@Column(name = "var1")
	private Double var1;	
	@Column(name = "var2")
	private Double var2;
	@Column(name = "var3")
	private Double var3;
	@Column(name = "var4")
	private Double var4;
	@Column(name = "var5")
	private Double var5;
	@Column(name = "var6")
	private Double var6;
	@Column(name = "var7")
	private Double var7;
	@Column(name = "var8")
	private Double var8;
	@Column(name = "var9")
	private Double var9;
	@Column(name = "var10")
	private Double var10;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public Integer getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(Integer municipalityId) {
		this.municipalityId = municipalityId;
	}
	
	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

//	public Integer getLocationId_A() {
//		return locationId_A;
//	}
//
//	public void setLocationId_A(Integer locationId_A) {
//		this.locationId_A = locationId_A;
//	}

//	public Integer getLocationId_B() {
//		return locationId_B;
//	}
//
//	public void setLocationId_B(Integer locationId_B) {
//		this.locationId_B = locationId_B;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Deprecated
	public Integer getPopulation_1() {
		return population_1;
	}

	@Deprecated
	public void setPopulation_1(Integer population_1) {
		this.population_1 = population_1;
	}

	@Deprecated
	public Integer getPopulation_2() {
		return population_2;
	}

	@Deprecated
	public void setPopulation_2(Integer population_2) {
		this.population_2 = population_2;
	}

	@Deprecated
	public Integer getPopulation_3() {
		return population_3;
	}

	@Deprecated
	public void setPopulation_3(Integer population_3) {
		this.population_3 = population_3;
	}

	@Deprecated
	public Integer getPopulation_4() {
		return population_4;
	}

	@Deprecated
	public void setPopulation_4(Integer population_4) {
		this.population_4 = population_4;
	}

//	public Integer getFinance_1() {
//		return finance_1;
//	}
//
//	public void setFinance_1(Integer finance_1) {
//		this.finance_1 = finance_1;
//	}

	public Integer getType_1() {
		return type_1;
	}

	public void setType_1(Integer type_1) {
		this.type_1 = type_1;
	}

	public Map<Integer, GeneralizedLocationDistance> getDistances() {
		return distances;
	}

	public void setDistances(Map<Integer, GeneralizedLocationDistance> distances) {
		this.distances = distances;
	}

	public Integer getRun() {
		return run;
	}

	public void setRun(Integer run) {
		this.run = run;
	}

	public List<Household> getHouseholds() {
		return households;
	}

	public void setHouseholds(List<Household> households) {
		this.households = households;
	}

	public List<Business> getBusinesses() {
		return businesses;
	}

	public void setBusinesses(List<Business> businesses) {
		this.businesses = businesses;
	}

	public Double getAv_1() {
		return av_1;
	}

	public void setAv_1(Double av_1) {
		this.av_1 = av_1;
	}

	public Double getAv_2() {
		return av_2;
	}

	public void setAv_2(Double av_2) {
		this.av_2 = av_2;
	}

	public Double getAv_3() {
		return av_3;
	}

	public void setAv_3(Double av_3) {
		this.av_3 = av_3;
	}

	public Double getAv_4() {
		return av_4;
	}

	public void setAv_4(Double av_4) {
		this.av_4 = av_4;
	}

	public Double getAv_5() {
		return av_5;
	}

	public void setAv_5(Double av_5) {
		this.av_5 = av_5;
	}

	public Double getAv_6() {
		return av_6;
	}

	public void setAv_6(Double av_6) {
		this.av_6 = av_6;
	}

	public Double getAv_7() {
		return av_7;
	}

	public void setAv_7(Double av_7) {
		this.av_7 = av_7;
	}

	public Double getAv_8() {
		return av_8;
	}

	public void setAv_8(Double av_8) {
		this.av_8 = av_8;
	}

	public Double getAv_9() {
		return av_9;
	}

	public void setAv_9(Double av_9) {
		this.av_9 = av_9;
	}

	public Double getAv_10() {
		return av_10;
	}

	public void setAv_10(Double av_10) {
		this.av_10 = av_10;
	}

	public Double getAv_11() {
		return av_11;
	}

	public void setAv_11(Double av_11) {
		this.av_11 = av_11;
	}

	public Double getAv_12() {
		return av_12;
	}

	public void setAv_12(Double av_12) {
		this.av_12 = av_12;
	}	
	
	public Boolean isLargeCity() {
		return landtype == RCConstants.LANDTYPE_LARGECITY;
	}

	public Boolean isAgglomeration() {
		return landtype == RCConstants.LANDTYPE_AGGLOMERATION;
	}

	public Integer getLandtype() {
		return landtype;
	}

	public void setLandtype(Integer landtype) {
		this.landtype = landtype;
	}

	public Double getMaxFloorAreaRes() {
		return maxFloorAreaRes;
	}

	public void setMaxFloorAreaRes(Double maxFloorAreaRes) {
		this.maxFloorAreaRes = maxFloorAreaRes;
	}

	public Double getUsedFloorAreaRes() {
		return usedFloorAreaRes;
	}

	public void setUsedFloorAreaRes(Double usedFloorAreaRes) {
		this.usedFloorAreaRes = usedFloorAreaRes;
	}

	public Double getMaxFloorAreaWrk() {
		return maxFloorAreaWrk;
	}

	public void setMaxFloorAreaWrk(Double maxFloorAreaWrk) {
		this.maxFloorAreaWrk = maxFloorAreaWrk;
	}

	public Double getUsedFloorAreaWrk() {
		return usedFloorAreaWrk;
	}

	public void setUsedFloorAreaWrk(Double usedFloorAreaWrk) {
		this.usedFloorAreaWrk = usedFloorAreaWrk;
	}

	public Double getMaxFloorAreaAll() {
		return maxFloorAreaAll;
	}

	public void setMaxFloorAreaAll(Double maxFloorAreaAll) {
		this.maxFloorAreaAll = maxFloorAreaAll;
	}

	public Double getUsedFloorAreaAll() {
		return usedFloorAreaAll;
	}

	public void setUsedFloorAreaAll(Double usedFloorAreaAll) {
		this.usedFloorAreaAll = usedFloorAreaAll;
	}
	
	public String getLocationSubsetH() {
		return locationSubsetH;
	}

	public void setLocationSubsetH(String locationSubset) {
		this.locationSubsetH = locationSubset;
	}
	
	public String getLocationSubsetB() {
		return locationSubsetB;
	}

	public void setLocationSubsetB(String locationSubset) {
		this.locationSubsetB = locationSubset;
	}
	
	public Integer getFlat_rent() {
		return flat_rent;
	}

	public void setFlat_rent(Integer flat_rent) {
		this.flat_rent = flat_rent;
	}
	
	public Double getTax_holdingcomp_n() {
		return tax_holdingcomp_n;
	}

	public void setTax_holdingcomp_n(Double tax_holdingcomp_n) {
		this.tax_holdingcomp_n = tax_holdingcomp_n;
	}

	public Double getTax_partnership_n() {
		return tax_partnership_n;
	}

	public void setTax_partnership_n(Double tax_partnership_n) {
		this.tax_partnership_n = tax_partnership_n;
	}

	public Double getTax_companies_n() {
		return tax_companies_n;
	}

	public void setTax_companies_n(Double tax_companies_n) {
		this.tax_companies_n = tax_companies_n;
	}

	public Integer getMotorway_access() {
		return motorway_access;
	}

	public void setMotorway_access(Integer motorway_access) {
		this.motorway_access = motorway_access;
	}

	public Integer getRailway_access() {
		return railway_access;
	}

	public void setRailway_access(Integer railway_access) {
		this.railway_access = railway_access;
	}

	public Double getLanduse_density_n() {
		return landuse_density_n;
	}

	public void setLanduse_density_n(Double landuse_density_n) {
		this.landuse_density_n = landuse_density_n;
	}

	public Double getAccessibility_t_n() {
		return accessibility_t_n;
	}

	public void setAccessibility_t_n(Double accessibility_t_n) {
		this.accessibility_t_n = accessibility_t_n;
	}

	public Integer getUrban_centre() {
		return urban_centre;
	}

	public void setUrban_centre(Integer urban_centre) {
		this.urban_centre = urban_centre;
	}

	public Integer getSettlement_area() {
		return settlement_area;
	}

	public void setSettlement_area(Integer settlement_area) {
		this.settlement_area = settlement_area;
	}

	public Integer getDiversity_sector() {
		return diversity_sector;
	}

	public void setDiversity_sector(Integer diversity_sector) {
		this.diversity_sector = diversity_sector;
	}


	public Double getResident_landprice_n() {
		return resident_landprice_n;
	}

	public void setResident_landprice_n(Double resident_landprice_n) {
		this.resident_landprice_n = resident_landprice_n;
	}
		
	public Double getUniversity_degree_n() {
		return university_degree_n;
	}

	public void setUniversity_degree_n(Double university_degree_n) {
		this.university_degree_n = university_degree_n;
	}

	public Double getDiversity_sector_n() {
		return diversity_sector_n;
	}

	public void setDiversity_sector_n(Double diversity_sector_n) {
		this.diversity_sector_n = diversity_sector_n;
	}

	public Double getBus_dev_mun_n() {
		return bus_dev_mun_n;
	}

	public void setBus_dev_mun_n(Double bus_dev_mun_n) {
		this.bus_dev_mun_n = bus_dev_mun_n;
	}

	public Double getBus_dev_cant_n() {
		return bus_dev_cant_n;
	}

	public void setBus_dev_cant_n(Double bus_dev_cant_n) {
		this.bus_dev_cant_n = bus_dev_cant_n;
	}

	public Integer getAccessibility_res() {
		return accessibility_res;
	}

	public void setAccessibility_res(Integer accessibility_res) {
		this.accessibility_res = accessibility_res;
	}

	public Double getVar1() {
		return var1;
	}

	public void setVar1(Double var1) {
		this.var1 = var1;
	}

	public Double getVar2() {
		return var2;
	}

	public void setVar2(Double var2) {
		this.var2 = var2;
	}

	public Double getVar3() {
		return var3;
	}

	public void setVar3(Double var3) {
		this.var3 = var3;
	}

	public Double getVar4() {
		return var4;
	}

	public void setVar4(Double var4) {
		this.var4 = var4;
	}

	public Double getVar5() {
		return var5;
	}

	public void setVar5(Double var5) {
		this.var5 = var5;
	}

	public Double getVar6() {
		return var6;
	}

	public void setVar6(Double var6) {
		this.var6 = var6;
	}

	public Double getVar7() {
		return var7;
	}

	public void setVar7(Double var7) {
		this.var7 = var7;
	}

	public Double getVar8() {
		return var8;
	}

	public void setVar8(Double var8) {
		this.var8 = var8;
	}

	public Double getVar9() {
		return var9;
	}

	public void setVar9(Double var9) {
		this.var9 = var9;
	}

	public Double getVar10() {
		return var10;
	}

	public void setVar10(Double var10) {
		this.var10 = var10;
	}
	
}