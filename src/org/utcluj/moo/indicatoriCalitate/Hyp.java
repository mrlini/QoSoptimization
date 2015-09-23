package org.utcluj.moo.indicatoriCalitate;


/**
 * Hypervolum Zytzler merge!!
 * @author m
 *
 */
public class Hyp {
	/* Variables from parameter file */
	int     dim;     /* number of objectives */
	int     []obj;    /* obj[i] = 0 means objective i is to be minimized */
	int     method;  /* 0 = no reference set, 1 = with respect to reference set */
	double  []nadir;  /* nadir point for hypervolume calculation */
	double  []ideal;  /* ideal point for hypervolume calculation */
	int     wdf;     /* weight distribution function according to zbt2007a */
	int     hyp_ind_type; /* no scaling if =0, usual hypervolume plus wdf based
	                    hypervolume in [0,1]^{dim} if =1, usual hypervolume plus
							  additional line integrals on axes if =2*/
	double[] reference_point; /* reference point for 3rd weight distribution function*/

	/* Global internal variables */
	double[] refPoints; /* needed for hypervolume calculation */

	
	/**
	 * 
	 * @param dim
	 * @param obj - obieactivul problemei: minimizare -> obj[i]=0
	 * @param method
	 * @param nadir
	 * @param ideal
	 * @param wdf
	 * @param hyp_ind_type
	 * @param reference_point
	 */
	public Hyp(int dim, int[] obj, int method, double[] nadir, double[] ideal,
			int wdf, int hyp_ind_type, double[] reference_point) {
		super();
		this.dim = dim;
		this.obj = obj;
		this.method = method;
		this.nadir = nadir;
		this.ideal = ideal;
		this.wdf = wdf;
		this.hyp_ind_type = hyp_ind_type;
		this.reference_point = reference_point;
		
		refPoints = new double[2*dim];
	}

	public boolean dominates(double  []point1, double  []point2, int  no_objectives)
	/* returns true if 'point1' dominates 'points2' with respect to the
	 * to the first 'no_objectives' objectives
	 */
	{
//		int  i;
//		int  better_in_any_objective, worse_in_any_objective;
//
//		better_in_any_objective = 0;
//		worse_in_any_objective = 0;
//		for (i = 0; i < no_objectives && !worse_in_any_objective; i++)
//			if (point1[i] > point2[i])
//				better_in_any_objective = 1;
//			else if (point1[i] < point2[i])
//				worse_in_any_objective = 1;
//		return (!worse_in_any_objective && better_in_any_objective)
		boolean maiBunLaUnOb = false, maiRauLaUnOb = false;
		int i;

		for (i = 0; i < no_objectives && !maiRauLaUnOb; i++) {
			if (point1[i] > point2[i])
				maiBunLaUnOb = true;
			else if (point1[i] < point2[i])
				maiRauLaUnOb = true;
		}
		return (!maiRauLaUnOb && maiBunLaUnOb);
	}

	public boolean  weakly_dominates(double  []point1, double  []point2, int  no_objectives)
	/* returns true if 'point1' weakly dominates 'points2' with respect to the
	 * to the first 'no_objectives' objectives
	 */
	{
//		int  i;
//		int  worse_in_any_objective;
//
//		worse_in_any_objective = 0;    
//		for (i = 0; i < no_objectives &&  !worse_in_any_objective; i++)
//			if (point1[i] < point2[i])
//				worse_in_any_objective = 1;
//		return (!worse_in_any_objective);
		boolean maiRauLaUnOb = false;
		int i;

		for (i = 0; i < no_objectives && !maiRauLaUnOb; i++)
			if (point1[i] < point2[i])
				maiRauLaUnOb = true;
		return (!maiRauLaUnOb);
	} 

	public void  swap(double  [][]front, int  i, int  j)
	{
		int  k;
		double  temp;

		for (k = 0; k < dim; k++) {
			temp = front[i][k];
			front[i][k] = front[j][k];
			front[j][k] = temp;
		}
	}

	public int  filter_nondominated_set(double  [][]front, int  no_points,
				     int  no_objectives)
	/* all nondominated points regarding the first 'no_objectives' dimensions
	 * are collected; the points 0..no_points-1 in 'front' are
	 * considered; the points in 'front' are resorted, such that points
	 * [0..n-1] represent the nondominated points; n is returned
	 */
	{
		int  i, j;
		int  n;

		n = no_points;
		i = 0;
		while (i < n) {
			j = i + 1;
			while (j < n) {
				if (dominates(front[i], front[j], no_objectives)) {
					/* remove point 'j' */
					n--;
					swap(front, j, n);
				}
				else if (dominates(front[j], front[i],
						no_objectives)) {
					/* remove point 'i'; ensure that the point copied to index 'i'
					   is considered in the next outer loop (thus, decrement i) */
					n--;
					swap(front, i, n);
					i--;
					break;
				}
				else
					j++;
			}
			i++;
		}
		return n;
	}

