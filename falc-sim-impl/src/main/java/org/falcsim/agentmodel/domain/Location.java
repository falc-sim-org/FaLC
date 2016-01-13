package org.falcsim.agentmodel.domain;

import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;
import org.falcsim.agentmodel.domain.statistics.RunStatistics;

/**
 * Extension of the core LocationSuperClass class of FaLC's conceptual module,
 * corresponds to the locations table
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Entity
@Table(name = "locations", schema = "population")	//formerly known as "locations_zones"
public class Location extends LocationSuperClass implements ICSVWrite, ICSVRead {
	
	public Location(){
		super.setBus_dev_mun_n(ClassStringUtil.ZERO_D);	//this is not define/used in Falc
	}
	
	public Location(Integer id, String name, Integer municipalityId, Integer locationId, Integer urbanCentre, Integer landType){
		super.setId(id);
		super.setName(name);
		super.setMunicipalityId(municipalityId);
		super.setLocationId(locationId);
		super.setRun(ClassStringUtil.ZERO_I);
		super.setActualResidents(ClassStringUtil.ZERO_I);
		super.setActualWorkers(ClassStringUtil.ZERO_I);
		super.setSettlement_area(ClassStringUtil.ZERO_I);
		super.setFlat_rent(ClassStringUtil.ZERO_I);
		super.setUrban_centre(urbanCentre);
		super.setDiversity_sector(ClassStringUtil.ZERO_I);
		super.setMotorway_access(ClassStringUtil.ZERO_I);
		super.setRailway_access(ClassStringUtil.ZERO_I);
		super.setResident_landprice_n(ClassStringUtil.ZERO_D);
		super.setLanduse_density_n(ClassStringUtil.ZERO_D);
		super.setUniversity_degree_n(ClassStringUtil.ZERO_D);
		super.setDiversity_sector_n(ClassStringUtil.ZERO_D);
		super.setTax_holdingcomp_n(ClassStringUtil.ZERO_D);
		super.setTax_partnership_n(ClassStringUtil.ZERO_D);
		super.setTax_companies_n(ClassStringUtil.ZERO_D);
		super.setAccessibility_t_n(ClassStringUtil.ZERO_D);
		super.setBus_dev_cant_n(ClassStringUtil.ZERO_D);
		super.setAccessibility_res(ClassStringUtil.ZERO_I);
		super.setBus_dev_mun_n(ClassStringUtil.ZERO_D);
		super.setAv_1(ClassStringUtil.ZERO_D);
		super.setAv_2(ClassStringUtil.ZERO_D);
		super.setAv_3(ClassStringUtil.ZERO_D);
		super.setAv_4(ClassStringUtil.ZERO_D);
		super.setAv_5(ClassStringUtil.ZERO_D);
		super.setAv_6(ClassStringUtil.ZERO_D);
		super.setAv_7(ClassStringUtil.ZERO_D);
		super.setAv_8(ClassStringUtil.ZERO_D);
		super.setAv_9(ClassStringUtil.ZERO_D);
		super.setAv_10(ClassStringUtil.ZERO_D);
		super.setAv_11(ClassStringUtil.ZERO_D);
		super.setAv_12(ClassStringUtil.ZERO_D);		
		super.setLandtype(landType);
		super.setMaxFloorAreaRes(ClassStringUtil.ZERO_D);
		super.setUsedFloorAreaRes(ClassStringUtil.ZERO_D);
		super.setMaxFloorAreaWrk(ClassStringUtil.ZERO_D);
		super.setUsedFloorAreaWrk(ClassStringUtil.ZERO_D);
		super.setMaxFloorAreaAll(ClassStringUtil.ZERO_D);
		super.setUsedFloorAreaAll(ClassStringUtil.ZERO_D);
		super.setLocationSubsetH(ClassStringUtil.EMPTY);
		super.setLocationSubsetB(ClassStringUtil.EMPTY);
		
		super.setVar1(ClassStringUtil.ZERO_D);
		super.setVar2(ClassStringUtil.ZERO_D);
		super.setVar3(ClassStringUtil.ZERO_D);
		super.setVar4(ClassStringUtil.ZERO_D);
		super.setVar5(ClassStringUtil.ZERO_D);
		super.setVar6(ClassStringUtil.ZERO_D);
		super.setVar7(ClassStringUtil.ZERO_D);
		super.setVar8(ClassStringUtil.ZERO_D);
		super.setVar9(ClassStringUtil.ZERO_D);
		super.setVar10(ClassStringUtil.ZERO_D);
		
	}
	
	private Double usedFloorAreaResDemography;	
	private Double usedFloorAreaWrkDemography;		
	private Double usedFloorAreaAllDemography;	
	private Double usedFloorAreaResMoveHH;	
	private Double usedFloorAreaWrkMoveHH;		
	private Double usedFloorAreaAllMoveHH;		
	
	private Boolean MG_overlimit_H;	
	private Boolean MG_overlimit_B;
	private Boolean LU_overlimit_H;
	private Boolean LU_overlimit_B;
	private Boolean LU_overlimit_T;	
	
	public Double getUsedFloorAreaResDemography() {
		return usedFloorAreaResDemography;
	}

	public void setUsedFloorAreaResDemography(Double usedFloorAreaResDemography) {
		this.usedFloorAreaResDemography = usedFloorAreaResDemography;
	}

	public Double getUsedFloorAreaWrkDemography() {
		return usedFloorAreaWrkDemography;
	}

	public void setUsedFloorAreaWrkDemography(Double usedFloorAreaWrkDemography) {
		this.usedFloorAreaWrkDemography = usedFloorAreaWrkDemography;
	}

	public Double getUsedFloorAreaAllDemography() {
		return usedFloorAreaAllDemography;
	}

	public void setUsedFloorAreaAllDemography(Double usedFloorAreaAllDemography) {
		this.usedFloorAreaAllDemography = usedFloorAreaAllDemography;
	}

	public Double getUsedFloorAreaResMoveHH() {
		return usedFloorAreaResMoveHH;
	}

	public void setUsedFloorAreaResMoveHH(Double usedFloorAreaResMoveHH) {
		this.usedFloorAreaResMoveHH = usedFloorAreaResMoveHH;
	}

	public Double getUsedFloorAreaWrkMoveHH() {
		return usedFloorAreaWrkMoveHH;
	}

	public void setUsedFloorAreaWrkMoveHH(Double usedFloorAreaWrkMoveHH) {
		this.usedFloorAreaWrkMoveHH = usedFloorAreaWrkMoveHH;
	}

	public Double getUsedFloorAreaAllMoveHH() {
		return usedFloorAreaAllMoveHH;
	}

	public void setUsedFloorAreaAllMoveHH(Double usedFloorAreaAllMoveHH) {
		this.usedFloorAreaAllMoveHH = usedFloorAreaAllMoveHH;
	}	
	
	
	public Boolean getMG_overlimit_H() {
		return MG_overlimit_H;
	}

	public void setMG_overlimit_H(Boolean mG_overlimit_H) {
		this.MG_overlimit_H = mG_overlimit_H;
	}

	public Boolean getMG_overlimit_B() {
		return MG_overlimit_B;
	}

	public void setMG_overlimit_B(Boolean mG_overlimit_B) {
		this.MG_overlimit_B = mG_overlimit_B;
	}

	public Boolean getLU_overlimit_H() {
		return LU_overlimit_H;
	}

	public void setLU_overlimit_H(Boolean lU_overlimit_H) {
		this.LU_overlimit_H = lU_overlimit_H;
	}

	public Boolean getLU_overlimit_B() {
		return LU_overlimit_B;
	}

	public void setLU_overlimit_B(Boolean lU_overlimit_B) {
		this.LU_overlimit_B = lU_overlimit_B;
	}
	
	public Boolean getLU_overlimit_T() {
		return LU_overlimit_T;
	}

	public void setLU_overlimit_T(Boolean lU_overlimit_T) {
		this.LU_overlimit_T = lU_overlimit_T;
	}	
	
	
	/**
	 * variable holds relocation info. From which location and number of relocated objects
	 *  
	 */
	private Map<Integer,RelocationInfo> relocmap = new TreeMap<Integer,RelocationInfo>();

	public Map<Integer, RelocationInfo> getRelocationMap() {
		return relocmap;
	}

	/**
	 * variable holds info about closed/created firms, households, persons
	 *  
	 */	
	LocationEventsInfo eventsStatus = new LocationEventsInfo();
	public LocationEventsInfo getEventsStatus() {
		return eventsStatus;
	}

	@Override
	public String getRowString(String separator, Object ... others) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.checkNull(super.getId()) + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.checkNull(super.getName()) + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.checkNull(super.getLocationId()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getUrban_centre()));		
		return sb.toString();
	}	
	
	public String getRowStringOld(String separator, Object ... others) {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassStringUtil.checkNull(super.getId()) + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.checkNull(super.getName()) + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.checkNull(super.getMunicipalityId()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getLocationId()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getRun()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getActualResidents()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getActualWorkers()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getSettlement_area()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getFlat_rent()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getUrban_centre()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getDiversity_sector()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getMotorway_access()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getRailway_access()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getResident_landprice_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getLanduse_density_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getUniversity_degree_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getDiversity_sector_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getTax_holdingcomp_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getTax_partnership_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getTax_companies_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAccessibility_t_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getBus_dev_cant_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAccessibility_res()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getBus_dev_mun_n()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_1()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_2()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_3()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_4()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_5()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_6()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_7()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_8()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_9()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_10()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_11()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getAv_12()) + separator);		
		sb.append(ClassStringUtil.checkNull(super.getLandtype()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getMaxFloorAreaRes()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getUsedFloorAreaRes()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getMaxFloorAreaWrk()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getUsedFloorAreaWrk()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getMaxFloorAreaAll()) + separator);
		sb.append(ClassStringUtil.checkNull(super.getUsedFloorAreaAll()) + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.checkNull(super.getLocationSubsetH()) + ClassStringUtil.QUOTE + separator);
		sb.append(ClassStringUtil.QUOTE + ClassStringUtil.checkNull(super.getLocationSubsetB()) + ClassStringUtil.QUOTE);
		
		return sb.toString();
	}

	@Override
	public void getRowValues(String[] values, Object... objects) {
		super.setId(ClassStringUtil.parseInt(values[0]));
		super.setName(ClassStringUtil.UnQuote(values[1]));
		super.setLocationId(ClassStringUtil.parseInt(values[2]));
		super.setUrban_centre(ClassStringUtil.parseInt(values[3]));	
	}
	
	public void getRowValuesOld(String[] values, Object... objects) {
		super.setId(ClassStringUtil.parseInt(values[0]));
		super.setName(ClassStringUtil.UnQuote(values[1]));
		super.setMunicipalityId(ClassStringUtil.parseInt(values[2]));
		super.setLocationId(ClassStringUtil.parseInt(values[3]));
		super.setRun(ClassStringUtil.parseInt(values[4]));
		super.setActualResidents(ClassStringUtil.parseInt(values[5]));
		super.setActualWorkers(ClassStringUtil.parseInt(values[6]));
		super.setSettlement_area(ClassStringUtil.parseInt(values[7]));
		super.setFlat_rent(ClassStringUtil.parseInt(values[8]));
		super.setUrban_centre(ClassStringUtil.parseInt(values[9]));
		super.setDiversity_sector(ClassStringUtil.parseInt(values[10]));
		super.setMotorway_access(ClassStringUtil.parseInt(values[11]));
		super.setRailway_access(ClassStringUtil.parseInt(values[12]));
		super.setResident_landprice_n(ClassStringUtil.parseDouble(values[13]));
		super.setLanduse_density_n(ClassStringUtil.parseDouble(values[14]));
		super.setUniversity_degree_n(ClassStringUtil.parseDouble(values[15]));
		super.setDiversity_sector_n(ClassStringUtil.parseDouble(values[16]));
		super.setTax_holdingcomp_n(ClassStringUtil.parseDouble(values[17]));
		super.setTax_partnership_n(ClassStringUtil.parseDouble(values[18]));
		super.setTax_companies_n(ClassStringUtil.parseDouble(values[19]));
		super.setAccessibility_t_n(ClassStringUtil.parseDouble(values[20]));
		super.setBus_dev_cant_n(ClassStringUtil.parseDouble(values[21]));
		super.setAccessibility_res(ClassStringUtil.parseInt(values[22]));
		super.setBus_dev_mun_n(ClassStringUtil.parseDouble(values[23]));
		super.setAv_1(ClassStringUtil.parseDouble(values[24]));
		super.setAv_2(ClassStringUtil.parseDouble(values[25]));
		super.setAv_3(ClassStringUtil.parseDouble(values[26]));
		super.setAv_4(ClassStringUtil.parseDouble(values[27]));
		super.setAv_5(ClassStringUtil.parseDouble(values[28]));
		super.setAv_6(ClassStringUtil.parseDouble(values[29]));
		super.setLandtype(ClassStringUtil.parseInt(values[30]));
		super.setMaxFloorAreaRes(ClassStringUtil.parseDouble(values[31]));
		super.setUsedFloorAreaRes(ClassStringUtil.parseDouble(values[32]));
		super.setMaxFloorAreaWrk(ClassStringUtil.parseDouble(values[33]));
		super.setUsedFloorAreaWrk(ClassStringUtil.parseDouble(values[34]));
		super.setMaxFloorAreaAll(ClassStringUtil.parseDouble(values[35]));
		super.setUsedFloorAreaAll(ClassStringUtil.parseDouble(values[36]));
		super.setLocationSubsetH(ClassStringUtil.UnQuote(values[37]));
		super.setLocationSubsetB(ClassStringUtil.UnQuote(values[38]));
	
	}

	RunStatistics runStatistics = new RunStatistics(this);
	public RunStatistics getRunStatistics() {
		return runStatistics;
	}	
	
	
}
