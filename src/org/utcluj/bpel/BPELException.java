package org.utcluj.bpel;

/**
 * 
 * This exception is thrown by the classes that work with BPEL processes.
 * 
 * @author Florin Pop
 *
 */
public class BPELException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor.
	 */
	public BPELException() {
	
		super();
	}
	
	/**
	 * Creates an exception with a given message.
	 * 
	 * @param message the exception message.
	 */
	public BPELException(String message) {
		
		super(message);
	}

}
