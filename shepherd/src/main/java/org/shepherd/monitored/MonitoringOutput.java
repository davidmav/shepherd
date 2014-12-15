package org.shepherd.monitored;

public interface MonitoringOutput {
	
	public static enum OutputColor {
		GREEN, YELLOW, RED
	}
	
	public Monitored getMonitored();
	
	public MonitoringTask getMonitoringTask();
	
	public OutputColor getOutputColor();
	
	public String getMessage();

}
