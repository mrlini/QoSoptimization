package org.utcluj.bpel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.utcluj.moo.optimizareServiciiReale.Params;

import cern.jet.math.Functions;

/**
 * 
 * WS-BPEL activities that perform the process logic.
 * 
 * @see <a href="http://docs.oasis-open.org/wsbpel/2.0/wsbpel-specification-draft.html#_Toc143402870">Web Services Business Process Execution Language Version 2.0</a>
 * 
 * @author Florin Pop
 *
 */

public class BPELActivity {

	public static enum AggregateType {
		CANFORA, MEAN, AGGREGATE_MIHAI
	};

	/**
	 * The name attribute of the activity.
	 */
	public String name;

	/**
	 * The list of inner activities.
	 */
	public List<BPELActivity> activities = new ArrayList<BPELActivity>();

	/**
	 * The list of inner services invocations.
	 */
	protected List<BPELInvoke> services = null;

	/**
	 * The parent activity.
	 */
	protected BPELActivity parent = null;
	
	/*
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		return name;
	}

	/**
	 * Recursively builds the list of inner services invocations.
	 * 
	 * @param result the generated list.
	 * @param activities the current inner activity list.
	 */
	protected void computeServicesList(List<BPELInvoke> result, List<BPELActivity> activities) {

		if (activities != null) {

			for (BPELActivity activity : activities) {

				if (activity instanceof BPELInvoke) {

					result.add((BPELInvoke) activity);
				} else {

					computeServicesList(result, activity.activities);
				}
			}
		}
	}

	/**
	 * Recursively builds the list of inner activities.
	 * 
	 * @param result the generated list.
	 * @param activities the current inner activity list.
	 * @param parent the parent of the current inner activity list.
	 */
	protected void computeActivityList(List<BPELActivity> result, List<BPELActivity> activities, BPELActivity parent) {

		if (activities != null) {

			for (BPELActivity activity : activities) {

				activity.parent = parent;
				result.add(activity);

				computeActivityList(result, activity.activities, activity);

			}
		}
	}
	
	/**
	 * Gets the list of inner activities.
	 * 
	 * @return the list of inner activities.
	 */
	public List<BPELActivity> activities() {
		
		List<BPELActivity> activityList = new ArrayList<BPELActivity>();
		computeActivityList(activityList, activities, this);
		
		return activityList;
	}

	/**
	 * Gets the list of inner services invocations.
	 * 
	 * @return the services list.
	 */
	public List<BPELInvoke> services() {

		//if (services == null) {

		services = new ArrayList<BPELInvoke>();
		computeServicesList(services, activities);
		//}

		return services;
	}

	/**
	 * Gets the number of inner services invocations.
	 * 
	 * @return the services invocations count.
	 */
	public int servicesCount() {

		if (services() == null) {

			return 0;
		}

		return services().size();
	}

	/**
	 * Returns the QoS model that contains the aggregate QoS.
	 * 
	 * @param activity the activity for which the aggegated QoS is computed.
	 * @param servicesQoS the map of individual services QoS.
	 * @return the aggregated QoS model.
	 */
	public QoSModel aggregateQos(BPELActivity activity, Map<BPELInvoke, QoSModel> servicesQoS) {

		return aggregateQos(activity, servicesQoS, AggregateType.MEAN);
	}

