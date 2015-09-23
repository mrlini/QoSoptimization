package org.utcluj.model;

import java.util.ArrayList;
import java.util.List;

public class MacroMedicalPlan {

	private static MacroMedicalPlan defaultInstance = null;
	
	private MacroActivity startActivity = null;
	
	protected List<MacroActivity> activityList = new ArrayList<MacroActivity>();
	
	protected MacroMedicalPlan() {
		
	}
	
	public static MacroMedicalPlan getDefault() {
		
		if (defaultInstance == null)
			defaultInstance = new MacroMedicalPlan();
		
		return defaultInstance;
	}
	
	public void addActivity(MacroActivity a) {
		
		activityList.add(a);
		if (startActivity == null)
			startActivity = a;
	}
	
	public MacroActivity getStartActivity() {
		return startActivity;
	}
	
	public int getStepCount() {
		
		return activityList.size();
	}

	public List<MacroActivity> getActivityList() {
		return activityList;
	}
	
	public void setActivityList(List<MacroActivity> list) {
		activityList = list;
	}
	
}
