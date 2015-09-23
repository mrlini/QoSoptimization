package org.utcluj.moo.optimizareServiciiReale;

import org.utcluj.moo.utils.FitnessMO;
import org.utcluj.moo.utils.UtilsMOO;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.IntegerVectorIndividual;

public class IntProblem extends Problem implements SimpleProblemForm{

	private static final long serialVersionUID = -6260603658876991284L;
	
	private int nrOb = 0;
	private String activities = "";
	private int alternative = 0;

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		
		// preiau nr obiective din setup
		nrOb = state.parameters.getInt(new Parameter(UtilsMOO.NROBJ),	null);
		activities = state.parameters.getString(new Parameter(UtilsMOO.ACTIVITIES), null);
		alternative = state.parameters.getInt(new Parameter(UtilsMOO.ALTERNATIVE), null);
	}
	
	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		if (!(ind instanceof IntegerVectorIndividual))
			state.output.fatal("Cromozom-ul nu este codat ca si IntegrVectorIndividual",null);

		// aici stochez fitness-ul pt fiecare obiectiv
		float[] obiective = FitnessMO.getInstance().getFitnessMooQoS(nrOb, true, ind, activities, alternative);
		
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, obiective);
		ind.evaluated = true;
	}
}
