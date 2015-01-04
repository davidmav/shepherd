package org.shepherd.monitored.process.jmx.task.expression;

import java.util.Map;

/**
 * 
 * 
 * @author DavidM
 * @since Dec 24, 2014
 * @version 5.0.2
 */
public interface JmxTreeObject {

	public JmxTreeObject getObject(String name);

	public Map<String, JmxTreeObject> getObjects();

	public Object getAttribute(String name);

	public void addObject(String name, JmxTreeObject object);

	public void turnImmutable();

}
