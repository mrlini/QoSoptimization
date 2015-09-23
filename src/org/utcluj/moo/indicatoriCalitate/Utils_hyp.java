package org.utcluj.moo.indicatoriCalitate;

public class Utils_hyp {

	@SuppressWarnings("unused")
	public static double calc_hypercube_integral(double[] refs, int dim,
			int hyp_ind_type, double[] ideal, int wdf, double[] ref_point) {
		/*
		 * calculates the hypervolume of the points in 'refs' according to specified
		 * weight distribution function in 'wdf'
		 */
		double volume;
		/*
		 * factor, the wdf near front is larger than maximal hypervolume on
		 * entire search space when hyp_ind_type == 1
		 */
		double factor;
		double[] scaledrefs;
		int i, j;
		int scalingnecessary;

		scaledrefs = new double[2 * dim];
		/* initialize factor: */
		factor = 1.0;
		for (i = 0; i < dim; i++) {
			factor *= ideal[i];
		}

		/*
		 * get deep copy of hypercube's reference points, the integral of which
		 * is sought
		 */
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

	public static double hypervolume(double[] refs, int dim)
	/* Usual hypervolume */
	{
		int i;
		double volume;

		/* calculate hypervolume */
		volume = 1.0;
		for (i = 0; i < dim; i++) {
			volume *= refs[dim + i] - refs[i];
		}
		return volume;
	}
}
