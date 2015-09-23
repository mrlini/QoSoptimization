package org.utcluj.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.utcluj.io.MacroActivitiesXMLReader;
import org.utcluj.util.ConfigurationProperties;

public class BaseMedicalPlan extends MacroMedicalPlan {
	
	private String macroDefinitionsPath;
	
	protected BaseMedicalPlan() {
		super();
	}
	
	public BaseMedicalPlan(List<BaseMedicalActivity> activityList) {
		super();
		
		if (activityList == null)
			return;
		
		this.activityList.addAll(activityList);
		
	}
	
	public String getSourcePath() {
		
		return macroDefinitionsPath;
	}
	
	public static BaseMedicalPlan newRandom(MacroMedicalPlan plan) {
		
		return newRandom(plan, ConfigurationProperties.getCurrentMacroDefinitions());
	}
	
	/**
	 * 
	 * @param plan - planul care va fi optimizat
	 * @param nameMacroA - fisierul care contine activitatile concrete
	 * @return
	 */
	public static BaseMedicalPlan newRandom(MacroMedicalPlan plan, String defPath) {
		
		BaseMedicalPlan result = new BaseMedicalPlan();
		result.macroDefinitionsPath = defPath;
		
		List<MacroActivity> oldGraph = plan.getActivityList();
		
		for (MacroActivity a : oldGraph) {
			
			// possible choices
			Vector<BaseMedicalActivity> choices = MacroActivitiesXMLReader.getInstance().getAllBaseMedicalActivities(a, result.macroDefinitionsPath);
//			System.out.println(a.getCategory() + " has " + choices.size() + " choices");
			
			// random choice
			BaseMedicalActivity choice = (BaseMedicalActivity)choices.get(new Random().nextInt(choices.size())).clone();
			
			result.addActivity(choice);
		}
		
		return result;
	}
	
	public void randomVertex(int index) {
		
		Vector<BaseMedicalActivity> choices = MacroActivitiesXMLReader.getInstance().getAllBaseMedicalActivities(getActivityList().get(index), macroDefinitionsPath);
		// random choice
		BaseMedicalActivity choice = choices.get(new Random().nextInt(choices.size()));
		
		getActivityList().remove(index);
		getActivityList().add(index, choice);
	}
	

	public BaseMedicalActivity getVertexAt(int index) {
		
		return (BaseMedicalActivity) getActivityList().get(index);
	}
	
	public boolean equals(BaseMedicalPlan otherPlan) {

		Iterator<MacroActivity> i2 = otherPlan.getActivityList().iterator();
		
		for (MacroActivity ma1 : getActivityList()) {
			
			MacroActivity ma2 = i2.next();
			
			if (!((BaseMedicalActivity) ma1).getName().equalsIgnoreCase(((BaseMedicalActivity) ma2).getName())) {
				
				return false;
			}
		}
		
		return true;
	}
	
	public List<BaseMedicalActivity> toBaseMedicalActivityList() {
		
		List<BaseMedicalActivity> result = new ArrayList<BaseMedicalActivity>();
		
		Iterator<MacroActivity> i = getActivityList().iterator();
		
		while (i.hasNext()) {
			
			BaseMedicalActivity a = ((BaseMedicalActivity) i.next());
			result.add(a);
		}
		
		return result;
	}
	
	public List<MacroActivity> toMacroActivityList() {
		
		return getActivityList();
	}
	
	public BaseMedicalPlan clone() {
		
		return new BaseMedicalPlan(toBaseMedicalActivityList());
	}
	
	public String toString() {
		
		String result = "";
		
		Iterator<MacroActivity> i = getActivityList().iterator();
		
		while (i.hasNext()) {
			
			result += ((BaseMedicalActivity) i.next()).getName() + "";
			
			if (i.hasNext())
				result += " --> ";
		}
		
		return result;
	}
}
