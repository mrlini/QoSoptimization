package org.utcluj.moo.algoritmi.moead;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingUtilities;

import resonanceSearch.problemeTest.FitnessCalc;
import resonanceSearch.utils.GUImooPonderi;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

public class StatisticsMOEAD extends SimpleStatistics{

	private static final long serialVersionUID = 404361651399326641L;
	
	public static double[] z = null;
	public static EpMOEAD arhiva = null;
	
	private boolean grafic = false;
	private String wp = null;
	private static int problema = 0;
	private static int nrObiective;
//	private static int dimPop = 0;
	private static int k_WFG = 0;
	private static int dimArhiva = 0;
	private static BufferedWriter bArh;
	private static BufferedWriter bPop;
	
	private GUImooPonderi gui;
	private int pauza = 50;
	
	
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		
		nrObiective = state.parameters.getInt(new Parameter("nrObiective"), null);
//		dimPop = state.parameters.getInt(new Parameter("pop.subpop.0.zise"), null);
		grafic = state.parameters.getBoolean(new Parameter("grafic"), null, false);
		problema = state.parameters.getInt(new Parameter("problemaRez"), null); 
		wp = state.parameters.getString(new Parameter("tip"), null);
		dimArhiva = state.parameters.getInt(new Parameter("dimArhiva"), null);
		
		if(wp.substring(0, 3).equalsIgnoreCase("wfg"))
			k_WFG = state.parameters.getIntWithDefault(base.push("k_WFG"), null, 1);
			
		if(z == null){
			z = new double[nrObiective];
			for(int j=0;j<nrObiective;j++)
				z[j] = Double.POSITIVE_INFINITY;
		}
		
		if(grafic && nrObiective==2){
			gui = new GUImooPonderi();
			gui.setVisible(true);
		}
		
		if(arhiva == null || (0==(int)(Integer)state.job[0]))
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void init() throws IOException {
		arhiva = new EpMOEAD(dimArhiva, problema, false, k_WFG);
		bArh = new BufferedWriter(new FileWriter("src/algoritmi/moead/teste/arhiva.txt", false));
		bPop = new BufferedWriter(new FileWriter("src/algoritmi/moead/teste/finalPops.txt", false));
	}
	
	public void preEvaluationStatistics(final EvolutionState state){
		double[] fitness;
		//caut punctul optim z* pentru fiecare subpop in fiecare generatie pentru Tchebycheff
		//z e pt probl de minim
		for(int i=0;i<state.population.subpops.length;i++)
			for(int j=0;j<state.population.subpops[i].individuals.length;j++){
				DoubleVectorIndividual dind = (DoubleVectorIndividual) state.population.subpops[i].individuals[j];
				fitness = FitnessCalc.getInstance().eval(problema, nrObiective, k_WFG, dind.genome);
				for(int k=0;k<nrObiective;k++)
					if(fitness[k] < z[k])
						z[k] = fitness[k];
				}
		
		//desenez graficul daca am doar 2 obiective
		if(nrObiective==2 && state.generation>2 && grafic==true){
			try{
				Thread.sleep(pauza);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					gui.desenareGrafic(state.population, problema, k_WFG);
					gui.labelGen.setText(Integer.toString(state.generation));
				}
			});
		}
	}
	
	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
	}
	
	@Override
	public void postBreedingStatistics(final EvolutionState state){
		// iau cel mai bun individ din fiecare populatie si il introduc in arhiva
		// quiets compiler complaints
		Individual[] best_i = new Individual[state.population.subpops.length]; 
		for (int x = 0; x < state.population.subpops.length; x++) {
			best_i[x] = state.population.subpops[x].individuals[0];
			for (int y = 1; y < state.population.subpops[x].individuals.length; y++)
				if (state.population.subpops[x].individuals[y].fitness.betterThan(best_i[x].fitness))
					best_i[x] = state.population.subpops[x].individuals[y];
		}
		//adaug indivizii in arhiva
		for (int i = 0; i < best_i.length; i++)
			arhiva.addIndivid(best_i[i], nrObiective);
			
			
		//adaug toti indivizii in arhiva
//			for (int x = 0; x < state.population.subpops.length; x++) 
//				for (int y = 1; y < state.population.subpops[x].individuals.length; y++)
//					arhiva.addIndivid(state.population.subpops[x].individuals[y], nrObiective);
			
	}
	
	
	@Override
	public void finalStatistics(final EvolutionState state, final int result) {

		double[] test = null;
		String linie = "";
		
		//scriu arhiva daca am: adaug toti indivizii in arhiva
		for (int x = 0; x < state.population.subpops.length; x++) 
			for (int y = 1; y < state.population.subpops[x].individuals.length; y++)
				arhiva.addIndivid(state.population.subpops[x].individuals[y], nrObiective);
		
		//scriu obiectivele indivizilor din arhiva intr-un fisier
		//apoi pop finale
		try {
			for(int i=0;i<arhiva.getNrElemInArhiva();i++){
				test = arhiva.getFitnessElement(i);
				linie = "";
				for(double d: test)
					linie += d+"\t";
				bArh.write(linie);
				bArh.newLine();
			}
			bArh.flush();
			
			//scriu pop finale intr-un fisier
			for(int i=0;i<state.population.subpops.length; i++)
				for(int j=0;j<state.population.subpops[i].individuals.length;j++){
					DoubleVectorIndividual dind = (DoubleVectorIndividual) state.population.subpops[i].individuals[j]; 
					test = FitnessCalc.getInstance().eval(problema, nrObiective, k_WFG, dind.genome);
					linie = "";
					for(double d: test)
						linie += d+"\t";
					bPop.write(linie);
					bPop.newLine();
				}
			bPop.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
