package org.utcluj.io;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.utcluj.bpel.BPELProcessFactory;
import org.utcluj.model.AbstractBpelProcess;
import org.utcluj.model.BaseMedicalActivity;
import org.utcluj.model.MacroActivity;
import org.utcluj.model.MacroMedicalPlan;
import org.utcluj.model.MacroPlan;
import org.utcluj.util.ConfigurationProperties;
import org.utcluj.util.XMLDocumentReader;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class PlanLoader {

	private final static String XPATH_PREFIX = "/*/plan/step";
	
	private final static String PDDL_PLAN_STEP_URI_ATTRIBUTE = "name";
	
	public static void main(String [] args) {
		
		try {
			System.out.println(PlanLoader.loadPDDLPlan("plan.xml", 5));
			
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static MacroPlan loadPDDLPlan(String path) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, URISyntaxException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	
		return loadPDDLPlan(path, 5);
	}
	
	public static MacroPlan loadPDDLPlan(String path, int taskCount) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, URISyntaxException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		// Read document
		Document d = XMLDocumentReader.read(path);
		XPath x = XPathFactory.newInstance().newXPath();
		
		// Find steps
		XPathExpression e = x.compile(XPATH_PREFIX);
		NodeList result = (NodeList) e.evaluate(d, XPathConstants.NODESET);
		
		int len = result.getLength();
		
		//System.out.println("Plan has " + len + " steps");
		
		MacroPlan plan = MacroPlan.getDefault();
		MacroActivity lastActivity = null;
		List<MacroActivity> activityList = new ArrayList<MacroActivity>();
		
		// For each step
		for (int i = 1; i <= Math.min(len, taskCount); i ++) {
			
			if (plan.getStepCount() >= taskCount)
				break;
			
			XPathExpression expr = x.compile(XPATH_PREFIX + "[" + i + "]/@" + PDDL_PLAN_STEP_URI_ATTRIBUTE);
			URI uri = new URI(expr.evaluate(d));
			
			String category = uri.getFragment();
			
			MacroActivity a = MacroActivitiesClassLoader.loadByCategory(category);			
			plan.addActivity(a);
			
			if (lastActivity != null) {
				plan.addEdge(lastActivity, a);
			}
			
			lastActivity = a;
			activityList.add(a);
		}
		
		while (plan.getStepCount() < taskCount) {
			
			for (MacroActivity a : activityList) {
				MacroActivity aa = a.clone(); 
				plan.addActivity(aa);
				plan.addEdge(lastActivity, aa);
				lastActivity = aa;
				
				if (plan.getStepCount() >= taskCount)
					break;
			}
		}
	
		return plan;
	}
	
	public static MacroMedicalPlan loadMedicalPDDLPlan(String path, int taskCount) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, URISyntaxException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		//System.out.println("Loaded " + path);
		
		// Read document
		Document d = XMLDocumentReader.read(path);
		XPath x = XPathFactory.newInstance().newXPath();
		
		// Find steps
		XPathExpression e = x.compile(XPATH_PREFIX);
		NodeList result = (NodeList) e.evaluate(d, XPathConstants.NODESET);
		
		int len = result.getLength();
		
		//System.out.println("Plan has " + len + " steps");
		
		MacroMedicalPlan plan = MacroMedicalPlan.getDefault();
		plan.setActivityList(new ArrayList<MacroActivity>());

		List<MacroActivity> activityList = new ArrayList<MacroActivity>();
		
		int i = 0;
		int j = 0;
		
		while (plan.getStepCount() < taskCount) {
		
//		// For each step
//		for (int i = 1; i <= Math.min(len, taskCount); i ++) {
//			
//			if (plan.getStepCount() >= taskCount)
//				break;
//			
//			XPathExpression expr = x.compile(XPATH_PREFIX + "[" + i + "]/@" + PDDL_PLAN_STEP_URI_ATTRIBUTE);
//			URI uri = new URI(expr.evaluate(d));
//			
//			String category = uri.getFragment();
//			
//			MacroActivity a = new BaseMedicalActivity();
//			a.setCategory(category);
//			plan.addActivity(a);
//			
//			activityList.add(a);
//		}
			String category = "MedicalService_" + (char)(65 + i) + "" + (char)(65 + j);
			
			MacroActivity a = new BaseMedicalActivity();
			a.setCategory(category);
			plan.addActivity(a);
			
			activityList.add(a);
			
			j ++;
			if (j >= 26) {
				j = 0;
				i ++;
			}
		}
		
		return plan;
	}
	
	public static AbstractBpelProcess loadBpelProcess(String path) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, URISyntaxException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		AbstractBpelProcess result = AbstractBpelProcess.getDefault();
		
		result.setProcess(BPELProcessFactory.fromFile(path));
		
		return result;
	}
	
	public static MacroPlan loadPDDLPlan() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, URISyntaxException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		return loadPDDLPlan(ConfigurationProperties.getPddlPath());
	}
}
