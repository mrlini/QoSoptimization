package org.utcluj.moo.algoritmi.posde;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.LineNumberReader;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Code;

/**
 * Subclasa a MultiObjectiveFitness, adaug o noua metrica (distanta fata de
 * vecini) utilizata in a mentine diversitatea populatiei (arhivei)
 * 
 * <p>
 * conform: C.S.Chang
 * "Pareto-optimal set based MO tuning of fuzzy automatic train operation for mass transit system"
 * 
 * @author Mihai
 * 
 */
public class PosdeFitness extends MultiObjectiveFitness {

	private static final long serialVersionUID = -5963260608285391128L;

	public static final String POSDE_DIST = "Distanta: ";

	public String[] getAuxilliaryFitnessNames() {
		return new String[] { "Distanta" };
	}

	public double[] getAuxilliaryFitnessValues() {
		return new double[] { distanta };
	}

	/** Distanta intre solutii (o distanta mai mare e mai buna) */
	public double distanta;

	public String fitnessToString() {
		return super.fitnessToString() + "\n" + POSDE_DIST
				+ Code.encode(distanta);
	}

	public String fitnessToStringForHumans() {
		return super.fitnessToStringForHumans() + "\n" + "D=" + distanta;
	}

	public void readFitness(final EvolutionState state,
			final LineNumberReader reader) throws IOException {
		super.readFitness(state, reader);
		distanta = Code.readDoubleWithPreamble(POSDE_DIST, state, reader);
	}

	public void writeFitness(final EvolutionState state,
			final DataOutput dataOutput) throws IOException {
		super.writeFitness(state, dataOutput);
		dataOutput.writeDouble(distanta);
	}

	public void readFitness(final EvolutionState state,
			final DataInput dataInput) throws IOException {
		super.readFitness(state, dataInput);
		distanta = dataInput.readDouble();
	}

	public boolean equivalentTo(Fitness _fitness) {
		PosdeFitness other = (PosdeFitness) _fitness;
		return (distanta == other.distanta);
	}

	/**
	 * Specific criteriul pentru selectia indivizilor. O distanta mai mare
	 * reprezinta o valoare mai buna
	 */
	public boolean betterThan(Fitness _fitness) {
		PosdeFitness other = (PosdeFitness) _fitness;

		return (distanta > other.distanta);
	}
}
