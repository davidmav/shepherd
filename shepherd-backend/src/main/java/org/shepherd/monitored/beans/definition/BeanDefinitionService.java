package org.shepherd.monitored.beans.definition;

import org.shepherd.monitored.Monitored;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

/**
 * 
 * 
 * @author davidm
 * @since Jan 3, 2015
 * @version 0.1.0
 */
public interface BeanDefinitionService {

	/**
	 * 
	 * @since Jan 4, 2015
	 * @author davidm
	 * @param id
	 * @param constructor
	 * @param args
	 * @return
	 */
	public BeanDefinition createMonitoredBeanDefinition(String id, Constructor<? extends Monitored> constructor, Object[] args);

}
