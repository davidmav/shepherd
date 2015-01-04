package org.shepherd.monitored.beans.registrar;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.beans.marshal.MarshalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

@Service
public class BeanRegistrarServiceImpl implements BeanRegistrarService, ApplicationContextAware {

	private static final String WORK_MONITORINGTASKS_BEANS_LOCATION = "./work/monitoringtasks/";

	private static final String WORK_MONITORED_BEANS_LOCATION = "./work/monitored/";

	private static final String ASTERIX = "*";

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanRegistrarServiceImpl.class);

	private ConfigurableApplicationContext applicationContext;

	@Autowired
	private MarshalService marshalService;

	@PostConstruct
	protected void loadFileSystemApplicationContext() {
		ApplicationContext ctx = new FileSystemXmlApplicationContext(new String[] { WORK_MONITORED_BEANS_LOCATION + ASTERIX, WORK_MONITORINGTASKS_BEANS_LOCATION + ASTERIX });
		applicationContext.setParent(ctx);
	}

	@Override
	public boolean beanExists(BeanDefinition beanDefintion) {
		return applicationContext.containsBeanDefinition(beanDefintion.getAttribute("id").toString());
	}

	@Override
	public void saveBeanDefinition(BeanDefinition beanDefinition, boolean overwrite) {
		String id = beanDefinition.getAttribute("id").toString();
		if (beanExists(beanDefinition)) {
			Object bean = this.applicationContext.getBean(id);
			String beanClassName = beanDefinition.getBeanClassName();
			if (beanClassName.equals(bean.getClass().getName())) {
				if (!overwrite) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("a bean with the same name {} and type {} already exists", new Object[] { id, beanClassName });
					}
					throw new BeanAlreadyExistsException();
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("a bean with the same name {} and a different type already exists", new Object[] { id });
				}
				throw new BeanOfOtherClassAlreadyExistsException();
			}
		}

		LOGGER.debug("Passed the tests, saving the bean now");
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)applicationContext.getBeanFactory();
		beanFactory.registerBeanDefinition(id, beanDefinition);

		FileOutputStream os = null;
		try {
			Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
			String beanFile = id + ".xml";
			File directory = null;
			if (Monitored.class.isAssignableFrom(beanClass)) {
				directory = new File(WORK_MONITORED_BEANS_LOCATION);

			} else {
				directory = new File(WORK_MONITORINGTASKS_BEANS_LOCATION);
			}
			directory.mkdirs();
			File beanXml = new File(directory, beanFile);
			os = new FileOutputStream(beanXml);
			Element marshaledBeanDefinition = marshalService.marshalBeanDefinition(beanDefinition);
			marshalService.writeElement(marshaledBeanDefinition, os);
		} catch (UnsupportedEncodingException | TransformerException | FileNotFoundException | ParserConfigurationException | ClassNotFoundException e) {
			throw new UnableToSaveBeanException(e);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext instanceof ConfigurableApplicationContext) {
			this.applicationContext = (ConfigurableApplicationContext)applicationContext;
		} else {
			throw new IllegalArgumentException("Someone played with the application context, did you change the project to a web project??");
		}
	}
}