package org.shepherd.monitored.consolidator;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoringOutput;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.MonitoringTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author DavidM
 * @since Dec 25, 2014
 */
public class HistoricalMonitoringConsolidator implements MonitoringConsolidator {

	protected Map<MonitoringTask, SortedSet<MonitoringOutput>> outputs;

	protected Map<Monitored, Set<MonitoringTask>> monitoringTasks;

	public HistoricalMonitoringConsolidator() {
		this.outputs = new HashMap<MonitoringTask, SortedSet<MonitoringOutput>>();
		this.monitoringTasks = new HashMap<Monitored, Set<MonitoringTask>>();
	}

	@Override
	public void insertOutput(MonitoringOutput monitoringOutput) {
		addMonitoringOutputToOutputs(monitoringOutput);
		addMonitoringTask(monitoringOutput);
	}

	protected void addMonitoringTask(MonitoringOutput monitoringOutput) {
		Set<MonitoringTask> monitoredMonitoringTasks = monitoringTasks.get(monitoringOutput.getMonitored());
		if (monitoredMonitoringTasks == null) {
			monitoredMonitoringTasks = new HashSet<MonitoringTask>();
			monitoringTasks.put(monitoringOutput.getMonitored(), monitoredMonitoringTasks);
		}
		monitoredMonitoringTasks.add(monitoringOutput.getMonitoringTask());
	}

	protected void addMonitoringOutputToOutputs(MonitoringOutput monitoringOutput) {
		SortedSet<MonitoringOutput> currentTaskOutputs = this.outputs.get(monitoringOutput.getMonitoringTask());
		if (currentTaskOutputs == null) {
			currentTaskOutputs = new TreeSet<MonitoringOutput>(new TimestampMonitoringOutputComparator());
			this.outputs.put(monitoringOutput.getMonitoringTask(), currentTaskOutputs);
		}
		currentTaskOutputs.add(monitoringOutput);
	}

	@Override
	public MonitoringOutput getLatestOutput(MonitoringTask monitoringTask) {
		SortedSet<MonitoringOutput> monitoringOutputs = this.outputs.get(monitoringTask);
		return monitoringOutputs.last();
	}

	@Override
	public Severity getCurrentMonitoredSeverity(Monitored monitored) {
		Set<MonitoringTask> monitoringTasks = this.monitoringTasks.get(monitored);
		Severity currentSeverity = Severity.INFO;
		for (MonitoringTask monitoringTask : monitoringTasks) {
			MonitoringOutput lastOutput = this.outputs.get(monitoringTask).last();
			if (lastOutput != null && lastOutput.getSeverity().ordinal() < currentSeverity.ordinal()) {
				currentSeverity = lastOutput.getSeverity();
			}
		}
		return currentSeverity;
	}

	@Override
	public Collection<MonitoringOutput> getAllRecentMonitoringOutputs(Monitored monitored) {
		Collection<MonitoringOutput> output = new ArrayList<MonitoringOutput>();
		Set<MonitoringTask> set = this.monitoringTasks.get(monitored);
		for (MonitoringTask monitoringTask : set) {
			MonitoringOutput last = this.outputs.get(monitoringTask).last();
			if (last != null) {
				output.add(last);
			}
		}
		return Collections.unmodifiableCollection(output);
	}

	private class TimestampMonitoringOutputComparator implements Comparator<MonitoringOutput> {

		@Override
		public int compare(MonitoringOutput o1, MonitoringOutput o2) {
			return o1.getTimestamp().compareTo(o2.getTimestamp());
		}

	}

}
