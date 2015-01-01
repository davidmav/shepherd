package org.shepherd.vaadin.dashboard.view.monitored;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import org.apache.commons.collections.CollectionUtils;
import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.provider.MonitoredProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
@VaadinView(name = "Monitored")
@UIScope
public class MonitoredView extends VerticalLayout implements View {

	@Autowired
	private MonitoredProvider monitoredProvider;

	public MonitoredView() {
		setSizeFull();
		addStyleName("monitored");

		addComponent(buildHeader());

	}

	private Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.addStyleName("viewheader");
		header.setSpacing(true);
		Responsive.makeResponsive(header);

		Label titleLabel = new Label("Monitored Applications");
		titleLabel.setSizeUndefined();
		titleLabel.addStyleName(ValoTheme.LABEL_H1);
		titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		header.addComponents(titleLabel, buildToolbar());

		return header;
	}

	private Component buildToolbar() {
		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.addStyleName("toolbar");
		toolbar.setSpacing(true);

		final Button add = new Button("Add");
		add.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Window newMonitoredWindow = new Window("New Monitored Application");
				ComboBox monitoredType = new ComboBox();
				monitoredType.setCaption("Monitoring Type");
				Map<MonitoredItem, Collection<Component>> monitoredItems = new HashMap<MonitoredItem, Collection<Component>>();
				for (Class<Monitored> monitoredClass : monitoredProvider.getAllMonitoredClasses()) {
					MonitoredItem item = new MonitoredItem(monitoredClass);
					monitoredItems.put(item, item.getComponents());
					monitoredType.addItem(item);
				}
				VerticalLayout verticalLayout = new VerticalLayout(monitoredType);
				monitoredType.addListener(new MonitoredItemSetChangeListener(monitoredItems, verticalLayout));

				verticalLayout.setSpacing(true);
				MarginInfo marginInfo = new MarginInfo(true);
				verticalLayout.setMargin(marginInfo);
				newMonitoredWindow.setStyleName("monitored");
				newMonitoredWindow.setContent(verticalLayout);
				newMonitoredWindow.setModal(true);
				newMonitoredWindow.setResizable(false);
				newMonitoredWindow.setDraggable(false);
				newMonitoredWindow.setHeight(500, Unit.PIXELS);
				newMonitoredWindow.setWidth(600, Unit.PIXELS);
				UI.getCurrent().addWindow(newMonitoredWindow);
			}
		});
		add.setEnabled(true);
		add.addStyleName(ValoTheme.BUTTON_PRIMARY);
		toolbar.addComponent(add);
		return toolbar;
	}

	private class MonitoredItemSetChangeListener implements Listener {

		private Map<MonitoredItem, Collection<Component>> monitoredItems;

		private Layout layout;

		private MonitoredItem currentSelection;

		public MonitoredItemSetChangeListener(Map<MonitoredItem, Collection<Component>> monitoredItems, Layout layout) {
			this.monitoredItems = monitoredItems;
			this.layout = layout;
			this.currentSelection = null;
		}

		@Override
		public void componentEvent(Event event) {
			if (event instanceof ValueChangeEvent) {
				ComboBox source = (ComboBox)event.getSource();
				Object value = source.getValue();
				Collection<Component> components = this.monitoredItems.get(value);
				if (currentSelection != null && value != currentSelection) {
					for (Component component : monitoredItems.get(currentSelection)) {
						layout.removeComponent(component);
					}
				}
				currentSelection = (MonitoredItem)value;
				if (CollectionUtils.isNotEmpty(components)) {
					for (Component component : components) {
						layout.addComponent(component);
					}
				}
			}

		}

	}

	@Override
	public void enter(final ViewChangeEvent event) {}

}
