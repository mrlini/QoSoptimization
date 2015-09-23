package org.utcluj.moo.algoritmi.gde3;

import java.util.ArrayList;
import java.util.Vector;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleEvaluator;
import ec.util.SortComparator;

public class GDE3Evaluator extends SimpleEvaluator {

	private static final long serialVersionUID = -8100184754951859962L;
	
	private Vector<Individual> tempPop;
	
	public void evaluatePopulation(EvolutionState state) {
		super.evaluatePopulation(state);

		tempPop = new Vector<Individual>();
		
		if (state.breeder instanceof GDE3Breeder) {
			Population previousPopulation = ((GDE3Breeder) (state.breeder)).previousPopulation;
			if (previousPopulation != null) {
				if (previousPopulation.subpops.length != state.population.subpops.length)
					state.output.fatal("GDE3Evaluator requires that the population have the same number of subpopulations every generation.");
				for (int i = 0; i < previousPopulation.subpops.length; i++) {
					if (state.population.subpops[i].individuals.length != previousPopulation.subpops[i].individuals.length)
						state.output.fatal("GDE3Evaluator requires that subpopulation "	+ i + " should have the same number of individuals in all generations.");
					Individual[] newp = buildNewPop(state.population.subpops[i], previousPopulation.subpops[i]);
					//TODO sa copiez toate elementele din vectorul tempPop in state.pop.subp[i]
					int c = 0;
					for(Individual ind: newp){
						state.population.subpops[i].individuals[c] = ind;
						c++;
					}

				}
			}
		} else
			state.output.fatal("GDE3Evaluator requires GDE3Breeder to be the breeder.");
	}

	/**
	 * 
	 * @param newPop contine copii
	 * @param oldPop contine parintii
	 * @return 
	 */
	private Individual[] buildNewPop(Subpopulation newPop, Subpopulation oldPop) {
		Individual[] newp = new Individual[newPop.individuals.length];
		for(int i=0; i<newPop.individuals.length; i++){
			//ver daca ind nou domina Pareto parintele
			//daca da il adaug in tempPop
			//daca parintele domina copilul il adaug in tempPop
			//daca sunt indiferenti ii adaug pe amandoi in tempPop
			if(((MultiObjectiveFitness)newPop.individuals[i].fitness).paretoDominates((MultiObjectiveFitness)oldPop.individuals[i].fitness))
				tempPop.add(newPop.individuals[i]);
			else if(((MultiObjectiveFitness)oldPop.individuals[i].fitness).paretoDominates((MultiObjectiveFitness)newPop.individuals[i].fitness))
				tempPop.add(oldPop.individuals[i]);
			else if(((MultiObjectiveFitness)newPop.individuals[i].fitness).equivalentTo((MultiObjectiveFitness)oldPop.individuals[i].fitness)){
				//adaug ambii indivizi in tempPop
				tempPop.add(newPop.individuals[i]);
				tempPop.add(oldPop.individuals[i]);
			}
		}
		//dupa aceea reduc populatia noua pe baza rank si crowding distance si copiez tempPop in newPop
		tempPop.trimToSize();
		if(tempPop.size() > newPop.individuals.length)
			newp = reducTempPop(tempPop, newPop.individuals.length);
		else{
			int c = 0;
			for(Individual in: tempPop){
				newp[c] = in;
				c++;
			}
		}
		
		return newp;
	}

