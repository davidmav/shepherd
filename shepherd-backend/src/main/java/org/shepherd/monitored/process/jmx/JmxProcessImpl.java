package org.shepherd.monitored.process.jmx;

import org.shepherd.monitored.annotation.MonitoredDisplayName;
import org.shepherd.monitored.annotation.ParamDisplayName;
import org.shepherd.monitored.annotation.UICreationPoint;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Jmx Process Implementation
 * 
 * @author DavidM
 *
 */
@MonitoredDisplayName("JMX Process")
public class JmxProcessImpl implements JmxProcess {

	private static final String FRONT = "service:jmx:rmi:///jndi/rmi://";
	private static final String BACK = "/jmxrmi";

	protected String name;

	protected String hostname;

	protected int port;

	protected JMXConnector connection;

	protected String url;

	Map<String, String[]> environment;

	public JmxProcessImpl(String name, String hostname, int port) throws IOException {
		this(name, hostname, port, null, null);
	}

	@UICreationPoint(params = {@ParamDisplayName(index = 0, displayName = "Name"),
							   @ParamDisplayName(index = 1, displayName = "Hostname"),
							   @ParamDisplayName(index = 2, displayName = "Port"),
							   @ParamDisplayName(index = 3, displayName = "Username"),
							   @ParamDisplayName(index = 4, displayName = "Password", passwordField = true)})
	public JmxProcessImpl(String name, String hostname, int port, String userName, String password) throws IOException {
		Assert.notNull(name);
		Assert.notNull(hostname);
		Assert.isTrue(port >= 1 && port <= 65535);
		this.name = name;
		this.hostname = hostname;
		this.port = port;
		this.url = FRONT + this.hostname + ":" + this.port + BACK;
		this.environment = new HashMap<String, String[]>();
		if (!StringUtils.isEmpty(userName) && password != null) {
			String[] credentials = new String[] { userName, password };
			this.environment.put(JMXConnector.CREDENTIALS, credentials);
		}

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getHostname() {
		return hostname;
	}

	@Override
	public JMXConnector getServerConnection() throws IOException {
		JMXServiceURL jMXServiceURL = new JMXServiceURL(this.url);
		return JMXConnectorFactory.connect(jMXServiceURL, environment);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + port;
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
		JmxProcessImpl other = (JmxProcessImpl)obj;
		if (hostname == null) {
			if (other.hostname != null) {
				return false;
			}
		} else if (!hostname.equals(other.hostname)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		return true;
	}

}
