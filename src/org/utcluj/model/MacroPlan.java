package org.utcluj.model;

import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

public class MacroPlan {

	private static MacroPlan defaultInstance = null;
	
	/**
	 * The plan as a graph.
	 */
	private DirectedGraph<MacroActivity, DefaultEdge> graph = new DirectedAcyclicGraph<MacroActivity, DefaultEdge>(DefaultEdge.class);
	
	private MacroActivity startActivity = null;
	
	protected MacroPlan() {
		
	}
	
	public static MacroPlan getDefault() {
		
		if (defaultInstance == null)
			defaultInstance = new MacroPlan();
		
		return defaultInstance;
	}
	
	public void addActivity(MacroActivity a) {
		
		graph.addVertex(a);
		if (startActivity == null)
			startActivity = a;
	}
	
	public void addEdge(MacroActivity a, MacroActivity b) {
		
		graph.addEdge(a, b);
	}

	public DirectedGraph<MacroActivity, DefaultEdge> getGraph() {
		
		return graph;
	}
	
	protected void setGraph(DirectedGraph<MacroActivity, DefaultEdge> graph) {
		
		this.graph = graph;
	}

	public MacroActivity getStartActivity() {
		return startActivity;
	}
	
	public int getStepCount() {
		
		return graph.vertexSet().size();
	}

}
