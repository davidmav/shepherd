package org.shepherd.monitored.repository;

import java.util.Collection;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoringTask;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author DavidM
 *
 */
public interface Repository {
	
	/**
	 * 
	 * @param m
	 */
	@Autowired(required = true)
	public void setMonitored(Collection<Monitored> m);
	
	/**
	 * 
	 * @param m
	 */
	@Autowired
	public void setMonitoringTasks(Collection<MonitoringTask> m);
	
	/**
	 * 
	 * @param monitoredItemName
	 * @return
	 */
	public Monitored getMonitored(String monitoredItemName);
	
	/**
	 * 
	 * @param monitored
	 */
	public void addMonitored(Monitored monitored);
	
	/**
	 * 
	 * @param monitored
	 */
	public void removeMonitored(Monitored monitored);
	
	/**
	 * 
	 * @return
	 */
	public Collection<MonitoringTask> getMonitoringTasks();
	
	/**
	 * 
	 * @param monitoringTask
	 */
	public void addMonitoringTask(MonitoringTask monitoringTask);
	
	/**
	 * 
	 * @param monitoringTask
	 */
	public void removeMonitoringTask(MonitoringTask monitoringTask);
	
}
