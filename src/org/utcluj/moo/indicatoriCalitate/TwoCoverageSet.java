package org.utcluj.moo.indicatoriCalitate;

/**
 * Indicator de calitate care masoara diferenta intre doua seturi.
 * <p>
 * C(S1,S2) = nr de puncte din S1 care domina pe S2 - nr total de pct din S2
 * 
 * @author Mihai
 * 
 */
public class TwoCoverageSet {

	public TwoCoverageSet() {
	}

	/**
	 * Met calc coverage set
	 * <p>
	 * C(S1,S2)=|{s1<=s2}|/|S2|
	 * 
	 * @param S1
	 * @param S2
	 * @param nrOb
	 * @return
	 */
	public double coverageSet(double[][] S1, double[][] S2, int nrOb) {
		int contor = 0;
		for (int i = 0; i < S2.length; i++)
			for (int j = 0; j < S1.length; j++) {
				if (dominaSlab(S1[j], S2[i], nrOb)) {
					contor++;
//					System.out.println("DA");
//					System.out.println(S1[j][0] + " " + S1[j][1]);
//					System.out.println(S2[i][0] + " " + S2[i][1]);
				}
			}
		System.out.println("contorul "+contor);
		return (double) contor - S2.length;
	}

	/**
	 * Metoda returneaza true daca a domina slab pe b in functie de cele nrOb
	 * obiective
	 * 
	 * @param a
	 * @param b
	 * @param nrOb
	 * @return
	 */
	private static boolean dominaSlab(double[] a, double[] b, int nrOb) {
		boolean maiRauLaUnOb = false;
		int i;

		for (i = 0; i < nrOb && !maiRauLaUnOb; i++)
			if (a[i] > b[i])
				maiRauLaUnOb = true;
		return (!maiRauLaUnOb);
	}

//	public static void main(String[] args) {
//		double[] a = { 4.5, 4.2, 6.3 };
//		double[] b = { 4.5, 5.2, 7.3 };
//		if(dominaSlab(a, b, 3))
//			System.out.println("a domina slab pe b");
//		else
//			System.out.println("eroare");
//	}
}
