package org.shepherd.monitored;

import java.io.IOException;

/**
 * This exception is thrown when a connection to the monitored application cannot be established.
 * 
 * @author davidm
 * @since Jan 1, 2015
 * @version 0.1.0
 */
public class MonitoredException extends IOException {

	private static final long serialVersionUID = 1L;

	public MonitoredException(String message, Throwable cause) {
		super(message, cause);
	}

}
