package org.falcsim.agentmodel.hist.dao;

import java.util.Arrays;
import java.util.List;

import org.falcsim.agentmodel.dao.AbstractDao;
import org.falcsim.agentmodel.dao.jdbc.JDBCApproach;
import org.falcsim.agentmodel.domain.Step;
import org.springframework.stereotype.Repository;

@Repository
public class StepDaoImpl extends AbstractDao implements StepDao {

	public Integer lastStep() {
		throw new RuntimeException("Implement me");
		/*List<Object[]> rslt = JDBCApproach.executeQuery(getSession(),
				" select max(step) from " + Step.class.getName(), Step.class);
		Integer max = (Integer) rslt.get(0)[0];
		max = (max == null ? 0 : max);
		return max;*/
	}

	@Deprecated
	public void updateSteps() {
		throw new RuntimeException("Unsupported method");
	}

	public void updateSteps(int run, int year){
		final Step step = new Step(run, year);
		List<Step> steps = Arrays.asList(step);
		JDBCApproach.saveList(Step.class, steps);	
	}
	
}
