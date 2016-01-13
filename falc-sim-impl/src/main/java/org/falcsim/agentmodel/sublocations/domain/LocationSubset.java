package org.falcsim.agentmodel.sublocations.domain;

import javax.persistence.Id;
import javax.persistence.Table;

import org.falcsim.agentmodel.dao.util.ClassStringUtil;
import org.falcsim.agentmodel.domain.ICSVWrite;

/**
 * Supplementary class to store locations subsets into CSV
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */
@Table(name="location_subset_development", schema="subsets")
public class LocationSubset implements ICSVWrite{

	public static final int COLUMNS_COUNT = 51;
	@Id
	private Integer locId;
	private String denot;
	private String[] columns_h = new String[COLUMNS_COUNT];
	private String[] columns_b = new String[COLUMNS_COUNT];
	
	@Override
	public String getRowString(String separator, Object... others) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.locId + separator);
		sb.append(ClassStringUtil.QUOTE + this.denot + ClassStringUtil.QUOTE + separator);
		
		for(int i = 0; i < this.columns_h.length; i++){
			sb.append(ClassStringUtil.QUOTE + columns_h[i] + ClassStringUtil.QUOTE);
			sb.append(separator);
		}
		
		for(int i = 0; i < this.columns_b.length; i++){
			sb.append(ClassStringUtil.QUOTE + columns_b[i] + ClassStringUtil.QUOTE);
			if(i < this.columns_b.length - 1)
				sb.append(separator);
		}
		return sb.toString();
	}
	
	public Integer getLocId() {
		return locId;
	}

	public void setLocId(Integer locId) {
		this.locId = locId;
	}

	public String getDenot() {
		return denot;
	}

	public void setDenot(String denot) {
		this.denot = denot;
	}

	public void setColumn_h(int columnNo, String column) {
		this.columns_h[columnNo] = column;
	}

	public void setColumn_b(int columnNo, String column) {
		this.columns_b[columnNo] = column;
	}

}