	/**
	 * 
	 * Calculates the aggregate QoS.
	 * 
	 * @param activity the activity for which the aggregate QoS is evaluated.
	 * @param servicesQoS the map of services QoS models.
	 * 
	 * @return the aggregated QoS model.
	 */
	public QoSModel aggregateQos(BPELActivity activity, Map<BPELInvoke, QoSModel> servicesQoS, BPELActivity.AggregateType aggregateType) {

		QoSModel result = QoSModel.emptyQoSModel(aggregateType);
		result.setThroughput(Double.POSITIVE_INFINITY);
		result.setAvailability(1.0);
		result.setReliability(1.0);

		if (activity instanceof BPELProcess ||
				activity instanceof BPELSequence) {
			//SEQUENCE

			if (activity.activities == null)
				return result;

			for (BPELActivity a : activity.activities) {

				QoSModel cQoS = aggregateQos(a, servicesQoS, aggregateType);
				// sum
				result.setCost(result.getCost() + cQoS.getCost());
				if (aggregateType == AggregateType.CANFORA) {
					//System.out.println(result.rating + "*" + cQoS.rating);
					// prod
					result.setRating(result.getRating() * cQoS.getRating());


				} else if (aggregateType == AggregateType.MEAN) {
					// mean
					result.setRating(result.getRating() + cQoS.getRating());
				}
				result.setResponseTime(result.getResponseTime() + cQoS.getResponseTime());
				result.setLatency(result.getLatency() + cQoS.getLatency());
				//produce
				result.setAvailability(result.getAvailability() * cQoS.getAvailability());
				result.setReliability(result.getReliability() * cQoS.getReliability());
				//minimum
				result.setThroughput(Functions.min.apply(result.getThroughput(), cQoS.getThroughput()));
			}

			if (activity.activities.size() > 0 && aggregateType == AggregateType.MEAN) {
				// mean
				result.setRating(result.getRating() / (float)activity.activities.size());
			}


		} else if (activity instanceof BPELFlow) {
			//FLOW

			if (activity.activities != null) {
				for (BPELActivity a : activity.activities) {

					QoSModel cQoS = aggregateQos(a, servicesQoS, aggregateType);

					// sum
					result.setCost(result.getCost() + cQoS.getCost());
					if (aggregateType == AggregateType.CANFORA) {
						//System.out.println(result.rating + "*" + cQoS.rating);
						// prod
						result.setRating(result.getRating() * cQoS.getRating());


					} else if (aggregateType == AggregateType.MEAN) {
						// mean
						result.setRating(result.getRating() + cQoS.getRating());
					}
					//produce
					result.setAvailability(result.getAvailability() * cQoS.getAvailability());
					result.setReliability(result.getReliability() * cQoS.getReliability());
					// max
					result.setResponseTime(Math.max(result.getResponseTime(), cQoS.getResponseTime()));
					result.setLatency(Math.max(result.getLatency(), cQoS.getLatency()));
					//minimum
					result.setThroughput(Functions.min.apply(result.getThroughput(), cQoS.getThroughput()));
				}

				if (activity.activities.size() > 0 && aggregateType == AggregateType.MEAN) {
					// mean
					result.setRating(result.getRating() / (float)activity.activities.size());
				}
			}
		}else if (activity instanceof BPELCondition) {

			BPELCondition condition = (BPELCondition) activity;

			if (activity.activities != null)
				for (BPELActivity a : activity.activities) {

					QoSModel cQoS = aggregateQos(a, servicesQoS, aggregateType);

					if (!(a instanceof BPELCondition)) {

						// sum prob
						result.setCost(result.getCost() + condition.probability() * cQoS.getCost());

						if (aggregateType == AggregateType.CANFORA) {

							//System.out.println(result.rating + "*" + condition.probability + "*" + cQoS.rating);
							// sum prob
							result.setRating(result.getRating() * condition.probability() * cQoS.getRating());

						} else if (aggregateType == AggregateType.MEAN) {
							// min prob
							result.setRating(result.getRating() + condition.probability() * cQoS.getRating());
						}
						//availability
						result.setAvailability(result.getAvailability() * condition.probability() * cQoS.getAvailability());
						// sum prob
						result.setResponseTime(result.getResponseTime() + condition.probability() * cQoS.getResponseTime());
						result.setLatency(result.getLatency() + condition.probability() * cQoS.getLatency());
						//minimum
						result.setThroughput(Functions.min.apply(result.getThroughput(), cQoS.getThroughput()));
					} else {

						// sum
						result.setCost(result.getCost() + cQoS.getCost());
						if (aggregateType == AggregateType.CANFORA) {
							// prod

							//System.out.println(result.rating + "*" + cQoS.rating);
							result.setRating(result.getRating() + cQoS.getRating());
						} else if (aggregateType == AggregateType.MEAN) {
							// min
							result.setRating(result.getRating() + cQoS.getRating());
						}
						result.setAvailability(result.getAvailability() * cQoS.getAvailability());
						// sum
						result.setResponseTime(result.getResponseTime() + cQoS.getResponseTime());
						result.setLatency(result.getLatency() + cQoS.getLatency());
						//minimum
						result.setThroughput(Functions.min.apply(result.getThroughput(), cQoS.getThroughput()));
						
//						//availability
//						result.setAvailability(result.getAvailability() * condition.probability() * cQoS.getAvailability());
//						// sum prob
//						result.setResponseTime(result.getResponseTime() + condition.probability() * cQoS.getResponseTime());
//						//minimum
//						result.setThroughput(Functions.min.apply(result.getThroughput(), cQoS.getThroughput()));
					}

				}
		} 
		else if (activity instanceof BPELCycle) {

			BPELCycle cycle = (BPELCycle) activity;

			if (activity.activities != null)
				for (BPELActivity a : activity.activities) {

					QoSModel cQoS = aggregateQos(a, servicesQoS, aggregateType);

					// sum prob
					result.setCost(result.getCost() + cycle.repeat() * cQoS.getCost());
					if (aggregateType == AggregateType.CANFORA) {
						//System.out.println(result.rating + "*" + cQoS.rating + "^" + cycle.repeat());
						/// pow
						result.setRating(result.getRating() * Math.pow(cQoS.getRating(), cycle.repeat()));

					} else if (aggregateType == AggregateType.MEAN) {
						// min
						result.setRating(result.getRating() + cQoS.getRating());
					}				
					result.setAvailability(Functions.pow.apply(cQoS.getAvailability(), cycle.repeat()));
					// sum prob
					result.setResponseTime(result.getResponseTime() + cycle.repeat() * cQoS.getResponseTime());
					result.setLatency(result.getLatency() + cycle.repeat() * cQoS.getLatency());
					//minimum
					result.setThroughput(Functions.min.apply(result.getThroughput(), cQoS.getThroughput()));
				} 

		}
		else if (activity instanceof BPELInvoke) {

			return servicesQoS.get(activity);
		}

		return result;
	}

