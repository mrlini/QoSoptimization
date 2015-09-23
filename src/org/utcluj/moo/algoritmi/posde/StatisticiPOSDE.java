package org.utcluj.moo.algoritmi.posde;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleShortStatistics;
import ec.util.Parameter;

public class StatisticiPOSDE extends SimpleShortStatistics{

	private static final long serialVersionUID = 6148015088458805226L;
	
	public static final String FRONT_PARETO = "front";
	private String fileFront = null;
	
	public void setup(final EvolutionState state, final Parameter base){
		super.setup(state, base);
		fileFront = state.parameters.getString(new Parameter(FRONT_PARETO), null);
	}

	public void finalStatistics(final EvolutionState state, final int result){
		Vector<Individual> arh = PosdeEvaluator.arhiva;
		if(arh.size() > 0){
			try{
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileFront, false));
				String linie = "";
				arh.trimToSize();
				for (int i = 0; i < arh.size(); i++){
					Individual ind = arh.elementAt(i);
					MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
					float[] objectives = mof.getObjectives();
					linie = "";
					for (int f = 0; f < objectives.length; f++){
						if(f==0)
							linie += (-objectives[f] + "\t");
						else
							linie += (objectives[f] + "\t");
	                   	}
					bw.write("\n"+linie);
				}
				bw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}
}
