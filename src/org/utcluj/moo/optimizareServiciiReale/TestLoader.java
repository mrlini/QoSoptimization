package org.utcluj.moo.optimizareServiciiReale;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.utcluj.bpel.BPELInvoke;
import org.utcluj.bpel.BPELProcess;
import org.utcluj.bpel.BPELProcessFactory;
import org.utcluj.bpel.QoSModel;
import org.utcluj.io.MacroActivitiesXMLReader;
import org.utcluj.util.ConfigurationProperties;

import cern.jet.math.Functions;

public class TestLoader {

	public static void main(String[] args) {
		try{
			//test incarcare plan
			Params.getInstance().resetMinMax();
			
//			random_process.bpel
//			BpelProcesses/BpelProcess5.bpel
			
//			ConfigurationProperties.setCurrentMacroDefinitions("MacroActivitiesRealNormalized/MacroActivitiesRealNormalized");
			ConfigurationProperties.setCurrentMacroDefinitions("MacroActivitiesReal/MacroActivitiesReal");
			BPELProcess p = BPELProcessFactory.fromFile("BpelProcesses/BpelProcess5.bpel");
			System.out.println(p.servicesCount());
			
//			System.out.println("MacroActivitiesReal/MacroActivitiesReal");
//			System.out.println(ConfigurationProperties.getCurrentMacroDefinitions().substring(15, 29));
			
			List<BPELInvoke> invokes = p.services();
			Map<BPELInvoke, QoSModel> qosMap = new HashMap<BPELInvoke, QoSModel>();
			
			for(BPELInvoke i: invokes){
				System.out.println(i);
				QoSModel q = MacroActivitiesXMLReader.getInstance().getBaseActivityByIndex(i.toString(), "BpelRealActivities/BpelRealActivities5.xml", 0);
				String rez = q.toString();
				System.out.println("throughput: "+q.getThroughput());
				System.out.println("availability: "+q.getAvailability());
				System.out.println(rez);
				qosMap.put(i, q);
			}
			double responseTime = p.aggregateQos(p, qosMap).getResponseTime();
			System.out.println("response Time: "+responseTime);
			double thr = p.aggregateQos(p, qosMap).getThroughput();
			System.out.println("Throughput: "+thr);
			System.out.println(Functions.lg.apply(100.0, 10.0));
			System.out.println(Functions.lg.apply(0.9, 10.0));
			
			//afisez valorile minime si maxime din toate posibilitatile pentru throughput, availability si response time
			System.out.println("min values");
			for (double val : Params.getMinAttr()) {
				System.out.print(val+", ");
			}
			System.out.println();
			System.out.println("max values");
			for (double val : Params.getMaxAttr()) {
				System.out.print(val+", ");
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

}
