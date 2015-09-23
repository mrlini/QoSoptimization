package org.utcluj.moo.indicatoriCalitate.utils;

import java.util.Comparator;

/**
 * Clasa implementeaza interfata <code>Comparator</code>. Este folosita la
 * compararea pct date ca si<code>double</code>. Aceste pct sunt comparate pe
 * baza unui index
 * 
 * @author Mihai
 * 
 */
@SuppressWarnings("rawtypes")
public class ValueComparator implements Comparator {

	private int index;

	/**
	 * Constructor. Creates a new instance of ValueComparator
	 */
	public ValueComparator(int index) {
		this.index = index;
	}

	/**
	 * Compara obiectele ob1 si ob2
	 * 
	 * @param o1
	 *            obiect referinta catre un double[]
	 * @param o2
	 *            obiect referinta catre un double[]
	 * @return -1 daca o1 < o1, 1 daca o1 > o2 sau 0 in alte cazuri
	 */
	public int compare(Object o1, Object o2) {
		double[] pointOne = (double[]) o1;
		double[] pointTwo = (double[]) o2;

		if (pointOne[index] < pointTwo[index]) {
			return -1;
		} else if (pointOne[index] > pointTwo[index]) {
			return 1;
		} else {
			return 0;
		}
	}
}
