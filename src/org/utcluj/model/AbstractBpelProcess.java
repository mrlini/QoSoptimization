package org.utcluj.model;

import org.utcluj.bpel.BPELProcess;

public class AbstractBpelProcess {

	private static AbstractBpelProcess defaultInstance = null;
	
	/**
	 * The plan as a graph.
	 */
	private BPELProcess process;
	
	protected AbstractBpelProcess() {
		
	}
	
	public static AbstractBpelProcess getDefault() {
		
		if (defaultInstance == null)
			defaultInstance = new AbstractBpelProcess();
		
		return defaultInstance;
	}
	
	public int getServicesCount() {
		
		return process.servicesCount();
	}

	public BPELProcess getProcess() {
		return process;
	}

	public void setProcess(BPELProcess process) {
		this.process = process;
	}

}
