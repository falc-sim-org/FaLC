package org.falcsim.agentmodel.utility.domain;

import java.util.ArrayList;
import java.util.List;

import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;
import org.falcsim.agentmodel.util.StringUtil;

public class RipisEntityExportInfo {
	public int id;
	public int sector;
	public int type;
	public int destlocid;
	List<RipisIntervalInfo> ripis;
	
	public RipisEntityExportInfo(int id, int sector, int type, int destlocid, List<RealIdProbInterval> ripis){
		this.id = id;
		this.sector = sector;
		this.type = type;
		this.destlocid = destlocid;
		this.ripis = new ArrayList<RipisIntervalInfo>();
		for(RealIdProbInterval ripi: ripis){
			this.ripis.add(new RipisIntervalInfo(ripi.getId(), ripi.getLower(), ripi.getUpper()));	
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.fix(id));
		sb.append("; ");
		sb.append(StringUtil.fix(sector));
		sb.append("; ");
		sb.append(type > 100 ? StringUtil.fix(type-100) + "; 1" : StringUtil.fix(type) + "; 0");
		sb.append("; ");
		sb.append(StringUtil.fix(destlocid));		
			
		for(RipisIntervalInfo ripi : ripis){
			sb.append("; ");
			sb.append(ripi.toString());
		}

		return sb.toString();
	}
}
