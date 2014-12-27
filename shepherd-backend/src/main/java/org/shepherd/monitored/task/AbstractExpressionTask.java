package org.shepherd.monitored.task;

import org.shepherd.monitored.MonitoringOutput;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.MonitoringTask;
import org.shepherd.monitored.SimpleMonitoringOutput;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author DavidM
 * @since Dec 25, 2014
 */
public abstract class AbstractExpressionTask implements MonitoringTask, ApplicationContextAware {

	private static final String OUTPUT_MESSAGE = "Expression: {0} is false";

	protected EvaluationContext evaluationContext;

	protected ConfigurableApplicationContext applicationContext;

	private Map<Expression, Severity> expressions;

	public AbstractExpressionTask(Map<String, Severity> expressions) {
		Assert.notEmpty(expressions);
		ExpressionParser spelParser = new SpelExpressionParser();
		this.expressions = new HashMap<Expression, Severity>();
		for (String stringExpression : expressions.keySet()) {
			this.expressions.put(spelParser.parseExpression(stringExpression), expressions.get(stringExpression));
		}
	}

	@Override
	public MonitoringOutput runMonitor() {
		EvaluationContext evaluationContext = getEvaluationContext();
		Severity currentSeverity = Severity.INFO;
		Object rootObject = getRootObject();
		StringBuilder messageBuilder = new StringBuilder();
		for (Expression expression : expressions.keySet()) {
			Severity severity = expressions.get(expression);
			Boolean value = expression.getValue(evaluationContext, rootObject, Boolean.class);
			if (!value && severity.ordinal() < currentSeverity.ordinal()) {
				currentSeverity = severity;
				String currentMessage = MessageFormat.format(OUTPUT_MESSAGE, expression.getExpressionString());
				if (messageBuilder.length() != 0) {
					messageBuilder.append("; ").append(currentMessage);
				} else {
					messageBuilder.append(currentMessage);
				}
			}
		}
		return new SimpleMonitoringOutput(getMonitored(), this, currentSeverity, messageBuilder.toString());
	}

	protected abstract Object getRootObject() throws RootObjectNotCreatedException;

	protected EvaluationContext getEvaluationContext() {
		if (this.evaluationContext == null) {
			StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
			if (this.applicationContext != null) {
				evaluationContext.setBeanResolver(new BeanFactoryResolver(this.applicationContext.getBeanFactory()));
			}
			this.evaluationContext = evaluationContext;
		}
		return this.evaluationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = (ConfigurableApplicationContext)applicationContext;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expressions == null) ? 0 : expressions.hashCode());
		result = prime * result + ((getMonitored() == null) ? 0 : getMonitored().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractExpressionTask other = (AbstractExpressionTask)obj;
		if (expressions == null) {
			if (other.expressions != null) {
				return false;
			}
		} else if (!expressions.equals(other.expressions)) {
			return false;
		}
		if (getMonitored() == null) {
			if (other.getMonitored() != null) {
				return false;
			}
		} else if (!getMonitored().equals(other.getMonitored())) {
			return false;
		}
		return true;
	}

}
