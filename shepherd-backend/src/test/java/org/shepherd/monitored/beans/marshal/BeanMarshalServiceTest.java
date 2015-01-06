package org.shepherd.monitored.beans.marshal;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.shepherd.monitored.AbstractMonitoringTest;
import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.annotation.UICreationPoint;
import org.shepherd.monitored.beans.definition.BeanDefinitionService;
import org.shepherd.monitored.beans.definition.BeanDefinitionServiceImpl;
import org.shepherd.monitored.process.jmx.JmxProcessImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class BeanMarshalServiceTest extends AbstractMonitoringTest {

	/**
	 * This test checks the marshal service, it checks if the output is the same as the saved file at
	 * /org/shepherd/monitored/beans/jmxProcess.xml
	 * 
	 * @since Jan 4, 2015
	 * @author davidm
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 */
	@Test
	public void testJmxProcessMarshal() throws ParserConfigurationException, TransformerException, IOException {
		@SuppressWarnings("unchecked")
		//This case is safe
		Constructor<? extends Monitored>[] constructors = (Constructor<? extends Monitored>[])JmxProcessImpl.class.getConstructors();
		Constructor<? extends Monitored> uiConstructor = null;
		for (Constructor<? extends Monitored> constructor : constructors) {
			if (constructor.getAnnotation(UICreationPoint.class) != null) {
				uiConstructor = constructor;
				break;
			}
		}
		Object[] args = new Object[] { "testName", "localhost", 2222, null, null };
		BeanDefinitionService beanDefinitionService = new BeanDefinitionServiceImpl();
		MarshalService marshalService = new MarshalServiceImpl();
		BeanDefinition beanDefinition = beanDefinitionService.createMonitoredBeanDefinition("test", uiConstructor, args);
		Element marshalBeanDefinition = marshalService.marshalBeanDefinition(beanDefinition);
		Assert.notNull(marshalBeanDefinition);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		marshalService.writeElement(marshalBeanDefinition, outputStream);
		try (InputStream inputStream = marshalService.getClass().getResourceAsStream("/org/shepherd/monitored/beans/jmxProcess.xml");) {
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, "UTF-8");
			String fromFile = writer.toString();
			String marshaled = outputStream.toString();
			Assert.isTrue(fromFile.equals(marshaled));
		}
	}

}
