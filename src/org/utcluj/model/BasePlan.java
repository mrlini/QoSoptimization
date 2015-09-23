package org.utcluj.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import org.utcluj.io.MacroActivitiesXMLReader;
import org.utcluj.util.ConfigurationProperties;

import ec.util.MersenneTwisterFast;

public class BasePlan extends MacroPlan {

	private String macroDefinitionsPath;

	protected BasePlan() {
		super();

	}

	public BasePlan(List<BaseActivity> activityList) {
		super();

		if (activityList == null)
			return;

		MacroActivity lastActivity = null;

		// For each step
		for (BaseActivity ba : activityList) {

			ba = (BaseActivity) ba.clone();

			addActivity(ba);

			if (lastActivity != null) {
				addEdge(lastActivity, ba);
			}

			lastActivity = ba;
		}

	}

	public String getSourcePath() {

		return macroDefinitionsPath;
	}

	public static BasePlan newRandom(MacroPlan plan) {

		return newRandom(plan, ConfigurationProperties
				.getCurrentMacroDefinitions());
	}

	/**
	 * 
	 * @param plan
	 *            - planul care va fi optimizat
	 * @param nameMacroA
	 *            - fisierul care contine activitatile concrete
	 * @return
	 */
	public static BasePlan newRandom(MacroPlan plan, String defPath) {

		BasePlan result = new BasePlan();
		result.macroDefinitionsPath = defPath;
		MersenneTwisterFast randomgen = new MersenneTwisterFast();
		randomgen.setSeed(System.nanoTime());

		DirectedGraph<MacroActivity, DefaultEdge> newGraph = result.getGraph();
		DirectedGraph<MacroActivity, DefaultEdge> oldGraph = plan.getGraph();

		Map<MacroActivity, BaseActivity> correspondeceMap = new HashMap<MacroActivity, BaseActivity>();

		for (MacroActivity a : oldGraph.vertexSet()) {

			// possible choices
			Vector<BaseActivity> choices = MacroActivitiesXMLReader
					.getInstance().getAllBaseActivities(a,
							result.macroDefinitionsPath);
			// System.out.println(a.getCategory() + " has " + choices.size() +
			// " choices");

			// random choice
			BaseActivity choice = (BaseActivity) choices.get(
					randomgen.nextInt(choices.size())).clone();
			correspondeceMap.put(a, choice);

			newGraph.addVertex(choice);
			// newGraph.addEdge(arg0, arg1)
		}

		// fix edges
		for (DefaultEdge e : oldGraph.edgeSet()) {

			newGraph.addEdge(correspondeceMap.get(oldGraph.getEdgeSource(e)),
					correspondeceMap.get(oldGraph.getEdgeTarget(e)));
		}

		return result;
	}

	public void randomVertex(int index) {

		Iterator<MacroActivity> i = getGraph().vertexSet().iterator();
		int j = 0;
		MacroActivity vertexToReplace = null;

		while (i.hasNext()) {

			MacroActivity currentVertex = i.next();
			if (j == index) {

				vertexToReplace = currentVertex;
			}
			j++;
		}

		Vector<BaseActivity> choices = MacroActivitiesXMLReader.getInstance()
				.getAllBaseActivities(vertexToReplace, macroDefinitionsPath);
		// random choice
		BaseActivity choice = choices.get(new Random().nextInt(choices.size()));

		DirectedGraph<MacroActivity, DefaultEdge> graph = getGraph();

		graph.addVertex(choice);

		MacroActivity vertexBefore = null;
		MacroActivity vertexAfter = null;
		DefaultEdge edgeBefore = null;
		DefaultEdge edgeAfter = null;

		Set<DefaultEdge> edgeSet = graph.edgeSet();

		for (DefaultEdge e : edgeSet) {

			if (graph.getEdgeSource(e).equals(vertexToReplace)) {
				vertexAfter = graph.getEdgeTarget(e);
				edgeBefore = e;
				// getGraph().removeEdge(e);
			}

			if (graph.getEdgeTarget(e).equals(vertexToReplace)) {
				vertexBefore = graph.getEdgeSource(e);
				edgeAfter = e;
				// getGraph().removeEdge(e);
			}
		}

		if (vertexBefore != null) {
			graph.addEdge(vertexBefore, choice);
			graph.removeEdge(edgeAfter);
		}

		if (vertexAfter != null) {
			graph.addEdge(choice, vertexAfter);
			graph.removeEdge(edgeBefore);
		}
		setGraph(graph);
	}

	public void replaceVertex(int index, BaseActivity a) {

		Iterator<MacroActivity> i = getGraph().vertexSet().iterator();
		int j = 0;
		MacroActivity vertexToReplace = null;

		while (i.hasNext()) {

			MacroActivity currentVertex = i.next();
			if (j == index) {

				vertexToReplace = currentVertex;
			}
			j++;
		}

		DirectedGraph<MacroActivity, DefaultEdge> graph = getGraph();

		graph.addVertex(a);

		MacroActivity vertexBefore = null;
		MacroActivity vertexAfter = null;
		DefaultEdge edgeBefore = null;
		DefaultEdge edgeAfter = null;

		Set<DefaultEdge> edgeSet = graph.edgeSet();

		for (DefaultEdge e : edgeSet) {

			if (graph.getEdgeSource(e).equals(vertexToReplace)) {
				vertexAfter = graph.getEdgeTarget(e);
				edgeBefore = e;
				// getGraph().removeEdge(e);
			}

			if (graph.getEdgeTarget(e).equals(vertexToReplace)) {
				vertexBefore = graph.getEdgeSource(e);
				edgeAfter = e;
				// getGraph().removeEdge(e);
			}
		}

		if (vertexBefore != null) {
			graph.addEdge(vertexBefore, a);
			graph.removeEdge(edgeAfter);
		}

		if (vertexAfter != null) {
			graph.addEdge(a, vertexAfter);
			graph.removeEdge(edgeBefore);
		}
		setGraph(graph);
	}

