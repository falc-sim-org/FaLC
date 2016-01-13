package org.falcsim.agentmodel.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.dao.jdbc.JDBCApproach;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.util.StringUtil;
import org.springframework.stereotype.Repository;

@Repository("HouseholdDao")
public class HouseholdDaoImpl extends AbstractDao implements HouseholdDao {

	@Override
	@Deprecated
	public Household selectHouseholdById(Integer householdId) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void deleteHousehold(Household household) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public List<Household> selectAllHouseholds() {
		return JDBCApproach.readAll(Household.class);
	}

	@Override
	@Deprecated
	public void deleteAllHouseholds() {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void saveOrUpdateHousehold(Household household) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void saveOrUpdateHouseholdList(List<Household> householdList) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void deleteHouseholdList(List<Household> householdList) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public List<Household> selectHouseholdsByLocation(Integer locationId) {
		throw new RuntimeException("Modify me");
		/*return JDBCApproach.readAll(Household.class,
				" where locationId = " + locationId);*/
	}

	@Override
	public Map<Integer, List<Household>> selectHouseholdsMapByLocationList(
			List<Location> locs) {

		String LocSetC = StringUtil.packageLocsString(locs);

		return getHoussMap(LocSetC);
	}

	private Map<Integer, List<Household>> getHoussMap(String qStr) {
		Map<Integer, List<Household>> parameters = new HashMap<Integer, List<Household>>();
		throw new RuntimeException("Modify Me");
		/*List<Household> lh = JDBCApproach.readAll(Household.class, "where locationId in " + qStr);
		if (lh.size() > 0)
			for (Household h : lh) {
				Integer hLocId = h.getLocationId();
				List<Household> lhloc = new ArrayList<Household>();
				if (parameters.containsKey(hLocId))
					lhloc = parameters.get(hLocId);
				lhloc.add(h);
				parameters.put(hLocId, lhloc);
			}

		return parameters;*/
	}

	@Override
	@Deprecated
	public Household selectRandomHousehold() {
		throw new RuntimeException("Unsupported method");
	}

}
