package org.utcluj.moo.optimizareServiciiReale;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import org.utcluj.moo.algoritmi.eMyDE.eMyDEBreeder;
import org.utcluj.moo.algoritmi.moead.UtilsMoeadQoS;
import org.utcluj.moo.algoritmi.posde.PosdeEvaluator;
import org.utcluj.moo.utils.FitnessMO;
import org.utcluj.moo.utils.UtilsMOO;

import cern.jet.math.Functions;
import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;
import ec.util.QuickSort;
import ec.util.SortComparator;

@SuppressWarnings("unused")
public class StatisticiServReale extends SimpleStatistics {

	private static final long serialVersionUID = 945383171186640334L;

	public static final String FRONT_PARETO = "front";
	public static final String FRONT_GENOM = "genomFront";
	public static final int FARA_FRONT = -1;
	public int frontLog;
	private String fileFront = null;
	// private String fileGenomFront = null;
	private String metoda = null;
	private int nrOb;
	private int alternative = 0, taskCount = 0;
	private String activities = "";

	public static double[] hypervolume;
	public static double[] spread;
	public static double[][] setCoverage;

	private static int nrJobs;
	private static int contor = 0;
	private HashMap<Integer, LinkedList<Double>> hm;
	// private int dimPop = 0;

	private boolean indicatori;

	// private static int[] obj;
	// private static double[] ideal;
	// private static double[] nadir;
	// private static double[] ref;

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		fileFront = state.parameters.getString(new Parameter(FRONT_PARETO),
				null);
		// fileGenomFront = state.parameters.getString(new
		// Parameter(FRONT_GENOM), null);
		metoda = state.parameters.getString(new Parameter("metoda"), null);
		nrOb = state.parameters.getInt(new Parameter("nrObj"), null);
		nrJobs = state.parameters.getIntWithDefault(new Parameter("jobs"),
				null, 1);
		alternative = state.parameters.getInt(new Parameter(
				UtilsMOO.ALTERNATIVE), null);
		activities = state.parameters.getString(new Parameter(
				UtilsMOO.ACTIVITIES), null);
		taskCount = state.parameters.getInt(new Parameter(UtilsMOO.TASKCOUNT),
				null);
		indicatori = state.parameters.getBoolean(new Parameter("indivatori"),
				null, false);
		// dimPop = state.parameters.getInt(new Parameter("pop.subpop.0.size"),
		// null);

