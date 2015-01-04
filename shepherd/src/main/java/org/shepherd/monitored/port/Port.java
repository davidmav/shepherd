package org.shepherd.monitored.port;

import org.shepherd.monitored.Monitored;

public interface Port extends Monitored {
	
	public enum PortType {
		TCP, UDP
	}
	
	public PortType getPortType();
	
	public int getPort();
	
	public String getHostname();

}
