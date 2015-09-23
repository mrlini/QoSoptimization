package org.utcluj.moo.algoritmi.moead;

import java.util.Vector;

import resonanceSearch.problemeTest.FitnessCalc;
import ec.Individual;
import ec.vector.DoubleVectorIndividual;

/**
 * External population used by MOEA/D to store non-dominated solutions
 * 
 * @author mihai
 *
 */
public class EpMOEAD {

	private int dimensiune;
	
	private int metoda;
	private int k_WFG;
	private boolean maximize = false;
	private Vector<Individual> elemente;
	private Vector<double[]> fitnessElemente;

	/**
	 * Constructorul arhivei
	 * 
	 * @param dimensiune
	 *            - dimensiunea arhivei
	 * @param metoda
	 *            - metoda optimizata
	 * @param maximize
	 *            - tipul problemei: true -> maximizare, false -> minimizare
	 */
	public EpMOEAD(int dimensiune, int metoda, boolean maximize, int k_WFG) {
		super();

		this.dimensiune = dimensiune;
		this.metoda = metoda;
		this.maximize = maximize;
		this.k_WFG = k_WFG;

		elemente = new Vector<Individual>();
		fitnessElemente = new Vector<double[]>();
	}

	public Individual getIndivid(int index) {
		return elemente.elementAt(index);
	}
	
	public double[] getFitnessElement(int index) {
		return fitnessElemente.elementAt(index);
	}
	
	public Vector<Individual> getArhiva(){
		return elemente;
	}
	
	public Vector<double[]> getFitnessArhiva() {
		return fitnessElemente;
	}
	
	public void resetArhiva() {
		fitnessElemente.removeAllElements();
		elemente.removeAllElements();
	}

	public int getNrElemInArhiva(){
		elemente.trimToSize();
		return elemente.size();
	}
	
	public int getNrElemFitnessArhiva(){
		fitnessElemente.trimToSize();
		return fitnessElemente.size();
	}
	
	/**
	 * Adaug un individ in arhiva
	 * @param ind
	 *            - Individul adaugat in arhiva
	 */
	public void addIndivid(Individual ind, int nrObj){
		boolean dominare = false;
		boolean adauga = false;
		boolean vectoriIdentici = false;
		int i = 0;
		DoubleVectorIndividual dind = (DoubleVectorIndividual) ind;
		double[] fitness = FitnessCalc.getInstance().eval(metoda, nrObj, k_WFG, dind.genome);
		
		if(elemente.size() == 0){
			elemente.add(ind);
			fitnessElemente.add(fitness);
		}
		else{
			dominare = false;
			adauga = false;
			
			for(i=0;i<elemente.size();i++){
				if(dominaSlabPareto(fitnessElemente.elementAt(i), fitness, maximize)){
					dominare = true;
					break;
				}else if(dominaSlabPareto(fitness, fitnessElemente.elementAt(i), maximize)){
					adauga = true;
					elemente.removeElementAt(i);
					fitnessElemente.removeElementAt(i);
					dominare = true;
				}
			}
			if(adauga){
				elemente.add(ind);
				fitnessElemente.add(fitness);
			}
			if(!dominare){
				//verific daca vectorii sunt identici
				vectoriIdentici = false;
				for(i=0;i<elemente.size();i++){
					vectoriIdentici = false;
					if(compare(fitnessElemente.elementAt(i), fitness)){
						vectoriIdentici = true;
						break;
					}
				}
				if(!vectoriIdentici && (elemente.size()<dimensiune)){
					elemente.add(ind);
					fitnessElemente.add(fitness);
				}
			}
		}
	}
	
	private boolean compare(double[] a, double[] b) {
		boolean vectoriEgali = false;
		
		if(a.length != b.length)
			System.err.println("Dimensiuni diferite la compararea vectorilor");
		
		for (int x = 0; x < a.length; x++) {
			if (a[x] == b[x])
				vectoriEgali = true;
			else if (a[x] != b[x])
				return false;
		}
		return vectoriEgali;
	}
	
	/**
	 * Returnez true daca a domina b. Regula: daca a e mai bun in unul sau mai
	 * multe criterii si in rest sunt egale returnez true altfel returnez false.
	 * 
	 * <p>
	 * ATENTIE metoda nu e comutativa: daca a nu domina pe b asta nu inseamna ca
	 * b domina pe a
	 * 
	 * @param a
	 * @param b
	 * @param maximizare
	 * @return
	 */
	public static boolean dominaSlabPareto(double[] a, double[] b,
			boolean maximizare) {
		boolean a_bate_b = false;

		if (a.length != b.length)
			throw new RuntimeException(
					"Se vrea compararea a 2 vectori; vectorii au un nr diferit de obiective."+a.length+"!="+b.length);

		if (maximizare) {
			for (int x = 0; x < a.length; x++) {
				if (a[x] > b[x])
					a_bate_b = true;
				else if (a[x] < b[x])
					return false;
			}
		} else {
			for (int x = 0; x < a.length; x++) {
				if (a[x] < b[x])
					a_bate_b = true;
				else if (a[x] > b[x])
					return false;
			}
		}
		return a_bate_b;
	}
}
