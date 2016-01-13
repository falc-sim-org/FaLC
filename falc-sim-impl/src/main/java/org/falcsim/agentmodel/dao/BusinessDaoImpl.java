package org.falcsim.agentmodel.dao;

import java.util.List;

import org.falcsim.agentmodel.dao.jdbc.JDBCApproach;
import org.falcsim.agentmodel.domain.Business;
import org.springframework.stereotype.Repository;

@Repository("BusinessDao")
public class BusinessDaoImpl extends AbstractDao implements BusinessDao {
	
	
	@Override
	@Deprecated
	public Business selectBusinessById(Integer businessId) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void deleteBusiness(Business business) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public List<Business> selectAllBusinesses() {
		return JDBCApproach.readAll(Business.class);
	}

	@Override
	@Deprecated
	public void saveOrUpdateBusiness(Business business) {
		throw new RuntimeException("Unsupported method");
	}


	@Override
	@Deprecated
	public void saveOrUpdateBusinessList(List<Business> businessList) {
		throw new RuntimeException("Unsupported method");
	}
				
	@Override
	@Deprecated
	public Business selectRandomBusiness() {		
		throw new RuntimeException("Unsupported method");
	}


}