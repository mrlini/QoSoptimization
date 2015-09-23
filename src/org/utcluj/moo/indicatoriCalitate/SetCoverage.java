package org.utcluj.moo.indicatoriCalitate;

/**
 * Indicator de calitate - fie A, B doua aproximari ale FP, C(A,B) = procentul
 * de sol din B care sunt dominate de cel putin o solutie din A.
 * <p>
 * C(A,B)!=1-C(B,A).
 * <p>
 * C(A,B)=1 - toate sol din B sunt dominate de ceva sol din A. C(A,B)=0 - nici o
 * sol din B nu e dominata de o sol din A.
 * 
 * @author mihai
 * 
 */
public class SetCoverage {

	private static SetCoverage instance = null;

	public SetCoverage() {
	}

	public static SetCoverage getInstance() {
		if (instance == null) {
			instance = new SetCoverage();
		}
		return instance;
	}

	/**
	 * 
	 * @param A
	 * @param B
	 * @return procentul de sol din B care sunt dominate de cel putin o solutie
	 *         din A.
	 */
	public double calculSetCoverage(double[][] A, double[][] B) {
		int nrElemDominateDinB = 0;
		boolean dominat = false;

		for (int i = 0; i < B.length; i++) {
			dominat = false;
			for (int j = 0; j < A.length; j++) {
				if (dominaSlabPareto(A[j], B[i], false)) {
					dominat = true;
					break;
				}
			}
			if (dominat)
				nrElemDominateDinB++;
		}

		return (double) (nrElemDominateDinB / (double)B.length);
	}

	/**
	 * Returnez true daca a domina b. Regula: daca a e mai bun in unul sau mai
	 * multe criterii si in rest sunt egale returnez true altfel returnez false.
	 * 
	 * <p>
	 * ATENTIE metoda nu e comutativa: daca a nu domina pe b asta nu inseamna ca
	 * b domina pe a
	 * 
	 * @param a
	 * @param b
	 * @param maximizare
	 * @return
	 */
	private static boolean dominaSlabPareto(double[] a, double[] b,
			boolean maximizare) {
		boolean a_bate_b = false;

		if (a.length != b.length) {
			System.out.println(a.length);
			System.out.println(b.length);
			throw new RuntimeException(
					"Se vrea compararea a 2 vectori; vectorii au un nr diferit de obiective.");

		}
		if (maximizare) {
			for (int x = 0; x < a.length; x++) {
				if (a[x] > b[x])
					a_bate_b = true;
				else if (a[x] < b[x])
					return false;
			}
		} else {
			for (int x = 0; x < a.length; x++) {
				if (a[x] < b[x])
					a_bate_b = true;
				else if (a[x] > b[x])
					return false;
			}
		}
		return a_bate_b;
	}
}
