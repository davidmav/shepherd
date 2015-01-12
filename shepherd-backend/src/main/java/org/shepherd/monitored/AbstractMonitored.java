package org.shepherd.monitored;

import org.springframework.beans.factory.BeanNameAware;

public abstract class AbstractMonitored implements Monitored, BeanNameAware {

	private String id;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setBeanName(String name) {
		this.id = name;

	}

}
