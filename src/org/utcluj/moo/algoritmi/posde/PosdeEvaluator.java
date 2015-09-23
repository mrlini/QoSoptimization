package org.utcluj.moo.algoritmi.posde;

import java.util.Vector;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleEvaluator;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

public class PosdeEvaluator extends SimpleEvaluator {

	private static final long serialVersionUID = -7321697663514275676L;

	/**
	 * Arhiva in care stochez setul optim
	 */
	public  static Vector<Individual> arhiva;
	public int dimArh = 0;
	public double lamda = 0;
	
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		
		lamda = state.parameters.getDouble(new Parameter("lamda"), null);
		dimArh = state.parameters.getInt(new Parameter("dimEP"), null);
		if(arhiva == null)
			arhiva = new Vector<Individual>();
	}
	
	
	public void evaluatePopulation(EvolutionState state) {
		super.evaluatePopulation(state);

		//realizez "process 2 din articol", calculez distanta intre sol
//		for(int i=0;i<state.population.subpops.length;i++){
//			alterFitness(state, i);
//		}
		
		if (state.breeder instanceof PosdeBreeder) {
			Population previousPopulation = ((PosdeBreeder)(state.breeder)).previousPopulation;
			if(previousPopulation != null){
				if( previousPopulation.subpops.length != state.population.subpops.length )
                    state.output.fatal( "PosdeEvaluator requires that the population have the same number of subpopulations every generation.");
				for( int i = 0 ; i < previousPopulation.subpops.length ; i++ ){
					if( state.population.subpops[i].individuals.length != previousPopulation.subpops[i].individuals.length )
                        state.output.fatal( "PosdeEvaluator requires that subpopulation " + i + " should have the same number of individuals in all generations." );
					
//					alterFitness(state, i);
					
					for( int j = 0 ; j < state.population.subpops[i].individuals.length ; j++ ){
						if(((PosdeFitness)previousPopulation.subpops[i].individuals[j].fitness).paretoDominates((MultiObjectiveFitness)state.population.subpops[i].individuals[j].fitness))
							state.population.subpops[i].individuals[j] = previousPopulation.subpops[i].individuals[j];
						
						//adaug noul elem in arhiva
						adaugaElementArhiva(arhiva, state.population.subpops[i].individuals[j], dimArh);
					}
				}
			}
		}
	}
	
	
	@SuppressWarnings("unused")
	private void alterFitness(EvolutionState state, int subPop) {
		double sum = 0, aux = 0;
		//pt fiecare ind din pop alterez fitness-ul conform distantei intre ob
		for(int i=0; i<state.population.subpops[subPop].individuals.length;i++){
			float[] obj = ((PosdeFitness) state.population.subpops[subPop].individuals[i].fitness).getObjectives();
			
			//det sum dist dintre ind si elem arhivei din ec 2
			sum = calcDist(state.population.subpops[subPop].individuals[i], state.population.subpops[subPop]);
			for(int j=0;j<obj.length;j++){
				aux = obj[j];
				obj[j] = (float)(aux / sum);
			}
			
			((PosdeFitness) state.population.subpops[subPop].individuals[i].fitness).setObjectives(state, obj);
		}
	}
	
	/**
	 * 
	 * @param individual
	 * @param subpopulation
	 * @return
	 */
	private double calcDist(Individual individual, Subpopulation subpopulation) {
		PosdeFitness fitInf = (PosdeFitness) individual.fitness;
		float[] objInd = fitInf.getObjectives();
		double sum = 0, dist = 0, aux;
		for(int i=0;i<subpopulation.individuals.length;i++){
			PosdeFitness fit_i = (PosdeFitness) subpopulation.individuals[i].fitness;
			float[] obj_i = fit_i.getObjectives();
			dist = 0;
			aux = 0;
			for(int j=0;j<objInd.length;j++)
				dist += Math.pow(objInd[j] - obj_i[j], 2);
			aux = Math.sqrt(dist);
			
			if(aux<lamda)
				sum += 1 - aux / lamda;
		}
		return sum;
	}


	/**
	 * 
	 * @param arhiva2
	 * @param individual
	 * @param dimArh2
	 */
	private void adaugaElementArhiva(Vector<Individual> arhiva, Individual individual, int dimArh) {
		arhiva.trimToSize();
		boolean adauga = false, dominare = false, vectoriIdentici = false;
		if(arhiva.size() == 0)
			arhiva.add(individual);
		else{
//			for(int i=0;i<arhiva.size();i++){
//				if (((PosdeFitness)individual.fitness).paretoDominates((MultiObjectiveFitness)arhiva.elementAt(i).fitness)) {
//				arhiva.removeElementAt(i);
//				domina = true;
//				}
//			}
//			for(int i=0;i<arhiva.size();i++){
//				if (((PosdeFitness)arhiva.elementAt(i).fitness).paretoDominates((MultiObjectiveFitness)individual.fitness)) {
//					dominat = true;
//					break;
//				}
//			}
//			if(domina){
//				arhiva.add(individual);
//			}else if(arhiva.size()<dimArh && !dominat)
//				arhiva.add(individual);
			adauga = false;
			dominare = false;
			for(int i=0;i<arhiva.size();i++){
				if (((PosdeFitness)arhiva.elementAt(i).fitness).paretoDominates((MultiObjectiveFitness)individual.fitness)) {
//					dominat = true;
					dominare = true;
					break;
				}else if (((PosdeFitness)individual.fitness).paretoDominates((MultiObjectiveFitness)arhiva.elementAt(i).fitness)) {
					arhiva.removeElementAt(i);
					adauga = true;
//					domina = true;
					dominare = true;

				}
			}
			if(adauga){
				arhiva.trimToSize();
				if(arhiva.size() <= dimArh)
					arhiva.add(individual);
			}
			if(!dominare){
				vectoriIdentici = false;
				DoubleVectorIndividual d1 = (DoubleVectorIndividual) individual;
				for(int i=0;i<arhiva.size();i++){
					DoubleVectorIndividual d2 = (DoubleVectorIndividual) arhiva.elementAt(i);
					if(compare(d1.genome, d2.genome)){
						vectoriIdentici = true;
						break;
					}
				}
				if(!vectoriIdentici)
					arhiva.add(individual);
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
}