	public QoSMedicalModel aggregateMedicalQos(BPELActivity activity, Map<BPELInvoke, QoSMedicalModel> servicesQoS, double userLatitude, double userLongitude) {
		
		this.userLatitude = userLatitude;
		this.userLongitude = userLongitude;
		
		Map<BPELInvoke, QoSModel> partialServicesQoS = new HashMap<BPELInvoke, QoSModel>(); 
		for (BPELInvoke i : servicesQoS.keySet())
			partialServicesQoS.put(i, servicesQoS.get(i));
		
		QoSModel partialModel = aggregateQos(activity, partialServicesQoS, AggregateType.MEAN);

		QoSMedicalModel partialResult = new QoSMedicalModel(partialModel);
		QoSMedicalModel partialResultDistance = aggregateMedicalQos(activity, servicesQoS, AggregateType.MEAN);
		partialResult.setDistance(partialResultDistance.getDistance());
		
		return partialResult;
		
	}
	
	public double calculateDistance(double lat1, double long1, double lat2, double long2) {
		
		return Math.sqrt(Math.pow(lat1 - lat2, 2)
				+ Math.pow(long1 - long2, 2));
	}
	
	 double userLatitude;
	 double userLongitude; 
	
	public QoSMedicalModel aggregateMedicalQos(BPELActivity activity, Map<BPELInvoke, QoSMedicalModel> servicesQoS, BPELActivity.AggregateType aggregateType) {
		
		QoSMedicalModel result = QoSMedicalModel.emptyQoSModel(aggregateType);

		// Mai ramane de agregat distanta
		
		if (activity instanceof BPELInvoke) {

			result.distance = calculateDistance(userLatitude, userLongitude, servicesQoS.get(activity).getLatitude(), servicesQoS.get(activity).getLongitude());
			userLatitude = servicesQoS.get(activity).getLatitude();
			userLongitude = servicesQoS.get(activity).getLongitude();
			
		} else if (activity instanceof BPELProcess ||
				activity instanceof BPELSequence) {

			if (activity.activities == null)
				return result;

			for (BPELActivity a : activity.activities) {

				QoSMedicalModel cQoS = aggregateMedicalQos(a, servicesQoS, aggregateType);
				result.setDistance(result.getDistance() + cQoS.getDistance());

			}
				

		} else if (activity instanceof BPELFlow) {

			double maxLatitude = userLatitude;
			double maxLongitude = userLongitude;
			double maxDist = 0;
			
			if (activity.activities != null) {
				for (BPELActivity a : activity.activities) {

					// save user coordinates
					double sLatitude = userLatitude;
					double sLongitude = userLongitude;
					
					QoSMedicalModel cQoS = aggregateMedicalQos(a, servicesQoS, aggregateType);
					
					// max
					result.setDistance(Math.max(result.getDistance(), cQoS.getDistance()));
					
					if (cQoS.getDistance() > maxDist) {
						
						maxDist = cQoS.getDistance();
						maxLatitude = userLatitude;
						maxLongitude = userLongitude;
					}
					
					// restore user coordinates
					userLatitude = sLatitude;
					userLongitude = sLongitude;
				}
			}
			
			// restore user coordinates to the ones corresponding to the highest distance.
			userLatitude = maxLatitude;
			userLongitude = maxLongitude;
		} 
		else if (activity instanceof BPELCondition) {

			BPELCondition condition = (BPELCondition) activity;
			double maxLatitude = userLatitude;
			double maxLongitude = userLongitude;
			double maxProb = 0;

			if (activity.activities != null)
				for (BPELActivity a : activity.activities) {

					// save user coordinates
					double sLatitude = userLatitude;
					double sLongitude = userLongitude;
					
					QoSMedicalModel cQoS = aggregateMedicalQos(a, servicesQoS, aggregateType);

					if (!(a instanceof BPELCondition)) {

						// sum prob
						result.setDistance(result.getDistance() + condition.probability() * cQoS.getDistance());

						
					} else {

						// sum
						result.setDistance(result.getDistance() + cQoS.getDistance());
					}

					if (condition.probability() > maxProb) {
						
						maxProb = condition.probability();
						maxLatitude = userLatitude;
						maxLongitude = userLongitude;
					}
					
					// restore user coordinates
					userLatitude = sLatitude;
					userLongitude = sLongitude;
				}
			

			// restore user coordinates to the ones corresponding to the highest prob.
			userLatitude = maxLatitude;
			userLongitude = maxLongitude;
		} 
		else if (activity instanceof BPELCycle) {

			BPELCycle cycle = (BPELCycle) activity;

			if (activity.activities != null)
				for (BPELActivity a : activity.activities) {

					QoSMedicalModel cQoS = aggregateMedicalQos(a, servicesQoS, aggregateType);

					// sum prob
					result.setDistance(result.getDistance() + cycle.repeat() * cQoS.getDistance());
				}
		}

		return result;
	}

