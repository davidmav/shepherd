package org.shepherd.vaadin.dashboard.view.monitored.table;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.annotation.MonitoredDisplayName;
import org.shepherd.monitored.beans.definition.BeanDefinitionService;
import org.shepherd.monitored.beans.registrar.BeanRegistrarService;
import org.shepherd.monitored.provider.MonitoredProvider;
import org.shepherd.vaadin.dashboard.view.monitored.window.MonitoredWindow;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

@Component
public class MonitoredTable extends Table implements ApplicationContextAware {

	private static final long serialVersionUID = 1L;

	private ApplicationContext applicationContext;

	private Map<Object, MonitoredWindow> monitoredWindows;

	@Autowired
	private MonitoredProvider monitoredProvider;

	@Autowired
	private BeanRegistrarService beanRegistrarService;

	@Autowired
	private BeanDefinitionService beanDefinitionService;

	@PostConstruct
	protected void init() {
		this.monitoredWindows = new HashMap<Object, MonitoredWindow>();
		addContainerProperty("Id", String.class, null);
		addContainerProperty("Type", String.class, null);
		addContainerProperty("Name", String.class, null);
		setSizeFull();

		setSelectable(true);
		addItemClickListener(new ItemClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					MonitoredWindow monitoredWindow = MonitoredTable.this.monitoredWindows.get(event.getItemId());
					if (monitoredWindow != null) {
						UI.getCurrent().addWindow(monitoredWindow);
					}
				}

			}
		});
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
			if (!this.monitoredWindows.containsKey(monitoredId)) {
				this.monitoredWindows.put(monitoredId, new MonitoredWindow(this, this.beanRegistrarService, this.beanDefinitionService, monitoredId));
			}
		}
	}

	public void addMonitoredWindow(Object id, MonitoredWindow monitoredWindow) {
		this.monitoredWindows.put(id, monitoredWindow);
	}

	public void removeMonitoredWindow(Object id) {
		this.monitoredWindows.remove(id);
	}

	public void addNewMonitored() {
		MonitoredWindow monitoredWindow = new MonitoredWindow(this, this.beanRegistrarService, this.beanDefinitionService, this.monitoredProvider.getAllMonitoredClasses());
		UI.getCurrent().addWindow(monitoredWindow);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
