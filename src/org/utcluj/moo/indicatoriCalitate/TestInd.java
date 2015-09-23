package org.utcluj.moo.indicatoriCalitate;

public class TestInd {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[][] A = { { 1.2, 2.3 }, { 1.4, 2.6 } };
		double[][] B = { { 0.3, 0.4 }, { 1, 2 } };
		
		double rez = new SetCoverage().calculSetCoverage(A, B);
		System.out.println(rez);
	}

}
