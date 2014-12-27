package org.shepherd.monitored;

import com.jcabi.aspects.Loggable;

/**
 * 
 * @author DavidM
 *
 */
public interface MonitoringAgent extends Runnable {
	
	/**
	 * 
	 * @return
	 */
	@Loggable(Loggable.DEBUG)
	public Monitored getMonitored();
	
	/**
	 * 
	 * @return
	 */
	@Loggable(Loggable.DEBUG)
	public MonitoringTask getMonitoringTask();
	
}
