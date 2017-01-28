package edu.jcu.rbac;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SubSetExampleOriginal {
	
	public static int binomial(final int N, final int K) {
	    BigInteger ret = BigInteger.ONE;
	    for (int k = 0; k < K; k++) {
	        ret = ret.multiply(BigInteger.valueOf(N-k))
	                 .divide(BigInteger.valueOf(k+1));
	    }
	    return ret.intValue();
	}
	
	public static void main (String[] args) {
		
		long startTime = System.currentTimeMillis();
			
		/*SubsetExample subsetHelper = new SubsetExample();
		List<Permission> permissions = new ArrayList<Permission>();
 
		for(Integer i = 1; i < 59; i++){
			permissions.add(new Permission(i.toString()));
		}
		
		subsetHelper.setInput(permissions);
		subsetHelper.setK(7);
		subsetHelper.processSubsets();
		
		System.out.println("Velikost x: " + subsetHelper.getSubsets().size());
		*/
		new SubSetExampleOriginal();
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime);
		System.out.println("done");
	}
	
	public SubSetExampleOriginal(){
		
		int[] input = new int[58]; 
		for(int i = 1; i < 59; i++){
			input[i-1] = i;
		}
		int k = 7;                             // sequence length   
	
		List<int[]> subsets = new ArrayList<>();
	
		int[] s = new int[k];                  // here we'll keep indices 
		                                       // pointing to elements in input array
	
		if (k <= input.length) {
		    // first index sequence: 0, 1, 2, ...
		    for (int i = 0; (s[i] = i) < k - 1; i++);  
		    subsets.add(getSubset(input, s));
		    for(;;) {
		        int i;
		        // find position of item that can be incremented
		        for (i = k - 1; i >= 0 && s[i] == input.length - k + i; i--); 
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
		
		System.out.println("Combincations: " + subsets.size());
		
	}
	// generate actual subset by index sequence
	int[] getSubset(int[] input, int[] subset) {
	    int[] result = new int[subset.length]; 
	    for (int i = 0; i < subset.length; i++) 
	        result[i] = input[subset[i]];
	    return result;
	}
}
