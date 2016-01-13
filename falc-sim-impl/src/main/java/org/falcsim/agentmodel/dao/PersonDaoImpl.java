package org.falcsim.agentmodel.dao;

import java.util.List;

import org.falcsim.agentmodel.dao.jdbc.JDBCApproach;
import org.falcsim.agentmodel.domain.Person;
import org.springframework.stereotype.Repository;

@Repository("PersonDao")
public class PersonDaoImpl extends AbstractDao implements PersonDao {

	@Override
	@Deprecated
	public Person selectPersonById(Integer personId) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void deletePerson(Person person) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public List<Person> selectAllPersons() {
		return JDBCApproach.readAll(Person.class);
	}

	@Override
	@Deprecated
	public void deleteAllPersons() {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void saveOrUpdatePerson(Person person) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void saveOrUpdatePersonList(List<Person> personList) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public void deletePersonList(List<Person> personList) {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	@Deprecated
	public Person selectRandomPerson() {
		throw new RuntimeException("Unsupported method");
	}

	@Override
	public Long selectPersonsCountByLocation(Integer locId) {
		// TODO dynamic table
		throw new RuntimeException("Implement me!");
		/*List<Object[]> rslt = JDBCApproach.executeQuery("select count (*) from " + Person.class.getName() + " p, "
						+ Household.class.getName() + " h where "
						+ " p.householdId=h.id and h.locationId=" + locId,
				Household.class, Person.class);

		return (rslt == null || rslt.size() == 0 || rslt.get(0) == null || rslt
				.get(0)[0] == null) ? 0 : (Long) (rslt.get(0)[0]);*/
	}

}