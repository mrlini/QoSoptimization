package org.utcluj.moo.indicatoriCalitate;

/**
 * Calculul indicatorului hypervolum conform zitzler2007a
 * 
 * @author Mihai
 */
@SuppressWarnings("unused")
public class Hyperv {

	static int dim;
	static int[] obj;
	private int method = 0; // 0=fara set de referinta, 1-cu set de
							// referinta
	private static double[] nadir; // pct nadyr pt calcularea hypervolumului
	private static double[] ideal; // pct ideal pt calcularea hipervolumului
	private static int wdf = 0; // weight distribution function according to
								// zbt2007a

	private int hyp_ind_type; // 0-fara scalare, usual hypervolume plus
								// wdf
	// based
	// hypervolume in [0,1]^{dim} if =1, usual
	// hypervolume plus
	// additional line integrals on axes if =2*/
	private static double[] reference_point; // pct referinta pt fct de wdf = 3

	// var globale locale
	private static double[] refPoints; // necesare pt calc hyperv

	public Hyperv(int nrOb, double[] nadir, double[] ideal) {
		super();
		Hyperv.dim = nrOb;
		Hyperv.nadir = nadir;
		Hyperv.ideal = ideal;

		method = 0;
		refPoints = new double[dim*2];
		obj = new int[nrOb];
		for (int i = 0; i < nrOb; i++) {
			obj[i] = 0;
		}
	}

	// public static void main(String[] args) {
	// int i;
	// int no_runs; /* number of runs */
	// int max_points; /* maximum number of points per run */
	// int ref_set_size; /* number of points in the reference set */
	// int curr_run_size; /* number of points associated with the current run */
	// double[] ref_set; /* reference set */
	// double[] curr_run; /* objective vectors fur current run */
	// double ref_set_value;
	// double ind_value;
	// File fp, out_fp;
	//
	// // // iau parametrii
	// // dim = 2;
	// // obj = new int[dim];
	// // nadir = new double[dim];
	// // ideal = new double[dim];
	// // for (i = 0; i < dim; i++) {
	// // obj[i] = 0;
	// // nadir[i] = 0.0;
	// // ideal[i] = 4.0;
	// // }
	// // method = 0;
	// // refPoints = new double[dim * 2];
	// //
	// // /* read reference set */
	// // if (method == 1) {
	// //
	// // no_runs = 0;
	// // max_points = 0;
	// // /*
	// // * scale objective values of reference points according to ideal and
	// // * nadir point to get maximization in all objectives
	// // */
	// // } else {
	// // ref_set = null;
	// // ref_set_size = 0;
	// // }
	//
	//
	// byte metoda = Fitness.MET_DEBBINOMIAL;
	// double[][] front = new double[10][2];
	// int dimFrontTeoretic = 832;
	// double[][] frontT = new
	// Utils().citireFrontTeoretic("src/mooCuPonderi/teste/frontDePeNetDebBimodal.txt",
	// metoda, dim, dimFrontTeoretic);
	// System.out.println(frontT[0].length);
	// double vol = calcIndVal(frontT, frontT.length);
	// System.out.println(vol);
	// }

	/**
	 * calculates the hypervolume of point set a according to minimzation or
	 * maximization of objectives and the ideal and nadir points
	 * <p>
	 * beforehand, the objective values are re-calculated relatively to the
	 * given nadir and ideal point to get maximization in all objectives and
	 * objective values between 0 and ideal[k]
	 * 
	 * @param front
	 * @param dimFront
	 * @return
	 */
	public double calcIndVal(double[][] front, int dimFront) {
		/*
		 * re-calculate objective values relative to given nadir and ideal point
		 * to get maximization in all objectives and objective values between 0
		 * and ideal[k]
		 */
		scalareValObjPtMax(front, dimFront);

		return calcHypervolum(front, refPoints, dimFront, dim, reference_point);
	}

	/**
	 * scalez obj din a ai sa am o probl de maximizare
	 * <p>
	 * post: all objective values in 'a' are in [0,|ideal[k]-nadir[k]|] for all
	 * objectives k and rescaled for maximization, i.e., (new) nadir point is
	 * always 0^{dim} and (new) ideal point is ideal_old[k]-nadir_old[k] for
	 * former maximization objectives k and nadir_old[k]-ideal_old[k] for former
	 * minimization objectives k
	 * 
	 * @param a
	 * @param dim_a
	 */
	protected static void scalareValObjPtMax(double[][] a, int dim_a) {
		int i, k;
		double temp;

		for (k = 0; k < dim; k++) {
			switch (obj[k]) {
			case 0: /* minimizarea obiectivului k */
				nadir[k] = nadir[k] - ideal[k];
				for (i = 0; i < dim_a; i++) {
					temp = (nadir[k]) - (a[i][k] - ideal[k]);
					// error(temp < 0,
					// "error in data or reference set file with 'nadir', 'ideal', or 'obj'\nPlease ensure that nadir and ideal point are set correctly according to maximization/minimization");
					a[i][k] = temp;
				}

				/*
				 * re-calculate reference point of wdf '3' relative to given
				 * nadir point to get maximization in all objectives
				 */
				if (wdf == 3) {
					temp = nadir[k] - (reference_point[k] - ideal[k]);
					// error(temp < 0,
					// "error in data or reference set file with 'reference_point' or 'obj'");
					reference_point[k] = temp;
				}

				break;

			default: /* maximization in objective k */
				for (i = 0; i < dim_a; i++) {
					temp = a[i][k] - nadir[k];
					// error(temp < 0,
					// "error in data or reference set file with 'nadir', 'ideal', or 'obj'");
					a[i][k] = temp;
				}

				/*
				 * re-calculate reference point of wdf '3' relative to given
				 * nadir point to get maximization in all objectives
				 */
				if (wdf == 3) {
					temp = reference_point[k] - nadir[k];
					// error(temp < 0,
					// "error in data or reference set file with 'reference_point' or 'obj'");
					reference_point[k] = temp;
				}
				break;
			}

			switch (obj[k]) {
			case 0:
				/* switch and adjust nadir and ideal point */
				ideal[k] = nadir[k];
				nadir[k] = 0;
				break;
			default:
				/* adjust nadir and ideal point */
				ideal[k] = ideal[k] - nadir[k];
				nadir[k] = 0;
				break;
			}
		}
	}