	/**
	 * Adds an activity to this business process. If the process is empty a sequence is first added.
	 * 
	 * @param activity the activity to add.
	 */
	public void addActivity(BPELActivity activity) {

		if (activities.size() == 0) {

			activities.add(new BPELSequence());
			activities.get(0).activities.add(activity);
		} else {		
			activities.get(0).activities.add(activity);
		}
	}

	/**
	 * Adds an activity to the given parent that should be an inner activity of this business process.
	 * 
	 * @param activity the activity to add.
	 * @param parent the parent activity.
	 * @throws BPELException if the parent activity is not found.
	 */
	public void addActivity(BPELActivity activity, BPELActivity parent) throws BPELException {

		if (parent == null) {

			addActivity(activity);
			return;
		}

		List<BPELActivity> activityList = new ArrayList<BPELActivity>();
		computeActivityList(activityList, activities, this);
		
		for (BPELActivity a : activityList) {

			if (a.equals(parent)) {
				a.addActivity(activity);
				return;
			}
		}

		throw new BPELException("Parent activity not found");
	}

	/**
	 * Inserts an activity before a given activity.
	 * 
	 * @param activity the activity to insert.
	 * @param before the activity before which the given activen activity is inserted.
	 * 
	 * @throws BPELException if the before activity is not found.
	 */
	public void insertActivity(BPELActivity activity, BPELActivity before) throws BPELException {

		if (before == null || !(before instanceof BPELInvoke)) {

			addActivity(activity);
			return;
		}

		List<BPELActivity> activityList = new ArrayList<BPELActivity>();
		computeActivityList(activityList, activities, this);
		
		for (BPELActivity a : activityList) {

			if (!(a instanceof BPELInvoke))
				continue;
			
			if (((BPELInvoke)a).type.equals(((BPELInvoke)before).type)) {
				a.parent.activities.add(a.parent.activities.indexOf(a), activity);
				return;
			}
		}

		throw new BPELException("Before activity not found");
	}
}
