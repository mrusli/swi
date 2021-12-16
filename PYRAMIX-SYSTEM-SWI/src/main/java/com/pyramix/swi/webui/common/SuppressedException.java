package com.pyramix.swi.webui.common;

public class SuppressedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1821043257335605085L;

	/**
	 * 	ref: https://stackoverflow.com/questions/11434431/exception-without-stack-trace-in-java
	 * 	Previously: 	
	 * 		IllegalArgumentException suppresedException = new IllegalArgumentException("No Journals for this Voucher");
	 * 		suppresedException.setStackTrace(new StackTraceElement[0]);
	 * 
	 * @param errorMessage
	 */
	private boolean suppressStackTrace = false;
	
	public SuppressedException(String errorMessage, boolean suppressStackTrace) {
		super(errorMessage, null, suppressStackTrace, !suppressStackTrace);
		
		this.suppressStackTrace = suppressStackTrace;
	}
	
	public String toString() {
		if (suppressStackTrace) {
			return getLocalizedMessage();
		} else {
			return super.toString();
		}
	}
}
