package org.utcluj.moo.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.utcluj.bpel.BPELActivity.AggregateType;
import org.utcluj.bpel.BPELInvoke;
import org.utcluj.bpel.BPELProcess;
import org.utcluj.bpel.QoSModel;
import org.utcluj.io.MacroActivitiesXMLReader;
import org.utcluj.model.BaseActivity;
import org.utcluj.model.BpelProcessSingleton;
import org.utcluj.model.MacroActivity;
import org.utcluj.model.MacroPlan;
import org.utcluj.util.ConfigurationProperties;

import cern.jet.math.Functions;

import ec.Individual;
import ec.vector.DoubleVectorIndividual;
import ec.vector.IntegerVectorIndividual;

public class FitnessMO {

	public static FitnessMO instance = null;
	
	public final static String BPEL = "BpelActivities";
	public final static String BPEL_MEDICAL = "BpelMedicalActivities";
	public final static String BPEL_REAL = "BpelRealActivities";
	public final static String BPEL_REAL_NORMALIZED = "BpelRealActivitiesNormalized";
	public final static String SIMPLU = "MacroActivities";
	
	public static byte AGGREGATE_CANFORA = 0;
	public static byte AGGREGATE_ADDITIVE = 1;
	public static byte AGGREGATE_LOGARITMIC = 2;
	

	private FitnessMO() {
	}

	public static FitnessMO getInstance() {
		if (instance == null)
			instance = new FitnessMO();
		return instance;
	}
	
	/**
	 * Calculez functia de adecvare pentru cazul uni-criterial, folosesc ponderi pentru a agrega obiectivele
	 *  
	 * @param nrObj
	 * @param integerCoding
	 * 			  - <b>true</b> =>algoritmul fol este nsga2 sau spea2 - alg ce
	 *            fol codare intreaga, <b>false</b> => alg bazati pe DE
	 * @param ind
	 *            - individul pt care calculez functia de adecvare
	 * @param activitiesUsed
	 *            - numarul de activitati
	 * @param nrAlternatives
	 *            - numarul de alternative
	 * @return
	 */
	public float getFitnessSoQoS(int nrObj, boolean integerCoding, Individual ind, String activitiesUsed, int nrAlternatives, byte aggregateType){
		float rez = 0;
		
		double a1 = 0.5;
		double a2 = 0.5;
		double a3 = 0.4;
//		double a1 = 1.0/3.0;
//		double a2 = 1.0/3.0;
//		double a3 = 1.0/3.0;
		
		int[] genome;
		double respTime = 0;
		double availability = 1;
		double throughput = 1;
		int nrGene = 0;

		if (integerCoding) {
			IntegerVectorIndividual temp = (IntegerVectorIndividual) ind;
			genome = temp.genome;
		} else {
			DoubleVectorIndividual temp = (DoubleVectorIndividual) ind;
			int [] indici = new int [temp.genome.length];
			for(int i=0;i<indici.length;i++)
				indici[i] = Math.min(nrAlternatives - 1, (int)Math.abs(Math.round(temp.genome[i] * nrAlternatives)));
			genome = indici;
		}
		
		nrGene = genome.length;
		
		if (activitiesUsed.equalsIgnoreCase(SIMPLU)) {
			MacroPlan plan = MacroPlan.getDefault();
			Iterator<MacroActivity> iteratie = plan.getGraph().vertexSet().iterator();
			
			for (int i = 0; i < nrGene; i++) {
				BaseActivity ba = MacroActivitiesXMLReader.getInstance().getBaseActivityByIndex(iteratie.next(),
								ConfigurationProperties.getCurrentMacroDefinitions(), genome[i]);
				respTime += ba.getResponseTime();
				availability += ba.getAvailability();
				throughput *= ba.getThroughput();
			}
		} else if(activitiesUsed.equalsIgnoreCase(BPEL) || activitiesUsed.compareTo(BPEL_REAL)==0 || activitiesUsed.compareTo(BPEL_REAL_NORMALIZED)==0){
			//bestGenomeEvaluator
			BPELProcess p = BpelProcessSingleton.getDefaultProcess();
			List<BPELInvoke> invokes = p.services();
			Map<BPELInvoke, QoSModel> qosMap = new HashMap<BPELInvoke, QoSModel>();
			

			for (int i = 0; i < genome.length; i ++) {
				QoSModel q = MacroActivitiesXMLReader.getInstance().getBaseActivityByIndex(invokes.get(i).name, ConfigurationProperties.getCurrentMacroDefinitions(), genome[i]);
				qosMap.put(invokes.get(i), q);
			}
			
			respTime = p.aggregateQos(p, qosMap).getResponseTime();
			availability = p.aggregateQos(p, qosMap).getAvailability();
			throughput = p.aggregateQos(p, qosMap, AggregateType.MEAN).getThroughput();
		}
		
		if(nrObj == 2){
			if(aggregateType == AGGREGATE_ADDITIVE) {
				rez = (float) (a1 * throughput + a2 * availability);
			} else if (aggregateType == AGGREGATE_CANFORA) {
				rez = (float) ((a1 * throughput + a2 * availability));
			} else if (aggregateType == AGGREGATE_LOGARITMIC) {
				rez = (float) (Functions.lg.apply(a1 * throughput, 10.0) + Functions.lg.apply(a2 * availability, 10.0));
			}
		} else if(nrObj == 3){
			if(aggregateType == AGGREGATE_ADDITIVE) {
				rez = (float) (a1 * throughput + a2 * availability + a3 * (1.0/respTime));
			} else if (aggregateType == AGGREGATE_CANFORA) {
				rez = (float) ((a1 * throughput + a2 * availability) / (a3 * respTime));
			} else if (aggregateType == AGGREGATE_LOGARITMIC) {
				rez = (float) ((Functions.lg.apply(a1 * throughput, 10.0) + Functions.lg.apply(a2 * availability, 10.0)) / (Functions.lg.apply(a3 * respTime, 10.0)));
			}
		} else{
			System.err.println("in functia de agregare nr obiectivelor > 3");
			System.exit(1);
		}
		
		return rez;
	}
	
