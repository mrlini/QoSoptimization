package org.utcluj.moo.optimizareServiciiReale;

import java.util.ArrayList;
import java.util.Vector;

import org.utcluj.moo.algoritmi.moead.UtilsMoeadQoS;
import org.utcluj.moo.algoritmi.posde.PosdeEvaluator;
import org.utcluj.moo.indicatoriCalitate.Hyp;
import org.utcluj.moo.indicatoriCalitate.SetCoverage;
import org.utcluj.moo.indicatoriCalitate.Spread;
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

public class StatisticiServRealeMultiRun extends SimpleStatistics{

	private static final long serialVersionUID = 1L;
	
	public static final String FRONT_PARETO = "front";
	public static final String FRONT_GENOM = "genomFront";
	public static final int FARA_FRONT = -1;
	public int frontLog;
	private String templateAdrFront = null;
//	private String fileGenomFront = null;
	private String metoda = null;
	private int nrOb;
	private int alternative = 0, taskCount = 0;
	private String activities = "";
	
	public static double hypervolume;
	public static double spread;
	public static double[] setCoverage;
	
	private static int nrRulare = 0;
	private int dimPop = 0;
	
	private boolean indicatori;
	private static int[] obj;
	private static double[] ideal;
	private static double[] nadir;
	private static double[] ref;
	private static String adresaHvSpr;
	private static String adresaSetCv;
	private static String tempAdr;

	public void setup(final EvolutionState state, final Parameter base){
		super.setup(state, base);
		templateAdrFront = state.parameters.getString(new Parameter("templateAdr"), null);
//		fileGenomFront = state.parameters.getString(new Parameter(FRONT_GENOM), null);
		metoda = state.parameters.getString(new Parameter("metoda"), null);
		nrOb = state.parameters.getInt(new Parameter("nrObj"), null);
		alternative = state.parameters.getInt(new Parameter(UtilsMOO.ALTERNATIVE), null);
		activities = state.parameters.getString(new Parameter(UtilsMOO.ACTIVITIES), null);
		taskCount = state.parameters.getInt(new Parameter(UtilsMOO.TASKCOUNT), null);
		indicatori = state.parameters.getBoolean(new Parameter("indicatori"), null, false);
		dimPop = state.parameters.getInt(new Parameter("pop.subpop.0.size"), null);
		nrRulare = state.parameters.getInt(new Parameter("nrRulare"), null);
		
		if(metoda.compareTo(UtilsMOO.MET_MOEAD) == 0)
			UtilsMoeadQoS.initZoptim();
		
		hypervolume = 0;
		spread = 0;
		setCoverage = new double[20];
		for(int j=0;j<setCoverage.length;j++){
			setCoverage[j] = 0;
		}
		
		initParamsStatistici();

		adresaHvSpr = templateAdrFront + "statistici/statisticiHvSpr"+nrOb+"Ob.txt";
		adresaSetCv = templateAdrFront + "statistici/statisticiSetCoverage"+nrOb+"Ob.txt";
		tempAdr = templateAdrFront + "statistici/temp_"+metoda+".txt";
	}
	
	public void preEvaluationStatistics(final EvolutionState state){
		
		if (metoda.compareTo(UtilsMOO.MET_MOEAD) == 0) {
			float[] fitness;
			//caut punctul optim z* pentru fiecare subpop in fiecare generatie pentru Tchebycheff
			//z e pt probl de minim
			double[] z = UtilsMoeadQoS.getzOptim().clone();
			for(int i=0;i<state.population.subpops.length;i++){
				for(int j=0;j<state.population.subpops[i].individuals.length;j++){
					fitness = FitnessMO.getInstance().getFitnessMooQoS(nrOb, false, state.population.subpops[i].individuals[j], activities, alternative);
					for(int k=0;k<nrOb;k++){
						if(nrOb==2){
							if((double)fitness[k] < z[k])
								z[k] = fitness[k];
						}else if(nrOb==3 && k<2){
							if((double)fitness[k] < z[k])
								z[k] = fitness[k];
						}else if(nrOb==3 && k==2){
							if((double)fitness[k] < z[k])
								z[k] = fitness[k];
						}
					}
				}
			}
			UtilsMoeadQoS.setzOptim(z);
		}//if metoda
	}
	
