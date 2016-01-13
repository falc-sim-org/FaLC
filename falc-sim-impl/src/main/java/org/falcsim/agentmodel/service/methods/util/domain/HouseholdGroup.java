package org.falcsim.agentmodel.service.methods.util.domain;

/**
 * Holds person counts household member type
 * 
 * @author regioConcept AG
 * @version 1.0
 * @since   0.5 
 * 
 */
public class HouseholdGroup {
	public int nr_of_parents;
	public int nr_of_children;
	public int nr_of_adult_children;
	
	public HouseholdGroup(int nr_of_parents, int nr_of_children, int nr_of_adult_children){
		this.nr_of_parents = nr_of_parents;
		this.nr_of_children = nr_of_children;
		this.nr_of_adult_children = nr_of_adult_children;
	}
	
	@Override
	public int hashCode() {
		return nr_of_parents * 10000 + nr_of_adult_children * 100 + nr_of_children;
	}
}
