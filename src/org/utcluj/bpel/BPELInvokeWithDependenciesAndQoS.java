package org.utcluj.bpel;

import java.util.ArrayList;
import java.util.List;


/**
 * This is an extension of the activity is used to call Web Services offered by service providers.
 * This extension adds some non-standard properties to the invoke activity, such as dependencies (i.e. preconditions) and a QoS model.
 * 
 * @author Florin Pop
 *
 */
public class BPELInvokeWithDependenciesAndQoS extends BPELInvoke {
	
	/**
	 * The service types on which this service depends on.
	 */
	public List<String> dependencies = new ArrayList<String>();
	
	/**
	 * The QoS properties of this service.
	 */
	public QoSModel qosModel = new QoSModel();
	
	/**
	 * Converts this non-standard invoke activity to the BPEL standard.
	 * 
	 * @return the BPEL activity.
	 */
	public BPELInvoke toBPELInvoke() {
		
		BPELInvoke result = new BPELInvoke();
		result.activities = activities;
		result.name = name;
		result.type = type;
		
		return result;
	}
}
