package com.pyramix.swi.webui.common;

public class SuppressableStacktraceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1821043257335605085L;

	private boolean suppressStacktrace = false;

	public SuppressableStacktraceException(String message, boolean suppressStacktrace) {
		super(message, null, suppressStacktrace, !suppressStacktrace);
		
		this.suppressStacktrace = suppressStacktrace;
	}

	@Override
	public String toString() {
		if (suppressStacktrace) {
	    	
			return getLocalizedMessage();
	    } else {
	        
	    	return super.toString();
	    }
	}
	
	
}
