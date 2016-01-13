package org.falcsim.agentmodel.dao;

import java.util.ArrayList;
import java.util.List;

import org.falcsim.agentmodel.dao.jdbc.JDBCApproach;
import org.falcsim.agentmodel.domain.Location;

//@Repository("LocationDao")
public class LocationDaoImpl extends AbstractDao implements LocationDao {

	@Override
	@Deprecated
	public Location selectLocationById(Integer locationId) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public List<Location> selectLocationsByLocationId(Integer kantonsnr) {
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Location> selectAllLocations() {
		return JDBCApproach.readAll(Location.class);
	}

	@Override
	@Deprecated
	public void updateLocation(Location location) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void saveOrUpdateLocation(Location location) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public List<Location> selectLocationsByCriterion(String criterion) {
		throw new RuntimeException("Implement me");
		/*return JDBCApproach.readAll(Location.class, criterion);*/
	}

	@Override
	@Deprecated
	public Location selectRandomLocation() {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void saveOrUpdateLocations(List<Location> locs) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public List<Integer> selectLocationIdsByCriterion(String criterion) {
		List<Location> list = selectLocationsByCriterion(criterion);
		List<Integer> rslt = new ArrayList<Integer>();
		for (Location l : list) {
			rslt.add(l.getId());
		}
		return rslt;
	}

}
