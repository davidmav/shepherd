package org.shepherd.monitored.port;

import java.io.IOException;
import java.net.Socket;

import org.shepherd.monitored.MonitoredException;
import org.shepherd.monitored.annotation.MonitoredDisplayName;
import org.shepherd.monitored.annotation.ParamDisplayName;
import org.shepherd.monitored.annotation.UICreationPoint;
import org.springframework.util.Assert;

/**
 * Port monitoring implementation
 * @author nickolayb
 *
 */
@MonitoredDisplayName("Port")
public class PortImpl implements Port {

	
	protected String id;
	
	protected String name;

	protected String hostname;

	protected int port;
	
	protected PortType portType;
	
	@UICreationPoint(params = {  @ParamDisplayName(index = 0, displayName = "Id"),@ParamDisplayName(index = 1, displayName = "Name"), 
			@ParamDisplayName(index = 2, displayName = "Hostname"), 
			@ParamDisplayName(index = 3, displayName = "Port"), 
			@ParamDisplayName(index = 4, displayName = "Port Type")})
	public PortImpl(String id,String name, String hostname, int port, PortType portType) {
		super();
		Assert.notNull(name);
		Assert.notNull(hostname);
		
		Assert.isTrue(port >= 1 && port <= 65535);
		Assert.isTrue(portType == PortType.UDP || portType == PortType.TCP);
		
		this.name = name;
		this.hostname = hostname;
		this.port = port;
		this.portType = portType;
	}
	

	@Override
	public String getName() {
		return this.name;
	}
	@Override
	public boolean test() throws MonitoredException {
		
		try (Socket ignored = new Socket(this.hostname, this.port)) {
	        return true;
	    } catch (IOException ignored) {
	        return false;
	    }
	}

	@Override
	public PortType getPortType() {
		return this.portType;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public String getHostname() {
		return this.hostname;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.hostname == null) ? 0 : this.hostname.hashCode());
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + this.port;
		result = prime * result + ( this.portType == null ? 0 :  this.portType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PortImpl other = (PortImpl)obj;
		if (this.hostname == null) {
			if (other.hostname != null) {
				return false;
			}
		} else if (!this.hostname.equals(other.hostname)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		
		if (this.portType == null) {
			if (other.portType != null) {
				return false;
			}
		} else if ( this.portType != other.portType) {
			return false;
		}
		
		if (this.port != other.port) {
			return false;
		}
		return true;
	}


	@Override
	public String getId() {
		return this.id;
	}
	

}
