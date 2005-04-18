/*
 * Created on Apr 18, 2005 by peterg : 
 * TemplateReadException.java in edu.wpi.mqp.napkin for MQP
 * 
 */
package edu.wpi.mqp.napkin;


/**
 * TemplateReadException: Thrown when there is an exception while trying to read a Template
 * 
 * @author peterg
 */
public class TemplateReadException extends Exception {

	/**
	 * 
	 */
	public TemplateReadException() {
		super();
	}

	/**
	 * @param message
	 */
	public TemplateReadException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public TemplateReadException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TemplateReadException(String message, Throwable cause) {
		super(message, cause);
	}

}