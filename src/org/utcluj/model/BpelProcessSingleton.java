package org.utcluj.model;

import java.io.FileNotFoundException;

import org.utcluj.bpel.BPELProcess;
import org.utcluj.bpel.BPELProcessFactory;


public class BpelProcessSingleton {

	private static BPELProcess defaultProcess;
	
	public static BPELProcess getDefaultProcess() {
		if (defaultProcess == null) {
			
			try {
				defaultProcess = BPELProcessFactory.fromFile("random_process.bpel");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return defaultProcess;
	}
	
	public static void loadDefaultProcess(int servicesCount) {
		
			
			try {
				defaultProcess = BPELProcessFactory.fromFile("BpelProcesses/BpelProcess" + servicesCount + ".bpel");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}

	public static void loadDefaultMedicalProcess(int servicesCount) {
		
		
		try {
			defaultProcess = BPELProcessFactory.fromFile("BpelMedicalProcesses/BpelMedicalProcess" + servicesCount + ".bpel");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
	
}
}
