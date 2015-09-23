package org.utcluj.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.utcluj.fitness.GlobalFitness;
import org.utcluj.model.BaseActivity;

public class MacroActivityCollection {
	private List<BaseActivityXmlModel> activitati = new ArrayList<BaseActivityXmlModel>();

	public void add(BaseActivityXmlModel act) {
		activitati.add(act);
	}

	public List<BaseActivityXmlModel> getBaseActivities() {
		return activitati;
	}
	
	public void sort() {
		
		Collections.sort(activitati, new Comparator<BaseActivityXmlModel>() {

			@Override
			public int compare(BaseActivityXmlModel a1, BaseActivityXmlModel a2) {
								
				List<BaseActivity> activityList = new ArrayList<BaseActivity>();
				activityList.add(a1.toBaseActivity());
				
				double fitness1 = GlobalFitness.evaluateFitness(GlobalFitness.aggregateQoS(activityList));
				
				activityList.clear();
				activityList.add(a2.toBaseActivity());
				
				double fitness2 = GlobalFitness.evaluateFitness(GlobalFitness.aggregateQoS(activityList));
				
				return Double.compare(fitness1, fitness2);
			}
		});
	}
}
