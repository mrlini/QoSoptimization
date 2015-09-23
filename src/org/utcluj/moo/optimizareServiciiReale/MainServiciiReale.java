package org.utcluj.moo.optimizareServiciiReale;

import org.utcluj.io.PlanLoader;
import org.utcluj.model.BpelProcessSingleton;
import org.utcluj.moo.algoritmi.moead.UtilsMoeadQoS;
import org.utcluj.moo.utils.FitnessMO;
import org.utcluj.moo.utils.UtilsMOO;
import org.utcluj.util.ConfigurationProperties;

import ec.Evolve;

public class MainServiciiReale {

	// ********* param generali **********
	private static final int dimPop = 150;
	private static final int dimArhiva = 150;
	private static final int nrGeneratii = 800;
	private static final int nrObiective = 2;
	private static final int nrJobs = 1;
	@SuppressWarnings("unused")
	private static final int T = 10; //param MOEA/D
	
	private static final String activities = FitnessMO.BPEL_REAL_NORMALIZED; 
	
	private static final boolean indicatori = false;
	private static final String fileFront = "src/org/utcluj/moo/optimizareServiciiReale/teste/";
//	private static String[] planTaskCount = { "10", "20", "30", "40", "50" }; //dim plan
//	private static String[] alternatives = { "10", "20", "30", "40", "50", "60", "70", "80", "90" }; //nr
	private static final String[] planTaskCount = { "10" }; // dim plan
	private static String[] alternatives = { "30" };
	private static String[] metode = {UtilsMOO.MET_NSGA2};//, UtilsMOO.MET_SPEA2, UtilsMOO.MET_MOEAD, UtilsMOO.MET_GDE3, UtilsMOO.MET_POSDE};//UtilsMOO.MET_GDE3_Lorenz, UtilsMOO.MET_GDE3};//};//, UtilsMOO.MET_GDE3};
	private static String fileParams = "src/org/utcluj/moo/algoritmi/";
	

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		// ****** param MOEAD ******
		String T = "10"; // necesar pt moead, tb sa fie > 3
		String dimEP = "150";
		String lamda = "1";

		// ****** param eMyDE conform articolului Coello ******
		String epsilon = "0.00051"; // epsilon e un vector de dim nr obiective
									// TODO de imbunatatit
		String sel2 = "0.7";

		if(activities == FitnessMO.BPEL_REAL)
			ConfigurationProperties.setCurrentMacroDefinitions("MacroActivitiesReal/MacroActivitiesReal");
		else if(activities== FitnessMO.BPEL_REAL_NORMALIZED)
			ConfigurationProperties.setCurrentMacroDefinitions("MacroActivitiesRealNormalized/MacroActivitiesRealNormalized");
		else 
			ConfigurationProperties.setCurrentMacroDefinitions("MacroActivities/MacroActivities");
		
