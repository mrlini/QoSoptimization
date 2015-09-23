package org.utcluj.bpel;

import java.text.DecimalFormat;

import org.utcluj.bpel.BPELActivity.AggregateType;


/**
 * 
 * A simple QoS model for Medical Services. 
 * It contains the following QoS properties:
 * <ul>
 * <li>Response time</li> 
 * <li>Cost</li> 
 * <li>Rating</li> 
 * </ul>
 * @author Florin Pop
 *
 */
public class QoSMedicalModel extends QoSModel {
	
	protected double latitude;
	
	protected double longitude;
	
	protected double distance;	
	
	public QoSMedicalModel() {}
	
	public QoSMedicalModel(QoSModel partialModel) {

		responseTime = partialModel.responseTime;
		rating = partialModel.rating;
		cost = partialModel.cost;
		availability = partialModel.getAvailability();
		throughput = partialModel.getThroughput();
		successability = partialModel.getSuccessability();
		reliability = partialModel.getReliability();
		latency = partialModel.getLatency();
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		
		DecimalFormat doubleFormat = new DecimalFormat("#.##");
		
//		return "[c=" + doubleFormat.format(cost) + ", r=" + doubleFormat.format(rating) + ", t=" + doubleFormat.format(responseTime) + ", d=" + doubleFormat.format(distance) + "]"; // + ", p=" + twoDForm.format(probability) + ", k=" + multiplier + "]";
		return "[cost=" + doubleFormat.format(cost) + ", r=" + doubleFormat.format(rating) + ", t=" + doubleFormat.format(responseTime) + ", a=" + doubleFormat.format(availability) + ", thr=" + doubleFormat.format(throughput) + ", s=" + doubleFormat.format(successability) + ", reliab=" + doubleFormat.format(reliability) + "]"; // + ", p=" + twoDForm.format(probability) + ", k=" + multiplier + "]";
		
	}

	/**
	 * Generates an empty QoS model with a given aggregation type.
	 * 
	 * @return the empty QoS model.
	 */
	public static QoSMedicalModel emptyQoSModel(BPELActivity.AggregateType aggregateType) {
		
		QoSMedicalModel result = new QoSMedicalModel();
		
		if (aggregateType == AggregateType.CANFORA)
			result.setRating(1);
		
		return result;
	}
	
	/**
	 * Generates an empty QoS model.
	 * 
	 * @return the empty QoS model.
	 */
	public static QoSMedicalModel emptyQoSModel() {
		
		return emptyQoSModel(AggregateType.CANFORA);
	}
}
