package org.shepherd.monitored.consolidator;

import com.jcabi.aspects.Loggable;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoringOutput;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.MonitoringTask;

import java.util.Collection;

/**
 * 
 * @author DavidM
 *
 */
public interface MonitoringConsolidator {

	/**
	 * 
	 * @since Dec 25, 2014
	 * @author DavidM
	 * @param monitoringOutput
	 */
	@Loggable(Loggable.DEBUG)
	public void insertOutput(MonitoringOutput monitoringOutput);

	/**
	 * 
	 * @since Dec 25, 2014
	 * @author DavidM
	 * @param monitoringTask
	 * @return
	 */
	@Loggable(Loggable.DEBUG)
	public MonitoringOutput getLatestOutput(MonitoringTask monitoringTask);

	/**
	 * 
	 * @since Dec 25, 2014
	 * @author DavidM
	 * @param monitored
	 * @return
	 */
	@Loggable(Loggable.DEBUG)
	public Severity getCurrentMonitoredSeverity(Monitored monitored);

	/**
	 * 
	 * @since Dec 25, 2014
	 * @author DavidM
	 * @param monitored
	 * @return
	 */
	@Loggable
	public Collection<MonitoringOutput> getAllRecentMonitoringOutputs(Monitored monitored);

}