	/**
	 * 
	 * @param front
	 * @param refPoints
	 * @param dimFront
	 * @param dim
	 * @param reference_point
	 * @return
	 */
	private static double calcHypervolum(double[][] front, double[] refPoints,
			int dimFront, int dim, double[] reference_point) {
		int n = dimFront, dist = 0;
		double vol = 0;
		refPoints[dim - 1] = 0.0;
		System.out.println("Dim front" + dimFront);
		while (n > 0) {
			int nrPctNedominate;
			double volTemp, distTemp;

			nrPctNedominate = filtreazaSetulNedominat(front, n, dim - 1);
			distTemp = surfaceUnchangedTo(front, n, dim - 1);
			refPoints[dim - 1] = distTemp;

			volTemp = 0;
			if (dim < 3) {
				refPoints[0] = 0.0;
				refPoints[dim] = front[0][0];
				volTemp = front[0][0];
				// volTemp = calcHypercubIntegral(ref, dim, hyp_ind_type, ideal,
				// wdf, pctRef);
			} else
				volTemp = calcHypervolum(front, refPoints, nrPctNedominate,
						dim - 1, reference_point);
			// System.out.println(volTemp);
			vol += volTemp;
			// System.out.println(vol);
			refPoints[dim - 1] = distTemp;
			n = reducSetulNedominat(front, n, dim - 1, dist);
		}
		return vol;
	}

	/**
	 * punctele din front sunt sortate astfel incat pct [0..n-1] reprez pct
	 * nedominate; se returneaza n
	 * 
	 * @param front
	 * @param nrPct
	 * @param nrOb
	 * @return
	 */
	protected static int filtreazaSetulNedominat(double[][] front, int nrPct,
			int nrOb) {
		int i = 0, j, n = nrPct;
		while (i < n) {
			j = i + 1;
			while (j < n) {
				if (domina(front[i], front[j], nrOb)) {
					n--;
					schimba(front, j, n);
				} else if (domina(front[j], front[i], nrOb)) {
					n--;
					schimba(front, i, n);
					i--;
					break;
				} else
					j++;
			}
			i++;
		}
		return n;
	}

	/**
	 * Metoda returneaza true daca a domina pe b
	 * 
	 * @param a
	 * @param b
	 * @param nrOb
	 * @return
	 */
	protected static boolean domina(double[] a, double[] b, int nrOb) {
		boolean maiBunLaUnOb = false, maiRauLaUnOb = false;
		int i;

		for (i = 0; i < nrOb && !maiRauLaUnOb; i++) {
			if (a[i] > b[i])
				maiBunLaUnOb = true;
			else if (a[i] < b[i])
				maiRauLaUnOb = true;
		}
		return (!maiRauLaUnOb && maiBunLaUnOb);
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
	protected boolean dominaSlab(double[] a, double[] b, int nrOb) {
		boolean maiRauLaUnOb = false;
		int i;

		for (i = 0; i < nrOb && !maiRauLaUnOb; i++)
			if (a[i] < b[i])
				maiRauLaUnOb = true;
		return (!maiRauLaUnOb);
	}

	protected static void schimba(double[][] front, int i, int j) {
		double[] temp;

		// for (int k = 0; k < dim; k++) {
		// temp = front[i * dim + k];
		// front[i * dim + k] = front[j * dim + k];
		// front[j * dim + k] = temp;
		// }
		temp = front[i];
		front[i] = front[j];
		front[j] = temp;
	}

	/**
	 * Metoda det minimul din front pt obiectivul dat
	 * <p>
	 * calculate next value regarding dimension 'objective'; consider points
	 * 0..no_points-1 in 'front'
	 * 
	 * @param front
	 * @param nrPct
	 * @param obiectiv
	 * @return
	 */
	protected static double surfaceUnchangedTo(double[][] front, int nrPct,
			int obiectiv) {
		double min, val;

		if (nrPct < 1)
			System.err.println("Eroare, nr pct < 1");
		min = front[0][obiectiv];
		for (int i = 1; i < nrPct; i++) {
			val = front[i][obiectiv];
			if (val < min)
				min = val;
		}
		return min;
	}

	/**
	 * Sterg punctele care au fi < prag pentru obiectivul specificat, Se iau in
	 * considerare punctele [0..nrPct-1] din front, frontul este resortat. Se
	 * returneaza n
	 * 
	 * @param front
	 * @param nrPct
	 * @param obiectiv
	 * @param prag
	 * @return
	 */
	protected static int reducSetulNedominat(double[][] front, int nrPct,
			int obiectiv, double prag) {
		int n = nrPct;

		for (int i = 0; i < n; i++) {
			if (front[i][obiectiv] <= prag) {
				n--;
				schimba(front, i, n);
			}
		}
		return n;
	}
}
