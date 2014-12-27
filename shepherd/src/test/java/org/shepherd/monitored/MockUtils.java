package org.shepherd.monitored;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.shepherd.monitored.process.jmx.JmxProcess;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

public class MockUtils {

	public static JmxProcess createJmxProcessMock() {
		JmxProcess mock = EasyMock.createMock(JmxProcess.class);
		JMXConnector jmxConnectorMock = EasyMock.createMock(JMXConnector.class);
		MBeanServerConnection mBeanServerConnectionMock = EasyMock.createMock(MBeanServerConnection.class);
		ObjectName healthObjectName = null;
		ObjectName brokerObjectName = null;
		try {
			healthObjectName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,service=Health");
			brokerObjectName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
		} catch (MalformedObjectNameException e1) {
			e1.printStackTrace();
		}
		Set<ObjectName> objects = new HashSet<ObjectName>();
		objects.add(brokerObjectName);
		objects.add(healthObjectName);
		try {
			EasyMock.expect(mBeanServerConnectionMock.queryNames(null, null)).andReturn(objects).anyTimes();
			EasyMock.expect(mBeanServerConnectionMock.getAttribute(healthObjectName, "CurrentStatus")).andAnswer(new IAnswer<Object>() {

				boolean goodAnswered = false;

				@Override
				public Object answer() throws Throwable {
					if (!goodAnswered) {
						goodAnswered = true;
						return "Good";

					} else {
						return "Bad";
					}
				}
			}).anyTimes();
			EasyMock.expect(mBeanServerConnectionMock.getAttribute(brokerObjectName, "StorePercentUsage")).andReturn(10).anyTimes();
			EasyMock.expect(mock.getServerConnection()).andReturn(jmxConnectorMock).anyTimes();
			EasyMock.expect(jmxConnectorMock.getMBeanServerConnection()).andReturn(mBeanServerConnectionMock).anyTimes();
		} catch (IOException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
			e.printStackTrace();
		}
		EasyMock.replay(mBeanServerConnectionMock, jmxConnectorMock, mock);
		return mock;
	}

}
