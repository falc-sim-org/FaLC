package org.falcsim.agentmodel.dao;

import java.util.List;

import org.falcsim.agentmodel.domain.Business;

/**
 * Dao interface for Business
 * 
 * @author regioConcept AG
 * @version 0.5
 * @since   0.5 
 */

public interface BusinessDao {

	public Business selectBusinessById(Integer businessId);

	public void saveOrUpdateBusiness(Business business);

	public void deleteBusiness(Business business);

	public List<Business> selectAllBusinesses();

	public void saveOrUpdateBusinessList(List<Business> businessList);

	public Business selectRandomBusiness();
}