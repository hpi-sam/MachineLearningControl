package mRUBiS.Observations;

import java.util.HashMap;
import java.util.Random;

/**
 * Keeps the hidden utility states of each component and shop.
 * The states are hidden because they are produce by a time series generator (auto-regressive model)
 * that is not visible to controller.
 *  
 * @author Christian Adriano
 *
 */
public class UtilityHiddenState {

	/** key,value pairs in which the key can be a string that concatenates shop and component_type*/
	public HashMap<String,Double> CurrentUtilityStateMap = new HashMap<String,Double>();

	/** holds the utility values that each component should converge to */
	public HashMap<String,Double> ReferenceUtilityStateMap = new HashMap<String,Double>(); 

	/** convergence rate towards the utility of reference */
	private Double theta = 0.1;

	/** rate to compute the variance around the current utility */
	private Double sigma = 1.0;

	/** object to generate Gaussian numbers, initial seed=9876 */
	private Random randomGenerator = new Random(9876);
	
	 /** determines how far from the "referenceUtility" should we start. 
	  * The default is 1.5, which implies a 150% of the reference utility value*/
	private Double delta = 1.5; 

	/** 
	 * In case one ones to change the standard calibration of the auto-regressive model
	 * @param theta convergence rate towards the utility of reference
	 * @param sigma rate to compute the variance around the current utility
	 * @param randomSeed initial seed to add noise to the auto-regressive model that updates the utility of each component
	 * @param delta determines how far from the "referenceUtility" should we start
	 */
	public UtilityHiddenState(Double theta, Double sigma, long randomSeed, Double delta) {
		this.theta =  theta;
		this.sigma = sigma;
		this.randomGenerator =  new Random(randomSeed); 
		this.delta = delta;
		//TODO for all components, initialize the ReferenceUtilityStateMap
		//TODO for all components, initialize the CurrentUtilityStateMap, for each component -> currentUtility = referenceUtility*delta
	}

	/**
	 * Implements the auto-regressive model combined with an Ornstein�Uhlenbeck procedure.
	 * @param shop: the key name of a shop
	 * @param componetType: the key name of a component type
	 * @return: currentUtility which is a utility shifted closer to the referenceUtility
	 */
	public Double updateCurrentUtility(String shop, String componentType) {

		String key = shop+":"+componentType;
		Double previousUtility = (Double) this.CurrentUtilityStateMap.get(key);
		Double referenceUtility = (Double) this.ReferenceUtilityStateMap.get(key);

		if(previousUtility==null) {
			//TODO obtain reference_utility for componetType at shop
			return(previousUtility);
		}
		else {//Compute the new utility based on previous one

			double variance = this.sigma.doubleValue() * this.randomGenerator.nextGaussian(); //nextGaussian samples from a normal distribution with mean=0,std=1
			double convergenceShift = this.theta.doubleValue() * (referenceUtility.doubleValue() - previousUtility.doubleValue());
			double currentUtility = previousUtility.doubleValue() +  convergenceShift + variance;
			
			//Stores new state
			this.CurrentUtilityStateMap.put(key,currentUtility);
			
			return currentUtility;
		}
	}


}