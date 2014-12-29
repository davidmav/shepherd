package org.shepherd.monitored;

public interface MonitoringTask<T extends Monitored> {

	public T getMonitored();

	public MonitoringOutput<T> runMonitor();

}
