package org.shepherd.monitored;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.shepherd.monitored.port.Port;
import org.shepherd.monitored.process.jmx.JmxProcess;

public class AbstractMonitoringTest {

	protected JmxProcess jmxProcess;
	protected Port port ;

	@BeforeClass
	public static void initClass() {
		BasicConfigurator.configure();
	}

	@Before
	public void init() {
		this.jmxProcess = MockUtils.createJmxProcessMock();
		this.port = MockUtils.createPortMock();
	}

}
