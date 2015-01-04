package org.shepherd.monitored.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * 
 * @author davidm
 * @since Jan 1, 2015
 * @version 0.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamDisplayName {

	public int index();

	public String displayName();

	public boolean passwordField() default false;

}
