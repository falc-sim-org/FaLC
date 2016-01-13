package org.falcsim.agentmodel.domain.service;

import java.util.List;
import java.util.Map;

import org.falcsim.agentmodel.domain.Business;
import org.falcsim.agentmodel.domain.Canton;
import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Municipality;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.domain.Universe;

/**
 * Inteface of Universe service utilities
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public interface UniverseServiceUtil {

	/**
	 * Universe state
	 * 
	 */
	public enum UniverseState {
		 CLEAR, LOADING, LOADED, SAVING, POPULATING; 
	}
	
	/**
	 * get current loaded universe
	 * 
	 * @return Universe
	 */
	public Universe getUniverse();
	
	/**
	 * get current state of universe
	 * 
	 * @return Universe state
	 */
	public UniverseState getUniverseState();
	
	/**
	 * clear universe cache and set universe state
	 * 
	 */
	public void clearUniverse();
	
	/**
	 * save or update location to the universe
	 * 
	 * @param location
	 */
	public void saveOrUpdateLocation(Location location);

	/**
	 * save or update list of locations
	 * 
	 * @param locations
	 */
	public void saveOrUpdateLocations(List<Location> locations);

	/**
	 * select all locations
	 * 
	 * @return List of Location 
	 */
	public List<Location> selectAllLocations();

	/**
	 * select all cantons
	 * 
	 * @return List of Cantons 
	 */
	public List<Canton> selectAllCantons();
	
	/**
	 * select all municipalities
	 * 
	 * @return List of Municipalities 
	 */
	public List<Municipality> selectAllMunicipalities();
	
	/**
	 * select canton by cantonId
	 * 
	 * @param cantonId
	 * @return Canton object
	 */
	public Canton selectCantonById(int cantonId);
	
	/**
	 * select municipality by municipalityId
	 * 
	 * @param municipalityId
	 * @return Municipality object
	 */
	public Municipality selectMunicipalityById(int municipalityId);
	
	/**
	 * select location by locationId
	 * 
	 * @param locationId
	 * @return Location object
	 */
	public Location selectLocationById(int locationId);

	/**
	 * select list of location ids by criteria
	 * 
	 * @param criterion
	 * @return List of primary keys
	 */
	public List<Integer> selectLocationIdsByCriterion(String criterion);

	/**
	 * return list of locations by entered criteria
	 * 
	 * @param syntheseQuery1
	 * @return List of Location 
	 */
	public List<Location> selectLocationsByCriterion(String syntheseQuery1);

	/**
	 * select locations by distance from location
	 * 
	 * @param locationId
	 * @param radius
	 * @return List of Location 
	 */
	public List<Location> selectLocationsByDistanceFromLocation(int locationId,
			float radius);

	/**
	 * update location
	 * 
	 * @param location
	 */
	public void updateLocation(Location location);

	/**
	 * select businesses by location list
	 * 
	 * @param locations
	 * @return Map of Businesses for Locations Id
	 */
	public Map<Integer, List<Business>> selectBusinessesesMapByLocationList(
			List<Location> locations);

	/**
	 * select all businesses
	 * 
	 * @return List of Businesses 
	 */
	public List<Business> selectAllBusinesses();

	/**
	 * select business by businessId
	 * 
	 * @param businessId
	 * @return Business 
	 */
	public Business selectBusinessById(int businessId);

	/**
	 * select households by locationId
	 * 
	 * @param locationId
	 * @return List of Households 
	 */
	public List<Household> selectHouseholdsByLocationId(int locationId);

	/**
	 * select household by householdId
	 * 
	 * @param householdId
	 * @return Household
	 */
	public Household selectHouseholdById(int householdId);

	/**
	 * save or update household
	 * 
	 * @param household
	 */
	public void saveOrUpdateHousehold(Household household);

	/**
	 * select all households
	 * 
	 * @return List of Households 
	 */
	public List<Household> selectAllHouseholds();

	/**
	 * return map of households for given location list
	 * 
	 * @param locations
	 * @return Map of Households for Locations Id
	 */
	public Map<Integer, List<Household>> selectHouseholdsMapByLocationList(
			List<Location> locations);

	/**
	 * select persons count by locationId
	 * 
	 * @param locationId
	 * @return Number of Persons
	 */
	public int selectPersonsCountByLocation(int locationId);

	/**
	 * return all persons
	 * 
	 * @return List of Persons 
	 */
	public List<Person> selectAllPersons();

	/**
	 * central universe load
	 * 
	 * @return Universe
	 */
	public Universe loadUniverse();
	
	/**
	 * recreate indexes to universe objects
	 * 
	 */
	public void resetUniverseIndexes();

	/**
	 * save or update universe
	 * 
	 * @param universe
	 */
	public void saveOrUpdateUniverse(Universe universe);

	/**
	 * save or update cached universe
	 */
	public void saveOrUpdateUniverse();

	/**
	 * save or update cached universe with/without StepTable
	 */
	public void saveOrUpdateUniverse(boolean saveStepTable);
	
	/**
	 * save or update cached universe with/without StepTable and with/without clear cached universe data
	 */
	public void saveOrUpdateUniverse(boolean saveStepTable, boolean freeUniverseCache);
	
	/**
	 * add history of universe
	 */
	public void addHistory();

	/**
	 * add history of universe
	 */
	public void saveHistory(List list, Class cls);
	
	public void populateUniverse();

}
