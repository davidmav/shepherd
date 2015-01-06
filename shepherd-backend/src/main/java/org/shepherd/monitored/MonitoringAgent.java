package org.shepherd.monitored;

import com.jcabi.aspects.Loggable;

/**
 * 
 * @author DavidM
 *
 */
public interface MonitoringAgent<T extends Monitored> extends Runnable {

	/**
	 * 
	 * @return
	 */
	@Loggable(Loggable.DEBUG)
	public T getMonitored();

	/**
	 * 
	 * @return
	 */
	@Loggable(Loggable.DEBUG)
	public MonitoringTask<T> getMonitoringTask();

}
