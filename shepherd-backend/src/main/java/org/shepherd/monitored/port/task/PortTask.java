package org.shepherd.monitored.port.task;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.shepherd.monitored.MonitoringOutput;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.MonitoringTask;
import org.shepherd.monitored.SimpleMonitoringOutput;
import org.shepherd.monitored.port.Port;
import org.springframework.util.Assert;

public class PortTask implements MonitoringTask<Port> {

	private Port monitoredPort;

	public PortTask(Port port,Map<String, Severity> expressions) {
		Assert.notNull(port);
		this.monitoredPort = port;
	}

	@Override
	public Port getMonitored() {
		return this.monitoredPort;
	}

	@Override
	public MonitoringOutput<Port> runMonitor() {
		
		boolean isPortOpen = false;
		String msg = StringUtils.EMPTY;
		
		try (Socket ignored = new Socket(this.monitoredPort.getHostname(), this.monitoredPort.getPort())) {
			isPortOpen = true;
			msg = String.format("Port %s is %s", this.monitoredPort.getPort(),"open");
	    } catch (IOException ignored) {
	    	isPortOpen = false;
	    	msg = String.format("Port %s is %s", this.monitoredPort.getPort(),"closed");
	    }
		Severity currentSeverity =  isPortOpen ? Severity.INFO : Severity.ERROR;
		
		return new SimpleMonitoringOutput<Port>(getMonitored(), this, currentSeverity, msg);
	}

}
