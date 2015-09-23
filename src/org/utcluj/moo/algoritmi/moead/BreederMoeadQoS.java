package org.utcluj.moo.algoritmi.moead;

import org.utcluj.moo.utils.UtilsMOO;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.de.DEBreeder;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

/**
 * Implements MOEA/D breeder based on DE/rand/1/bin for QoS web based service composition
 * 
 * @author mihai
 *
 */
@SuppressWarnings("serial")
public class BreederMoeadQoS extends DEBreeder {

	public static final String P_T = "t";
	
	public int [][] neighbours = null;
	
	public double[][] ponderi = null;

	public int T = 0;
//	private int nrObj = 0;
	private int N = 0;

	public void setup(final EvolutionState state, final Parameter base) {
		//take Cr
		if (!state.parameters.exists(base.push(P_Cr), null)) { 
			Cr = CR_UNSPECIFIED;
		}else {
			Cr = state.parameters.getDouble(base.push(P_Cr), null, 0.0);
			if (Cr < 0.0 || Cr > 1.0)
				state.output .fatal("Parameter not found, or its value is outside of [0.0,1.0].", base.push(P_Cr), null);
		}
		//take F
		F = state.parameters.getDouble(base.push(P_F),null,0.0);
        if ( F < 0.0 || F > 1.0 ){
            state.output.fatal( "Parameter not found, or its value is outside of [0.0,1.0].", base.push(P_F), null );
        }
		
        //take other params
        T = state.parameters.getInt(base.push(P_T), null);
//        nrObj = state.parameters.getInt(new Parameter("nrObj"), null);
        N = state.parameters.getInt(new Parameter("pop.subpop.0.size"), null);
        
        //initialize neighbours
        if(neighbours == null){
        	initializeNeighbours();
        }
	}
	
	public Population breedPopulation(EvolutionState state) {
		if (!(state.evaluator instanceof EvaluatorMoeadQoS)) {
			state.output.warnOnce("EvalatorMOEAD not used, but BreederMOEAD used, this is a mistake");
		}
		
		// create the new population
        Population newpop = (Population) state.population.emptyClone();

        // breed the children
        for( int subpop = 0 ; subpop < state.population.subpops.length ; subpop++ ) {
            if (state.population.subpops[subpop].individuals.length < 4)  // Magic number, sorry.  createIndividual() requires at least 4 individuals in the pop
                state.output.fatal("Subpopulation " + subpop + " has fewer than four individuals, and so cannot be used with DEBreeder.");
            
            Individual[] inds = newpop.subpops[subpop].individuals;
            for( int i = 0 ; i < inds.length ; i++ ) {
                newpop.subpops[subpop].individuals[i] = createIndividual( state, subpop, i, 0);  // unthreaded for now
            }
        }

        // store the current population for competition with the new children
        previousPopulation = state.population;
        return newpop;
	}
	
	
	/**
	 * DE/rand/1/bin
	 * 
	 * <p> vectorii sunt alesi dintre vecini
	 */
	@Override
	public DoubleVectorIndividual createIndividual(EvolutionState state, int subpop, int index, int thread){
        Individual[] inds = state.population.subpops[subpop].individuals;

        DoubleVectorIndividual v = (DoubleVectorIndividual)(inds[index].clone());
        do {
        	// select three indexes different from each other and from that of the current parent
            int r0, r1, r2;
            do {
                r0 = state.random[thread].nextInt(T);
            }while( r0 == index );
            do {
                r1 = state.random[thread].nextInt(T);
            }while( r1 == r0 || r1 == index );
            do {
                r2 = state.random[thread].nextInt(T);
            }while( r2 == r1 || r2 == r0 || r2 == index );

            DoubleVectorIndividual g0 = (DoubleVectorIndividual)(inds[neighbours[index][r0]]);
            DoubleVectorIndividual g1 = (DoubleVectorIndividual)(inds[neighbours[index][r1]]);
            DoubleVectorIndividual g2 = (DoubleVectorIndividual)(inds[neighbours[index][r2]]);

            //TODO ai grija ca ai schimbat
            for(int i = 0; i < v.genome.length; i++){
                v.genome[i] = g0.genome[i] + F * (g1.genome[i] - g2.genome[i]);
//                v.genome[i] = Math.abs(v.genome[i]);
//                if(v.genome[i]>1)
//                	v.genome[i] = v.genome[i] - Math.floor(v.genome[i]);
            }
        }while(!valid(v));

        return crossover(state, (DoubleVectorIndividual)(inds[index]), v, thread);
    }

	/**
	 * initializez neighour matrix
	 * 
	 * <p>
	 * detects T closes neighbours to weight vector i, i=1...N
	 */
	private void initializeNeighbours() {
		neighbours = new int[N][T];
		ponderi = UtilsMoeadQoS.ponderi;
		
		double[][] distMatrix = new double[N][N];
		for(int i=0;i<N;i++){
			distMatrix[i][i] = 0;
			for(int j=i+1;j<N;j++){
				distMatrix[i][j] = UtilsMOO.getInstance().distanta(ponderi[i], ponderi[j]);
				distMatrix[j][i] = distMatrix[i][j];
			}
		}
		
		for (int i = 0; i < N; i++) {
			int[] index = sortare(distMatrix[i]);
			int[] array = new int[T];
			System.arraycopy(index, 0, array, 0, T);
			neighbours[i] = array;
		}
	}

	/**
	 * Srots the given vewctor in assending order, with the order returned and
	 * the array unchanged.
	 * 
	 * @param vector
	 * @return
	 */
	private int[] sortare(double[] vector) {
		int[] index = new int[vector.length];
		
		for (int i = 0; i < vector.length; i++) {
			index[i] = i;
		}
		
		for (int i = 0; i < vector.length; i++) {
			for (int j = 0; j < i; j++) {
				if(vector[index[i]] < vector[index[j]]) {
					int temp = index[i];
					for (int k = i-1; k >= j; k--) {
						index[k+1] = index[k];
					}
					index[j] = temp;
					break;
				}
			}
		}

		return index;
	}

}
