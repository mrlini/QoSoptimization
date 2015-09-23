package org.utcluj.moo.algoritmi.gde3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleStatistics;
import ec.util.QuickSort;
import ec.util.SortComparator;

public class Statistici extends SimpleStatistics{

	private static final long serialVersionUID = 9159470035709151661L;
	private BufferedWriter bw1;
	double[][] frontulMeu;

	public void postEvaluationStatistics(final EvolutionState state){
		super.postEvaluationStatistics(state);
        
        // for now we just print the best fitness per subpopulation.
        Individual[] best_i = new Individual[state.population.subpops.length];  // quiets compiler complaints
        for(int x=0;x<state.population.subpops.length;x++)
            {
            best_i[x] = state.population.subpops[x].individuals[0];
            for(int y=1;y<state.population.subpops[x].individuals.length;y++)
            	try{
                if (state.population.subpops[x].individuals[y].fitness.betterThan(best_i[x].fitness))
                    best_i[x] = state.population.subpops[x].individuals[y];
            	}catch(NullPointerException e){
            		e.printStackTrace();
            	}
            // now test to see if it's the new best_of_run
            if (best_of_run[x]==null || best_i[x].fitness.betterThan(best_of_run[x].fitness))
                best_of_run[x] = (Individual)(best_i[x].clone());
            }
        
        // print the best-of-generation individual
        state.output.println("\nGeneration: " + state.generation,statisticslog);
        state.output.println("Best Individual:",statisticslog);
        for(int x=0;x<state.population.subpops.length;x++)
            {
            state.output.println("Subpopulation " + x + ":",statisticslog);
            best_i[x].printIndividualForHumans(state,statisticslog);
            if (best_i[x].evaluated)
                state.output.message("Subpop " + x + " best fitness of generation: " + best_i[x].fitness.fitnessToStringForHumans());
            else
                state.output.message("Subpop " + x + " not evaluated.");  // can happen if we're doing sequential coevolution
            }	
	}
	
	
	@SuppressWarnings({ "rawtypes", "static-access" })
	public void finalStatistics(final EvolutionState state, final int result) {
		for (int s = 0; s < state.population.subpops.length; s++) {
			MultiObjectiveFitness fitness = (MultiObjectiveFitness) (state.population.subpops[s].individuals[0].fitness);
			// construiesc frontul
			ArrayList front = fitness.partitionIntoParetoFront(
					state.population.subpops[s].individuals, null, null);
			// fac o sortare dupa obiectivul 0
			Object[] frontSortat = front.toArray();
			QuickSort.qsort(frontSortat, new SortComparator() {
				@Override
				public boolean lt(Object a, Object b) {
					return (((MultiObjectiveFitness) (((Individual) a).fitness))
							.getObjective(0) < (((MultiObjectiveFitness) ((Individual) b).fitness))
							.getObjective(0));
				}

				@Override
				public boolean gt(Object a, Object b) {
					return (((MultiObjectiveFitness) (((Individual) a).fitness))
							.getObjective(0) > ((MultiObjectiveFitness) (((Individual) b).fitness))
							.getObjective(0));
				}
			});
			String linie;
			frontulMeu = new double[frontSortat.length][3];
			
			try {
				bw1 = new BufferedWriter(new FileWriter(
						"src/org/utcluj/moo/gde3/gde3DTLZ.txt", false));
				for (int i = 0; i < frontSortat.length; i++) {
					Individual ind = (Individual) (frontSortat[i]);
					MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
					float[] objectives = mof.getObjectives();
					linie = "";
					for (int f = 0; f < objectives.length; f++) {
						linie += (objectives[f] + "\t");
					}
					bw1.write(linie+"\n");
				}
				bw1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
