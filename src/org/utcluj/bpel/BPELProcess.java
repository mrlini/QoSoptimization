package org.utcluj.bpel;

/**
 * 
 * The model for a BPEL business process.
 * 
 *  @see <a href="http://docs.oasis-open.org/wsbpel/2.0/wsbpel-specification-draft.html#_Toc143402847">Web Services Business Process Execution Language Version 2.0</a>
 * 
 * @author Florin Pop
 *
 */

public class BPELProcess extends BPELActivity {
	
	/**
	 * Default constructor.
	 */
	public BPELProcess() {
		name = "abstract";
	}
	
	/**
	 * The target namespace of this business process.
	 */
	public String targetNamespace = "ro.utcluj";
	
	/**
	 * The xml namespace.
	 */
	public String xmlNS = "http://docs.oasis-open.org/wsbpel/2.0/process/abstract";
}
