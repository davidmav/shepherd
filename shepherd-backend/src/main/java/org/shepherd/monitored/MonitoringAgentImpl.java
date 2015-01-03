package org.shepherd.monitored;

import org.shepherd.monitored.consolidator.MonitoringConsolidator;
import org.springframework.util.Assert;

import com.jcabi.aspects.Loggable;

/**
 * 
 * @author DavidM
 *
 */
public class MonitoringAgentImpl implements MonitoringAgent {
	
	protected Monitored monitored;
	
	protected MonitoringTask monitoringTask;
	
	protected MonitoringConsolidator outputCollector;
	
	public MonitoringAgentImpl(Monitored monitored, MonitoringTask monitoringTask, MonitoringConsolidator outputCollector) {
		Assert.notNull(monitored);
		Assert.notNull(monitoringTask);
		Assert.notNull(outputCollector);
		this.monitored = monitored;
		this.monitoringTask = monitoringTask;
		this.outputCollector = outputCollector;
	}

	@Loggable(Loggable.INFO)
	public void run() {
		MonitoringOutput output = getMonitoringTask().runMonitor();
		this.outputCollector.insertOutput(output);
	}

	public Monitored getMonitored() {
		return this.monitored;
	}

	public MonitoringTask getMonitoringTask() {
		return this.monitoringTask;
	}

}
