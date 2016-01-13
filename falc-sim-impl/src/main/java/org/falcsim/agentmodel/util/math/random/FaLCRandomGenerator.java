package org.falcsim.agentmodel.util.math.random;

import javax.annotation.PostConstruct;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.falcsim.agentmodel.app.domain.RunParameters;
import org.falcsim.agentmodel.demography.methods.DemographyMethods;
import org.falcsim.agentmodel.domain.Person;
import org.falcsim.agentmodel.exceptions.RCCustomException;
import org.falcsim.exit.ExitCodes;
import org.springframework.beans.factory.annotation.Autowired;

/** Encapsulates usage of random generators
*	fully random (MersenneTwister, XS) or random by ID(XS)  
*
* @author regioConcept AG
* @version 1.0
* 
*/
public class FaLCRandomGenerator implements RCRandomGenerator {
	
	private static Logger logger = Logger.getLogger(FaLCRandomGenerator.class);
	
	@Autowired
	private RandomGenerator rg;	
	@Autowired
	private RunParameters runp;
	@Autowired
	private DemographyMethods demogMs;
	

	private XSRandom XSrndgen;
	private MersenneTwister MTrndgen;
	private RandomGeneratorType currentGenerator = RandomGeneratorType.XSRandom; 
	
	public FaLCRandomGenerator(){
//		XSrndgen = new XSRandom();
//		MTrndgen = new MersenneTwister();
	}

	@PostConstruct
	protected void initialize() {
		if(runp.getUniverse_use_random_byID()){
			XSrndgen = new XSRandom();
			MTrndgen = new MersenneTwister();
		}
		else{
			currentGenerator = RandomGeneratorType.ApacheMath3; 
		}
	}	
	
	public FaLCRandomGenerator(RandomGenerator rg, RandomGeneratorType currentGenerator, RunParameters runp, DemographyMethods demogMs){
		this.currentGenerator = currentGenerator;
		if(currentGenerator == RandomGeneratorType.XSRandom) XSrndgen = new XSRandom();
		if(currentGenerator == RandomGeneratorType.MersenneTwister) MTrndgen = new MersenneTwister();
		this.rg = rg;
		if(rg == null){
			if(currentGenerator == RandomGeneratorType.ApacheMath3){
				this.rg = new MersenneTwister();
			}
		}
		this.runp = runp;
		if(runp == null){
			throw new IllegalArgumentException("Parameter is Null: RunParameters");
		}
		this.demogMs = demogMs;
		if(demogMs == null){
			throw new IllegalArgumentException("Parameter is Null: demogMs");
		}
	}	
	
	@Override
	public RCRandomGenerator clone(){
		if(runp.getUniverse_use_random_byID()){
			return new FaLCRandomGenerator(null, currentGenerator, runp, demogMs);
		}
		return this;
	}
	
	@Override
	public RandomGeneratorType getCurrentGenerator() {
		return currentGenerator;
	}
	
	@Override
	public void setCurrentGenerator(RandomGeneratorType currentGenerator) {
		if(runp.getUniverse_use_random_byID()){
			this.currentGenerator = currentGenerator;
		}
	}
	
	@Override
	public void setSeed(int seed){
		if(currentGenerator == RandomGeneratorType.XSRandom){
			XSrndgen.setSeed(seed);
		}
		else if(currentGenerator == RandomGeneratorType.MersenneTwister){
			MTrndgen.setSeed(seed);
		}		
		else if(currentGenerator == RandomGeneratorType.ApacheMath3){
			rg.setSeed(seed);
		}
	}
	
	@Override
	public double nextDouble(RandomGeneratorModelID model){
		double nextval = 0;		
		try{
			if(model == null || model == RandomGeneratorModelID.None){
				if(currentGenerator == RandomGeneratorType.XSRandom){
					nextval = XSrndgen.nextDouble();
				}
				else if(currentGenerator == RandomGeneratorType.MersenneTwister){
					nextval = MTrndgen.nextDouble();
				}				
				else if(currentGenerator == RandomGeneratorType.ApacheMath3){
					nextval = rg.nextDouble();
				}
			}
			else throw new RCCustomException("Model ID not supported for this case..."); 
		}
		catch(RCCustomException e){
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.RANDOM_GENERAL_ERROR.getValue());
		}
		return nextval;
	}
	
	@Override
	public double nextDouble(Person p, int year, int run){
		setSeed(p, year, run);
		double nextval = 0;
		
		if(currentGenerator == RandomGeneratorType.XSRandom){
			nextval = XSrndgen.nextDouble();
		}
		else if(currentGenerator == RandomGeneratorType.MersenneTwister){
			nextval = MTrndgen.nextDouble();
		}			
		else if(currentGenerator == RandomGeneratorType.ApacheMath3){
			nextval = rg.nextDouble();
		}
		return nextval;
	}
		
	@Override
	public int nextInt(int n, RandomGeneratorModelID model){		
		int nextval = 0;		
		try{
			if(model == null || model == RandomGeneratorModelID.None){
				if(currentGenerator == RandomGeneratorType.XSRandom){
					nextval = XSrndgen.nextInt(n);
				}
				else if(currentGenerator == RandomGeneratorType.MersenneTwister){
					nextval = MTrndgen.nextInt(n);
				}					
				else if(currentGenerator == RandomGeneratorType.ApacheMath3){
					nextval = rg.nextInt(n);
				}
			}
			else throw new RCCustomException("Model ID not supported for this case..."); 
		}
		catch(RCCustomException e){
			logger.error(e.getMessage(), e);
			System.exit(ExitCodes.RANDOM_GENERAL_ERROR.getValue());
		}
		return nextval;
	}
		
	@Override
	public int nextInt(int n, Person p, int year, int run){
		setSeed(p, year, run);
		int nextval = 0;
		
		if(currentGenerator == RandomGeneratorType.XSRandom){
			nextval = XSrndgen.nextInt(n);
		}
		else if(currentGenerator == RandomGeneratorType.MersenneTwister){
			nextval = MTrndgen.nextInt(n);
		}			
		else if(currentGenerator == RandomGeneratorType.ApacheMath3){
			nextval = rg.nextInt(n);
		}
		return nextval;
	}
	
	private Long setSeed(Person p, int year, int run){
		if(runp.getUniverse_use_random_byID()){
			int age = demogMs.getAgeOfPerson(p, year);
			int systemseed = runp.getUniverse_reference_runs_rg_seed() == null ? 0 : runp.getUniverse_reference_runs_rg_seed();
			int decNUmber = p.getDecissionNumber() != null ? p.getDecissionNumber() :
							p.getMotherId() != null ? p.getMotherId() : p.getId();
			//TODO: pf check plus/multiple difference with decNUmber
			long seed = (systemseed == 0 ? 123456789 : systemseed) * run * decNUmber;	//warning system seed can be NULL or zero...just fix
			seed = seed * age;			
			seedRndGen(seed);
			return seed;
		}
		return null;
	}
	
	private void seedRndGen(long seed){
		if(runp.getUniverse_use_random_byID()){		
			if(currentGenerator == RandomGeneratorType.XSRandom){
				XSrndgen.setSeed(seed);	
			}
			else if(currentGenerator == RandomGeneratorType.MersenneTwister){
				MTrndgen.setSeed(seed);	
			}			
			else if(currentGenerator == RandomGeneratorType.ApacheMath3){
				rg.setSeed(seed);
			}			
		}
	}
	
}



