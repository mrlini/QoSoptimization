package org.utcluj.moo.indicatoriCalitate.utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

/**
 * Metode utilizate la calcularea indicatorilor de calitate
 * 
 * @author mihai
 * 
 */
public class Utils {

	public Utils() {

	}

	/**
	 * Metoda prin care normez frontul. preiau val min/max din front si normez
	 * fiecare ind din front
	 * 
	 * @param front
	 * @param nrOb
	 * @return
	 */
	public double[][] normareFront(double[][] front, int nrOb) {
		double[][] frontNormat = new double[front.length][nrOb];
		double[] min = new double[nrOb];
		double[] max = new double[nrOb];
		int i, j = 0;

		// initializez min/max
		for (i = 0; i < nrOb; i++) {
			min[i] = 1;
			max[i] = 0;
		}

		try {
			// det val min si max pt fiecare obiectiv
			for (i = 0; i < front.length; i++)
				for (j = 0; j < nrOb; j++) {
					if (min[j] > front[i][j])
						min[j] = front[i][j];
					if (max[j] < front[i][j])
						max[j] = front[i][j];
				}

			for (i = 0; i < front.length; i++)
				for (j = 0; j < nrOb; j++)
					frontNormat[i][j] = (front[i][j] - min[j])
							/ (max[j] - min[j]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println(j + " nr obiective: " + nrOb);
		}
		return frontNormat;
	}

	/**
	 * Metoda prin care inversez un front in cazul unei probleme de minimizare.
	 * Inversez frontul ca sa iau ca si pct de referinta 0,0
	 * 
	 * @param frontNormat
	 * @return
	 */
	public double[][] inversareFront(double[][] frontNormat) {
		double[][] frontInversat = new double[frontNormat.length][];

		for (int i = 0; i < frontNormat.length; i++) {
			frontInversat[i] = new double[frontNormat[i].length];
			for (int j = 0; j < frontNormat[i].length; j++) {
				if (frontNormat[i][j] <= 1.0 && frontNormat[i][j] >= 0.0) {
					frontInversat[i][j] = 1.0 - frontNormat[i][j];
				} else if (frontNormat[i][j] > 1.0) {
					frontInversat[i][j] = 0.0;
				} else if (frontNormat[i][j] < 0.0) {
					frontInversat[i][j] = 1.0;
				}
			}
		}
		return frontInversat;
	}

	/**
	 * 
	 * @param adresa
	 * @param metoda
	 * @param nrOb
	 * @param dimFrontTeoretic
	 * @return
	 */
	public double[][] citireFrontTeoretic(String adresa, byte metoda, int nrOb,
			int dimFrontTeoretic) {
		double[][] frontT = new double[dimFrontTeoretic][nrOb];
		int i = 0;
		int j = 0;
		StreamTokenizer parser;

		try {
			parser = new StreamTokenizer(new FileReader(adresa));
			parser.eolIsSignificant(true);
			while (parser.nextToken() != StreamTokenizer.TT_EOF) {
				if (parser.ttype == StreamTokenizer.TT_NUMBER) {
					frontT[i][j] = parser.nval;
					j++;
				} else if (parser.ttype == StreamTokenizer.TT_EOL) {
					i++;
					j = 0;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return frontT;
	}

	/**
	 * Metoda calculeaza distanta Euclidiana intre 2 puncte date ca si un vector
	 * de double
	 * 
	 * @param a
	 *            punctul a
	 * @param b
	 *            punctul b
	 * @return distanta euclidiana intre puncte
	 **/
	public static double distanta(double[] a, double[] b) {
		double dist = 0.0;

		for (int i = 0; i < a.length; i++) {
			dist += Math.pow(a[i] - b[i], 2.0);
		}
		return Math.sqrt(dist);
	}

	/**
	 * Metoda preia distanta intre un pct si cel mai apropiat pct dintr-un front
	 * dat. Frontul este sub forma <code>double [][]</code>
	 * 
	 * @param point
	 *            punctul
	 * @param front
	 *            frontul ce contine celelalte pct
	 * @return dist minima intre front si punctul dat
	 **/
	public static double distMinPctFront(double[] point, double[][] front) {
		double mindist = distanta(point, front[0]);

		for (int i = 1; i < front.length; i++) {
			double aux = distanta(point, front[i]);
			if (aux < mindist) {
				mindist = aux;
			}
		}
		return mindist;
	}

	/**
	 * Iau distanta intre un pct si cel mai apropiat pct dintr-un front dat,
	 * aceasta dist > 0
	 * 
	 * @param point
	 *            punctul de referinta
	 * @param front
	 *            frontul ce contine pct
	 * @return dist minima (mai mare ca 0) intre front si punctul dat the front
	 */
	public static double distCatreCelMaiApropiatPct(double[] point, double[][] front) {
		double minDistance = Double.MAX_VALUE;

		for (int i = 0; i < front.length; i++) {
			double aux = distanta(point, front[i]);
			if ((aux < minDistance) && (aux > 0.0)) {
				minDistance = aux;
			}
		}
		return minDistance;
	}
}
