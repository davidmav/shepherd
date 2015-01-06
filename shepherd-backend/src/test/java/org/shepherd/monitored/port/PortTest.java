package org.shepherd.monitored.port;

import org.junit.Assert;
import org.junit.Test;
import org.shepherd.monitored.AbstractMonitoringTest;
import org.shepherd.monitored.MonitoringOutput;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.port.task.PortTask;

import java.util.HashMap;
import java.util.Map;

public class PortTest extends AbstractMonitoringTest {

	@Test
	public void testRunMonitor() {

		Map<String, Severity> expressionMap = new HashMap<String, Severity>();

		PortTask portTask = new PortTask(super.port, expressionMap);

		MonitoringOutput<Port> output = portTask.runMonitor();

		Assert.assertEquals(output.getSeverity(), Severity.ERROR);

	}
}
