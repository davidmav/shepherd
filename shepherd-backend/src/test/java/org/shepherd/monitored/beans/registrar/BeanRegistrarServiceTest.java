package org.shepherd.monitored.beans.registrar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.shepherd.monitored.AbstractMonitoringTest;
import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoredException;
import org.shepherd.monitored.annotation.UICreationPoint;
import org.shepherd.monitored.beans.definition.BeanDefinitionService;
import org.shepherd.monitored.process.jmx.JmxProcessImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.lang.reflect.Constructor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/bootstrap/bootstrap.xml", "classpath:/META-INF/bean-registrar-test-context.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BeanRegistrarServiceTest extends AbstractMonitoringTest implements ApplicationContextAware {

	@Autowired
	private BeanRegistrarService beanRegistrarService;

	@Autowired
	private BeanDefinitionService beanDefinitionService;

	private ApplicationContext applicationContext;

	@Test
	public void testAddingBeanDefinition() {
		@SuppressWarnings("unchecked")
		//Safe casting
		Constructor<? extends Monitored>[] constructors = (Constructor<? extends Monitored>[])JmxProcessImpl.class.getConstructors();
		Constructor<? extends Monitored> uiConstructor = null;
		for (Constructor<? extends Monitored> constructor : constructors) {
			if (constructor.getAnnotation(UICreationPoint.class) != null) {
				uiConstructor = constructor;
				break;
			}
		}
		Object[] args = new Object[] { "testName", "localhost", 2222, null, null };
		BeanDefinition beanDefinition = this.beanDefinitionService.createMonitoredBeanDefinition("test", uiConstructor, args);
		Assert.assertTrue(this.beanRegistrarService.beanExists(beanDefinition) == false);
		this.beanRegistrarService.saveBeanDefinition(beanDefinition, false);
		Object bean = this.applicationContext.getBean(beanDefinition.getAttribute("id").toString());
		Assert.assertTrue(bean != null);
		Assert.assertTrue(bean.getClass().getName().equals(beanDefinition.getBeanClassName()));
		File createBeanFile = new File("./work/monitored/test.xml");
		Assert.assertTrue(createBeanFile.exists());
		createBeanFile.delete();
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testDeletingBeanDefinition() {
		@SuppressWarnings("unchecked")
		//Safe casting
		Constructor<? extends Monitored>[] constructors = (Constructor<? extends Monitored>[])JmxProcessImpl.class.getConstructors();
		Constructor<? extends Monitored> uiConstructor = null;
		for (Constructor<? extends Monitored> constructor : constructors) {
			if (constructor.getAnnotation(UICreationPoint.class) != null) {
				uiConstructor = constructor;
				break;
			}
		}
		Object[] args = new Object[] { "testName", "localhost", 2222, null, null };
		BeanDefinition beanDefinition = this.beanDefinitionService.createMonitoredBeanDefinition("test", uiConstructor, args);
		Assert.assertTrue(this.beanRegistrarService.beanExists(beanDefinition) == false);
		this.beanRegistrarService.saveBeanDefinition(beanDefinition, false);
		Object bean = this.applicationContext.getBean(beanDefinition.getAttribute("id").toString());
		Assert.assertTrue(bean != null);
		Assert.assertTrue(bean.getClass().getName().equals(beanDefinition.getBeanClassName()));
		File createBeanFile = new File("./work/monitored/test.xml");
		Assert.assertTrue(createBeanFile.exists());
		this.beanRegistrarService.deleteBean("test");
		Assert.assertTrue(!createBeanFile.exists());
		this.beanRegistrarService.getBeanDefinition("test");
	}

	@Test
	public void testOverwritingBeanDefinition() {
		testAddingBeanDefinition();
		Monitored bean = (Monitored)this.applicationContext.getBean("test");
		Assert.assertTrue(bean.getName().equals("testName"));
		@SuppressWarnings("unchecked")
		//Safe casting
		Constructor<? extends Monitored>[] constructors = (Constructor<? extends Monitored>[])JmxProcessImpl.class.getConstructors();
		Constructor<? extends Monitored> uiConstructor = null;
		for (Constructor<? extends Monitored> constructor : constructors) {
			if (constructor.getAnnotation(UICreationPoint.class) != null) {
				uiConstructor = constructor;
				break;
			}
		}
		Object[] args = new Object[] { "secondBean", "localhost", 2222, null, null };
		BeanDefinition beanDefinition = this.beanDefinitionService.createMonitoredBeanDefinition("test", uiConstructor, args);
		Assert.assertTrue(this.beanRegistrarService.beanExists(beanDefinition) == true);
		this.beanRegistrarService.saveBeanDefinition(beanDefinition, true);
		bean = (Monitored)this.applicationContext.getBean("test");
		Assert.assertTrue(bean.getName().equals("secondBean"));
		File createBeanFile = new File("./work/monitored/test.xml");
		Assert.assertTrue(createBeanFile.exists());
		createBeanFile.delete();
	}

	@Test(expected = BeanAlreadyExistsException.class)
	public void testThrowingExceptionIfBeanExists() {
		testAddingBeanDefinition();
		@SuppressWarnings("unchecked")
		//Safe Casting
		Constructor<? extends Monitored>[] constructors = (Constructor<? extends Monitored>[])JmxProcessImpl.class.getConstructors();
		Constructor<? extends Monitored> uiConstructor = null;
		for (Constructor<? extends Monitored> constructor : constructors) {
			if (constructor.getAnnotation(UICreationPoint.class) != null) {
				uiConstructor = constructor;
				break;
			}
		}
		Object[] args = new Object[] { "anotherBean", "localhost", 3333, null, null };
		BeanDefinition beanDefinition = this.beanDefinitionService.createMonitoredBeanDefinition("test", uiConstructor, args);
		Assert.assertTrue(this.beanRegistrarService.beanExists(beanDefinition) == true);
		this.beanRegistrarService.saveBeanDefinition(beanDefinition, false);
	}

	@Test(expected = BeanOfOtherClassAlreadyExistsException.class)
	public void testThrowingExceptionIfBeanExistsOfOtherClass() {
		testAddingBeanDefinition();
		Object[] args = new Object[] {};
		@SuppressWarnings("unchecked")
		//Safe Casting
		Constructor<? extends Monitored> constructor = (Constructor<? extends Monitored>)MonitoredTest.class.getConstructors()[0];
		BeanDefinition beanDefinition = this.beanDefinitionService.createMonitoredBeanDefinition("test", constructor, args);
		Assert.assertTrue(this.beanRegistrarService.beanExists(beanDefinition) == true);
		this.beanRegistrarService.saveBeanDefinition(beanDefinition, false);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	private class MonitoredTest implements Monitored {

		@SuppressWarnings("unused")
		public MonitoredTest() {}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public boolean test() throws MonitoredException {
			return false;
		}

		@Override
		public String getId() {
			return null;
		}

	}

}
