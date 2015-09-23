package org.utcluj.moo.algoritmi.posde;

import java.util.Random;
import java.util.Vector;

import ec.EvolutionState;
import ec.Individual;
import ec.vector.DoubleVectorIndividual;

/**
 * Arhiva care va contine elementele nedominate. Voi stoca un vector de
 * Individuals, si o matrice[][] care va contine obiectivele. Ca si param iau
 * dim arhivei, metoda (implicit nr de obiective), daca sa maximizez sau nu
 * obiectivele.
 * 
 * @author Mihai
 * 
 */
@SuppressWarnings("unused")
public class Arhiva {

	private int dimensiune;

	private boolean maximize = false;
	private Vector<Individual> elemente;
	private int nrObiective = 0;

	/**
	 * Constructorul arhivei
	 * 
	 * @param dimensiune
	 *            - dimensiunea arhivei
	 * @param nrOb
	 *            - numarul obiectivelor
	 * @param maximize
	 *            - tipul problemei: true -> maximizare, false -> minimizare
	 */
	public Arhiva(int dimensiune, int nrOb, boolean maximize) {
		super();

		this.dimensiune = dimensiune;
		this.nrObiective = nrOb;
		this.maximize = maximize;

		elemente = new Vector<Individual>();
	}

	public Individual getIndivid(int index) {
		return elemente.elementAt(index);
	}

	public Vector<Individual> getArhiva() {
		return elemente;
	}

	public int nrElemInArhiva() {
		elemente.trimToSize();
		return elemente.size();
	}
}
