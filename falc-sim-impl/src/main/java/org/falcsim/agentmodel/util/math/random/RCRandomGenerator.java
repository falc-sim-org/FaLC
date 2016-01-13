package org.falcsim.agentmodel.util.math.random;

import org.falcsim.agentmodel.domain.Person;

/** Custom random generator interface
*
* @author regioConcept AG
* @version 1.0
* 
*/
public interface RCRandomGenerator {
	
	public RandomGeneratorType getCurrentGenerator();
	public void setCurrentGenerator(RandomGeneratorType type);
	
	public void setSeed(int seed);
	public double nextDouble(RandomGeneratorModelID model);
	public int nextInt(int n, RandomGeneratorModelID model);
	public double nextDouble(Person p, int year, int run);
	public int nextInt(int n, Person p, int year, int run);
	
	public RCRandomGenerator clone();
}
