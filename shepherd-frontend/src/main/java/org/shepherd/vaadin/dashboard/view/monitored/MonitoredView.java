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
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoredException;
import org.shepherd.monitored.beans.definition.BeanDefinitionService;
import org.shepherd.monitored.beans.registrar.BeanAlreadyExistsException;
import org.shepherd.monitored.beans.registrar.BeanOfOtherClassAlreadyExistsException;
import org.shepherd.monitored.beans.registrar.BeanRegistrarService;
import org.shepherd.monitored.beans.registrar.UnableToSaveBeanException;
import org.shepherd.monitored.provider.MonitoredProvider;
import org.shepherd.vaadin.dashboard.view.monitored.table.MonitoredTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

@SuppressWarnings("serial")
@VaadinView(name = "Monitored")
@UIScope
public class MonitoredView extends VerticalLayout implements View {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitoredView.class);

	@Autowired
	private MonitoredProvider monitoredProvider;

	@Autowired
	private BeanRegistrarService beanRegistrarService;

	@Autowired
	private BeanDefinitionService beanDefinitionService;

	@Autowired
	private MonitoredTable monitoredTable;

	public MonitoredView() {
		addStyleName("monitored");
		setSpacing(false);

	}

	@PostConstruct
	private void init() {
		this.monitoredTable.refreshTable();
		addComponents(buildHeader(), this.monitoredTable);
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

		private static final String SAVE_BUTTON_CAPTION = "save";
		private static final String TEST_BUTTON_CAPTION = "test";
		private Layout newTestSaveButtonsLayout;

		@Override
		public void buttonClick(ClickEvent event) {
			Window newMonitoredWindow = new Window(" New Monitored Application");
			newMonitoredWindow.setIcon(FontAwesome.CLOUD);
			ComboBox monitoredType = new ComboBox();
			monitoredType.setCaption("Monitoring Type");
			Map<MonitoredUIItem, Layout> monitoredItems = new HashMap<MonitoredUIItem, Layout>();
			for (Class<Monitored> monitoredClass : MonitoredView.this.monitoredProvider.getAllMonitoredClasses()) {
				MonitoredUIItem item = new MonitoredUIItem(monitoredClass);
				monitoredItems.put(item, item.getLayout());
				monitoredType.addItem(item);
			}
			HorizontalLayout horizontalLayout = new HorizontalLayout();
			Layout testSaveButtonsLayout = createTestSaveButtonsLayout();
			this.newTestSaveButtonsLayout = testSaveButtonsLayout;
			Layout comboBoxLayout = createComboBoxLayout(monitoredType, monitoredItems);
			registerButtonListeners(this.newTestSaveButtonsLayout, monitoredType, newMonitoredWindow);
			horizontalLayout.addComponents(comboBoxLayout, testSaveButtonsLayout);
			newMonitoredWindow.setStyleName("monitored");
			newMonitoredWindow.setContent(horizontalLayout);
			newMonitoredWindow.setModal(true);
			newMonitoredWindow.setResizable(false);
			newMonitoredWindow.setPositionY(event.getClientY() - event.getRelativeY() + 50);
			newMonitoredWindow.setPositionX(event.getClientX() - event.getClientX() + 400);
			//			newMonitoredWindow.setDraggable(false);
			//			newMonitoredWindow.setHeight(500, Unit.PIXELS);
			//			newMonitoredWindow.setWidth(600, Unit.PIXELS);
			UI.getCurrent().addWindow(newMonitoredWindow);
		}

		private void registerButtonListeners(Layout newTestSaveButtonsLayout1, ComboBox monitoredType, Window newMonitoredWindow) {
			for (Component component : newTestSaveButtonsLayout1) {
				if (isTestButton(component)) {
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
										notification = new Notification("Test Failed", Notification.Type.WARNING_MESSAGE);
									}
								} catch (MonitoredException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
									notification = new Notification("Test Failed with error: " + e.getMessage(), Notification.Type.WARNING_MESSAGE);
									LOGGER.error("Test Failed with error", e);
								}
								notification.show(UI.getCurrent().getPage());
							}

						}
					});
				} else if (isSaveButton(component)) {
					((Button)component).addClickListener(new ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							MonitoredUIItem value = (MonitoredUIItem)monitoredType.getValue();
							if (value != null) {
								Notification notification = null;
								Window overwriteWindow = null;
								try {
									Constructor<? extends Monitored> constructor = value.getConstructor();
									Object[] arguments = value.getArguments();
									TextField id = (TextField)value.getLayout().iterator().next();
									BeanDefinition beanDefinition = MonitoredView.this.beanDefinitionService.createMonitoredBeanDefinition(id.getValue(), constructor, arguments);
									overwriteWindow = createOverwriteWindow(newMonitoredWindow, beanDefinition);
									MonitoredView.this.beanRegistrarService.saveBeanDefinition(beanDefinition, false);
									newMonitoredWindow.close();
									notification = new Notification("Monitored Application Saved Successfully");
									MonitoredView.this.monitoredTable.refreshTable();
								} catch (BeanAlreadyExistsException e) {
									UI.getCurrent().addWindow(overwriteWindow);
								} catch (BeanOfOtherClassAlreadyExistsException e) {
									notification = new Notification("Monitored with the same Id but different type already exists", Notification.Type.WARNING_MESSAGE);

								} catch (UnableToSaveBeanException e) {
									notification = new Notification("Something wrong with the parameters: " + e.getMessage(), Notification.Type.WARNING_MESSAGE);
								} catch (IllegalArgumentException e) {
									notification = new Notification("Something wrong with the parameters: " + e.getMessage(), Notification.Type.WARNING_MESSAGE);
								}
								if (notification != null) {
									notification.show(UI.getCurrent().getPage());
								}
							}

						}

						private Window createOverwriteWindow(Window newMonitoredWindow1, final BeanDefinition beanDefinition) {
							Window overwriteWindow = new Window(" Monitored with the same Id already exists ");
							overwriteWindow.setIcon(FontAwesome.INFO);
							overwriteWindow.setModal(true);
							overwriteWindow.setResizable(false);
							VerticalLayout verticalLayout = new VerticalLayout();
							HorizontalLayout horizontalLayout = new HorizontalLayout();
							Label label = new Label("Monitored with the same Id already exists, overwrite?");
							Button noButton = new Button("No");
							noButton.addClickListener(new ClickListener() {

								@Override
								public void buttonClick(ClickEvent event) {
									overwriteWindow.close();
								}
							});
							Button yesButton = new Button("Yes");
							yesButton.addClickListener(new ClickListener() {

								@Override
								public void buttonClick(ClickEvent event) {
									MonitoredView.this.beanRegistrarService.saveBeanDefinition(beanDefinition, true);
									overwriteWindow.close();
									newMonitoredWindow1.close();
									Notification notification = new Notification("Monitored Application Saved Successfully");
									notification.show(UI.getCurrent().getPage());
									MonitoredView.this.monitoredTable.refreshTable();

								}
							});
							horizontalLayout.addComponents(yesButton, noButton);
							horizontalLayout.setSpacing(true);
							verticalLayout.addComponents(label, horizontalLayout);
							verticalLayout.setSpacing(true);
							verticalLayout.setMargin(true);
							overwriteWindow.setContent(verticalLayout);
							return overwriteWindow;
						}
					});
				}
			}

		}

		private boolean isSaveButton(Component component) {
			return component instanceof Button && component.getId().equals(SAVE_BUTTON_CAPTION);
		}

		private boolean isTestButton(Component component) {
			return component instanceof Button && component.getId().equals(TEST_BUTTON_CAPTION);
		}

		private Layout createTestSaveButtonsLayout() {
			Button testMonitored = new Button("Test");
			testMonitored.setId(TEST_BUTTON_CAPTION);
			Button saveMonitored = new Button("Save");
			saveMonitored.setId(SAVE_BUTTON_CAPTION);
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
			monitoredType.addListener(new MonitoredItemSetChangeListener(monitoredItems, verticalLayout, this.newTestSaveButtonsLayout));
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
				if (this.currentSelection != null && value != this.currentSelection) {
					this.layout.removeComponent(this.monitoredItems.get(this.currentSelection));
				}
				this.currentSelection = (MonitoredUIItem)value;
				if (currentSelectionLayout != null) {
					this.layout.addComponent(currentSelectionLayout);
					this.newTestSaveButtonsLayout.setEnabled(true);
				} else {
					this.newTestSaveButtonsLayout.setEnabled(false);
				}
			}

		}

	}

	@Override
	public void enter(final ViewChangeEvent event) {}

}
