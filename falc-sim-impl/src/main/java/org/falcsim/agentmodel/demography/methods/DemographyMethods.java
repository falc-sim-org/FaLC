package org.falcsim.agentmodel.demography.methods;

import java.util.Date;
import java.util.List;

import org.falcsim.agentmodel.domain.Household;
import org.falcsim.agentmodel.domain.Location;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;


/** Interface to the basic demographic methods
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 */
public interface DemographyMethods {
	
	/**
	 * Assigns date of death
	 * @param theYear - the current FaLC year
	 * @return the date
	 */
	public Date assignDateOfDeath(Integer theYear);
	/**
	 * Assigns date of birth within an age interval
	 * @param startYear - the year from which the age is computed
	 * @param minAge - lower age limit
	 * @param maxAge - upper age limit
	 * @return the date
	 */
	public Date assignDateOfBirth(Integer startYear, Integer minAge, Integer maxAge);
	/**
	 * Returns the age of a person p from its date of birth and the current FaLC year
	 * @param p - the person whose age is to be obtained
	 * @param currentYear
	 * @return the age
	 */
	public Integer getAgeOfPerson(Person p, Integer currentYear);
	/**
	 * Assigns a date of birth probabilistically depending on a age distribution ripis 
	 * @param startYear - year from which the ages in the age ripis are calculated
	 * @param p - person wdate of birth is to be assigned
	 * @param ripis -  rips-> age distribution list of probability intervals 
	 * @throws RCCustomException
	 */
	public void assignDateOfBirth (Integer startYear, Person p, List<RealIdProbInterval> ripis) throws RCCustomException;
	/**
	 * Returns a random date within a year
	 * @param year - the year 
	 * @return - the date
	 */
	public Date assignDateOnYear(Integer year);
	
	/**
	 * Assign mother and father IDs to persons in household
 	 * @param h - household 
	 */	
	public void setPersonParentsInHousehold(Household h);
		
	/**
	 * Fill type_2 attribute for all household with relocation type
	 * @param locs - universe 
	 * @param year - the year 
	 */		
	public void setRelocationTypeForHouseholds(List<Location> locs, Integer year);
	
	/**
	 * Calculate relocation type for household
	 * @param h - household 
	 * @param year - the year 
	 */		
	public int getHouseholdTypeForRelocation(Household h, Integer year);
}