		String[] paramEvolutie = { "-file",	"",
				"-p", "pop.subpop.0.size=" + dimPop, // dim pop
				"-p", "generations=" + nrGeneratii, // nr generatii
				"-p", "nrObj="+nrObiective, // nr obiective
				"-p", "", // dim genom
				"-p", "", // max gene, min gene este 0 totdeauna
				"-p", "multi.fitness.num-objectives=" + nrObiective, 
				"-p", "pop.subpop.0.species.fitness.num-objectives=" + nrObiective,
				"-p", "", // stat file
				"-p", "", //dim arhiva spea2, EP pt moead
				"-p", "",
				"-p", "",  //alternative
				"-p", "",  //lungime plan
				"-p", "", 
				"-p", "", //transmit un string cu met fol pt a sti ce statistici sa fac
				"-p", "jobs=" + nrJobs,
				"-p", "epsilon="+epsilon,
				"-p", "breed.s="+sel2,
				"-p", "", //file genom
				"-p", "activities=" + activities,
				"-p", "breed.cr=0.75",
				"-p", "breed.f=0.3",
				"-p", "eval.problem=",
				"-p", "stat=org.utcluj.moo.optimizareServiciiReale.StatisticiServReale",
				"-p", "indicatori="+indicatori,
				"-p", ""};
		int max_gene = 0;
		for(int i=0;i<planTaskCount.length;i++){
			paramEvolutie[9] = "pop.subpop.0.species.genome-size=" + planTaskCount[i];
			paramEvolutie[23] = UtilsMOO.TASKCOUNT + "=" + planTaskCount[i];
			//incarc planul
			try{
				if(activities.equalsIgnoreCase("MacroActivities")){				
					PlanLoader.loadPDDLPlan(ConfigurationProperties.getPddlPath(), 
						Integer.parseInt(planTaskCount[i]));
				} else if (activities.equalsIgnoreCase("BpelActivities") || (activities.compareTo(FitnessMO.BPEL_REAL) == 0) || (activities.compareTo(FitnessMO.BPEL_REAL_NORMALIZED) == 0)){
					BpelProcessSingleton.loadDefaultProcess(Integer.parseInt(planTaskCount[i]));
				} else if (activities.equalsIgnoreCase("BpelMedicalActivities")){
					BpelProcessSingleton.loadDefaultMedicalProcess(Integer.parseInt(planTaskCount[i]));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			for(int j=0;j<alternatives.length;j++){
				max_gene = Integer.parseInt(alternatives[j]) - 1;
				
				paramEvolutie[25] = UtilsMOO.ALTERNATIVE + "=" + alternatives[j]; 
//				ConfigurationProperties.setCurrentMacroDefinitions("MacroActivities/MacroActivities" + alternatives[j] + ".xml");
//				if(activities ==  null)
//					activities = "MacroActivities";
				ConfigurationProperties.setCurrentMacroDefinitions(activities + "/" + activities + alternatives[j] + ".xml");
				
				for(int k=0;k<metode.length;k++){
					paramEvolutie[1] = fileParams+metode[k]+"/"+metode[k]+".params";
					paramEvolutie[17] = "front=" + fileFront+metode[k] + planTaskCount[i]+"_"+alternatives[j]+".txt";
					paramEvolutie[37] = "genomFront=" + fileFront+metode[k] + "genom.txt";
					paramEvolutie[29] = "metoda=" + metode[k];
					
					if(metode[k].compareTo(UtilsMOO.MET_SPEA2) == 0){
						paramEvolutie[19] = "sbreed.elite.0="+dimArhiva; //pt SPEA2 tb specificata dim arhivei
						paramEvolutie[11] = "pop.subpop.0.species.max-gene=" + Integer.toString(max_gene);
						paramEvolutie[45] = "eval.problem=org.utcluj.moo.optimizareServiciiReale.IntProblem";
					} 
					else if(metode[k].compareTo(UtilsMOO.MET_NSGA2) == 0){
						paramEvolutie[11] = "pop.subpop.0.species.max-gene=" + Integer.toString(max_gene);
						paramEvolutie[45] = "eval.problem=org.utcluj.moo.optimizareServiciiReale.IntProblem";
					}
					else if(metode[k].compareTo(UtilsMOO.MET_NSGA2_Lorenz) == 0){
						paramEvolutie[11] = "pop.subpop.0.species.max-gene=" + Integer.toString(max_gene);
						paramEvolutie[45] = "eval.problem=org.utcluj.moo.optimizareServiciiReale.IntProblemLorenz";
					}
					else if(metode[k].compareTo(UtilsMOO.MET_MOEAD) == 0){
						//init ponderi pt MOEA/D
						UtilsMoeadQoS.setDimPop(dimPop);
						UtilsMoeadQoS.setNrOb(nrObiective);
						UtilsMoeadQoS.getInstance().generateWeights();
						
						paramEvolutie[45] = "eval.problem=org.utcluj.moo.algoritmi.moead.ProblemMoeadQoS";
						paramEvolutie[51] = "breed.t="+T;
						paramEvolutie[11] = "pop.subpop.0.species.max-gene=" + "1";
						paramEvolutie[19] = "dimEP=" + dimEP; //dimensiunea populatiei externe in care pastrez indivizii nedominati
					}
					else if(metode[k].equals(UtilsMOO.MET_POSDE)){
						paramEvolutie[11] = "pop.subpop.0.species.max-gene=1";
						paramEvolutie[19] = "dimEP=" + dimEP; //dimensiunea populatiei externe in care pastrez indivizii nedominati
						paramEvolutie[27] = "lamda=" + lamda;
						paramEvolutie[45] = "eval.problem=org.utcluj.moo.optimizareServiciiReale.DoubleProblem";
					}
					else if(metode[k].equals(UtilsMOO.MET_GDE3) || metode[k].equals(UtilsMOO.MET_DEMO)){
						paramEvolutie[11] = "pop.subpop.0.species.max-gene=1";
						paramEvolutie[45] = "eval.problem=org.utcluj.moo.optimizareServiciiReale.DoubleProblem";
					}
					else if(metode[k].equals(UtilsMOO.MET_GDE3_Lorenz)){
//						paramEvolutie[11] = "pop.subpop.0.species.max-gene=" + Integer.toString(max_gene);
						paramEvolutie[11] = "pop.subpop.0.species.max-gene=1";
						paramEvolutie[45] = "eval.problem=org.utcluj.moo.optimizareServiciiReale.DoubleProblemLorenz";
					}
					else if(metode[k].equals(UtilsMOO.MET_DE_Lorenz)){
//						paramEvolutie[11] = "pop.subpop.0.species.max-gene=" + Integer.toString(max_gene);
						paramEvolutie[11] = "pop.subpop.0.species.max-gene=1";
						paramEvolutie[45] = "eval.problem=org.utcluj.moo.optimizareServiciiReale.DoubleProblemLorenz";
					}
					else if(metode[k].compareTo(UtilsMOO.MET_eMyDE) == 0){						
						paramEvolutie[11] = "pop.subpop.0.species.max-gene=1";
					}
					
					Evolve.main(paramEvolutie);
				}
				 
			}
		}
		
		
//		java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//            	//TODO Atentie la taskCount, tb sa fie la fel cu ce am simulat ultimadata
//                new FinalSolGUI(Integer.parseInt(nrObiective), fileFront, planTaskCount[planTaskCount.length-1]).setVisible(true);
//            }
//        });
		System.out.println("Gata!");
		
	}

}
