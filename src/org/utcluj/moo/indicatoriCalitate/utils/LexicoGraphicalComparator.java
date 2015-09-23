package org.utcluj.moo.indicatoriCalitate.utils;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class LexicoGraphicalComparator implements Comparator{

	/** 
	   * Se compara cele doua obiecte
	   * @param o1 An object that reference a double[]
	   * @param o2 An object that reference a double[]
	   * @return -1 if o1 < o1, 1 if o1 > o2 or 0 in other case.
	   */
	public int compare(Object o1, Object o2) {
		//Cast to double [] o1 and o2.
	    double [] pointOne = (double [])o1;
	    double [] pointTwo = (double [])o2;
	    
	    //To determine the first i, that pointOne[i] != pointTwo[i];
	    int index = 0;
	    while ((index < pointOne.length) && (index < pointTwo.length) && 
	           pointOne[index] == pointTwo[index]) {
	      index++;
	    }
	    if ((index >= pointOne.length) || (index >= pointTwo.length )) {
	      return 0;
	    } else if (pointOne[index] < pointTwo[index]) {
	      return -1;
	    } else if (pointOne[index] > pointTwo[index]) {
	      return 1;
	    }
		return 0;
	}
}
