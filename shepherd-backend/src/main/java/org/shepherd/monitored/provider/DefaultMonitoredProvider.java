package org.shepherd.monitored.provider;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoringTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * 
 * 
 * @author DavidM
 * @since Dec 28, 2014
 * @version 0.1.0
 */
@Component
public class DefaultMonitoredProvider implements MonitoredProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMonitoredProvider.class);

	@Value("${monitored.provider.scanned.package:org.shepherd}")
	private String scannedPackage;

	private Map<Class<Monitored>, Collection<Class<MonitoringTask>>> providerMap;

	@PostConstruct
	protected void init() throws ClassNotFoundException {
		this.providerMap = new HashMap<Class<Monitored>, Collection<Class<MonitoringTask>>>();
		autoscanMonitored();
		autoscanMonitoringTasks();
	}

	protected void autoscanMonitored() throws ClassNotFoundException {
		LOGGER.debug("Auto Scanning {} for Monitored classes", scannedPackage);
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(Monitored.class));
		for (BeanDefinition bd : scanner.findCandidateComponents(scannedPackage)) {
			String className = bd.getBeanClassName();
			LOGGER.debug("Found Monitored class {}", className);
			Class<Monitored> clas = (Class<Monitored>)Class.forName(className);
			if (!Modifier.isAbstract(clas.getModifiers()) && !Modifier.isInterface(clas.getModifiers())) {
				if (!this.providerMap.containsKey(clas)) {
					this.providerMap.put(clas, new ArrayList<Class<MonitoringTask>>());
				}
			} else {
				LOGGER.warn("Igonring class {}, it's abstract or interface", className);
			}
		}
	}

	protected void autoscanMonitoringTasks() throws ClassNotFoundException {
		LOGGER.debug("Auto Scanning {} for MonitoringTask classes", scannedPackage);
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(MonitoringTask.class));
		for (BeanDefinition bd : scanner.findCandidateComponents(scannedPackage)) {
			String className = bd.getBeanClassName();
			LOGGER.debug("Found MonitoringTask class {}", className);
			Class<MonitoringTask> clas = (Class<MonitoringTask>)Class.forName(className);
			if (!Modifier.isAbstract(clas.getModifiers()) && !Modifier.isInterface(clas.getModifiers())) {
				Method getMonitoredMethod = null;
				try {
					getMonitoredMethod = clas.getMethod("getMonitored");
				} catch (NoSuchMethodException | SecurityException e) {
					LOGGER.warn("Couldn't locate getMonitored method, skipping class {}", clas.getName());
				}
				if (getMonitoredMethod != null) {
					Class<?> monitoredType = getMonitoredMethod.getReturnType();
					addMonitoringTaskToMonitored(clas, monitoredType);
				}
			} else {
				LOGGER.warn("Igonring class {}, it's abstract or interface", className);
			}
		}
	}

	private void addMonitoringTaskToMonitored(Class<MonitoringTask> monitoringTask, Class<?> monitoredType) {
		if (this.providerMap.containsKey(monitoredType)) {
			Collection<Class<MonitoringTask>> currentClassMonitoringTasks = this.providerMap.get(monitoredType);
			currentClassMonitoringTasks.add(monitoringTask);
		} else {
			boolean found = false;
			for (Class<Monitored> monitored : this.providerMap.keySet()) {
				if (monitoredType.isAssignableFrom(monitored)) {
					found = true;
					this.providerMap.get(monitored).add(monitoringTask);
				}
			}
			if (!found) {
				LOGGER.warn("Couldn't locate Monitored class {} in the registry, no implementations for that class, skipping class {}", new Object[] { monitoredType, monitoringTask });
			}
		}
	}

	@Override
	public Collection<Class<Monitored>> getAllMonitoredClasses() {
		return Collections.unmodifiableCollection(this.providerMap.keySet());
	}

	@Override
	public Collection<Class<MonitoringTask>> getAllMonitoringTaskClasses(Class<? extends Monitored> monitoredClass) {
		return Collections.unmodifiableCollection(this.providerMap.get(monitoredClass));
	}

}
