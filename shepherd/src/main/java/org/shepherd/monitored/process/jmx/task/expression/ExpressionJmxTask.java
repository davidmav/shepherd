package org.shepherd.monitored.process.jmx.task.expression;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoringOutput.Severity;
import org.shepherd.monitored.process.jmx.JmxProcess;
import org.shepherd.monitored.task.AbstractExpressionTask;
import org.shepherd.monitored.task.RootObjectNotCreatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class ExpressionJmxTask extends AbstractExpressionTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionJmxTask.class);

	protected JmxProcess jmxProcess;

	protected Object rootObject;

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
		if (this.rootObject == null) {
			try {
				this.rootObject = createRootObject(this.jmxProcess.getServerConnection().getMBeanServerConnection());
			} catch (IOException | RootObjectNotCreatedException e) {
				LOGGER.error("Could not create root object", e);
			}
		}
		return this.rootObject;
	}

	protected Object createRootObject(final MBeanServerConnection mBeanServerConnection) {
		Map<String, JmxTreeObject> jmxMap = new TreeMap<String, JmxTreeObject>();
		try {
			Set<ObjectName> allObjectNames = mBeanServerConnection.queryNames(null, null);

			LOGGER.debug("Walking the wonderful JMX tree");
			for (final ObjectName objectName : allObjectNames) {
				String domain = objectName.getDomain();
				LOGGER.debug("Processing object {}", objectName.toString());
				JmxTreeObject jmxTreeObject = null;

				if (!jmxMap.containsKey(domain)) {
					jmxTreeObject = new JmxTreeObjectImpl() {

						@Override
						public Object getAttribute(String name) {
							return null;
						}
					};

					jmxMap.put(domain, jmxTreeObject);
				} else {
					jmxTreeObject = jmxMap.get(domain);
				}

				LOGGER.debug("Parsing the canonical object name, something of the format 'type=Broker,brokerName=localhost,service=Health'");
				String objectString = objectName.toString().split(":")[1];
				String[] items = objectString.split(",");

				for (int i = 0; i < items.length; i++) {
					String[] split = items[i].split("=");
					String itemObjectName = split[1];
					JmxTreeObject object = jmxTreeObject.getObject(itemObjectName);
					if (object == null) {
						if (i != items.length - 1) {
							LOGGER.debug("We are not at a point with attributes yet with {}", items[i]);
							object = createTreeObjectWithoutAttributes();
							jmxTreeObject.addObject(itemObjectName, object);
						} else {
							LOGGER.debug("We are at the end of the object, there should be attributes with {}", items[i]);
							object = createTreeObjectWithAttributes(mBeanServerConnection, objectName);
							jmxTreeObject.addObject(itemObjectName, object);
						}
					} else if (i != items.length - 1) {
						LOGGER.debug("Object already existed at this location without attributes, adding the attributes part with {}", items[i]);
						jmxTreeObject.addObject(itemObjectName, createTreeObjectWithAttributes(mBeanServerConnection, objectName, object));
					}

					jmxTreeObject = object;
				}
			}
		} catch (RuntimeException | IOException e) {
			throw new RootObjectNotCreatedException(e);
		}

		return jmxMap;
	}

	private JmxTreeObjectImpl createTreeObjectWithAttributes(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName, JmxTreeObject object) {
		return new JmxTreeObjectImpl(object) {

			@Override
			public Object getAttribute(String name) {
				try {
					return mBeanServerConnection.getAttribute(objectName, name);
				} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException | IOException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("From some reason, can't get attribute {} from {}", new Object[] { name, objectName }, e);
					}
				}
				return null;
			}
		};
	}

	private JmxTreeObject createTreeObjectWithAttributes(final MBeanServerConnection mBeanServerConnection, final ObjectName objectName) {
		JmxTreeObject object;
		object = new JmxTreeObjectImpl() {

			@Override
			public Object getAttribute(String name) {
				try {
					return mBeanServerConnection.getAttribute(objectName, name);
				} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException | IOException e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("From some reason, can't get attribute {} from {}", new Object[] { name, objectName }, e);
					}
				}
				return null;
			}
		};
		return object;
	}

	private JmxTreeObject createTreeObjectWithoutAttributes() {
		JmxTreeObject object;
		object = new JmxTreeObjectImpl() {

			@Override
			public Object getAttribute(String name) {
				return null;
			}
		};
		return object;
	}

}
