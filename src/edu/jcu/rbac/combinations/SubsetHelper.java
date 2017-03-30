package edu.jcu.rbac.combinations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.jcu.rbac.elements.Permission;


//@author http://stackoverflow.com/a/29914908
public class SubsetHelper {

	private List<List<Permission>> subsets;
	private List<Permission> input;
	private int k;
	
	public List<List<Permission>> getSubsets() {
		return subsets;
	}
	public SubsetHelper(){
		this.subsets = new ArrayList<List<Permission>>();
	}
	
	
	/**
	 * Nalezení všech možných kombinací
	 */
	public void processSubsets(){

		subsets.clear();
		int[] s = new int[k];                  // here we'll keep indices 
		                                       // pointing to elements in input array
		// pokud je input menší než K tak nic nedělej
		if (k <= input.size()) {
			
			// první sekvence
		    // first index sequence: 0, 1, 2, ...
		    for (int i = 0; (s[i] = i) < k - 1; i++);
		    
		    // prvni set
		    subsets.add(getSubset(input, s));

		    for(;;) {
		    
		    	int i;
		        // find position of item that can be incremented
		        for (i = k - 1; i >= 0 && s[i] == input.size() - k + i; i--); 
		        
		        
		        if (i < 0) {
		            break;
		        } else {
		            s[i]++;                    // increment this item
		            for (++i; i < k; i++) {    // fill up remaining items
		                s[i] = s[i - 1] + 1; 
		            }
		            subsets.add(getSubset(input, s));
		        }
		    }
		}
	}

	/**
	 * Vygenerování aktuálního páru
	 * @param input
	 * @param subset
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Permission> getSubset(List<Permission> input, int[] subset) {
	    
		Set<Permission> result = new HashSet<Permission>();	
	    for (int i = 0; i < subset.length; i++) 
	        result.add(input.get(subset[i]));
	    
	    return new ArrayList<Permission>(result);
	}
	public List<Permission> getInput() {
		return input;
	}
	public void setInput(List<Permission> input) {
		this.input = input;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}

}