	public double  surface_unchanged_to(double[][] front, int  no_points, int  objective)
	/* calculate next value regarding dimension 'objective'; consider
	 * points 0..no_points-1 in 'front'
	 */
	{
		int     i;
		double  min, value;
		if(no_points<1)
			System.err.println("eroare: nr pct < 1");
		min = front[0][objective];
		for (i = 1; i < no_points; i++) {
			value = front[i][objective];
			if (value < min)
				min = value;
		}
		return min;
	} 

	public int  reduce_nondominated_set(double[][] front, int  no_points, int  objective,
	                             double  threshold)
	/* remove all points which have a value <= 'threshold' regarding the
	 * dimension 'objective'; the points [0..no_points-1] in 'front' are
	 * considered; 'front' is resorted, such that points [0..n-1] represent
	 * the remaining points; 'n' is returned
	 */
	{
		int  n;
		int  i;

		n = no_points;
		for (i = 0; i < n; i++)
			if (front[i][objective] <= threshold) {
				n--;
				swap(front, i, n);
			}

		return n;
	} 

	public double  calc_hypervolume_cubewise(double[][] front, double[] refs,
	                                  int  no_points, int  no_objectives, double[] ref_point)
	{
		int     n;
		double  volume, distance;

		volume = 0;
		distance = 0;
		n = no_points;
		refs[no_objectives - 1] = 0.0;
		while (n > 0)
		{
			int     no_nondominated_points;
			double  temp_vol, temp_dist;

			no_nondominated_points = filter_nondominated_set(front, n,
			                         no_objectives - 1);	
											 
			temp_dist = surface_unchanged_to(front, n, no_objectives - 1);
			refs[dim + no_objectives - 1] = temp_dist;

			temp_vol = 0;
			if (no_objectives < 3)
			{
				assert(no_nondominated_points > 0);
				refs[0] = 0.0;
				refs[dim] = front[0][0];
				temp_vol = Utils_hyp.calc_hypercube_integral(refs, dim, hyp_ind_type, ideal, wdf, ref_point);
//				if(temp_vol!=0d)
//					System.out.println("vol temporar: "+temp_vol);
			}
			else
				temp_vol = calc_hypervolume_cubewise(front, refs, no_nondominated_points,
				                                     no_objectives - 1, ref_point);
			volume += temp_vol;

			refs[no_objectives - 1] = temp_dist;
			distance = temp_dist;
			n = reduce_nondominated_set(front, n, no_objectives - 1, distance);
		}

		return volume;
	}


	void scaleObjectiveValuesForMaximization(double[][] a, int size_a)
	/* scales the objective values of all points in 'a' wrt
	 * ideal and nadir point such that all objectives are maximized:
	 * 
	 * post:
	 * all objective values in 'a' are in [0,|ideal[k]-nadir[k]|]
	 * for all objectives k and rescaled for maximization, i.e.,
	 * (new) nadir point is always 0^{dim} and (new) ideal point is
	 * ideal_old[k]-nadir_old[k] for former maximization objectives k and
	 * nadir_old[k]-ideal_old[k] for former minimization objectives k
	 */
	{
		int  i, k;
		double  temp;

		for (k = 0; k < dim; k++) {
			switch (obj[k]) {
				case 0: /* minimization in objective k */
					nadir[k] = nadir[k] - ideal[k];
					for (i = 0; i < size_a; i++) {
						temp = (nadir[k]) - (a[i][k] - ideal[k]);
//						error(temp < 0, "error in data or reference set file with 'nadir', 'ideal', or 'obj'\nPlease ensure that nadir and ideal point are set correctly according to maximization/minimization");
						a[i][k] = temp;
					}
					
					/* re-calculate reference point of wdf '3' relative to given nadir point
				   to get maximization in all objectives */
					if (wdf == 3) 
					{
						temp = nadir[k] - (reference_point[k] - ideal[k]);
						reference_point[k] = temp;
					}	
					
					break;
					
				default: /* maximization in objective k */
					for (i = 0; i < size_a; i++) {
						temp = a[i][k] - nadir[k];
//						error(temp < 0, "error in data or reference set file with 'nadir', 'ideal', or 'obj'");
						a[i][k] = temp;
					}

					/* re-calculate reference point of wdf '3' relative to given nadir point
				   to get maximization in all objectives */
					if (wdf == 3) 
					{				
						temp = reference_point[k] - nadir[k];
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

	public double  calc_ind_value(double[][] a, int  size_a)
	/* calculates the hypervolume of point set a according to minimzation or
	 * maximization of objectives and the ideal and nadir points
	 *	
	 * beforehand, the objective values are re-calculated relatively to
	 * the given nadir and ideal point to get maximization in all objectives
	 * and objective values between 0 and ideal[k]
	 */
	{
		/* re-calculate objective values relative to given nadir and ideal point 
		   to get maximization in all objectives and objective values between 0 and ideal[k] */
		scaleObjectiveValuesForMaximization(a, size_a);
		
		/* calculate indicator values */
		return calc_hypervolume_cubewise(a, refPoints, size_a, dim , reference_point);
	}

	/**
	 * 
	 * @param state
	 * @param nrOb
	 * @param metoda
	 * @return
	 */
	public double calcHypervolume(double[][] front) {
		//determin val min si max din front
		return calc_ind_value(front, front.length);
	}
	
}

