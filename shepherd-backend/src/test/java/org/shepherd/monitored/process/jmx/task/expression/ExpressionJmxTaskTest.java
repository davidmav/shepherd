package org.shepherd.monitored.process.jmx.task.expression;

import org.junit.Assert;
import org.junit.Test;
import org.shepherd.monitored.AbstractMonitoringTest;
import org.shepherd.monitored.MonitoringOutput;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.process.jmx.JmxProcess;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author DavidM
 * @since Dec 25, 2014
 */
public class ExpressionJmxTaskTest extends AbstractMonitoringTest {

	@Test
	public void testRootObject() {
		ExpressionJmxTask task = new ExpressionJmxTask(this.jmxProcess, Collections.<String, Severity> singletonMap("test", Severity.INFO));
		@SuppressWarnings("unchecked")
		//Safe Casting
		Map<String, JmxTreeObject> rootObject = (Map<String, JmxTreeObject>)task.getRootObject();
		Object attribute = rootObject.get("org.apache.activemq").getObject("Broker").getObject("localhost").getObject("Health").getAttribute("CurrentStatus");
		Assert.assertEquals(attribute, "Good");
	}

	@Test
	public void testRunMonitor() {
		Map<String, Severity> expressionMap = new HashMap<String, Severity>();
		expressionMap.put("get('org.apache.activemq').getObject('Broker').getObject('localhost').getAttribute('StorePercentUsage') < 10", Severity.WARN);
		expressionMap.put("get('org.apache.activemq').getObject('Broker').getObject('localhost').getObject('Health').getAttribute('CurrentStatus') == 'Good'", Severity.ERROR);
		ExpressionJmxTask task = new ExpressionJmxTask(this.jmxProcess, expressionMap);
		MonitoringOutput<JmxProcess> output = task.runMonitor();
		Assert.assertEquals(output.getSeverity(), Severity.WARN);
		Assert.assertEquals(output.getMessage(), "Expression: get('org.apache.activemq').getObject('Broker').getObject('localhost').getAttribute('StorePercentUsage') < 10 is false");

	}

}
