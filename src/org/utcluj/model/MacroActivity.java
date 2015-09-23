package org.utcluj.model;

import org.utcluj.bpel.QoSMedicalModel;
import org.utcluj.bpel.QoSModel;

/**
 * 
 * An general purpose activity.
 * 
 * @author Florin Pop
 *
 */
public abstract class MacroActivity extends QoSMedicalModel {
//public abstract class MacroActivity extends QoSModel {

	/**
	 * The type of the activity (i.e. airport locator, flight booking)
	 */
	private String category;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public abstract MacroActivity clone();
}
