package org.falcsim.agentmodel.dao;

import java.util.List;

import org.falcsim.agentmodel.domain.Person;


/**
 * Dao interface for Person
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */

public interface PersonDao {

	public Person selectPersonById(Integer personId);

	public void saveOrUpdatePerson(Person person);

	public void saveOrUpdatePersonList(List<Person> personList);

	public void deletePerson(Person person);

	public List<Person> selectAllPersons();

	public void deleteAllPersons();

	public void deletePersonList(List<Person> personList);

	public Person selectRandomPerson();

	public Long selectPersonsCountByLocation(Integer locId);

}