		if (metoda.compareTo(UtilsMOO.MET_MOEAD) == 0)
			UtilsMoeadQoS.initZoptim();
	}

	public void preEvaluationStatistics(final EvolutionState state) {

		if (metoda.compareTo(UtilsMOO.MET_MOEAD) == 0) {
			float[] fitness;
			// caut punctul optim z* pentru fiecare subpop in fiecare generatie
			// pentru Tchebycheff
			// z e pt probl de minim
			double[] z = UtilsMoeadQoS.getzOptim().clone();
			for (int i = 0; i < state.population.subpops.length; i++) {
				for (int j = 0; j < state.population.subpops[i].individuals.length; j++) {
					fitness = FitnessMO.getInstance().getFitnessMooQoS(nrOb,
							false, state.population.subpops[i].individuals[j],
							activities, alternative);
					for (int k = 0; k < nrOb; k++) {
						if (nrOb == 2) {
							if ((double) fitness[k] < z[k])
								z[k] = fitness[k];
						} else if (nrOb == 3 && k < 2) {
							if ((double) fitness[k] < z[k])
								z[k] = fitness[k];
						} else if (nrOb == 3 && k == 2) {
							if ((double) fitness[k] < z[k])
								z[k] = fitness[k];
						}
					}
				}
			}
			UtilsMoeadQoS.setzOptim(z);
		}// if metoda
	}

	public void postEvaluationStatistics(final EvolutionState state) {
	}

	@SuppressWarnings({ "static-access", "rawtypes" })
	public void finalStatistics(final EvolutionState state, final int result) {

		double[][] frontPareto = null;
		// folosesc doar o subpopulatie
		int subpop = 0;
		int dimPop = state.population.subpops[subpop].individuals.length;

		if (metoda.equals(UtilsMOO.MET_NSGA2)
				|| metoda.equals(UtilsMOO.MET_NSGA2_Lorenz)
				|| metoda.equals(UtilsMOO.MET_DE_Lorenz)
				|| metoda.equals(UtilsMOO.MET_SPEA2)
				|| metoda.equals(UtilsMOO.MET_GDE3)
				|| metoda.equals(UtilsMOO.MET_GDE3_Lorenz)
				|| metoda.equals(UtilsMOO.MET_DEMO)) {
			// fac statistica pt metodele ^
			MultiObjectiveFitness fitness = (MultiObjectiveFitness) (state.population.subpops[subpop].individuals[0].fitness);

			// construiesc frontul
			ArrayList front = fitness.partitionIntoParetoFront(
					state.population.subpops[subpop].individuals, null, null);
			// fac o sortare dupa obiectivul 0
			Object[] frontSortat = front.toArray();
			QuickSort.qsort(frontSortat, new SortComparator() {
				@Override
				public boolean lt(Object a, Object b) {
					return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) < (((MultiObjectiveFitness) ((Individual) b).fitness)).getObjective(0));
				}

				@Override
				public boolean gt(Object a, Object b) {
					return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) > ((MultiObjectiveFitness) (((Individual) b).fitness)).getObjective(0));
				}
			});

			//
			frontPareto = new double[frontSortat.length][fitness.getNumObjectives()];
			for (int i = 0; i < frontSortat.length; i++) {
				Individual ind = (Individual) (frontSortat[i]);
				MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
				float[] objectives = mof.getObjectives();
				for (int f = 0; f < objectives.length; f++) {
					frontPareto[i][f] = Functions.abs.apply((double) objectives[f]);
				}
			}

			// if(metoda.equals(UtilsMOO.MET_GDE3_Lorenz)){
			// LorenzFitness fit = (LorenzFitness)
			// (state.population.subpops[subpop].individuals[0].fitness);
			// //construiesc frontul
			// ArrayList front =
			// fit.partitionIntoLorenzFront(state.population.subpops[subpop].individuals,
			// null, null);
			// //fac o sortare dupa obiectivul 0
			// Object[] frontSortat = front.toArray();
			// QuickSort.qsort(frontSortat, new SortComparator() {
			// @Override
			// public boolean lt(Object a, Object b) {
			// return (((MultiObjectiveFitness) (((Individual)
			// a).fitness)).getObjective(0) <
			// (((MultiObjectiveFitness) ((Individual)
			// b).fitness)).getObjective(0));
			// }
			// @Override
			// public boolean gt(Object a, Object b) {
			// return (((MultiObjectiveFitness) (((Individual)
			// a).fitness)).getObjective(0) >
			// ((MultiObjectiveFitness) (((Individual)
			// b).fitness)).getObjective(0));
			// }
			// });
			//
			//
			// frontPareto = new
			// double[frontSortat.length][fitness.getNumObjectives()];
			// for (int i = 0; i < frontSortat.length; i++){
			// Individual ind = (Individual)(frontSortat[i]);
			// MultiObjectiveFitness mof = (MultiObjectiveFitness)
			// (ind.fitness);
			// float[] objectives = mof.getObjectives();
			// for (int f = 0; f < objectives.length; f++){
			// frontPareto[i][f] = (double)objectives[f];
			// }
			// }
			// }else{
			// //afisez toata populatia
			// frontPareto = new double[dimPop][fitness.getNumObjectives()];
			// for(int i=0;i<dimPop;i++){
			// Individual ind = state.population.subpops[subpop].individuals[i];
			// MultiObjectiveFitness mof = (MultiObjectiveFitness)ind.fitness;
			// // GDE3FitnessLorenz mof = (GDE3FitnessLorenz)ind.fitness;
			// float[] objectives = mof.getObjectives();
			// for (int f = 0; f < nrOb; f++){
			// frontPareto[i][f] = Functions.abs.apply((double)objectives[f]);
			// // frontPareto[i][f] = -(double)objectives[f];
			// // if(f==2)
			// // frontPareto[i][f] = Functions.abs.apply(frontPareto[i][f]);
			// }
			// }
			// }

		} else if (metoda.equals(UtilsMOO.MET_POSDE)
				|| metoda.equals(UtilsMOO.MET_eMyDE)) {
			Vector<Individual> arh;

			if (metoda.equals(UtilsMOO.MET_eMyDE))
				arh = ((eMyDEBreeder) (state.breeder)).arhiva;
			else
				arh = PosdeEvaluator.arhiva;
			frontPareto = new double[arh.size()][nrOb];
			for (int i = 0; i < arh.size(); i++) {
				Individual ind = arh.elementAt(i);
				MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
				float[] objectives = mof.getObjectives();
				for (int f = 0; f < objectives.length; f++) {
					frontPareto[i][f] = Functions.abs.apply((double) objectives[f]);
				}
			}
		} else if (metoda.compareTo(UtilsMOO.MET_MOEAD) == 0) {
			frontPareto = new double[dimPop][nrOb];
			for (int i = 0; i < dimPop; i++) {
				float[] objectives = FitnessMO.getInstance().getFitnessMooQoS(
						nrOb, false,
						state.population.subpops[subpop].individuals[i],
						activities, alternative);
				for (int f = 0; f < nrOb; f++) {
					frontPareto[i][f] = Functions.abs.apply((double) objectives[f]);
					// frontPareto[i][f] = -(double)objectives[f];
					// if(f==2)
					// frontPareto[i][f] =
					// Functions.abs.apply(frontPareto[i][f]);
				}
			}
		}
		// scriu frontul pe disc
		if (fileFront != null) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						fileFront, false));
				String linie = "";
				for (int i = 0; i < frontPareto.length; i++) {
					linie = "";
					for (int f = 0; f < nrOb; f++) {
						linie += frontPareto[i][f] + "\t";
					}
					bw.write(linie + "\n");
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// fac si partea de decision making pentru 2 obiective
			// ponderile obiectivelor sunt f1=0.3, f2=0.7
			double f1 = 0.3;
			double f2 = 0.7;
			// scalez f1 si f2 in fct de val obtinute din frontul Pareto
			double[] max = new double[2];
			double[] min = new double[2];
			for (int i = 0; i < 2; i++) {
				max[i] = Double.NEGATIVE_INFINITY;
				min[i] = Double.POSITIVE_INFINITY;
			}
			// met merge doar pt 2 obiective
			if (nrOb > 2) {
				System.err.println("Decision making merge doar pentru 2 obiective");
				System.exit(1);
			}
			for (int i = 0; i < frontPareto.length; i++) {
				for (int j = 0; j < nrOb; j++) {
					if (frontPareto[i][j] > max[j]) {
						max[j] = frontPareto[i][j];
					}
					if (frontPareto[i][j] < min[j]) {
						min[j] = frontPareto[i][j];
					}
				}
			}
			f1 = min[0] + f1 * (max[0] - min[0]);
			f2 = min[1] + f2 * (max[1] - min[1]);

			System.out.println("f1 = "+f1);
			System.out.println("f2 = "+f2);
			String fileFrintDM = fileFront.substring(0, fileFront.length() - 4)
					+ "DM.txt";
			double distMin = Double.POSITIVE_INFINITY;
			double[] solDM = new double[nrOb];
			double aux;
			for (int i = 0; i < frontPareto.length; i++) {
				aux = Functions.abs.apply(dist(frontPareto[i], f1, f2));
				if (aux < distMin) {
					distMin = aux;
					solDM = frontPareto[i].clone();
				}
			}
			String rez = solDM[0] + "\t" + solDM[1] + "\n";
			try {
				BufferedWriter bw1 = new BufferedWriter(new FileWriter(
						fileFrintDM, false));
				bw1.write(rez);
				bw1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Met calculeaza distanta intre o dreapta si un punct in 2 dimensiuni.
	 * <p>
	 * Drealta are ecuatia y=m*x+b unde b=0 si m=f2/f1
	 * 
	 * @param point
	 * @param f1
	 *            - preferinta pentru obiectivul 1
	 * @param f2
	 *            - preferinta pentru obiectivul 2
	 * @return
	 */
	private double dist(double[] point, double f1, double f2) {
		double panta = f2 / f1;
		double r = Functions.sqrt.apply(1 + Functions.pow.apply(panta, 2));
		double rez = ((point[1] - panta * point[0])) / r;
		return rez;
	}
}
