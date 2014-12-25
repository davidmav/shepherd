package org.shepherd.monitored.process.jmx.task.expression;

import org.junit.Assert;
import org.junit.Test;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.process.jmx.JmxProcess;
import org.shepherd.monitored.process.jmx.JmxProcessImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class RootObjectTest {

	@Test
	public void testRootObject() throws IOException {
		JmxProcess jmxProcess = new JmxProcessImpl("ActiveMQ", "localhost", 2000);
		ExpressionJmxTask task = new ExpressionJmxTask(jmxProcess, Collections.singletonMap("test", Severity.INFO));
		Map<String, JmxTreeObject> rootObject = (Map<String, JmxTreeObject>)task.getRootObject();
		Object attribute = rootObject.get("org.apache.activemq").getObject("Broker").getObject("localhost").getObject("Health").getAttribute("CurrentStatus");
		Assert.assertEquals(attribute, "Good");

	}

}
