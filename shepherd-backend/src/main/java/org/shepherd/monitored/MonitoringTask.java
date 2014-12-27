package org.shepherd.monitored;

public interface MonitoringTask {
	
	public Monitored getMonitored();
	
	public MonitoringOutput runMonitor();

}
