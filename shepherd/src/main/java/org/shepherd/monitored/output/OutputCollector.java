package org.shepherd.monitored.output;

import org.shepherd.monitored.MonitoringOutput;

import com.jcabi.aspects.Loggable;

/**
 * 
 * @author DavidM
 *
 */
public interface OutputCollector {
	
	@Loggable(Loggable.DEBUG)
	public void insertOutput(MonitoringOutput monitoringOutput);

}