	/**
	 * 
	 * @param tempPop vector ce contine noua pop
	 * @param dim dimensiunea care tb sa o aiba noua pop
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Individual[] reducTempPop(Vector<Individual> tempPop, int dim) {
		Individual[] dummy = new Individual[0];
		ArrayList ranks = asigneazaRank(tempPop);
		
		ArrayList newSubpop = new ArrayList();
		
		for(int i=0; i<ranks.size(); i++){
			Individual[] rank = (Individual[])((ArrayList)(ranks.get(i))).toArray(dummy);
            asigneazaDist(rank);
            if(rank.length+newSubpop.size() >= dim){
            	//sortez rangul dupa dist
            	ec.util.QuickSort.qsort(rank, new SortComparator()
                {
                public boolean lt(Object a, Object b)
                    {
                    Individual i1 = (Individual) a;
                    Individual i2 = (Individual) b;
                    return (((GDE3Fitness) i1.fitness).sparsity > ((GDE3Fitness) i2.fitness).sparsity);
                    }

                public boolean gt(Object a, Object b)
                    {
                    Individual i1 = (Individual) a;
                    Individual i2 = (Individual) b;
                    return (((GDE3Fitness) i1.fitness).sparsity < ((GDE3Fitness) i2.fitness).sparsity);
                    }
                });

            // pun cei mai m departati indivizi in newSubpop
            int m = dim - newSubpop.size();
            for(int j = 0 ; j < m; j++)
                newSubpop.add(rank[j]);
                            
            // si opresc for-ul
            break;
            }else{
            	//pun toti indivizii in newSubpop
            	for(int j = 0 ; j < rank.length; j++)
                    newSubpop.add(rank[j]);
            }
		}
		
		Individual[] p = (Individual[])(newSubpop.toArray(dummy));
		
        return p;
	}

	@SuppressWarnings("rawtypes")
	private ArrayList asigneazaRank(Vector<Individual> tempPop2) {
		Individual[] inds = new Individual[tempPop2.size()];
		int i=0;
		for(Individual ind: tempPop2){
			inds[i] = ind;
			i++;
		}
		
		ArrayList frontDupaRang = MultiObjectiveFitness.partitionIntoRanks(inds);
		
		for(int rank = 0; rank < frontDupaRang.size(); rank++){
			ArrayList front = (ArrayList)(frontDupaRang.get(rank));
			for(int ind = 0; ind < front.size(); ind++)
				((GDE3Fitness)(((Individual)(front.get(ind))).fitness)).rank = rank;
        }
		return frontDupaRang;
	}
	
	private void asigneazaDist(Individual[] front) {
		int nrObj = ((GDE3Fitness) front[0].fitness).getObjectives().length;
		
		for (int i = 0; i < front.length; i++)
            ((GDE3Fitness) front[i].fitness).sparsity = 0;

		for (int i = 0; i < nrObj; i++)
        {
        final int o = i;
        // 1. sortez frontul dupa fiecare obiectiv
        // 2. suma dist manhattan a vecinilor unui individ pt fiecare obiectiv
        ec.util.QuickSort.qsort(front, new SortComparator(){
            public boolean lt(Object a, Object b)
                {
                Individual i1 = (Individual) a;
                Individual i2 = (Individual) b;
                return (((GDE3Fitness) i1.fitness).getObjective(o) < ((GDE3Fitness) i2.fitness).getObjective(o));
                }

            public boolean gt(Object a, Object b)
                {
                Individual i1 = (Individual) a;
                Individual i2 = (Individual) b;
                return (((GDE3Fitness) i1.fitness).getObjective(o) > ((GDE3Fitness) i2.fitness).getObjective(o));
                }
            });

        // Calculez si asignez distanta
        // primul si ultimul individ sunt cei mai departati
        ((GDE3Fitness) front[0].fitness).sparsity = Double.POSITIVE_INFINITY;
        ((GDE3Fitness) front[front.length - 1].fitness).sparsity = Double.POSITIVE_INFINITY;
        for (int j = 1; j < front.length - 1; j++)
            {
        	GDE3Fitness f_j = (GDE3Fitness) (front[j].fitness);
        	GDE3Fitness f_jplus1 = (GDE3Fitness) (front[j+1].fitness);
        	GDE3Fitness f_jminus1 = (GDE3Fitness) (front[j-1].fitness);
                            
            // stochez distanta in GDE3Fitness.sparsity
            f_j.sparsity += (f_jplus1.getObjective(o) - f_jminus1.getObjective(o)) / (f_j.maxObjective[o] - f_j.minObjective[o]);
            }
        }
	}
}
