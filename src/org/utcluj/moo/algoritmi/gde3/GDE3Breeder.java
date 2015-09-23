package org.utcluj.moo.algoritmi.gde3;

import ec.Breeder;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import ec.vector.FloatVectorSpecies;

public class GDE3Breeder extends Breeder {

	private static final long serialVersionUID = 2181155614441958922L;

	public static final double CR_UNSPECIFIED = -1;
	public double F = 0.0;
	public double Cr = CR_UNSPECIFIED;
	public static final String P_F = "f";
	public static final String P_Cr = "cr";

	public Population previousPopulation = null;
	public int[] bestSoFarIndex = null;

	public void setup(final EvolutionState state, final Parameter base) {
		if (!state.parameters.exists(base.push(P_Cr), null)) 
			Cr = CR_UNSPECIFIED;
		else {
			Cr = state.parameters.getDouble(base.push(P_Cr), null, 0.0);
			if (Cr < 0.0 || Cr > 1.0)
				state.output.fatal("Parameter not found, or its value is outside of [0.0,1.0].", base.push(P_Cr), null);
		}

		F = state.parameters.getDouble(base.push(P_F), null, 0.0);
		if (F < 0.0 || F > 1.0)
			state.output.fatal("Parameter not found, or its value is outside of [0.0,1.0].", base.push(P_F), null);
	}

	public void prepareDEBreeder(EvolutionState state) {
		// update the bestSoFar for each population
		if (bestSoFarIndex == null || state.population.subpops.length != bestSoFarIndex.length)
			bestSoFarIndex = new int[state.population.subpops.length];

		for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
			Individual[] inds = state.population.subpops[subpop].individuals;
			bestSoFarIndex[subpop] = 0;
			for (int j = 1; j < inds.length; j++)
				if (((MultiObjectiveFitness)inds[j].fitness).paretoDominates((MultiObjectiveFitness)inds[bestSoFarIndex[subpop]].fitness))
					bestSoFarIndex[subpop] = j;
		}
	}

	public Population breedPopulation(EvolutionState state) {
		if (!(state.evaluator instanceof GDE3Evaluator))
			state.output.warnOnce("GDE3Evaluator not used, but GDE3Breeder used.  This is almost certainly wrong.");

		prepareDEBreeder(state);

		// create the new population
		Population newpop = (Population) state.population.emptyClone();

		// breed the children
		for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
			if (state.population.subpops[subpop].individuals.length < 4) 
				state.output.fatal("Subpopulation "	+ subpop
								+ " has fewer than four individuals, and so cannot be used with GDE3Breeder.");

			Individual[] inds = newpop.subpops[subpop].individuals;
			for (int i = 0; i < inds.length; i++) {
				newpop.subpops[subpop].individuals[i] = createIndividual(state, subpop, i, 0); // unthreaded for now
			}
		}

		// store the current population for competition with the new children
		previousPopulation = state.population;
		return newpop;
	}

	/** Tests the Individual to see if its values are in range. */
	public boolean valid(DoubleVectorIndividual ind) {
		FloatVectorSpecies species = (FloatVectorSpecies) (ind.species);
		return (!(species.mutationIsBounded && !ind.isInRange()));
	}

	public DoubleVectorIndividual createIndividual(EvolutionState state,
			int subpop, int index, int thread) {
		Individual[] inds = state.population.subpops[subpop].individuals;

		DoubleVectorIndividual v = (DoubleVectorIndividual) (inds[index].clone());
		do {
			// select three indexes different from each other and from that of
			// the current parent
			int r0, r1, r2;
			do {
				r0 = state.random[thread].nextInt(inds.length);
			} while (r0 == index);
			do {
				r1 = state.random[thread].nextInt(inds.length);
			} while (r1 == r0 || r1 == index);
			do {
				r2 = state.random[thread].nextInt(inds.length);
			} while (r2 == r1 || r2 == r0 || r2 == index);

			DoubleVectorIndividual g0 = (DoubleVectorIndividual) (inds[r0]);
			DoubleVectorIndividual g1 = (DoubleVectorIndividual) (inds[r1]);
			DoubleVectorIndividual g2 = (DoubleVectorIndividual) (inds[r2]);

			for (int i = 0; i < v.genome.length; i++)
				v.genome[i] = g0.genome[i] + F * (g1.genome[i] - g2.genome[i]);
		} while (!valid(v));

		return crossover(state, (DoubleVectorIndividual) (inds[index]), v,
				thread);
	}

	/**
	 * Crosses over child with target, storing the result in child and returning
	 * it. The default procedure copies each value from the target, with
	 * independent probability CROSSOVER, into the child. The crossover
	 * guarantees that at least one child value, chosen at random, will not be
	 * overwritten. Override this method to perform some other kind of
	 * crossover.
	 */

	public DoubleVectorIndividual crossover(EvolutionState state,
			DoubleVectorIndividual target, DoubleVectorIndividual child,
			int thread) {
		if (Cr == CR_UNSPECIFIED)
			state.output.warnOnce("Differential Evolution Parameter cr unspecified.  Assuming cr = 0.5");

		// first, hold one value in abeyance
		int index = state.random[thread].nextInt(child.genome.length);
		double val = child.genome[index];

		// do the crossover
		for (int i = 0; i < child.genome.length; i++) {
			if (state.random[thread].nextDouble() < Cr)
				child.genome[i] = target.genome[i];
		}

		// reset the one value so it's not just a duplicate copy
		child.genome[index] = val;

		return child;
	}

}
