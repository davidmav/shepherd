package org.shepherd.monitored.process.jmx.task.expression;

import com.sun.jmx.mbeanserver.Util;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.process.jmx.JmxProcess;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

@SuppressWarnings("restriction")
public class RootObjectTest {

	@Test
	public void testRootObject() throws IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
		JmxProcess jmxProcess = createJmxProcessMock();
		ExpressionJmxTask task = new ExpressionJmxTask(jmxProcess, Collections.singletonMap("test", Severity.INFO));
		Map<String, JmxTreeObject> rootObject = (Map<String, JmxTreeObject>)task.getRootObject();
		Object attribute = rootObject.get("org.apache.activemq").getObject("Broker").getObject("localhost").getObject("Health").getAttribute("CurrentStatus");
		Assert.assertEquals(attribute, "Good");
	}

	private JmxProcess createJmxProcessMock() throws IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
		JmxProcess mock = EasyMock.createMock(JmxProcess.class);
		JMXConnector jmxConnectorMock = EasyMock.createMock(JMXConnector.class);
		MBeanServerConnection mBeanServerConnectionMock = EasyMock.createMock(MBeanServerConnection.class);
		ObjectName objectName = Util.newObjectName("org.apache.activemq:type=Broker,brokerName=localhost,service=Health");
		EasyMock.expect(mBeanServerConnectionMock.queryNames(null, null)).andReturn(Collections.singleton(objectName)).anyTimes();
		EasyMock.expect(mBeanServerConnectionMock.getAttribute(objectName, "CurrentStatus")).andReturn("Good");
		EasyMock.expect(mock.getServerConnection()).andReturn(jmxConnectorMock).anyTimes();
		EasyMock.expect(jmxConnectorMock.getMBeanServerConnection()).andReturn(mBeanServerConnectionMock).anyTimes();
		EasyMock.replay(mBeanServerConnectionMock, jmxConnectorMock, mock);
		return mock;

	}
}
