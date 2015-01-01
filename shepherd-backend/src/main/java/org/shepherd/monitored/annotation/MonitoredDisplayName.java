package org.shepherd.monitored.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 
 * @author davidm
 * @since Jan 1, 2015
 * @version 0.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface MonitoredDisplayName {

	public String value();
}
