package org.utcluj.bpel;

/**
 * 
 * An activity that provides a cycle based on a condition.
 * 
 * @author Florin Pop
 *
 */
public class BPELCycle extends BPELActivity {

	/**
	 * The average number of cycles.
	 */
	public String repeat;

	/**
	 * Gets the average number of cycles.
	 * 
	 * @return the cycle count.
	 */
	public int repeat() {
		
		if (repeat == null)
			return 1;
		
		return Integer.parseInt(repeat);
	}
	
}
