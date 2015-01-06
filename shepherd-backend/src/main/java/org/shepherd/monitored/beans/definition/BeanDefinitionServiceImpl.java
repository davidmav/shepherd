package org.shepherd.monitored.beans.definition;

import org.apache.commons.lang3.ArrayUtils;
import org.shepherd.monitored.Monitored;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;

@Service
public class BeanDefinitionServiceImpl implements BeanDefinitionService {

	private static final String BEAN_ID_ATTRIBUTE = "id";

	@Override
	public BeanDefinition createMonitoredBeanDefinition(String id, Constructor<? extends Monitored> constructor, Object[] args) {
		Assert.notNull(id, "id must be provided");
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setAttribute(BEAN_ID_ATTRIBUTE, id);
		beanDefinition.setBeanClass(constructor.getDeclaringClass());
		if (ArrayUtils.isNotEmpty(args)) {
			ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
			for (int i = 0; i < args.length; i++) {
				constructorArgumentValues.addIndexedArgumentValue(i, args[i]);
			}
			beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
		}
		return beanDefinition;
	}

}