	public void postEvaluationStatistics(final EvolutionState state){
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public void finalStatistics(final EvolutionState state, final int result){
		double[][] frontPareto = null;
		int subpop = 0;
		int algoritm = 0;
		
		if(metoda.compareTo(UtilsMOO.MET_NSGA2)==0 || metoda.compareTo(UtilsMOO.MET_SPEA2)==0 || metoda.compareTo(UtilsMOO.MET_GDE3)==0) {
			MultiObjectiveFitness fitness = (MultiObjectiveFitness) (state.population.subpops[subpop].individuals[0].fitness);
			
			//construiesc frontul
			ArrayList front = fitness.partitionIntoParetoFront(state.population.subpops[subpop].individuals, null, null);
			//fac o sortare dupa obiectivul 0
			Object[] frontSortat = front.toArray();
			QuickSort.qsort(frontSortat, new SortComparator() {
				@Override
				public boolean lt(Object a, Object b) {
					return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) < 
							(((MultiObjectiveFitness) ((Individual) b).fitness)).getObjective(0));
				}
				@Override
				public boolean gt(Object a, Object b) {
					return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) > 
					((MultiObjectiveFitness) (((Individual) b).fitness)).getObjective(0));
				}
			});
			
			int dimFrotnSortat = frontSortat.length;
			frontPareto = new double[dimFrotnSortat][nrOb];
			for(int i=0; i<dimFrotnSortat;i++){
				Individual ind = (Individual)(frontSortat[i]);
				MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
				float[] objectives = mof.getObjectives();
				for (int f = 0; f < objectives.length; f++){
					frontPareto[i][f] = Functions.abs.apply((double)objectives[f]);
				}
			}
			
			//pentru statistici
			if(metoda.compareTo(UtilsMOO.MET_NSGA2)==0){
				algoritm = 1;
			} else if(metoda.compareTo(UtilsMOO.MET_SPEA2)==0){
				algoritm = 2;
			} else if(metoda.compareTo(UtilsMOO.MET_GDE3)==0){
				algoritm = 4;
			}
		} else if(metoda.compareTo(UtilsMOO.MET_POSDE)==0) {
			Vector<Individual> arh = PosdeEvaluator.arhiva;	
			frontPareto = new double[arh.size()][nrOb];
			algoritm = 5;
			for (int i = 0; i < arh.size(); i++){
				Individual ind = arh.elementAt(i);
				MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
				float[] objectives = mof.getObjectives();
				for (int f = 0; f < objectives.length; f++){
					frontPareto[i][f] = Functions.abs.apply((double)objectives[f]);
				}
			}
		} else if(metoda.compareTo(UtilsMOO.MET_MOEAD)==0) {
			frontPareto = new double[dimPop][nrOb];
			algoritm = 3;
			for(int i=0;i<dimPop;i++){
				float[] objectives = FitnessMO.getInstance().getFitnessMooQoS(nrOb, false, state.population.subpops[subpop].individuals[i], activities, alternative);
				for (int f = 0; f < nrOb; f++){
					frontPareto[i][f] = Functions.abs.apply((double)objectives[f]);
				}
			}
		}
		
		//scriu frontul pe disc pentru a calcula apoi setCoverage
		UtilsMOO.getInstance().scrieMatrice(tempAdr, false, frontPareto);
		
		if(indicatori){
			//statistici
			Hyp h = new Hyp(obj.length, obj, 0, nadir, ideal, 0, 0, ref);
			hypervolume = h.calcHypervolume(frontPareto);
			//calc spread doar pt 2 obiective
			if(nrOb == 2){
				spread = new Spread().calculDisp(frontPareto, nrOb);
			}
			//scriu rez, set coverage se scrie in metoda => am de scris HV si spread
			String linieRez = nrRulare + "," + algoritm + "," + taskCount + "," + alternative + "," + hypervolume + ",";
			if(nrOb == 2){
				linieRez += spread + ",";
			}else {
				linieRez += 0.0 + ",";
			}
			UtilsMOO.getInstance().scrieString(adresaHvSpr, true, 2, linieRez);
			
			
			//daca metoda e posde => s-au terminat rularile pentru cei 5 allg si pot sa calculez si SetCoverage
			if(metoda.compareTo(UtilsMOO.MET_POSDE) == 0){
				StatSetCoverage();
			}
		}
	}
	
	/**
	 * Metoda calculeaza indicatorul SetCoverage si scrie rezultatul intr-un fisier
	 * @return
	 */
	private void StatSetCoverage() {
		//citesc fronturile salvate
		String templateAdrTemp = templateAdrFront + "statistici/temp_";
		String adrN = templateAdrTemp + UtilsMOO.MET_NSGA2 + ".txt";
		String adrS = templateAdrTemp + UtilsMOO.MET_SPEA2 + ".txt";
		String adrM = templateAdrTemp + UtilsMOO.MET_MOEAD + ".txt";
		String adrG = templateAdrTemp + UtilsMOO.MET_GDE3 + ".txt";
		String adrP = templateAdrTemp + UtilsMOO.MET_POSDE + ".txt";
		double[][] nsga2Temp = UtilsMOO.getInstance().citireFrontTeoretic(adrN, nrOb);
		double[][] spea2Temp = UtilsMOO.getInstance().citireFrontTeoretic(adrS, nrOb);
		double[][] moeadTemp = UtilsMOO.getInstance().citireFrontTeoretic(adrM, nrOb);
		double[][] gde3Temp = UtilsMOO.getInstance().citireFrontTeoretic(adrG, nrOb);
		double[][] posdeTemp = UtilsMOO.getInstance().citireFrontTeoretic(adrP, nrOb);
		
		//calculez indicatorul
		setCoverage[0] = SetCoverage.getInstance().calculSetCoverage(nsga2Temp, spea2Temp);
		setCoverage[1] = SetCoverage.getInstance().calculSetCoverage(nsga2Temp, moeadTemp);
		setCoverage[2] = SetCoverage.getInstance().calculSetCoverage(nsga2Temp, gde3Temp);
		setCoverage[3] = SetCoverage.getInstance().calculSetCoverage(nsga2Temp, posdeTemp);
		setCoverage[4] = SetCoverage.getInstance().calculSetCoverage(spea2Temp, nsga2Temp);
		setCoverage[5] = SetCoverage.getInstance().calculSetCoverage(spea2Temp, moeadTemp);
		setCoverage[6] = SetCoverage.getInstance().calculSetCoverage(spea2Temp, gde3Temp);
		setCoverage[7] = SetCoverage.getInstance().calculSetCoverage(spea2Temp, posdeTemp);
		setCoverage[8] = SetCoverage.getInstance().calculSetCoverage(moeadTemp, nsga2Temp);
		setCoverage[9] = SetCoverage.getInstance().calculSetCoverage(moeadTemp, spea2Temp);
		setCoverage[10] = SetCoverage.getInstance().calculSetCoverage(moeadTemp, gde3Temp);
		setCoverage[11] = SetCoverage.getInstance().calculSetCoverage(moeadTemp, posdeTemp);
		setCoverage[12] = SetCoverage.getInstance().calculSetCoverage(gde3Temp, nsga2Temp);
		setCoverage[13] = SetCoverage.getInstance().calculSetCoverage(gde3Temp, spea2Temp);
		setCoverage[14] = SetCoverage.getInstance().calculSetCoverage(gde3Temp, moeadTemp);
		setCoverage[15] = SetCoverage.getInstance().calculSetCoverage(gde3Temp, posdeTemp);
		setCoverage[16] = SetCoverage.getInstance().calculSetCoverage(posdeTemp, nsga2Temp);
		setCoverage[17] = SetCoverage.getInstance().calculSetCoverage(posdeTemp, spea2Temp);
		setCoverage[18] = SetCoverage.getInstance().calculSetCoverage(posdeTemp, moeadTemp);
		setCoverage[19] = SetCoverage.getInstance().calculSetCoverage(posdeTemp, gde3Temp);
		
		String rez = nrRulare + "," + taskCount + "," + alternative + ",";
		for(double sc: setCoverage){
			rez += sc + ",";
		}
		UtilsMOO.getInstance().scrieString(adresaSetCv, true, 10, rez);
	}
	
	private void initParamsStatistici() {
		obj = new int[nrOb];
		ideal = new double[nrOb];
		nadir = new double[nrOb];
		ref = new double[nrOb];
		
		//setez pct pt calculul "hypervolume"
		for(int i=0;i<obj.length;i++){
			obj[i] = 0;
			ideal[i] = 0.0;
		}
				
		switch (taskCount) {
		case 10:
			switch (alternative) {
			case 10:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 10;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 10;
				}
				break;
			case 20:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 10;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 10;
				}
				break;
			case 30:
				if(nrOb == 2){
					nadir[0] = 35;
					nadir[1] = 1;
					ref[0] = 35;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 10;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 10;
				}
				break;
			case 40:
				if(nrOb == 2){
					nadir[0] = 30;
					nadir[1] = 1;
					ref[0] = 30;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 30;
					nadir[1] = 1;
					nadir[2] = 10;
					ref[0] = 30;
					ref[1] = 1;
					ref[2] = 10;
				}
				break;
			case 50:
				if(nrOb == 2){
					nadir[0] = 30;
					nadir[1] = 1;
					ref[0] = 30;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 30;
					nadir[1] = 1;
					nadir[2] = 20;
					ref[0] = 30;
					ref[1] = 1;
					ref[2] = 20;
				}
				break;
			case 60:
				if(nrOb == 2){
					nadir[0] = 30;
					nadir[1] = 1;
					ref[0] = 30;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 30;
					nadir[1] = 1;
					nadir[2] = 10;
					ref[0] = 30;
					ref[1] = 1;
					ref[2] = 10;
				}
				break;
			case 70:
				if(nrOb == 2){
					nadir[0] = 30;
					nadir[1] = 1;
					ref[0] = 30;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 30;
					nadir[1] = 1;
					nadir[2] = 10;
					ref[0] = 30;
					ref[1] = 1;
					ref[2] = 10;
				}
				break;
			case 80:
				if(nrOb == 2){
					nadir[0] = 30;
					nadir[1] = 1;
					ref[0] = 30;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 30;
					nadir[1] = 1;
					nadir[2] = 10;
					ref[0] = 30;
					ref[1] = 1;
					ref[2] = 10;
				}
				break;
			case 90:
				if(nrOb == 2){
					nadir[0] = 30;
					nadir[1] = 1;
					ref[0] = 30;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 30;
					nadir[1] = 1;
					nadir[2] = 15;
					ref[0] = 30;
					ref[1] = 1;
					ref[2] = 15;
				}
				break;
			default:
				break;
			}
			break;
		case 20:
			//20 serv abstracte
			switch (alternative) {
			case 10:
				if(nrOb == 2){
					nadir[0] = 30;
					nadir[1] = 1;
					ref[0] = 30;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 2;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 2;
				}
				break;
			case 20:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 2;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 2;
				}
				break;
			case 30:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 2;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 2;
				}
				break;
			case 40:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 2;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 2;
				}
				break;
			case 50:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 2;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 2;
				}
				break;
			case 60:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 2;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 2;
				}
				break;
			case 70:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 2;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 2;
				}
				break;
			case 80:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 2;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 2;
				}
				break;
			case 90:
				if(nrOb == 2){
					nadir[0] = 30;
					nadir[1] = 1;
					ref[0] = 30;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 25;
					nadir[1] = 1;
					nadir[2] = 2;
					ref[0] = 25;
					ref[1] = 1;
					ref[2] = 2;
				}
				break;
			default:
				break;
			}
			break;
		case 30:
			switch (alternative) {
			case 10:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 20;
					nadir[1] = 1;
					nadir[2] = 720;
					ref[0] = 20;
					ref[1] = 1;
					ref[2] = 720;
				}
				break;
			case 20:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 20;
					nadir[1] = 1;
					nadir[2] = 720;
					ref[0] = 20;
					ref[1] = 1;
					ref[2] = 720;
				}
				break;
			case 30:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 20;
					nadir[1] = 1;
					nadir[2] = 5000;
					ref[0] = 20;
					ref[1] = 1;
					ref[2] = 5000;
				}
				break;
			case 40:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 20;
					nadir[1] = 1;
					nadir[2] = 3000;
					ref[0] = 20;
					ref[1] = 1;
					ref[2] = 3000;
				}
				break;
			case 50:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 20;
					nadir[1] = 1;
					nadir[2] = 820;
					ref[0] = 20;
					ref[1] = 1;
					ref[2] = 820;
				}
				break;
			case 60:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 20;
					nadir[1] = 1;
					nadir[2] = 820;
					ref[0] = 20;
					ref[1] = 1;
					ref[2] = 820;
				}
				break;
			case 70:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 20;
					nadir[1] = 1;
					nadir[2] = 820;
					ref[0] = 20;
					ref[1] = 1;
					ref[2] = 820;
				}
				break;
			case 80:
				if(nrOb == 2){
					nadir[0] = 20;
					nadir[1] = 1;
					ref[0] = 20;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 20;
					nadir[1] = 1;
					nadir[2] = 820;
					ref[0] = 20;
					ref[1] = 1;
					ref[2] = 820;
				}
				break;
			case 90:
				if(nrOb == 2){
					nadir[0] = 25;
					nadir[1] = 1;
					ref[0] = 25;
					ref[1] = 1;
				}else if(nrOb == 3){
					nadir[0] = 20;
					nadir[1] = 1;
					nadir[2] = 720;
					ref[0] = 20;
					ref[1] = 1;
					ref[2] = 720;
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}
}
