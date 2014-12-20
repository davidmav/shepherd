package org.shepherd.monitored.process.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Jmx Process Implementation
 * @author DavidM
 *
 */
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
	
	public JmxProcessImpl(String name, String hostname, int port, String userName, String password) throws IOException {
		Assert.notNull(name);
		Assert.notNull(hostname);
		Assert.isTrue(port >=1 && port <=65535);
		this.name = name;
		this.hostname = hostname;
		this.port = port;
		this.url = FRONT + this.hostname + ":" + this.port + BACK;
		this.environment = new HashMap<String, String[]>(); 
		if (!StringUtils.isEmpty(userName) && password != null) {
			String[] credentials = new String[] { userName,	password};
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
	
}
