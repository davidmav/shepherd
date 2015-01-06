package org.shepherd.monitored;

import com.jcabi.aspects.Loggable;

import org.shepherd.monitored.consolidator.MonitoringConsolidator;
import org.springframework.util.Assert;

/**
 * 
 * @author DavidM
 *
 */
public class MonitoringAgentImpl<T extends Monitored> implements MonitoringAgent<T> {

	protected T monitored;

	protected MonitoringTask<T> monitoringTask;

	protected MonitoringConsolidator outputCollector;

	public MonitoringAgentImpl(T monitored, MonitoringTask<T> monitoringTask, MonitoringConsolidator outputCollector) {
		Assert.notNull(monitored);
		Assert.notNull(monitoringTask);
		Assert.notNull(outputCollector);
		this.monitored = monitored;
		this.monitoringTask = monitoringTask;
		this.outputCollector = outputCollector;
	}

	@Loggable(Loggable.INFO)
	public void run() {
		MonitoringOutput<T> output = getMonitoringTask().runMonitor();
		this.outputCollector.insertOutput(output);
	}

	public T getMonitored() {
		return this.monitored;
	}

	public MonitoringTask<T> getMonitoringTask() {
		return this.monitoringTask;
	}

}
