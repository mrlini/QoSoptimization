package org.utcluj.moo.algoritmi.moead;

import org.utcluj.moo.utils.UtilsMOO;

import ec.EvolutionState;
import ec.Population;
import ec.simple.SimpleEvaluator;
import ec.util.Parameter;

/**
 * Implements MOEA/D evaluator based on DE
 * @author mihai
 *
 */
@SuppressWarnings("serial")
public class EvaluatorMoeadQoS extends SimpleEvaluator{

	private String activities = "";
	private int alternative = 0;
	
	public void setup(final EvolutionState state, final Parameter base){
		super.setup(state, base);
		
		activities = state.parameters.getString(new Parameter(UtilsMOO.ACTIVITIES), null);
		alternative = state.parameters.getInt(new Parameter(UtilsMOO.ALTERNATIVE), null);
	}
    
	
	public void evaluatePopulation(EvolutionState state){
		super.evaluatePopulation(state);
		
		if(state.breeder instanceof BreederMoeadQoS){
			Population previousPop = ((BreederMoeadQoS)(state.breeder)).previousPopulation;
			int T = ((BreederMoeadQoS)state.breeder).T;
			int[][] neighbours = ((BreederMoeadQoS)state.breeder).neighbours;
			
			if(previousPop != null){
				if( previousPop.subpops.length != state.population.subpops.length )
                    state.output.fatal( "EvaluatorMOEAD requires that the population have the same number of subpopulations every generation.");
                for( int i = 0 ; i < previousPop.subpops.length ; i++ ){
                    if( state.population.subpops[i].individuals.length != previousPop.subpops[i].individuals.length )
                        state.output.fatal( "DEEvaluator requires that subpopulation " + i + " should have the same number of individuals in all generations." );
                    for( int j = 0 ; j < state.population.subpops[i].individuals.length ; j++ ){
                    	//compar cu parintele
//                    	float previousF = previousPop.subpops[i].individuals[j].fitness.fitness();
//                    	float curentF = state.population.subpops[i].individuals[j].fitness.fitness();
//                        if( previousPop.subpops[i].individuals[j].fitness.betterThan( state.population.subpops[i].individuals[j].fitness ) )
//                        	state.population.subpops[i].individuals[j] = previousPop.subpops[i].individuals[j];
                    	for(int k=0;k<T;k++){
                    		double curentF = UtilsMoeadQoS.getInstance().computeTCHwithSpecificWeightQoS(neighbours[j][k], state.population.subpops[i].individuals[j], activities, alternative);
                    		double neighbourF = UtilsMoeadQoS.getInstance().computeTCHwithSpecificWeightQoS(neighbours[j][k], previousPop.subpops[i].individuals[neighbours[j][k]], activities, alternative);
                    		if (curentF < neighbourF) {
								previousPop.subpops[i].individuals[neighbours[j][k]] = state.population.subpops[i].individuals[j];
							}
                    	}
                    }
                    //pun old pop in newPop
                    for(int j=0;j<state.population.subpops[i].individuals.length;j++)
                    	state.population.subpops[i].individuals[j] = previousPop.subpops[i].individuals[j]; 
                    	
                }
			}
		}
	}
}
