package org.shepherd.monitored.provider;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoringTask;

import java.util.Collection;

/**
 * 
 * 
 * @author DavidM
 * @since Dec 28, 2014
 * @version 0.1.0
 */
public interface MonitoredProvider {

	/**
	 * 
	 * @since Dec 28, 2014
	 * @author DavidM
	 * @return
	 */
	public Collection<Class<Monitored>> getAllMonitoredClasses();

	/**
	 * 
	 * @since Dec 28, 2014
	 * @author DavidM
	 * @param monitoredClass
	 * @return
	 */
	public <T extends Monitored> Collection<Class<MonitoringTask<T>>> getAllMonitoringTaskClasses(Class<T> monitoredClass);

}
