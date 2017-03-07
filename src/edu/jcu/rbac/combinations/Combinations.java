package edu.jcu.rbac.combinations;

import java.util.List;
import java.util.stream.Collectors;

import org.paukov.combinatorics3.Generator;

import edu.jcu.rbac.elements.Permission;

public class Combinations {
	
	private List<Permission> input;

	public Combinations(List<Permission> permissions){
		this.input = permissions;
	}
	
	public Combinations() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Vygenerování kombinací
	 * @param size
	 * @return 
	 */
	public List<List<Permission>> generateCombinations(int size){
		
		List<List<Permission>> result = Generator.combination(this.input)
        .simple(size)
        .stream()
        .collect(Collectors.<List<Permission>>toList());
		
		return result;
	}

	public void setInput(List<Permission> permissions) {
		this.input = permissions;
	}

}
