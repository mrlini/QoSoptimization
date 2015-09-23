package org.utcluj.moo.algoritmi.posde;

import ec.Breeder;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import ec.vector.FloatVectorSpecies;

public class PosdeBreeder extends Breeder {

	private static final long serialVersionUID = -7895102627353884370L;

	public static final double CR_UNSPECIFIED = -1;
	public double F = 0.0;
	public double Cr = CR_UNSPECIFIED;

	public static final String P_F = "f";
	public static final String P_Cr = "cr";

	/**
	 * retin pop veche pt a putea face comparatii
	 */
	public Population previousPopulation = null;
	
	/**
	 * retin si pop noua pt a vedea ce individ introduc in arhiva
	 */
	public Population newPopulation = null;
	
	@Override
	public void setup(EvolutionState state, Parameter base) {
		if (!state.parameters.exists(base.push(P_Cr), null))
			Cr = CR_UNSPECIFIED;
		else {
			Cr = state.parameters.getDouble(base.push(P_Cr), null, 0.0);
			if (Cr < 0.0 || Cr > 1.0)
				state.output
						.fatal("Parameter not found, or its value is outside of [0.0,1.0].",
								base.push(P_Cr), null);
		}

		F = state.parameters.getDouble(base.push(P_F), null, 0.0);
		if (F < 0.0 || F > 1.0)
			state.output
					.fatal("Parameter not found, or its value is outside of [0.0,1.0].",
							base.push(P_F), null);
	}

	@Override
	public Population breedPopulation(EvolutionState state) {
		// ver daca fol clasa evaluator corecta
		if (!(state.evaluator instanceof PosdeEvaluator))
			state.output.warnOnce("PosdeEvaluator not used, but PosdeBreeder used.  This is almost certainly wrong.");

		// pop noua
		Population newpop = (Population) state.population.emptyClone();

		// breed the children
		for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
			if (state.population.subpops[subpop].individuals.length < 4) 
				state.output.fatal("Subpopulation "	+ subpop + " has fewer than four individuals, and so cannot be used with DEBreeder.");

			Individual[] inds = newpop.subpops[subpop].individuals;
			for (int i = 0; i < inds.length; i++) {
				newpop.subpops[subpop].individuals[i] = createIndividual(state,	subpop, i, 0); // unthreaded for now
			}
		}

		previousPopulation = state.population;
		newPopulation = newpop;
		
		return newpop;
	}


	public boolean valid(DoubleVectorIndividual ind) {
		FloatVectorSpecies species = (FloatVectorSpecies) (ind.species);
		return (!(species.mutationIsBounded && !ind.isInRange()));
	}

	public DoubleVectorIndividual createIndividual(EvolutionState state, int subpop, int index, int thread) {
		Individual[] inds = state.population.subpops[subpop].individuals;

		DoubleVectorIndividual v = (DoubleVectorIndividual) (inds[index].clone());
		do {
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

	// Crossover DE
	public DoubleVectorIndividual crossover(EvolutionState state, DoubleVectorIndividual target, DoubleVectorIndividual child, int thread) {
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
