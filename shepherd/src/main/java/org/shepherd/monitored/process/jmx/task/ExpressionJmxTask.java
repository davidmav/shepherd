package org.shepherd.monitored.process.jmx.task;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.MonitoringTask;
import org.shepherd.monitored.process.jmx.JmxProcess;
import org.shepherd.monitored.task.AbstractExpressionTask;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import java.util.Map;

public class ExpressionJmxTask extends AbstractExpressionTask implements MonitoringTask {

	private JmxProcess jmxProcess;

	public ExpressionJmxTask(JmxProcess jmxProcess, Map<String, Severity> expressions) {
		super(expressions);
		Assert.notNull(jmxProcess);
		this.jmxProcess = jmxProcess;
	}

	@Override
	public Monitored getMonitored() {
		return this.jmxProcess;
	}

	@Override
	protected Object getRootObject() {
		this.jmxProcess.getServerConnection().getMBeanServerConnection().
		return null;
	}

	@Override
	protected EvaluationContext getEvaluationContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
