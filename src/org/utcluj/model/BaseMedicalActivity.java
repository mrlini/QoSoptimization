package org.utcluj.model;

public class BaseMedicalActivity extends MacroActivity {

	/**
	 * The name of this activity.
	 */
	private String name;
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public MacroActivity clone() {
		
		BaseMedicalActivity a = new BaseMedicalActivity();
		a.cost = cost;
		a.name = name;
		a.rating = rating;
		a.responseTime = responseTime;
		a.distance = distance;
		a.setCategory(getCategory());
		return a;
	}
}
