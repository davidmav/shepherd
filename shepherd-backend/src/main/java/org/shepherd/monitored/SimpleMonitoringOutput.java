package org.shepherd.monitored;

import org.springframework.util.Assert;

import java.util.Date;

public class SimpleMonitoringOutput<T extends Monitored> implements MonitoringOutput<T> {

	private T monitored;
	private MonitoringTask<T> monitoringTask;
	private Severity severity;
	private String message;
	private Date timestamp;

	public SimpleMonitoringOutput(T monitored, MonitoringTask<T> monitoringTask, Severity severity, String message) {
		Assert.notNull(monitored);
		Assert.notNull(monitoringTask);
		Assert.notNull(severity);
		this.monitored = monitored;
		this.monitoringTask = monitoringTask;
		this.severity = severity;
		this.message = message;
		this.timestamp = new Date();
	}

	@Override
	public T getMonitored() {
		return this.monitored;
	}

	@Override
	public MonitoringTask<T> getMonitoringTask() {
		return this.monitoringTask;
	}

	@Override
	public Severity getSeverity() {
		return this.severity;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public Date getTimestamp() {
		return this.timestamp;
	}

}