	/**
	 * Calculez functia de adecvare multi-obiectiv pentru problema compunerii
	 * serviciilor.
	 * 
	 * <p> Cromozomul este codat ca si un vector de intregi unde gena apartine
	 * [0,nrServConcrete-1] MacroActivitiesXX.xml, nrAlterntive = XX allele
	 * imi da indexul dupa care citesc serv concret cu
	 * MacroActivitiesXMLReader allele le iau din genome
	 * </p>
	 * 
	 * @param nrObj
	 * @param integerCoding
	 *            - <b>true</b> =>algoritmul fol este nsga2 sau spea2 - alg ce
	 *            fol codare intreaga, <b>false</b> => alg bazati pe DE
	 * @param ind
	 *            - individul pt care calculez functia de adecvare
	 * @param activitiesUsed
	 *            - numarul de activitati
	 * @param nrAlternatives
	 *            - numarul de alternative
	 * @return
	 */
	@SuppressWarnings("unused")
	public float[] getFitnessMooQoS(int nrObj, boolean integerCoding,
			Individual ind, String activitiesUsed, int nrAlternatives) {

		float[] obiective = new float[nrObj];
		int[] genome;
		double respTime = 0;
		double availability = 1;
		double throughput = 1;
		double latency = 0;
		int nrGene = 0;

		if (integerCoding) {
			IntegerVectorIndividual temp = (IntegerVectorIndividual) ind;
			genome = temp.genome;
		} else {
			DoubleVectorIndividual temp = (DoubleVectorIndividual) ind;
			int [] indici = new int [temp.genome.length];
			for(int i=0;i<indici.length;i++)
				indici[i] = Math.min(nrAlternatives - 1, (int)Math.abs(Math.round(temp.genome[i] * nrAlternatives)));
			genome = indici;
		}
		
		nrGene = genome.length;
		
		if (activitiesUsed.equalsIgnoreCase(SIMPLU)) {
			MacroPlan plan = MacroPlan.getDefault();
			Iterator<MacroActivity> iteratie = plan.getGraph().vertexSet().iterator();
			
			for (int i = 0; i < nrGene; i++) {
				BaseActivity ba = MacroActivitiesXMLReader.getInstance().getBaseActivityByIndex(iteratie.next(),
								ConfigurationProperties.getCurrentMacroDefinitions(), genome[i]);
				respTime += ba.getResponseTime();
				availability += ba.getAvailability();
				throughput *= ba.getThroughput();
			}
		} else if(activitiesUsed.equalsIgnoreCase(BPEL) || activitiesUsed.compareTo(BPEL_REAL)==0 || activitiesUsed.compareTo(BPEL_REAL_NORMALIZED)==0){
			//bestGenomeEvaluator
			BPELProcess p = BpelProcessSingleton.getDefaultProcess();
			List<BPELInvoke> invokes = p.services();
			Map<BPELInvoke, QoSModel> qosMap = new HashMap<BPELInvoke, QoSModel>();

			for (int i = 0; i < genome.length; i ++) {
				QoSModel q = MacroActivitiesXMLReader.getInstance().getBaseActivityByIndex(invokes.get(i).name, ConfigurationProperties.getCurrentMacroDefinitions(), genome[i]);
				qosMap.put(invokes.get(i), q);
			}
			
			respTime = p.aggregateQos(p, qosMap).getResponseTime();
			availability = p.aggregateQos(p, qosMap).getAvailability();
			throughput = p.aggregateQos(p, qosMap).getThroughput();
			latency = p.aggregateQos(p, qosMap).getLatency();
		}
		
		switch (nrObj) {
		case 2:
			obiective[0] = (float) -throughput; //max prob
			obiective[1] = (float) -availability; //max probl
//			obiective[0] = (float) respTime; //max prob
//			obiective[1] = (float) latency; //max probl
			break;
		case 3:
			obiective[0] = (float) -throughput; 
			obiective[1] = (float) -availability;
			obiective[2] = (float) respTime; //min probl
			break;
		}
		
		return obiective;
	}
	
	/**
	 * Aplic transformarea Lorenz obiectivelor mele
	 * 
	 * @param maxProblem
	 * @param objectives
	 */
	public float[] applyLorenz(boolean maxProblem, float[] objectives){
		int nrObj = objectives.length;
		float[] lorenzObj = new float[nrObj];
		
		for(int i=0;i<nrObj;i++){
			lorenzObj[i] = objectives[i];//(float)Functions.abs.apply((double)objectives[i]);
		}
		
		//ordonez
		float aux = 0;
		for(int i=0;i<nrObj-1;i++){
			for(int j=i+1;j<nrObj;j++){
				if(maxProblem){
					if(lorenzObj[i] > lorenzObj[j]){
						aux = lorenzObj[i];
						lorenzObj[i] = lorenzObj[j];
						lorenzObj[j] = aux;
					}
				}else{
					if(lorenzObj[i] < lorenzObj[j]){
						aux = lorenzObj[i];
						lorenzObj[i] = lorenzObj[j];
						lorenzObj[j] = aux;
					}
				}
			}
		}
		
		//fac suma obiectivelor
		for(int i=1;i<nrObj;i++){
			lorenzObj[i] += lorenzObj[i-1];
		}
		
//		for(int i=0;i<nrObj;i++)
//			lorenzObj[i] = -lorenzObj[i];
		
		return lorenzObj;
	}

}
