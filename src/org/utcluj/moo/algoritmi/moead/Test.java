package org.utcluj.moo.algoritmi.moead;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] v = {3.2, 2.1, 1.0, 8.9};
		int [] index = sortare(v);
		
		for (int i : index) {
			System.out.print(i+ " ");
		}

	}

	private static int[] sortare(double[] vector) {
		int[] index = new int[vector.length];
		
		for (int i = 0; i < vector.length; i++) {
			index[i] = i;
		}
		
		for (int i = 0; i < vector.length; i++) {
			for (int j = 0; j < i; j++) {
				if(vector[index[i]] < vector[index[j]]) {
					int temp = index[i];
					for (int k = i-1; k >= j; k--) {
						index[k+1] = index[k];
					}
					index[j] = temp;
					break;
				}
			}
		}

		return index;
	}
}
