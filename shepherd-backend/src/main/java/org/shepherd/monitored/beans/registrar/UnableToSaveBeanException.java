package org.shepherd.monitored.beans.registrar;

/**
 * 
 * 
 * @author davidm
 * @since Jan 4, 2015
 * @version 0.1.0
 */
public class UnableToSaveBeanException extends RuntimeException {

	/*

	 */
	private static final long serialVersionUID = 1L;

	public UnableToSaveBeanException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		return getCause().getMessage();
	}

}
