package org.shepherd.monitored.utils;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;




@SuppressWarnings({"rawtypes","unchecked"})
@Component
public class EnumConverter implements Converter, InitializingBean {
	
	public Object convert(Class type, Object value) {
		//TODO: think what to do in such case
		if(value == null) return null;
        
		return Enum.valueOf(type, (String) value);
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		ConvertUtils.register(this, Enum.class);
	}
	
}
