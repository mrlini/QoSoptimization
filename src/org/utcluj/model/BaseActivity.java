package org.utcluj.model;

public class BaseActivity extends MacroActivity {

	/**
	 * The name of this activity.
	 */
	private String name;
	
	private double score;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public MacroActivity clone() {
		
		BaseActivity a = new BaseActivity();
		a.cost = cost;
		a.name = name;
		a.rating = rating;
		a.responseTime = responseTime;
		a.availability = availability;
		a.throughput = throughput;
		a.successability = successability;
		a.reliability = reliability;
		a.latency = latency;
		a.setCategory(getCategory());
		return a;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
}