	public BaseActivity getVertexAt(int index) {

		Iterator<MacroActivity> i = getGraph().vertexSet().iterator();
		int j = 0;

		while (i.hasNext()) {

			MacroActivity currentVertex = i.next();
			if (j == index) {

				return (BaseActivity) currentVertex;
			}
			j++;
		}

		return null;
	}

	public boolean equals(BasePlan otherPlan) {

		Iterator<MacroActivity> i2 = otherPlan.getGraph().vertexSet()
				.iterator();

		for (MacroActivity ma1 : getGraph().vertexSet()) {

			MacroActivity ma2 = i2.next();

			if (!((BaseActivity) ma1).getName().equalsIgnoreCase(
					((BaseActivity) ma2).getName())) {

				return false;
			}
		}

		return true;
	}

	public double[] toDoubleVector() {

		List<Double> result = new ArrayList<Double>();

		DirectedGraph<MacroActivity, DefaultEdge> graph = getGraph();
		DepthFirstIterator<MacroActivity, DefaultEdge> i = new DepthFirstIterator(
				graph, getStartActivity());

		while (i.hasNext()) {

			BaseActivity a = ((BaseActivity) i.next());

			result.add(encodeDouble(a.getCost(), a.getResponseTime(), a
					.getRating()));
		}

		double[] d = new double[result.size()];
		for (int j = 0; j < result.size(); j++)
			d[j] = result.get(j);

		return d;
	}

	public List<BaseActivity> toBaseActivityList() {

		List<BaseActivity> result = new ArrayList<BaseActivity>();

		DirectedGraph<MacroActivity, DefaultEdge> graph = getGraph();
		DepthFirstIterator<MacroActivity, DefaultEdge> i = new DepthFirstIterator(
				graph, getStartActivity());

		while (i.hasNext()) {

			BaseActivity a = ((BaseActivity) i.next());
			result.add(a);
		}

		return result;
	}

	public List<MacroActivity> toUniqueMacroActivityList() {

		List<MacroActivity> result = new ArrayList<MacroActivity>();

		DirectedGraph<MacroActivity, DefaultEdge> graph = getGraph();
		DepthFirstIterator<MacroActivity, DefaultEdge> i = new DepthFirstIterator(
				graph, getStartActivity());

		while (i.hasNext()) {

			MacroActivity a = ((MacroActivity) i.next());

			boolean resultContainsA = false;

			for (MacroActivity ma : result) {
				if (ma.getClass().getSimpleName().equalsIgnoreCase(
						a.getClass().getSimpleName())) {

					resultContainsA = true;
					break;
				}
			}

			if (!resultContainsA)
				result.add(a);
		}

		return result;
	}

	public List<MacroActivity> toMacroActivityList() {

		List<MacroActivity> result = new ArrayList<MacroActivity>();

		DirectedGraph<MacroActivity, DefaultEdge> graph = getGraph();
		DepthFirstIterator<MacroActivity, DefaultEdge> i = new DepthFirstIterator(
				graph, getStartActivity());

		while (i.hasNext()) {

			MacroActivity a = ((MacroActivity) i.next());

			result.add(a);
		}

		return result;
	}

	public BasePlan clone() {

		return new BasePlan(toBaseActivityList());
	}

	public String toString() {

		String result = "";

		DirectedGraph<MacroActivity, DefaultEdge> graph = getGraph();
		DepthFirstIterator<MacroActivity, DefaultEdge> i = new DepthFirstIterator(
				graph, getStartActivity());

		while (i.hasNext()) {

			result += ((BaseActivity) i.next()).getName() + "";

			if (i.hasNext())
				result += " --> ";
		}

		return result;
	}

	public static double encodeDouble(double a, double b, double c) {

		double d = ((int) (a * Math.pow(10, 3)) * Math.pow(10, 6))
				+ ((int) (b * Math.pow(10, 3)) * Math.pow(10, 3))
				+ (int) (c * Math.pow(10, 3));

		// a = (int) (d / (double) Math.pow(10, 6)) / Math.pow(10, 3);
		//		
		// b = (int) (((d / (double) Math.pow(10, 6)) - (int) (d / (double) Math
		// .pow(10, 6))) * Math.pow(10, 3)) / Math.pow(10, 3);
		//		
		// c = ((d / Math.pow(10, 3)) - (long) (d / (double) Math.pow(10, 3))) ;
		// System.out.println(a + " " + b + " " + c);
		return d;
	}

	public static double decodeDouble(double d, int index) {

		if (index == 0) {

			return (int) (d / (double) Math.pow(10, 6)) / Math.pow(10, 3);
		} else if (index == 1) {

			return (int) (((d / (double) Math.pow(10, 6)) - (int) (d / (double) Math
					.pow(10, 6))) * Math.pow(10, 3))
					/ Math.pow(10, 3);
		} else {

			return ((d / Math.pow(10, 3)) - (long) (d / (double) Math
					.pow(10, 3)));
		}
	}
}
