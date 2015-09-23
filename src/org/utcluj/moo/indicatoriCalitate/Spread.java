package org.utcluj.moo.indicatoriCalitate;

import java.util.Arrays;

import org.utcluj.moo.indicatoriCalitate.utils.LexicoGraphicalComparator;
import org.utcluj.moo.utils.UtilsMOO;

import cern.jet.math.Functions;

/**
 * This class implements the spread quality indicator. 
 * <p>
 * <b>This metric is only applicable to two bi-objective problems.</b>
 * </p>
 * Reference: Deb, K., Pratap, A., Agarwal, S., Meyarivan, T.: A fast and 
 *            elitist multiobjective genetic algorithm: NSGA-II. IEEE Trans. 
 *            on Evol. Computation 6 (2002) 182-197
 */
public class Spread {

	public Spread() {
	}

	@SuppressWarnings("unchecked")
	public double calculDisp(double[][] front, int nrOb) {
		// normez si frontul teoretic
		double[] max = new double[nrOb];
		double[] min = new double[nrOb];
		int dimFront = front.length;
		double[][] frontNormat = new double[dimFront][nrOb];

		for (int i = 0; i < nrOb; i++) {
			min[i] = Double.MAX_VALUE;
			max[i] = Double.NEGATIVE_INFINITY;
		}
		
		for(int i=0;i<dimFront;i++){
			for(int j=0;j<nrOb;j++){
				if(front[i][j] > max[j]){
					max[j] = front[i][j];
				}
				if(front[i][j] < min[j]){
					min[j] = front[i][j];
				}
			}
		}
		
		//normez frontul
		for(int i=0;i<dimFront;i++){
			for(int j=0;j<nrOb;j++){
				frontNormat[i][j] = (front[i][j] - min[j]) / (max[j] - min[j]);
			}
		}

		// sortez frontul normat
		Arrays.sort(frontNormat, new LexicoGraphicalComparator());

		int nrPct = frontNormat.length;
		
		//nu am puncte extreme pentru ca nu am frontul teoretic 
		//si atunci calculez doar distanta medie intre puncte
		
		double mean = 0.0;
		double diversitySum = 0;

		// STEP 5. Calculate the mean of distances between points i and (i - 1).
		// (the poins are in lexicografical order)
		for (int i = 0; i < dimFront-1; i++) {
			mean +=  UtilsMOO.getInstance().distanta(frontNormat[i], frontNormat[i+1]);
		}

		mean = mean / (double) (dimFront - 1);

		// STEP 6. If there are more than a single point, continue computing the
		// metric. In other case, return the worse value (1.0, see metric's
		// description).
		if (nrPct > 1) {
			for (int i = 0; i < (nrPct - 1); i++) {
				diversitySum += Functions.abs.apply(UtilsMOO.getInstance().distanta(frontNormat[i], frontNormat[i+1])-mean); 
			} // for
			return diversitySum / ((nrPct - 1) * mean);
		} else
			return 1.0;
	}
}
