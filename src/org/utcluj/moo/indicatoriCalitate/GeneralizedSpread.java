package org.utcluj.moo.indicatoriCalitate;

import java.util.Arrays;

import org.utcluj.moo.indicatoriCalitate.utils.LexicoGraphicalComparator;
import org.utcluj.moo.indicatoriCalitate.utils.Utils;
import org.utcluj.moo.indicatoriCalitate.utils.ValueComparator;

/**
 * Clasa implementeaza indicatorul Generalized spread ce poate fi aplicat pentru
 * 2 sau mai multe dimensiuni
 * 
 * @author Mihai
 * 
 */
public class GeneralizedSpread {

	public GeneralizedSpread() {
	}

	/**
	 * Met calculeaza indicatorul Generalized Spread. fiind dat frontul obtinut,
	 * frontul teoretic (ca si <code>double []</code>) si numarul de obiective
	 * 
	 * @param front
	 *            frontul obtinut
	 * @param frontT
	 *            frontul teoretic
	 * @param nrOb
	 *            numarul de obiective
	 * @return val GS
	 **/
	@SuppressWarnings("unchecked")
	public double generalizedSpread(double[][] front, double[][] frontT,
			int nrOb) {

		// gasesc pct extreme
		double[][] extremVal = new double[nrOb][nrOb];
		for (int i = 0; i < nrOb; i++) {
			Arrays.sort(front, new ValueComparator(i));
			for (int j = 0; j < nrOb; j++) {
				extremVal[i][j] = front[front.length - 1][j];
			}
		}

		int nrPct = front.length;

		// sortez frontul
		Arrays.sort(front, new LexicoGraphicalComparator());

		// calculez indicatorul, aesta este 1 ca si default
		if (Utils.distanta(front[0], front[front.length - 1]) == 0.0) {
			return 1.0;
		} else {

			double dmean = 0.0;

			//calc dist medie intre fiecare pct si cel mai apropiat vecin
			for (int i = 0; i < front.length; i++) {
				dmean += Utils.distCatreCelMaiApropiatPct(front[i], front);
			}

			dmean = dmean / (nrPct);

			// calc dist catre pct extreme
			double dExtrems = 0.0;
			for (int i = 0; i < extremVal.length; i++) {
				dExtrems += Utils.distMinPctFront(extremVal[i], front);
			}

			// calc val metricii
			double mean = 0.0;
			for (int i = 0; i < front.length; i++) {
				mean += Math.abs(Utils.distCatreCelMaiApropiatPct(front[i],
						front) - dmean);
			}

			double value = (dExtrems + mean) / (dExtrems + (nrPct * dmean));
			return value;

		}
	} 

}
