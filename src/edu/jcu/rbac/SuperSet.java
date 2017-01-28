package edu.jcu.rbac;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuperSet {
	
	public static void main (String[] args) {
		
		long startTime = System.currentTimeMillis();
		
		List<Integer> superSet = new ArrayList<>();
		for(int i = 1; i < 59; i++){
			superSet.add(i);
		}
		System.out.println(getSubsets(superSet,5));
		
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime);
		System.out.println("done");
	}
	
	private static void getSubsets(List<Integer> superSet, int k, int idx, Set<Integer> current,List<Set<Integer>> solution) {
	    //successful stop clause
	    if (current.size() == k) {
	        solution.add(new HashSet<>(current));
	        return;
	    }
	    //unseccessful stop clause
	    if (idx == superSet.size()) return;
	    Integer x = superSet.get(idx);
	    current.add(x);
	    //"guess" x is in the subset
	    getSubsets(superSet, k, idx+1, current, solution);
	    current.remove(x);
	    //"guess" x is not in the subset
	    getSubsets(superSet, k, idx+1, current, solution);
	}

	public static List<Set<Integer>> getSubsets(List<Integer> superSet, int k) {
	    List<Set<Integer>> res = new ArrayList<>();
	    getSubsets(superSet, k, 0, new HashSet<Integer>(), res);
	    return res;
	}
}
