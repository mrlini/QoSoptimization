package org.utcluj.io;

/*Iulian Benta 12.08.2010
 * 
 * Clasa de citire a activitatilor concrete corespunzatoare unei Macro-activitati
 * */

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.utcluj.model.BaseActivity;
import org.utcluj.model.BaseMedicalActivity;
import org.utcluj.model.MacroActivity;
import org.utcluj.moo.optimizareServiciiReale.Params;
import org.utcluj.util.ConfigurationProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MacroActivitiesXMLReader {

	private static final MacroActivitiesXMLReader INSTANCE = new MacroActivitiesXMLReader();// singleton

	private Map<String, Map<String, Vector<BaseActivity>>> activityFileMap = new HashMap<String, Map<String,Vector<BaseActivity>>>();

	private Map<String, Map<String, Vector<BaseMedicalActivity>>> medicalActivityFileMap = new HashMap<String, Map<String,Vector<BaseMedicalActivity>>>();

	// caching
	static {
		
		String nume = "";
		String numeClasic = "MacroActivities/MacroActivities"; // numele fisierului xml
		String numeReal = "MacroActivitiesReal/MacroActivitiesReal"; // numele fisierului xml
		String numeRealNormalized = "MacroActivitiesRealNormalized/MacroActivitiesRealNormalized"; // numele fisierului xml
		
		String s1 = ConfigurationProperties.getCurrentMacroDefinitions().substring(4, 8);
		String s2 = ConfigurationProperties.getCurrentMacroDefinitions().substring(15, 19);
		if(s1.compareTo("Real")==0 || s2.compareTo("Real")==0){
			if (ConfigurationProperties.getCurrentMacroDefinitions().substring(4, 18).compareTo("RealNormalized")==0 || ConfigurationProperties.getCurrentMacroDefinitions().substring(15, 29).compareTo("RealNormalized")==0) {
				nume = numeRealNormalized;
			} else {
				nume = numeReal;
			}
		} else {
			nume = numeClasic;
		}
		
		// int n = 5; // nr activitatilor concrete/MacroActivitate
		for (int n = 5; n < 105; n = n + 5) {
			// numele MacroActivitatilor
			String[] numeMA = { "FindNearestAirportService",
					"ProposeMedicalFlightService",
					"CreateMedicalFlightAccountService",
					"BookMedicalFlightService" };

			for (int i = 0; i < numeMA.length; i++) {
				
				BaseActivity ba = new BaseActivity();
				ba.setCategory(numeMA[i]);

				getInstance().getAllBaseActivities(ba, nume + n + ".xml");
			}
		}
		
	}
	
	private MacroActivitiesXMLReader() {
	}

	public static MacroActivitiesXMLReader getInstance() {
		return INSTANCE;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private Vector<BaseActivity> loadActivityMap(String numeF) {
		
		Vector<BaseActivity> base_act_vector = new Vector<BaseActivity>();

		try {
			// Fisierul XML in care sunt descrise activitatile concrete
			// corespunzatoare macroactivitatilor
			File file = new File(numeF);
			// Se pregateste citirea din fisierul XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			// System.out.println("Root element "
			// + doc.getDocumentElement().getNodeName());
			NodeList nodeLstBA = doc.getElementsByTagName("BaseActivity");

			// Se parcurg toate macroactivitatile din fisierul XML
			for (int i = 0; i < nodeLstBA.getLength(); i++) {

				Node fstNode = nodeLstBA.item(i);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element fstElmnt = (Element) fstNode;
					NodeList BActCatElmntLst = fstElmnt.getElementsByTagName("category");
					Element BActCatElmnt = (Element) BActCatElmntLst.item(0);
					Node b_act_cat = BActCatElmnt.getChildNodes().item(0);

					//if (b_act_cat.getTextContent().trim().equalsIgnoreCase(
							//ba.getCategory())) {

//						BaseActivity base_act = (BaseActivity) Class.forName(
//								"org.utcluj.model.service." + b_act_cat.getTextContent().trim())
//								.newInstance();
					BaseActivity base_act = new BaseActivity();
					base_act.setCategory(b_act_cat.getTextContent().trim());
						
					Element secElmnt = (Element) fstNode;

					NodeList BActNmElmntLst = secElmnt.getElementsByTagName("name");
					Element BActNmElmnt = (Element) BActNmElmntLst.item(0);
					NodeList b_act_name = BActNmElmnt.getChildNodes();
					// se adauga numele pentru activitatea concreta curenta
					base_act.setName(((Node) b_act_name.item(0)).getNodeValue());
					
//					 System.out.println("Base Activity name : "
//					 + ((Node) b_act_name.item(0))
//					 .getNodeValue());

					NodeList BActCostElmntLst = secElmnt.getElementsByTagName("cost");
					Element BActCostElmnt = (Element) BActCostElmntLst.item(0);
					NodeList b_act_cost = BActCostElmnt.getChildNodes();
					// se adauga costul la activitatea concreta curenta
					base_act.setCost(Double.parseDouble(((Node) b_act_cost.item(0)).getNodeValue()));
					
//					 System.out.println("Base Activity cost : "
//					 + ((Node) b_act_cost.item(0))
//					 .getNodeValue());

					NodeList BActRatingElmntLst = secElmnt.getElementsByTagName("rating");
					Element BActRatingElmnt = (Element) BActRatingElmntLst.item(0);
					NodeList b_act_rating = BActRatingElmnt.getChildNodes();
					// se adauga ratingul la activitatea concreta curenta
					base_act.setRating(Double.parseDouble(((Node) b_act_rating.item(0)).getNodeValue()));
					
//					 System.out.println("Base Activity rating : "
//					 + ((Node) b_act_rating.item(0))
//					 .getNodeValue());

					NodeList BActRespTmElmntLst = secElmnt.getElementsByTagName("responseTime");
					Element BActRespTmElmnt = (Element) BActRespTmElmntLst.item(0);
					NodeList b_act_resp_tm = BActRespTmElmnt.getChildNodes();
					// se adauga timpul de raspuns la activitatea concreta curenta
					base_act.setResponseTime(Double.parseDouble(((Node) b_act_resp_tm.item(0)).getNodeValue()));
					
//					System.out.println("Base Activity responseTime : "
//							 + ((Node) b_act_resp_tm.item(0))
//							 .getNodeValue());
					
					if(ConfigurationProperties.getCurrentMacroDefinitions().substring(4, 8).compareTo("Real")==0 || ConfigurationProperties.getCurrentMacroDefinitions().substring(15, 19).compareTo("Real")==0){
						NodeList BActAvailabilityElmntLst = secElmnt.getElementsByTagName("availability");
						Element BActAvailabilityElmnt = (Element) BActAvailabilityElmntLst.item(0);
						NodeList b_act_availability = BActAvailabilityElmnt.getChildNodes();
						// se adauga timpul de raspuns la activitatea concreta curenta
						base_act.setAvailability(Double.parseDouble(((Node) b_act_availability.item(0)).getNodeValue()));
						
//						System.out.println("Base Activity availability : "
//								 + ((Node) b_act_availability.item(0))
//								 .getNodeValue());
						
						NodeList BActThroughputElmntLst = secElmnt.getElementsByTagName("throughput");
						Element BActThroughputElmnt = (Element) BActThroughputElmntLst.item(0);
						NodeList b_act_throughput = BActThroughputElmnt.getChildNodes();
						// se adauga timpul de raspuns la activitatea concreta curenta
						base_act.setThroughput(Double.parseDouble(((Node) b_act_throughput.item(0)).getNodeValue()));
						
//						System.out.println("Base Activity throughput : "
//								 + ((Node) b_act_throughput.item(0))
//								 .getNodeValue());
						
						NodeList BActSuccessabilityElmntLst = secElmnt.getElementsByTagName("successability");
						Element BActSuccessabilityElmnt = (Element) BActSuccessabilityElmntLst.item(0);
						NodeList b_act_successability = BActSuccessabilityElmnt.getChildNodes();
						// se adauga timpul de raspuns la activitatea concreta curenta
						base_act.setSuccessability(Double.parseDouble(((Node) b_act_successability.item(0)).getNodeValue()));
						
//						System.out.println("Base Activity successability : "
//								 + ((Node) b_act_successability.item(0))
//								 .getNodeValue());
						
						NodeList BActReliabilityElmntLst = secElmnt.getElementsByTagName("reliability");
						Element BActReliabilityElmnt = (Element) BActReliabilityElmntLst.item(0);
						NodeList b_act_reliability = BActReliabilityElmnt.getChildNodes();
						// se adauga timpul de raspuns la activitatea concreta curenta
						base_act.setReliability(Double.parseDouble(((Node) b_act_reliability.item(0)).getNodeValue()));
						
//						System.out.println("Base Activity reliability : "
//								 + ((Node) b_act_reliability.item(0))
//								 .getNodeValue());
						
						NodeList BActLatencyElmntLst = secElmnt.getElementsByTagName("latency");
						Element BActLatencyElmnt = (Element) BActLatencyElmntLst.item(0);
						NodeList b_act_latency = BActLatencyElmnt.getChildNodes();
						// se adauga timpul de raspuns la activitatea concreta curenta
						base_act.setLatency(Double.parseDouble(((Node) b_act_latency.item(0)).getNodeValue()));
						
						/*
						 * MIHAI
						 * 
						 * iau valorile minime si maxime pentru normalizare, atributele QoS considerate sunt throughput, 
						 * availability si response time
						 */
						//throughput
						if (base_act.getThroughput() < Params.getSpMinAttr(0)) {
							Params.setSpMinAttr(0, base_act.getThroughput());
						}
						if (base_act.getThroughput() > Params.getSpMaxAttr(0)) {
							Params.setSpMaxAttr(0, base_act.getThroughput());
						}
						//availabiity
						if (base_act.getAvailability() < Params.getSpMinAttr(1)) {
							Params.setSpMinAttr(1, base_act.getAvailability());
						}
						if (base_act.getAvailability() > Params.getSpMaxAttr(1)) {
							Params.setSpMaxAttr(1, base_act.getAvailability());
						}
						//response time
						if (base_act.getResponseTime() < Params.getSpMinAttr(2)) {
							Params.setSpMinAttr(2, base_act.getResponseTime());
						}
						if (base_act.getResponseTime() > Params.getSpMaxAttr(2)) {
							Params.setSpMaxAttr(2, base_act.getResponseTime());
						}
						
						
					}
						
					NodeList BActScoreElmntLst = secElmnt.getElementsByTagName("score");
					Element BActScoreElmnt = (Element) BActScoreElmntLst.item(0);
					NodeList b_act_score = BActScoreElmnt.getChildNodes();
					base_act.setScore(Double.parseDouble(((Node) b_act_score.item(0)).getNodeValue()));


					base_act_vector.addElement(base_act);


					// System.out.println("test"+
					// base_act.getName());

				}// end if

			}// end for i
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return base_act_vector;
	}
	
	private Vector<BaseMedicalActivity> loadMedicalActivityMap(String numeF) {
		
		Vector<BaseMedicalActivity> base_act_vector = new Vector<BaseMedicalActivity>();

		try {
			// Fisierul XML in care sunt descrise activitatile concrete
			// corespunzatoare macroactivitatilor
			File file = new File(numeF);
			// Se pregateste citirea din fisierul XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			// System.out.println("Root element "
			// + doc.getDocumentElement().getNodeName());
			NodeList nodeLstBA = doc.getElementsByTagName("BaseMedicalActivity");

			// Se parcurg toate macroactivitatile din fisierul XML
			for (int i = 0; i < nodeLstBA.getLength(); i++) {

				Node fstNode = nodeLstBA.item(i);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element fstElmnt = (Element) fstNode;
					NodeList BActCatElmntLst = fstElmnt
							.getElementsByTagName("category");
					Element BActCatElmnt = (Element) BActCatElmntLst.item(0);
					Node b_act_cat = BActCatElmnt.getChildNodes().item(0);

					//if (b_act_cat.getTextContent().trim().equalsIgnoreCase(
							//ba.getCategory())) {

						BaseMedicalActivity base_act = new BaseMedicalActivity();
						base_act.setCategory(b_act_cat.getTextContent().trim());

						Element secElmnt = (Element) fstNode;

						NodeList BActNmElmntLst = secElmnt
								.getElementsByTagName("name");
						Element BActNmElmnt = (Element) BActNmElmntLst.item(0);
						NodeList b_act_name = BActNmElmnt.getChildNodes();
						base_act.setName(((Node) b_act_name.item(0))
								.getNodeValue());// se adauga numele pentru
						// activitatea concreta
						// curenta

						// System.out.println("\t\t\tBase Activity name : "
						// + ((Node) b_act_name.item(0))
						// .getNodeValue());

						NodeList BActCostElmntLst = secElmnt
								.getElementsByTagName("cost");
						Element BActCostElmnt = (Element) BActCostElmntLst
								.item(0);
						NodeList b_act_cost = BActCostElmnt.getChildNodes();
						base_act.setCost(Double.parseDouble(((Node) b_act_cost
								.item(0)).getNodeValue()));// se adauga costul
						// la activitatea
						// concreta curenta

						// System.out.println("\t\t\tBase Activity cost : "
						// + ((Node) b_act_cost.item(0))
						// .getNodeValue());

						NodeList BActRatingElmntLst = secElmnt
								.getElementsByTagName("rating");
						Element BActRatingElmnt = (Element) BActRatingElmntLst
								.item(0);
						NodeList b_act_rating = BActRatingElmnt.getChildNodes();
						base_act.setRating(Double
								.parseDouble(((Node) b_act_rating.item(0))
										.getNodeValue()));// se adauga ratingul
						// la activitatea
						// concreta curenta

						// System.out.println("\t\t\tBase Activity rating : "
						// + ((Node) b_act_rating.item(0))
						// .getNodeValue());

						NodeList BActRespTmElmntLst = secElmnt
								.getElementsByTagName("responseTime");
						Element BActRespTmElmnt = (Element) BActRespTmElmntLst
								.item(0);
						NodeList b_act_resp_tm = BActRespTmElmnt
								.getChildNodes();
						base_act.setResponseTime(Double
								.parseDouble(((Node) b_act_resp_tm.item(0))
										.getNodeValue()));// se adauga timpul de
						// raspuns la
						// activitatea
						// concreta curenta

						// System.out.println("\t\t\tBase Activity response time : "
						// + ((Node) b_act_resp_tm.item(0))
						// .getNodeValue());

						Element latitudeElement = (Element) secElmnt.getElementsByTagName("latitude").item(0);
				
						base_act.setLatitude(Double
						.parseDouble(((Node) latitudeElement.getFirstChild())
								.getNodeValue()));
						
						Element longitudeElement = (Element) secElmnt.getElementsByTagName("longitude").item(0);
						
						base_act.setLongitude(Double
						.parseDouble(((Node) longitudeElement.getFirstChild())
								.getNodeValue()));
						
						
						base_act_vector.addElement(base_act);


						// System.out.println("test"+
						// base_act.getName());

					}// end if

			}// end for i
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return base_act_vector;
	}
	
	/**
	 * 
	 * @param ba
	 * @param numeF
	 *            - fisierul care contine activitatile concrete
	 * @return
	 */
	public Vector<BaseActivity> getAllBaseActivities(MacroActivity ba,
			String numeF) {

		Map<String, Vector<BaseActivity>> activityMap = activityFileMap.get(numeF);
		
		if (activityMap == null) {
			
			activityMap = new HashMap<String, Vector<BaseActivity>>();
			
			Vector<BaseActivity> actVector = loadActivityMap(numeF);
			for (BaseActivity a : actVector) {
				
				Vector<BaseActivity> actVectorByCategory = activityMap.get(a.getCategory());
				if (actVectorByCategory == null) {
					
					actVectorByCategory = new Vector<BaseActivity>();
				}
				actVectorByCategory.add(a);
				activityMap.put(a.getCategory(), actVectorByCategory);
			}
			activityFileMap.put(numeF, activityMap);
		}
		
		// Parametrul este activitatea de baza pentru care se doreste returnarea
		// unui vectori cu toate activitatile de baza posibile


		return activityFileMap.get(numeF).get(ba.getCategory());// se raspunde cu vectorul activitatilor concrete
	}
	
	public Vector<BaseMedicalActivity> getAllBaseMedicalActivities(MacroActivity ba,
			String numeF) {

		Map<String, Vector<BaseMedicalActivity>> activityMap = medicalActivityFileMap.get(numeF);
		
		if (activityMap == null) {
			
			activityMap = new HashMap<String, Vector<BaseMedicalActivity>>();
			
			Vector<BaseMedicalActivity> actVector = loadMedicalActivityMap(numeF);
			for (BaseMedicalActivity a : actVector) {
				
				Vector<BaseMedicalActivity> actVectorByCategory = activityMap.get(a.getCategory());
				if (actVectorByCategory == null) {
					
					actVectorByCategory = new Vector<BaseMedicalActivity>();
				}
				actVectorByCategory.add(a);
				activityMap.put(a.getCategory(), actVectorByCategory);
			}
			medicalActivityFileMap.put(numeF, activityMap);
		}
		
		// Parametrul este activitatea de baza pentru care se doreste returnarea
		// unui vectori cu toate activitatile de baza posibile


		return medicalActivityFileMap.get(numeF).get(ba.getCategory());// se raspunde cu vectorul activitatilor concrete
	}
	
	public BaseActivity getBaseActivityByIndex(MacroActivity ba,
			String numeF, int index) {
	
		return getAllBaseActivities(ba, numeF).get(index);
	}
	
	public BaseActivity getBaseActivityByIndex(String category,
			String numeF, int index) {
	
		BaseActivity ba = new BaseActivity();
		ba.setCategory(category);
		
		return getAllBaseActivities(ba, numeF).get(index);
	}
	
	public BaseActivity getBaseActivityByScore(MacroActivity ba,
			String numeF, double score) {
	
		BaseActivity result = getAllBaseActivities(ba, numeF).get(0);
		double dScore = Math.abs(score - result.getScore());
		
		for (BaseActivity act : getAllBaseActivities(ba, numeF)) {
			
			if (Math.abs(score - act.getScore()) < dScore)
				result = act;
		}
		
		return result;
	}
	
	public BaseMedicalActivity getBaseMedicalActivityByIndex(String category,
			String numeF, int index) {
	
		BaseActivity ba = new BaseActivity();
		ba.setCategory(category);
		
		return getAllBaseMedicalActivities(ba, numeF).get(index);
	}
	
	public BaseMedicalActivity getBaseMedicalActivityByIndex(MacroActivity ba,
			String numeF, int index) {
	
		return getAllBaseMedicalActivities(ba, numeF).get(index);
	}
}
