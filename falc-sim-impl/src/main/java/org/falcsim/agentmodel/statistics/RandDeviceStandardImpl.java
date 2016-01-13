package org.falcsim.agentmodel.statistics;

import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.agentmodel.statistics.domain.RealIdProbInterval;
import org.falcsim.agentmodel.util.math.random.RCRandomGenerator;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Provides statistical methods
 * 
 * @author regioConcept AG
 * @version 0.5
 * 
 */

public class RandDeviceStandardImpl implements RandDevice {

	@Autowired
	private RandomGenerator rgMT;	
	@Autowired
	private RCRandomGenerator rg;
	
	public static Logger logger = Logger.getLogger(RandDevice.class);

	/**
	 * Probabilistic assignment method
	 * 
	 * @param ripis - list of probability intervals
	 * @return an integer assigned on the basis of the probability distribution
	 *         of the input param
	 * @throws RCCustomException
	 */
	public int assignWeightedRandomId(List<RealIdProbInterval> ripis) throws RCCustomException {

		double max = 0.0;
		double min = 0.0;
		int id = -3;

		int s = ripis.size();
		if (s > 0) {
			max = ripis.get(s - 1).getUpper();
			min = ripis.get(0).getLower();
		} else {
			logger.warn("A. No Id assigned. The probability list has size 0.");
			throw new RCCustomException("B. No Id assigned. The intervals may all be zero.");
		}

		double die = min + (max - min) * rgMT.nextDouble();  //allways use Mersenne Twister

		for (RealIdProbInterval rpi : ripis) {
			if (rpi.getUpper() > die && rpi.getLower() <= die) {
				id = rpi.getId();
				return id;
			}

		}
		logger.warn("B. No Id assigned. The intervals may all be zero.");
		throw new RCCustomException("B. No Id assigned. The intervals may all be zero.");

		// return RCConstants.NO_ID;
	}
	
	public int assignWeightedRandomId(List<RealIdProbInterval> ripis, Person p, int year, int run) throws RCCustomException {
		return assignWeightedRandomId(rg, ripis, p, year, run);
	}
	public int assignWeightedRandomId(RCRandomGenerator rg, List<RealIdProbInterval> ripis, Person p, int year, int run) throws RCCustomException {

		double max = 0.0;
		double min = 0.0;
		int id = -3;

		int s = ripis.size();
		if (s > 0) {
			max = ripis.get(s - 1).getUpper();
			min = ripis.get(0).getLower();
		} else {
			logger.warn("A. No Id assigned. The probability list has size 0.");
			throw new RCCustomException("B. No Id assigned. The intervals may all be zero.");
		}

		double die = min + (max - min) * rg.nextDouble(p, year, run);

		for (RealIdProbInterval rpi : ripis) {
			if (rpi.getUpper() > die && rpi.getLower() <= die) {
				id = rpi.getId();
				return id;
			}

		}
		logger.warn("B. No Id assigned. The intervals may all be zero.");
		throw new RCCustomException("B. No Id assigned. The intervals may all be zero.");			
	}	
	
	
	/**
	 * Probabilistic assignment method
	 * 
	 * @param probTrue - probability of a boolean true output
	 * @return a boolean assigned on the basis of the probability of the input
	 *         param
	 */
	public boolean assignRandomWeightedBoolean(double probTrue) {
		return rg.nextDouble(null) > probTrue ? false : true;
	}		
	public boolean assignRandomWeightedBoolean(double probTrue, Person p, int year, int run){
		return assignRandomWeightedBoolean(rg, probTrue, p, year, run);
	}
	public boolean assignRandomWeightedBoolean(RCRandomGenerator rg, double probTrue, Person p, int year, int run){
		return rg.nextDouble(p, year, run) > probTrue ? false : true;
	}
	
}