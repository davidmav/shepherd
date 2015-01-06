package org.shepherd.monitored.consolidator;

import org.junit.Assert;
import org.junit.Test;
import org.shepherd.monitored.AbstractMonitoringTest;
import org.shepherd.monitored.MonitoringOutput;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.process.jmx.JmxProcess;
import org.shepherd.monitored.process.jmx.task.expression.ExpressionJmxTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author DavidM
 * @since Dec 25, 2014
 */
public class HistoricalMonitoringConsolidatorTest extends AbstractMonitoringTest {

	@Test
	public void testSeverity() {
		Map<String, Severity> expressionMap = new HashMap<String, Severity>();
		expressionMap.put("get('org.apache.activemq').getObject('Broker').getObject('localhost').getObject('Health').getAttribute('CurrentStatus') == 'Good'", Severity.ERROR);
		ExpressionJmxTask task1 = new ExpressionJmxTask(this.jmxProcess, expressionMap);
		expressionMap = new HashMap<String, Severity>();
		expressionMap.put("get('org.apache.activemq').getObject('Broker').getObject('localhost').getAttribute('StorePercentUsage') < 10", Severity.WARN);
		ExpressionJmxTask task2 = new ExpressionJmxTask(this.jmxProcess, expressionMap);
		MonitoringOutput<JmxProcess> runMonitor1 = task1.runMonitor();
		MonitoringOutput<JmxProcess> runMonitor2 = task2.runMonitor();
		HistoricalMonitoringConsolidator consolidator = new HistoricalMonitoringConsolidator();
		consolidator.insertOutput(runMonitor1);
		consolidator.insertOutput(runMonitor2);
		Assert.assertEquals(consolidator.getCurrentMonitoredSeverity(this.jmxProcess), Severity.WARN);
	}

	@Test
	public void testLatestOutput() throws InterruptedException {
		Map<String, Severity> expressionMap = new HashMap<String, Severity>();
		expressionMap.put("get('org.apache.activemq').getObject('Broker').getObject('localhost').getObject('Health').getAttribute('CurrentStatus') == 'Good'", Severity.ERROR);
		ExpressionJmxTask task1 = new ExpressionJmxTask(this.jmxProcess, expressionMap);
		MonitoringOutput<JmxProcess> runMonitor1 = task1.runMonitor();
		Thread.sleep(100);
		MonitoringOutput<JmxProcess> runMonitor2 = task1.runMonitor();
		HistoricalMonitoringConsolidator consolidator = new HistoricalMonitoringConsolidator();
		consolidator.insertOutput(runMonitor2);
		consolidator.insertOutput(runMonitor1);
		Assert.assertEquals(consolidator.getCurrentMonitoredSeverity(this.jmxProcess), Severity.ERROR);
		Assert.assertEquals(consolidator.getLatestOutput(task1), runMonitor2);
	}

	@Test
	public void testRecentMonitoringOutputs() throws InterruptedException {
		Map<String, Severity> expressionMap = new HashMap<String, Severity>();
		expressionMap.put("get('org.apache.activemq').getObject('Broker').getObject('localhost').getObject('Health').getAttribute('CurrentStatus') == 'Good'", Severity.ERROR);
		ExpressionJmxTask task1 = new ExpressionJmxTask(this.jmxProcess, expressionMap);
		expressionMap = new HashMap<String, Severity>();
		expressionMap.put("get('org.apache.activemq').getObject('Broker').getObject('localhost').getAttribute('StorePercentUsage') < 10", Severity.WARN);
		ExpressionJmxTask task2 = new ExpressionJmxTask(this.jmxProcess, expressionMap);
		MonitoringOutput<JmxProcess> runMonitor1 = task1.runMonitor();
		MonitoringOutput<JmxProcess> runMonitor2 = task2.runMonitor();
		Thread.sleep(100);
		MonitoringOutput<JmxProcess> runMonitor3 = task1.runMonitor();
		MonitoringOutput<JmxProcess> runMonitor4 = task2.runMonitor();
		HistoricalMonitoringConsolidator consolidator = new HistoricalMonitoringConsolidator();
		consolidator.insertOutput(runMonitor1);
		consolidator.insertOutput(runMonitor2);
		consolidator.insertOutput(runMonitor3);
		consolidator.insertOutput(runMonitor4);
		Collection<MonitoringOutput<JmxProcess>> allRecentMonitoringOutputs = consolidator.getAllRecentMonitoringOutputs(this.jmxProcess);
		Assert.assertTrue(allRecentMonitoringOutputs.size() == 2);
		Assert.assertTrue(allRecentMonitoringOutputs.contains(runMonitor3));
		Assert.assertTrue(allRecentMonitoringOutputs.contains(runMonitor4));
		Assert.assertEquals(consolidator.getCurrentMonitoredSeverity(this.jmxProcess), Severity.ERROR);
	}

}
