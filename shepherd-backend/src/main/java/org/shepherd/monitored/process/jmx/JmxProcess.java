package org.shepherd.monitored.process.jmx;

import java.io.IOException;

import javax.management.remote.JMXConnector;

import org.shepherd.monitored.process.Process;

public interface JmxProcess extends Process {
	
	public int getPort();
	
	public String getHostname();
	
	/**
	 * Gets the connections for the jmx enabled process, you're responsible to close it.
	 * @return
	 * @throws IOException 
	 */
	public JMXConnector getServerConnection() throws IOException;

}
