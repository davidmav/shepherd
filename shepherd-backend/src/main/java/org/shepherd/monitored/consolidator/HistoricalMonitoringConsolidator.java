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

	protected Map<MonitoringTask<? extends Monitored>, SortedSet<MonitoringOutput<? extends Monitored>>> outputs;

	protected Map<Monitored, Set<MonitoringTask<? extends Monitored>>> monitoringTasks;

	public HistoricalMonitoringConsolidator() {
		this.outputs = new HashMap<MonitoringTask<? extends Monitored>, SortedSet<MonitoringOutput<? extends Monitored>>>();
		this.monitoringTasks = new HashMap<Monitored, Set<MonitoringTask<? extends Monitored>>>();
	}

	@Override
	public void insertOutput(MonitoringOutput<? extends Monitored> monitoringOutput) {
		addMonitoringOutputToOutputs(monitoringOutput);
		addMonitoringTask(monitoringOutput);
	}

	protected void addMonitoringTask(MonitoringOutput<? extends Monitored> monitoringOutput) {
		Set<MonitoringTask<? extends Monitored>> monitoredMonitoringTasks = this.monitoringTasks.get(monitoringOutput.getMonitored());
		if (monitoredMonitoringTasks == null) {
			monitoredMonitoringTasks = new HashSet<MonitoringTask<?>>();
			this.monitoringTasks.put(monitoringOutput.getMonitored(), monitoredMonitoringTasks);
		}
		monitoredMonitoringTasks.add(monitoringOutput.getMonitoringTask());
	}

	protected void addMonitoringOutputToOutputs(MonitoringOutput<? extends Monitored> monitoringOutput) {
		SortedSet<MonitoringOutput<?>> currentTaskOutputs = this.outputs.get(monitoringOutput.getMonitoringTask());
		if (currentTaskOutputs == null) {
			currentTaskOutputs = new TreeSet<MonitoringOutput<?>>(new TimestampMonitoringOutputComparator());
			this.outputs.put(monitoringOutput.getMonitoringTask(), currentTaskOutputs);
		}
		currentTaskOutputs.add(monitoringOutput);
	}

	//This is save casting
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Monitored> MonitoringOutput<T> getLatestOutput(MonitoringTask<T> monitoringTask) {
		SortedSet<MonitoringOutput<? extends Monitored>> monitoringOutputs = this.outputs.get(monitoringTask);
		return (MonitoringOutput<T>)monitoringOutputs.last();
	}

	@Override
	public Severity getCurrentMonitoredSeverity(Monitored monitored) {
		Set<MonitoringTask<? extends Monitored>> monitoredMonitoringTasks = this.monitoringTasks.get(monitored);
		Severity currentSeverity = Severity.INFO;
		for (MonitoringTask<? extends Monitored> monitoringTask : monitoredMonitoringTasks) {
			MonitoringOutput<? extends Monitored> lastOutput = this.outputs.get(monitoringTask).last();
			if (lastOutput != null && lastOutput.getSeverity().ordinal() < currentSeverity.ordinal()) {
				currentSeverity = lastOutput.getSeverity();
			}
		}
		return currentSeverity;
	}

	//This is save casting
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Monitored> Collection<MonitoringOutput<T>> getAllRecentMonitoringOutputs(T monitored) {
		Collection<MonitoringOutput<T>> output = new ArrayList<MonitoringOutput<T>>();
		Set<MonitoringTask<? extends Monitored>> set = this.monitoringTasks.get(monitored);
		for (MonitoringTask<? extends Monitored> monitoringTask : set) {
			MonitoringOutput<? extends Monitored> last = this.outputs.get(monitoringTask).last();
			if (last != null) {
				output.add((MonitoringOutput<T>)last);
			}
		}
		return Collections.unmodifiableCollection(output);
	}

	private class TimestampMonitoringOutputComparator implements Comparator<MonitoringOutput<?>> {

		public TimestampMonitoringOutputComparator() {}

		@Override
		public int compare(MonitoringOutput<?> o1, MonitoringOutput<?> o2) {
			return o1.getTimestamp().compareTo(o2.getTimestamp());
		}

	}

}
