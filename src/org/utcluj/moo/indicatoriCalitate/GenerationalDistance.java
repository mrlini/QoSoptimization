package org.utcluj.moo.indicatoriCalitate;

import org.utcluj.moo.indicatoriCalitate.utils.Utils;

public class GenerationalDistance {

	private static final double pow = 2.0;

	public GenerationalDistance() {
	}

	/**
	 * Metoda calculeaza indicatorul GD pentru frontul dat. Fronturile trebuie
	 * normate la val max si min
	 * 
	 * @param front
	 *            frontul pentru care se calc GD
	 * @param frontT
	 *            frontul teoretic
	 * @param nrOb
	 *            numarul de obiective
	 */
	public double generationalDistance(double[][] front, double[][] frontT,
			int nrOb) {

		// fac suma distantelor intre fiecare pct din front si cel mai aproape
		// pct din frontul teoretic
		double sum = 0.0;
		for (int i = 0; i < front.length; i++)
			sum += Math.pow(Utils.distMinPctFront(front[i], frontT), pow);

		// radical din suma
		sum = Math.pow(sum, 1.0 / pow);

		// impart la nr max de pct din front
		double generationalDistance = sum / front.length;

		return generationalDistance;
	} 
}
