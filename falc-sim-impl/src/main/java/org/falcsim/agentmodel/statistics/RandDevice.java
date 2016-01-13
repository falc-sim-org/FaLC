package org.falcsim.agentmodel.statistics;

import java.util.List;

import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;


/**
 * Statistical methods
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */

public interface RandDevice {

	public static Logger logger = Logger.getLogger(RandDevice.class);

	/**
	 * Probabilistic assignment method
	 * 
	 * @param  ripis - list of probability intervals
	 * @return an integer assigned on the basis of the probability distribution
	 *         of the input param
	 * @throws RCCustomException
	 */
	public int assignWeightedRandomId(List<RealIdProbInterval> ripis) throws RCCustomException;
	public int assignWeightedRandomId(List<RealIdProbInterval> ripis, Person p, int year, int run) throws RCCustomException;
	public int assignWeightedRandomId(RCRandomGenerator rg, List<RealIdProbInterval> ripis, Person p, int year, int run) throws RCCustomException;
	
	/**
	 * Probabilistic assignment method
	 * 
	 * @param probTrue - the probability of a boolean true outpt
	 * @return a boolean assigned on the basis of the probability of the input
	 *         param
	 */
	public boolean assignRandomWeightedBoolean(double probTrue);	
	public boolean assignRandomWeightedBoolean(double probTrue, Person p, int year, int run);
	public boolean assignRandomWeightedBoolean(RCRandomGenerator rg, double probTrue, Person p, int year, int run);

}