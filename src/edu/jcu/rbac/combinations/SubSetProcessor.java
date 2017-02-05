package edu.jcu.rbac.combinations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.jcu.rbac.elements.Permission;
import edu.jcu.rbac.elements.Role;

public class SubSetProcessor {
	
	private  int[] intArray;
	private  int[] subSetArray;
	// unikátní role
	private HashSet<Role> result = new HashSet<Role>();
	private List<List<Permission>> subsets = new ArrayList<List<Permission>>();
	private List<Permission> input;
	
	private Integer currentIndex = 0;
	private Integer combinationSize = 5;
	private Integer numberOfCombinations = 0;
	
	
	public static void main (String[] args) {
		
		long startTime = System.currentTimeMillis();
		
		new SubSetProcessor();
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime);
		System.out.println("done");
	}
	
	public SubSetProcessor(){
		intArray = new int[58]; 
		for(int i = 1; i < 59; i++){
			intArray[i-1] = i;
		}
		 
		subSetArray = new int[combinationSize];
		for(int i = 0; i < intArray.length; i++){
			generateSubSets(i, i+1);
		}
		System.out.print("number:" + numberOfCombinations);
		
	}
	/*
	 * Vygenerování subsetu ve velikosti okna kombinace - 1
	 */
	protected void generateSubSets(Integer fixedIndex,Integer indexToStart){
		
		// pokud je okno větší, než maximální počet elementů
		Integer maxIndex = indexToStart + (combinationSize - 1);
		if(maxIndex > intArray.length){
			return;
		}
		
		subSetArray[0] = intArray[fixedIndex];
		int z = 1;
		for(int i = indexToStart; i < maxIndex; i++){
			subSetArray[z] = intArray[i];
			z++;
		}
		numberOfCombinations++;
		
		//System.out.println(Arrays.toString(subSetArray));
		
		// pokud není, zavoláme okna znovu
		generateSubSets(fixedIndex, (indexToStart + 1));
		
	}
	
}

