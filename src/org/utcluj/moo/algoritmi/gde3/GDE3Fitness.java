package org.utcluj.moo.algoritmi.gde3;

import java.io.IOException;
import java.io.LineNumberReader;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Code;

public class GDE3Fitness extends MultiObjectiveFitness {

	private static final long serialVersionUID = -2977520124154774509L;

	public static final String GDE3_RANK_PREAMBLE = "Rank: ";
	public static final String GDE3_SPARSITY_PREAMBLE = "Sparsity: ";

	public String[] getAuxilliaryFitnessNames() {
		return new String[] { "Rank", "Sparsity" };
	}

	public double[] getAuxilliaryFitnessValues() {
		return new double[] { rank, sparsity };
	}

	/** rangul frontului pareto(val mici sunt bune) */
	public int rank;

	/** distanta intre sol (val mari sunt bune) */
	public double sparsity;

	public String fitnessToString() {
		return super.fitnessToString() + "\n" + GDE3_RANK_PREAMBLE
				+ Code.encode(rank) + "\n" + GDE3_SPARSITY_PREAMBLE
				+ Code.encode(sparsity);
	}

	public String fitnessToStringForHumans() {
		return super.fitnessToStringForHumans() + "\n" + "R=" + rank + " S="
				+ sparsity;
	}

	public void readFitness(final EvolutionState state,
			final LineNumberReader reader) throws IOException {
		super.readFitness(state, reader);
		rank = Code.readIntegerWithPreamble(GDE3_RANK_PREAMBLE, state, reader);
		sparsity = Code.readDoubleWithPreamble(GDE3_SPARSITY_PREAMBLE, state,
				reader);
	}

//	public void writeFitness(final EvolutionState state,
//			final DataOutput dataOutput) throws IOException {
//		super.writeFitness(state, dataOutput);
//		dataOutput.writeInt(rank);
//		dataOutput.writeDouble(sparsity);
//		writeTrials(state, dataOutput);
//	}
//
//	public void readFitness(final EvolutionState state,
//			final DataInput dataInput) throws IOException {
//		super.readFitness(state, dataInput);
//		rank = dataInput.readInt();
//		sparsity = dataInput.readDouble();
//		readTrials(state, dataInput);
//	}

	public boolean equivalentTo(Fitness _fitness) {
		GDE3Fitness other = (GDE3Fitness) _fitness;
		return (rank == ((GDE3Fitness) _fitness).rank)
				&& (sparsity == other.sparsity);
	}

	/**
	 * We specify the tournament selection criteria, Rank (lower values are
	 * better) and Sparsity (higher values are better)
	 */
	public boolean betterThan(Fitness _fitness) {
		GDE3Fitness other = (GDE3Fitness) _fitness;
		// Rank should always be minimized.
		if (rank < ((GDE3Fitness) _fitness).rank)
			return true;
		else if (rank > ((GDE3Fitness) _fitness).rank)
			return false;

		// otherwise try sparsity
		return (sparsity > other.sparsity);
	}

}
