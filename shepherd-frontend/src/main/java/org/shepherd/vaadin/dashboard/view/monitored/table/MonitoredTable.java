package org.shepherd.vaadin.dashboard.view.monitored.table;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.annotation.MonitoredDisplayName;
import org.shepherd.monitored.beans.definition.BeanDefinitionService;
import org.shepherd.monitored.beans.registrar.BeanRegistrarService;
import org.shepherd.monitored.provider.MonitoredProvider;
import org.shepherd.vaadin.dashboard.view.monitored.window.MonitoredWindow;
import org.shepherd.vaadin.ui.YesNoWindow;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.VaadinComponent;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

@UIScope
@VaadinComponent
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

	private ContextMenu contextMenu;

	@PostConstruct
	protected void init() {
		this.monitoredWindows = new HashMap<Object, MonitoredWindow>();
		addContainerProperty("Id", String.class, null);
		addContainerProperty("Type", String.class, null);
		addContainerProperty("Name", String.class, null);
		//setSizeFull();
		setWidth(90, Unit.PERCENTAGE);
		setHeight(90, Unit.PERCENTAGE);
		this.contextMenu = createContextMenu();
		addExtension(this.contextMenu);
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
				} else if (event.getButton() == MouseButton.RIGHT) {
					MonitoredTable.this.select(event.getItemId()); //Selecting the item, so the item can be accessible from the ContextMenuItemClickListener
					MonitoredTable.this.contextMenu.open(event.getClientX(), event.getClientY());
				}

			}
		});
	}

	private ContextMenu createContextMenu() {
		ContextMenu menu = new ContextMenu();
		//		menu.setAsContextMenuOf(this);
		ContextMenuItem editItem = menu.addItem("Edit");
		ContextMenuItem deleteItem = menu.addItem("Delete");
		menu.setOpenAutomatically(false);
		menu.addItemClickListener(new ContextMenuItemClickListener() {

			@Override
			public void contextMenuItemClicked(ContextMenuItemClickEvent event) {
				Object monitoredId = MonitoredTable.this.getValue();
				if (event.getSource().equals(editItem)) {
					MonitoredWindow monitoredWindow = MonitoredTable.this.monitoredWindows.get(monitoredId);
					if (monitoredWindow != null) {
						UI.getCurrent().addWindow(monitoredWindow);
					}
				} else if (event.getSource().equals(deleteItem)) {
					YesNoWindow yesNoWindow = new YesNoWindow("Delete " + monitoredId, "Are you sure you want to delete this Monitored Application?", new ClickListener() {

						private static final long serialVersionUID = 1L;

						@Override
						public void buttonClick(ClickEvent event1) {
							MonitoredTable.this.beanRegistrarService.deleteBean(monitoredId.toString());
							MonitoredTable.this.monitoredWindows.remove(monitoredId.toString());
							Notification notification = new Notification("Monitored Application Deleted");
							MonitoredTable.this.refreshTable();
							notification.show(UI.getCurrent().getPage());
						}
					}, null);
					UI.getCurrent().addWindow(yesNoWindow);
				}
			}
		});
		return menu;

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
