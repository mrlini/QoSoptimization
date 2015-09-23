package org.utcluj.moo.indicatoriCalitate;

public class Epsilon {

	int nrOb; // nr obiective
	int[] obj; // obj[i]=0-> minimizarea ob
	int metoda; // 0->additive epsilon, 1->multiplicative epsilon

	public Epsilon() {
	}

	/**
	 * Calc indicatorul epsilon
	 * 
	 * @param b
	 *            front teoretic
	 * @param a
	 *            frontul obtinut
	 * @param dim
	 * @return
	 */
	protected double epsilon(double[][] b, double[][] a, int dim) {
		int i, j, k;
		double eps, eps_j = 0.0, eps_k = 0.0, eps_temp;

		nrOb = dim;
		set_params();

		if (metoda == 0)
			eps = Double.MIN_VALUE;
		else
			eps = 0;

		for (i = 0; i < a.length; i++) {
			for (j = 0; j < b.length; j++) {
				for (k = 0; k < nrOb; k++) {
					switch (metoda) {
					case 0:
						if (obj[k] == 0)
							eps_temp = b[j][k] - a[i][k];
						else
							eps_temp = a[i][k] - b[j][k];
						break;
					default:
						if ((a[i][k] < 0 && b[j][k] > 0)
								|| (a[i][k] > 0 && b[j][k] < 0)
								|| (a[i][k] == 0 || b[j][k] == 0)) {
							System.err.println("error in data file");
							System.exit(0);
						}
						if (obj[k] == 0)
							eps_temp = b[j][k] / a[i][k];
						else
							eps_temp = a[i][k] / b[j][k];
						break;
					}
					if (k == 0)
						eps_k = eps_temp;
					else if (eps_k < eps_temp)
						eps_k = eps_temp;
				}
				if (j == 0)
					eps_j = eps_k;
				else if (eps_j > eps_k)
					eps_j = eps_k;
			}
			if (i == 0)
				eps = eps_j;
			else if (eps < eps_j)
				eps = eps_j;
		}
		return eps;
	}

	/**
	 * param standard
	 */
	protected void set_params() {
		int i;
		obj = new int[nrOb];
		for (i = 0; i < nrOb; i++) {
			obj[i] = 0;
		}
		metoda = 0;
	}
}
