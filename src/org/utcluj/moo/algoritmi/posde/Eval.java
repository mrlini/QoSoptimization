package org.utcluj.moo.algoritmi.posde;

import java.util.Iterator;

import org.utcluj.io.MacroActivitiesXMLReader;
import org.utcluj.model.BaseActivity;
import org.utcluj.model.MacroActivity;
import org.utcluj.model.MacroPlan;
import org.utcluj.moo.utils.UtilsMOO;
import org.utcluj.util.ConfigurationProperties;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

public class Eval {

	/**
	 * 
	 * @param state
	 * @param ind
	 * @param nrOb
	 * @return
	 */
	public static double[] fitness(EvolutionState state, Individual ind, int nrOb) {
		double[] obiective = new double[nrOb];
		int alternative = state.parameters.getInt(new Parameter(UtilsMOO.ALTERNATIVE), null);
		MacroPlan plan = MacroPlan.getDefault();
		Iterator<MacroActivity> iteratie = plan.getGraph().vertexSet().iterator();
		DoubleVectorIndividual dind = (DoubleVectorIndividual) ind;
		int [] indici = new int [plan.getStepCount()];
		
		for(int i=0;i<indici.length;i++)
			indici[i] = Math.min(alternative - 1, (int)Math.abs(Math.round(dind.genome[i] * alternative)));
		
		double cost = 0;
		double timp = 0;
		double rating = 1;
		for (int i = 0; i < dind.genome.length; i++) {
			BaseActivity ba = MacroActivitiesXMLReader.getInstance().getBaseActivityByIndex(iteratie.next(),
							ConfigurationProperties.getCurrentMacroDefinitions(), indici[i]);
			cost += ba.getCost();
			timp += ba.getResponseTime();
			rating *= ba.getRating();
		}
		switch (nrOb) {
		case 2:
			// eval de 2 ob(ranking este un ob, c+t alt ob)
			// este o problema de minimizare => -timp
//			obiective[0] = (float) 1 / rating; // !!! trans in probl de min
			obiective[0] = (float) rating; 
			obiective[1] = (float) (cost + timp);
			break;
		case 3:
			// eval de 3 obiective
			obiective[0] = (float) 1 / rating;
			obiective[1] = (float) cost;
			obiective[2] = (float) timp;
			break;
		}
		return obiective;
	}
}
