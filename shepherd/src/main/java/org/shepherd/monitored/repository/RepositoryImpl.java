package org.shepherd.monitored.repository;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoringTask;
import org.shepherd.monitored.repository.exception.DuplicateMonitoringTaskException;
import org.shepherd.monitored.repository.exception.MonitoredAlreadyExistsException;
import org.shepherd.monitored.repository.exception.MonitoredNotFoundException;
import org.shepherd.monitored.repository.exception.MonitoringTaskNotFoundException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class RepositoryImpl implements Repository, InitializingBean {

	protected Map<String, Monitored> monitoredMap = new HashMap<String, Monitored>();

	protected Collection<MonitoringTask> monitoringTasks = new ArrayList<MonitoringTask>();

	public void setMonitored(Collection<Monitored> m) {
		for (Monitored monitored : m) {
			this.monitoredMap.put(monitored.getName(), monitored);
		}

	}

	public Monitored getMonitored(String monitoredItemName) {
		return this.monitoredMap.get(monitoredItemName);
	}

	@Override
	public void setMonitoringTasks(Collection<MonitoringTask> m) {
		for (MonitoringTask monitoringTask : m) {
			Monitored monitored = monitoringTask.getMonitored();
			if (!this.monitoredMap.values().contains(monitored)) {
				this.monitoredMap.put(monitored.getName(), monitored);
			}
			this.monitoringTasks.add(monitoringTask);
		}
	}

	@Override
	public void addMonitored(Monitored monitored) {
		if (this.monitoredMap.containsKey(monitored.getName())) {
			throw new MonitoredAlreadyExistsException();
		} else {
			this.monitoredMap.put(monitored.getName(), monitored);
		}
	}

	@Override
	public void removeMonitored(Monitored monitored) {
		String name = monitored.getName();
		if (!this.monitoredMap.containsKey(name)) {
			throw new MonitoredNotFoundException();
		} else {
			this.monitoredMap.remove(name);
		}
	}

	@Override
	public Collection<MonitoringTask> getMonitoringTasks() {
		return Collections.unmodifiableCollection(this.monitoringTasks);
	}

	@Override
	public void addMonitoringTask(MonitoringTask monitoringTask) {
		if (this.monitoringTasks.contains(monitoringTask)) {
			throw new DuplicateMonitoringTaskException();
		}

	}

	@Override
	public void removeMonitoringTask(MonitoringTask monitoringTask) {
		if (!this.monitoringTasks.contains(monitoringTask)) {
			throw new MonitoringTaskNotFoundException();
		} else {
			this.monitoringTasks.remove(monitoringTask);
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

}
