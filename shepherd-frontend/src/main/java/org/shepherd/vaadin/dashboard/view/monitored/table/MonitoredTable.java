package org.shepherd.vaadin.dashboard.view.monitored.table;

import com.vaadin.ui.Table;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.annotation.MonitoredDisplayName;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.PostConstruct;

@Component
public class MonitoredTable extends Table implements ApplicationContextAware {

	private static final long serialVersionUID = 1L;

	private ApplicationContext applicationContext;

	@PostConstruct
	protected void init() {
		addContainerProperty("Id", String.class, null);
		addContainerProperty("Type", String.class, null);
		addContainerProperty("Name", String.class, null);
		setSizeFull();
	}

	public void refreshTable() {
		removeAllItems();
		Map<String, Monitored> monitoredCollection = BeanFactoryUtils.beansOfTypeIncludingAncestors(this.applicationContext, Monitored.class, true, false);
		for (String monitoredId : monitoredCollection.keySet()) {
			Monitored monitored = monitoredCollection.get(monitoredId);
			//TODO create an utils service or registry for getting monitored properties, like display name
			MonitoredDisplayName annotation = monitored.getClass().getAnnotation(MonitoredDisplayName.class);
			String type = null;
			if (annotation != null) {
				type = annotation.value();
			} else {
				type = monitored.getClass().getSimpleName();
			}
			addItem(new Object[] { monitoredId, type, monitored.getName() }, monitoredId);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
