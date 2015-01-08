package org.shepherd.monitored.beans.registrar;

import org.springframework.beans.factory.config.BeanDefinition;

public interface BeanRegistrarService {

	/**
	 * 
	 * @since Jan 4, 2015
	 * @author davidm
	 * @param beanDefintion
	 * @return
	 */
	public boolean beanExists(BeanDefinition beanDefintion);

	/**
	 * 
	 * @since Jan 4, 2015
	 * @author davidm
	 * @param beanDefintion
	 * @return
	 */
	public void saveBeanDefinition(BeanDefinition beanDefinition, boolean overwrite);

	/**
	 * 
	 * @since Jan 7, 2015
	 * @author davidm
	 * @param id
	 * @return
	 */
	public BeanDefinition getBeanDefinition(String id);

}
