package org.utcluj.moo.algoritmi.moead;

import org.utcluj.moo.utils.FitnessMO;
import org.utcluj.moo.utils.UtilsMOO;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

public class ProblemMoeadQoS extends Problem implements SimpleProblemForm{

	private static final long serialVersionUID = -2466978821286630002L;
	
	
	private int nrOb = 0;
	private int alternative = 0;
	private String activities = "";
	
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		
		// preiau nr obiective din setup
		nrOb = state.parameters.getInt(new Parameter(UtilsMOO.NROBJ),	null);
		activities = state.parameters.getString(new Parameter(UtilsMOO.ACTIVITIES), null);
		alternative = state.parameters.getInt(new Parameter(UtilsMOO.ALTERNATIVE), null);
	}
	
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		
		if( !( ind instanceof DoubleVectorIndividual ) )
            state.output.fatal( "Indivizii pt aceasta problema tb sa fie de tipul DoubleVectorIndividual." );
		
		float[] obiective = FitnessMO.getInstance().getFitnessMooQoS(nrOb, false, ind, activities, alternative);
		double fitnessAgregat = UtilsMoeadQoS.getInstance().computeTchQoS(state, subpopulation, ind, obiective);
		
		//TODO de verificat
		fitnessAgregat = -fitnessAgregat;  //vreau sa minimizez
		SimpleFitness fit = (SimpleFitness) ind.fitness;
		fit.setFitness(state, (float)fitnessAgregat, false);
		ind.evaluated = true;
	}
	
}
