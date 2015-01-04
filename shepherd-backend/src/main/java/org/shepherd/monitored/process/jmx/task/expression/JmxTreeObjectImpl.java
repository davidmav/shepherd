package org.shepherd.monitored.process.jmx.task.expression;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * 
 * @author DavidM
 * @since Dec 24, 2014
 */
abstract class JmxTreeObjectImpl implements JmxTreeObject {

	private Map<String, JmxTreeObject> objects;

	public JmxTreeObjectImpl() {
		this.objects = new TreeMap<String, JmxTreeObject>();
	}

	public JmxTreeObjectImpl(JmxTreeObject jmxTreeObject) {
		this.objects = new TreeMap<String, JmxTreeObject>(jmxTreeObject.getObjects());
	}

	@Override
	public JmxTreeObject getObject(String name) {
		return this.objects.get(name);
	}

	@Override
	public void addObject(String name, JmxTreeObject object) {
		this.objects.put(name, object);
	}

	@Override
	public void turnImmutable() {
		this.objects = Collections.unmodifiableMap(this.objects);
	}

	@Override
	public Map<String, JmxTreeObject> getObjects() {
		return this.objects;
	}
}
