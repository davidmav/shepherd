package org.shepherd.monitored;

import java.util.Date;

public interface MonitoringOutput<T extends Monitored> {

	public static enum Severity {
		ERROR,
		WARN,
		INFO
	}

	public T getMonitored();

	public MonitoringTask<T> getMonitoringTask();

	public Severity getSeverity();

	public String getMessage();

	public Date getTimestamp();

}
