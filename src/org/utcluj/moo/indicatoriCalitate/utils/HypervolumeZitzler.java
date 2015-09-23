package org.utcluj.moo.indicatoriCalitate.utils;

/**
 * am modificat fata de zitzler relatia de dominare (in loc sa fol a>b => a
 * domina pe b am fol a<b => a domina pe b)
 * 
 * @author mihai
 */
@SuppressWarnings("unused")
public class HypervolumeZitzler {

	private int dim; // nr obiective
	private int[] obj; // obj[i] = 0 => ob i tb minimizat; 1=> maximizat

	private int metoda = 0; // 0=fara set de referinta, 1-cu set de referinta
	private double[] nadir; // pct nadyr pt calcularea hypervolumului
	private double[] ideal; // pct ideal pt calcularea hipervolumului
	private int wdf; // weight distribution function according to zbt2007a
	private int hyp_ind_type; // 0-fara scalare, usual hypervolume plus wdf
								// based
								// hypervolume in [0,1]^{dim} if =1, usual
								// hypervolume plus
								// additional line integrals on axes if =2*/
	private double[] reference_point; // pct referinta pt fct de distributie 3

	// var globale locale
	private double[] refPoints;

	public HypervolumeZitzler(int dim, int[] obj, int metoda, double[] nadir,
			double[] ideal, int wdf, int hyp_ind_type,
			double[] reference_point) {
		super();
		this.dim = dim;
		this.obj = obj;
		this.metoda = metoda;
		this.nadir = nadir;
		this.ideal = ideal;
		this.wdf = wdf;
		this.hyp_ind_type = hyp_ind_type;
		this.reference_point = reference_point;
		refPoints = new double[2*dim];
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
		// TODO de verificat metoda filtreaza
	}

	/**
	 * Metoda det minimul din front pt obiectivul dat
	 * 
	 * @param front
	 * @param nrPct
	 * @param obiectiv
	 * @return
	 */
	protected double surfaceUnchangedTo(double[][] front, int nrPct,
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
	protected int reducSetulNedominat(double[][] front, int nrPct,
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

	/**
	 * 
	 * @param front
	 * @param ref
	 * @param nrPct
	 * @param nrOb
	 * @param pctRef
	 * @return
	 */
	protected double calcHypervolum(double[][] front, double[] ref, int nrPct,
			int nrOb, double[] pctRef) {
		int n = nrPct, vol = 0, dist = 0;
		ref[nrOb - 1] = 0.0;

		while (n > 0) {
			int nrPctNedominate;
			double volTemp, distTemp;

			nrPctNedominate = filtreazaSetulNedominat(front, n, nrOb - 1);
			distTemp = surfaceUnchangedTo(front, n, nrOb - 1);
			ref[dim + nrOb - 1] = distTemp;

			volTemp = 0;
			if (nrOb < 3) {
				ref[0] = 0.0;
				ref[dim] = front[0][0];
				volTemp = front[0][0];
				volTemp = calcHypercubIntegral(ref, dim, hyp_ind_type, ideal,
						wdf, pctRef);
			} else
				volTemp = calcHypervolum(front, ref, nrPctNedominate, nrOb - 1,
						pctRef);

			vol += volTemp;

			ref[nrOb - 1] = distTemp;
			n = reducSetulNedominat(front, n, nrOb - 1, dist);
		}
		return vol;
	}

	private double calcHypercubIntegral(double[] refs, int dim,
			int hyp_ind_type, double[] ideal, int wdf, double[] ref_point) {

		double volume;
		/*
		 * factor, the wdf near front is larger than maximal hypervolume on
		 * entire search space when hyp_ind_type == 1
		 */
		double factor = 1.0;
		double[] scaledrefs = new double[2 * dim];
		int i, j;
		int scalingnecessary;

		for (i = 0; i < dim; i++) {
			factor *= ideal[i];
		}
		
		// get deep copy of hypercube's reference points, the integral of which
		// is sought
		for (i = 0; i < 2 * dim; i++) {
			scaledrefs[i] = refs[i];
		}

		volume = 0.0;
		if (hyp_ind_type == 0) {
			/* usual hypervolume on entire search space */
			volume = hypervolume(scaledrefs, dim);
		}
		return volume;
	}

	private double hypervolume(double[] refs, int dim) {
		int i;
		double volume;

		/* calculate hypervolume */
		volume = 1.0;
		for (i = 0; i < dim; i++) {
			volume *= refs[dim + i] - refs[i];
		}

		return volume;
	}

	/**
	 * scalez obj din a ai sa am o probl de maximizare
	 * 
	 * @param a
	 * @param dim_a
	 */
	protected void scalareValObjPtMax(double[][] a, int dim_a) {
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
					a[i ][k] = temp;
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
	 * calculates the hypervolume of point set a according to minimzation or
	 * maximization of objectives and the ideal and nadir points
	 * 
	 * beforehand, the objective values are re-calculated relatively to the
	 * given nadir and ideal point to get maximization in all objectives and
	 * objective values between 0 and ideal[k]
	 * 
	 * @param a
	 * @param dim
	 * @return
	 */
	protected double calcIndVal(double[][] a, int dim_a) {
		/*
		 * re-calculate objective values relative to given nadir and ideal point
		 * to get maximization in all objectives and objective values between 0
		 * and ideal[k]
		 */
		scalareValObjPtMax(a, dim_a);

		return calcHypervolum(a, refPoints, dim_a, dim, reference_point);
	}

	public static void main(String[] args) {
		double[] a = { 2.0, 4.3 };
		double[] b = { 22.0, 4.7 };
		if (domina(a, b, 2))
			System.out.println("a domina pe b");
		else
			System.out.println("b domina pe a");

		double[][] v = { { 0.1, 0.3, 0.4 }, { 2.2, 4.2, 3.1 },
				{ 1.1, 2.2, 2.1 }, { 0.03, 0.2, 0.2 } };
		System.out.println(v[1][1]);
		filtreazaSetulNedominat(v, v.length, 3);
		for (int i = 0; i < v.length; i++) {
			for (int j = 0; j < v[0].length; j++)
				System.out.print(v[i][j] + " ");
			System.out.println();
		}
	}

}
