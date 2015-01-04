package org.shepherd.monitored;

import org.junit.Before;
import org.shepherd.monitored.process.jmx.JmxProcess;

public class AbstractMonitoringTest {

	protected JmxProcess jmxProcess;

	@Before
	public void init() {
		this.jmxProcess = MockUtils.createJmxProcessMock();
	}

}
