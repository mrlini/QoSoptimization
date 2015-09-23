package org.utcluj.util;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.Random;

public class PermutationGenerator {

	private int size;
	private Random rand = new Random();

	public PermutationGenerator(int size) {
		this.size = size;
	}

	public ArrayList nextPermutation() {
		// Make a list of the numbers we are going to permute.
		ArrayList<Integer> unused = new ArrayList<Integer>();
		for (int num = 0; num < size; num++) {
			// Add the next number to the unused set.
			unused.add(num);
		}
		// Now generate a permutation.
		ArrayList<Integer> perm = new ArrayList<Integer>();
		for (int num = 0; num < size; num++) {
			int pos = rand.nextInt(unused.size());
			perm.add(unused.get(pos));
			unused.remove(pos);
		}
		// Return the result.
		return perm;
	}
}