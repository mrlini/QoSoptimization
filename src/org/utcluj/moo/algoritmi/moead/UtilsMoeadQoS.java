package org.utcluj.moo.algoritmi.moead;

import java.util.ArrayList;
import java.util.List;

import org.utcluj.moo.utils.FitnessMO;
import org.utcluj.moo.utils.UtilsMOO;
import org.utcluj.moo.utils.ec.util.MersenneTwisterFast;

import cern.jet.math.Functions;
import ec.EvolutionState;
import ec.Individual;

public class UtilsMoeadQoS {

	private static UtilsMoeadQoS instance = null;
	
	public static double[][] ponderi;
	private static double[] zOptim;
	private static int nrObj;
	private static int dimPop;

	private UtilsMoeadQoS() {
		
	}

	public static UtilsMoeadQoS getInstance() {
		if (instance == null)
			instance = new UtilsMoeadQoS();
		return instance;
	}

	public static void setNrOb(int nrOb) {
		UtilsMoeadQoS.nrObj = nrOb;
	}

	public static void setDimPop(int dimPop) {
		UtilsMoeadQoS.dimPop = dimPop;
	}

	public static double[] getzOptim() {
		return zOptim;
	}

	public static void setzOptim(double[] zOptim) {
		UtilsMoeadQoS.zOptim = zOptim;
	}

	/**
	 * Agreg obiectivele folosind metoda de scalarizare Tchebycheff conform MOEA/D
	 * 
	 * @param state
	 * @param subpopulation
	 * @param ind
	 * @param obiective
	 * @return - obiectivul agregat
	 */
	public double computeTchQoS(EvolutionState state, int subpopulation,
			Individual ind, float[] obiective) {

		double max = Double.NEGATIVE_INFINITY;

		int j = 0;

		// preiau vectorul de ponderi
		// ponderile sunt generate pentru fiecare individ
		// caut indicele individului din pop si pe baza lui iau vectorul de
		// ponderi
		for (Individual indComo : state.population.subpops[subpopulation].individuals) {
			if (ind.equals(indComo))
				break;
			j++;
		}

		// aplic ponderarea TCEBYCHEFF
		for (int i = 0; i < nrObj; i++) {
//			double obiectiv = 0;
//			if(i<2)
//				obiectiv = -(double)obiective[i];
//			else
//				obiectiv = (double)obiective[i];
//			max = Functions.max.apply(max, ponderi[j][i] * Functions.abs.apply(obiectiv - zOptim[i]));
			max = Functions.max.apply(max, ponderi[j][i] * Functions.abs.apply(obiective[i] - zOptim[i]));
		}

		return max;
	}

	/**
	 * Agreg obiectivele pentru un anumit vector de ponderi
	 * 
	 * @param indexWeight
	 * @param ind
	 * @param activitiesUsed
	 * @param nrAlternatives
	 * @return
	 */
	public double computeTCHwithSpecificWeightQoS(int indexWeight, Individual ind, 
			String activitiesUsed, int nrAlternatives) {
		
		double max = Double.NEGATIVE_INFINITY;
		float[] objectives = FitnessMO.getInstance().getFitnessMooQoS(nrObj, false, ind, activitiesUsed, nrAlternatives);

		// aplic ponderarea TCEBYCHEFF
		for (int i = 0; i < nrObj; i++) {
//			double obiectiv = 0;
//			if(i<2)
//				obiectiv = -(double)objectives[i];
//			else
//				obiectiv = (double)objectives[i];
//			max = Functions.max.apply(max, ponderi[indexWeight][i] * Functions.abs.apply(obiectiv - zOptim[i]));
			max = Functions.max.apply(max, ponderi[indexWeight][i] * Functions.abs.apply(objectives[i] - zOptim[i]));
		}

		return max;
	}
	
	public static void initZoptim(){
		zOptim = new double[nrObj];
		
		for(int i=0;i<nrObj;i++)
			zOptim[i] = Double.POSITIVE_INFINITY;
	}
	
	/**
	 * generez ponderile pentru MOEA/D
	 * 
	 */
	public void generateWeights() {
		ponderi = new double[dimPop][nrObj];
		double a = 0;
		
		if(nrObj == 2){
			for(int i=0;i<dimPop;i++){
				a = i / (double)(dimPop-1);
				ponderi[i][0] = a;
				ponderi[i][1] = 1-a;
			}
		}else{
			List<double[]> finalWeights = new ArrayList<double[]>();
			List<double[]> weights = new ArrayList<double[]>(dimPop*50);
			double sum = 0;
			MersenneTwisterFast random = new MersenneTwisterFast();
			
			//create random weights
			for (int i = 0; i < 50 * dimPop; i++) {
				double[] weight = new double[nrObj];
				
				for (int j = 0; j < nrObj; j++) {
					weight[j] = random.nextDouble();
					sum += weight[j];
				}
				
				for (int j = 0; j < nrObj; j++) 
					weight[j] /= sum;

				weights.add(weight);
			}
			
			// initialize population with weights (1,0,...,0), (0,1,...,0), ...,
			// (0,...,0,1)
			for(int i=0;i<nrObj;i++){
				double[] v = new double[nrObj];
				v[i] = 1;
				finalWeights.add(v);
			}
			
			// fill in remaining weights with the weight vector with the largest
			// distance from the assigned weights
			while (finalWeights.size() < dimPop) {
				double[] weight = null;
				double distance = Double.NEGATIVE_INFINITY;
				
				for (int i = 0; i < weights.size(); i++) {
					double d = Double.POSITIVE_INFINITY;

					for (int j = 0; j < finalWeights.size(); j++) 
						d = Math.min(d, UtilsMOO.getInstance().distanta(weights.get(i), finalWeights.get(j)));

					if (d > distance) {
						weight = weights.get(i);
						distance = d;
						break;
					}
				}
				
				finalWeights.add(weight);
				weights.remove(weight);
			}//while distanta
		}//else
		
	}

}
