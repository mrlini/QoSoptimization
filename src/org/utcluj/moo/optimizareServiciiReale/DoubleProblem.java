package org.utcluj.moo.optimizareServiciiReale;

import org.utcluj.moo.utils.FitnessMO;
import org.utcluj.moo.utils.UtilsMOO;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

public class DoubleProblem extends Problem implements SimpleProblemForm{

	private static final long serialVersionUID = 5262105145839709760L;

	private int nrOb = 0;
	private int alternative = 0;//, taskcount = 0;
	private String activities = "";

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		
		nrOb = state.parameters.getInt(new Parameter(UtilsMOO.NROBJ), null);
		alternative = state.parameters.getInt(new Parameter(UtilsMOO.ALTERNATIVE), null);
		activities = state.parameters.getString(new Parameter(UtilsMOO.ACTIVITIES), null);
	}

	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		if (!(ind instanceof DoubleVectorIndividual))
			state.output.fatal("Cromozom-ul nu este codat ca si DoubleVectorIndividual",null);
		
		// aici stochez fitness-ul pt fiecare obiectiv
		float[] obiective = FitnessMO.getInstance().getFitnessMooQoS(nrOb, false, ind, activities, alternative);

		((MultiObjectiveFitness) ind.fitness).setObjectives(state, obiective);
		ind.evaluated = true;
	}
}
