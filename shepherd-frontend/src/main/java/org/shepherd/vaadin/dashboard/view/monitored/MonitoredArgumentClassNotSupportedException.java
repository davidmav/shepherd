package org.shepherd.vaadin.dashboard.view.monitored;

import java.text.MessageFormat;

/**
 * Thrown when a Monitored Object Constructor has an argument type that is not supported
 * 
 * @author davidm
 * @since Jan 3, 2015
 * @version 0.1.0
 */
public class MonitoredArgumentClassNotSupportedException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE = "{0} is not supported and cannot be converted from String";

	public MonitoredArgumentClassNotSupportedException(Class clas) {
		super(MessageFormat.format(MESSAGE, clas.getName()));
	}

}
