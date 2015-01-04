package org.shepherd.monitored.beans.registrar;

/**
 * This Exception is thrown when trying to save a bean with name of a bean that's already exists and this bean is of another
 * class.
 * 
 * @author davidm
 * @since Jan 4, 2015
 * @version 0.1.0
 */
public class BeanOfOtherClassAlreadyExistsException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

}
