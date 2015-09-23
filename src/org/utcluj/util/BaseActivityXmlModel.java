package org.utcluj.util;

import org.utcluj.fitness.GlobalFitness;
import org.utcluj.model.BaseActivity;

@SuppressWarnings("unused")
public class BaseActivityXmlModel {
	private String category, name, description;
	private double cost, rating, responseTime, availability, throughput,
			successability, reliability, score, latency;

	public BaseActivityXmlModel(String category, String name,
			String description, double cost, double rating,
			double responseTime, double availability, double throughput,
			double successability, double reliability, double latency) {
		this.category = category;
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.rating = rating;
		this.responseTime = responseTime;
		this.availability = availability;
		this.throughput = throughput;
		this.successability = successability;
		this.reliability = reliability;
		this.latency = latency;
		
		//TODO sa am grija ce eu am modificat si am pus scorul sa fie 0
//		this.score = GlobalFitness.evaluateFitness(toBaseActivity());
		this.score = 0;

	}

	public BaseActivity toBaseActivity() {

		BaseActivity result = new BaseActivity();

		result.setCategory(category);
		result.setCost(cost);
		result.setName(name);
		result.setRating(rating);
		result.setResponseTime(responseTime);
		result.setAvailability(availability);
		result.setThroughput(throughput);
		result.setSuccessability(successability);
		result.setReliability(reliability);
		result.setScore(score);
		result.setLatency(latency);

		return result;
	}
}
