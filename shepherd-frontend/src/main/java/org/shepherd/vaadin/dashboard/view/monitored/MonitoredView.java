
package org.shepherd.vaadin.dashboard.view.monitored;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoredException;
import org.shepherd.monitored.provider.MonitoredProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
@VaadinView(name = "Monitored")
@UIScope
public class MonitoredView extends VerticalLayout implements View {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitoredView.class);

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
		add.addClickListener(new AddNewMonitoredClickListener());
		add.setEnabled(true);
		add.addStyleName(ValoTheme.BUTTON_PRIMARY);
		toolbar.addComponent(add);
		return toolbar;
	}

	private class AddNewMonitoredClickListener implements ClickListener {

		private Layout newTestSaveButtonsLayout;

		@Override
		public void buttonClick(ClickEvent event) {
			Window newMonitoredWindow = new Window(" New Monitored Application");
			newMonitoredWindow.setIcon(FontAwesome.CLOUD);
			ComboBox monitoredType = new ComboBox();
			monitoredType.setCaption("Monitoring Type");
			Map<MonitoredUIItem, Layout> monitoredItems = new HashMap<MonitoredUIItem, Layout>();
			for (Class<Monitored> monitoredClass : monitoredProvider.getAllMonitoredClasses()) {
				MonitoredUIItem item = new MonitoredUIItem(monitoredClass);
				monitoredItems.put(item, item.getLayout());
				monitoredType.addItem(item);
			}
			HorizontalLayout horizontalLayout = new HorizontalLayout();
			Layout testSaveButtonsLayout = createTestSaveButtonsLayout();
			newTestSaveButtonsLayout = testSaveButtonsLayout;
			Layout comboBoxLayout = createComboBoxLayout(monitoredType, monitoredItems);
			registerButtonListeners(newTestSaveButtonsLayout, monitoredType);
			horizontalLayout.addComponents(comboBoxLayout, testSaveButtonsLayout);
			newMonitoredWindow.setStyleName("monitored");
			newMonitoredWindow.setContent(horizontalLayout);
			newMonitoredWindow.setModal(true);
			newMonitoredWindow.setResizable(false);
			//			newMonitoredWindow.setDraggable(false);
			//			newMonitoredWindow.setHeight(500, Unit.PIXELS);
			//			newMonitoredWindow.setWidth(600, Unit.PIXELS);
			UI.getCurrent().addWindow(newMonitoredWindow);
		}

		private void registerButtonListeners(Layout newTestSaveButtonsLayout, ComboBox monitoredType) {
			for (Component component : newTestSaveButtonsLayout) {
				if (component instanceof Button && component.getId().equals("test")) {
					((Button)component).addClickListener(new ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							MonitoredUIItem value = (MonitoredUIItem)monitoredType.getValue();
							if (value != null) {
								Notification notification = null;
								try {
									Monitored monitored = value.createNewMonitoredInstance();
									if (monitored != null && monitored.test()) {
										notification = new Notification("Test Successful");
									} else {
										notification = new Notification("Test Failed");
									}
								} catch (MonitoredException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
									notification = new Notification("Test Failed with error: " + e.getMessage());
									LOGGER.error("Test Failed with error", e);
								}
								notification.show(UI.getCurrent().getPage());
							}

						}
					});
				} else if (component instanceof Button && component.getId().equals("save")) {

				}
			}

		}

		private Layout createTestSaveButtonsLayout() {
			Button testMonitored = new Button("Test");
			testMonitored.setId("test");
			Button saveMonitored = new Button("Save");
			saveMonitored.setId("save");
			HorizontalLayout layout = new HorizontalLayout(testMonitored, saveMonitored);
			layout.setCaption("");
			layout.setSpacing(true);
			MarginInfo marginInfo = new MarginInfo(true);
			layout.setMargin(marginInfo);
			layout.setEnabled(false);
			return layout;
		}

		private Layout createComboBoxLayout(ComboBox monitoredType, Map<MonitoredUIItem, Layout> monitoredItems) {
			VerticalLayout verticalLayout = new VerticalLayout(monitoredType);
			monitoredType.addListener(new MonitoredItemSetChangeListener(monitoredItems, verticalLayout, newTestSaveButtonsLayout));
			verticalLayout.setSpacing(true);
			MarginInfo marginInfo = new MarginInfo(true);
			verticalLayout.setMargin(marginInfo);
			return verticalLayout;
		}

	}

	private class MonitoredItemSetChangeListener implements Listener {

		private Map<MonitoredUIItem, Layout> monitoredItems;

		private Layout layout;

		private MonitoredUIItem currentSelection;

		private Layout newTestSaveButtonsLayout;

		public MonitoredItemSetChangeListener(Map<MonitoredUIItem, Layout> monitoredItems, Layout layout, Layout newTestSaveButtonsLayout) {
			this.monitoredItems = monitoredItems;
			this.layout = layout;
			this.currentSelection = null;
			this.newTestSaveButtonsLayout = newTestSaveButtonsLayout;
		}

		@Override
		public void componentEvent(Event event) {
			if (event instanceof ValueChangeEvent) {
				ComboBox source = (ComboBox)event.getSource();
				Object value = source.getValue();
				Layout currentSelectionLayout = this.monitoredItems.get(value);
				if (currentSelection != null && value != currentSelection) {
					layout.removeComponent(monitoredItems.get(currentSelection));
				}
				currentSelection = (MonitoredUIItem)value;
				if (currentSelectionLayout != null) {
					layout.addComponent(currentSelectionLayout);
					newTestSaveButtonsLayout.setEnabled(true);
				} else {
					newTestSaveButtonsLayout.setEnabled(false);
				}
			}

		}

	}

	@Override
	public void enter(final ViewChangeEvent event) {}

}
