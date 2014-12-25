package org.shepherd.monitored.task;

/**
 * This Exception is thrown when a root object cannot be created in an Abstract Expression Task
 * 
 * @author DavidM
 * @since Dec 24, 2014
 */
public class RootObjectNotCreatedException extends RuntimeException {

	/*

	 */
	private static final long serialVersionUID = 1L;

	public RootObjectNotCreatedException() {}

	public RootObjectNotCreatedException(Throwable cause) {
		super(cause);
	}

}
