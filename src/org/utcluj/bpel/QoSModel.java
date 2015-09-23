package org.utcluj.bpel;

import java.text.DecimalFormat;

import org.utcluj.bpel.BPELActivity.AggregateType;


/**
 * 
 * A simple QoS model for Web Services. 
 * It contains the following QoS properties:
 * <ul>
 * <li>Response time</li> 
 * <li>Cost</li> 
 * <li>Rating</li>
 * <li>Availability</li>
 * <li>Throughput</li> 
 * <li>Successability</li> 
 * <li>Reliability</li>   
 * <li>Latency</li>
 * </ul>
 * @author Florin Pop amd Mihai Suciu
 *
 */
public class QoSModel {

	/**
	 * The response time QoS property.
	 */
	protected double responseTime;
	
	/**
	 * The cost QoS property.
	 */
	protected double cost;
	
	/**
	 * The rating QoS property.
	 */
	protected double rating;
	
	/**
	 * The availability QoS property.
	 */
	protected double availability;
	
	/**
	 * The throughput QoS property.
	 */
	protected double throughput;
	
	/**
	 * The successability QoS property.
	 */
	protected double successability;
	
	/**
	 * The reliability QoS property.
	 */
	protected double reliability;
	
	/**
	 * The latency QoS property.
	 */
	protected double latency;
	
	/**
	 * Gets the response time QoS property.
	 * 
	 * @return the response time.
	 */
	public double getResponseTime() {
		return responseTime;
	}

	/**
	 * Sets the response time QoS property.
	 * 
	 * @param responseTime the response time.
	 */
	public void setResponseTime(double responseTime) {
		this.responseTime = responseTime;
	}

	/**
	 * Gets the cost QoS property.
	 * 
	 * @return the cost.
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Sets the cost QoS property.
	 * 
	 * @param cost the cost.
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * Gets the rating QoS property.
	 * 
	 * @return the rating.
	 */
	public double getRating() {
		return rating;
	}

	/**
	 * Sets the rating QoS property.
	 * 
	 * @param rating the rating.
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}
	
	
		
	public double getAvailability() {
		return availability;
	}

	public void setAvailability(double availability) {
		this.availability = availability;
	}

	public double getThroughput() {
		return throughput;
	}

	public void setThroughput(double throughput) {
		this.throughput = throughput;
	}

	public double getSuccessability() {
		return successability;
	}

	public void setSuccessability(double successability) {
		this.successability = successability;
	}

	public double getReliability() {
		return reliability;
	}

	public void setReliability(double reliability) {
		this.reliability = reliability;
	}

	public double getLatency() {
		return latency;
	}

	public void setLatency(double latency) {
		this.latency = latency;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		DecimalFormat doubleFormat = new DecimalFormat("#.##");
		
		return "[cost=" + doubleFormat.format(cost) + ", r=" + doubleFormat.format(rating) + ", t=" + doubleFormat.format(responseTime) + ", a=" + doubleFormat.format(availability) + ", thr=" + doubleFormat.format(throughput) + ", s=" + doubleFormat.format(successability) + ", reliab=" + doubleFormat.format(reliability) + ", latency=" + doubleFormat.format(latency)+ "]"; // + ", p=" + twoDForm.format(probability) + ", k=" + multiplier + "]";
	}

	/**
	 * Generates an empty QoS model with a given aggregation type.
	 * 
	 * @return the empty QoS model.
	 */
	public static QoSModel emptyQoSModel(BPELActivity.AggregateType aggregateType) {
		
		QoSModel result = new QoSModel();
		
		if (aggregateType == AggregateType.CANFORA)
			result.setRating(1);
		
		return result;
	}
	
	/**
	 * Generates an empty QoS model.
	 * 
	 * @return the empty QoS model.
	 */
	public static QoSModel emptyQoSModel() {
		
		return emptyQoSModel(AggregateType.CANFORA);
	}
}
