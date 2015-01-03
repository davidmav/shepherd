package org.shepherd.monitored.provider;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.shepherd.monitored.AbstractMonitoringTest;
import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoringTask;
import org.shepherd.monitored.process.jmx.JmxProcessImpl;
import org.shepherd.monitored.process.jmx.task.expression.ExpressionJmxTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/bootstrap/bootstrap.xml", "classpath:/META-INF/monitored-provider-test-context.xml" })
public class DefaultMonitoredProviderTest extends AbstractMonitoringTest {

	@Autowired
	private MonitoredProvider monitoredProvider;

	@Test
	public void testDefaultMonitoredProvider() {
		Collection<Class<Monitored>> allMonitoredClasses = monitoredProvider.getAllMonitoredClasses();
		Assert.assertEquals(1, allMonitoredClasses.size());
		Assert.assertEquals(JmxProcessImpl.class, allMonitoredClasses.iterator().next());
		Collection<Class<MonitoringTask>> allMonitoringTaskClasses = monitoredProvider.getAllMonitoringTaskClasses(JmxProcessImpl.class);
		Assert.assertEquals(1, allMonitoringTaskClasses.size());
		Assert.assertEquals(ExpressionJmxTask.class, allMonitoringTaskClasses.iterator().next());
	}

}
