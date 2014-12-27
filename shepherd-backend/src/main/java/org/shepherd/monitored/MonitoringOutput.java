package org.shepherd.monitored;

import java.util.Date;

public interface MonitoringOutput {

	public static enum Severity {
		ERROR,
		WARN,
		INFO
	}

	public Monitored getMonitored();

	public MonitoringTask getMonitoringTask();

	public Severity getSeverity();

	public String getMessage();

	public Date getTimestamp();

}
