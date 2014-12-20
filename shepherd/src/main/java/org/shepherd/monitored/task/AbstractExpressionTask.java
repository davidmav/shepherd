package org.shepherd.monitored.task;

import org.shepherd.monitored.MonitoringOutput;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.MonitoringTask;
import org.shepherd.monitored.SimpleMonitoringOutput;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractExpressionTask implements MonitoringTask {

	private static final String OUTPUT_MESSAGE = "Expression: {0} is false";

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

	protected abstract Object getRootObject();

	protected abstract EvaluationContext getEvaluationContext();

}
