package org.shepherd.vaadin.dashboard.view.monitored.window;

import com.vaadin.server.FontAwesome;
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

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.MonitoredException;
import org.shepherd.monitored.beans.definition.BeanDefinitionService;
import org.shepherd.monitored.beans.registrar.BeanAlreadyExistsException;
import org.shepherd.monitored.beans.registrar.BeanOfOtherClassAlreadyExistsException;
import org.shepherd.monitored.beans.registrar.BeanRegistrarService;
import org.shepherd.monitored.beans.registrar.UnableToSaveBeanException;
import org.shepherd.vaadin.dashboard.view.monitored.MonitoredUIItem;
import org.shepherd.vaadin.dashboard.view.monitored.MonitoredView;
import org.shepherd.vaadin.dashboard.view.monitored.table.MonitoredTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MonitoredWindow extends Window {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitoredView.class);

	private static final String SAVE_BUTTON_CAPTION = "save";
	private static final String TEST_BUTTON_CAPTION = "test";

	private ComboBox monitoredComboBox;

	private TextField idField;

	private BeanRegistrarService beanRegistrarService;

	private BeanDefinitionService beanDefinitionService;

	private MonitoredTable parentTable;

	private BeanDefinition beanDefinition;

	protected MonitoredWindow(MonitoredTable parentTable, BeanRegistrarService beanRegistrarService, BeanDefinitionService beanDefinitionService) {
		this.parentTable = parentTable;
		this.beanRegistrarService = beanRegistrarService;
		this.beanDefinitionService = beanDefinitionService;
		this.setIcon(FontAwesome.CLOUD);
		this.setStyleName("monitored");
		this.setModal(true);
		this.setResizable(false);
		int browserWindowHeight = UI.getCurrent().getPage().getBrowserWindowHeight();
		int browserWindowWidth = UI.getCurrent().getPage().getBrowserWindowWidth();
		this.setPositionY(browserWindowHeight / 10);
		this.setPositionX(browserWindowWidth / 3);

	}

	/**
	 * For new MonitoredWindow
	 * 
	 * @param parentTable
	 * @param beanRegistrarService
	 * @param beanDefinitionService
	 * @param allMonitoredClasses
	 */
	public MonitoredWindow(MonitoredTable parentTable, BeanRegistrarService beanRegistrarService, BeanDefinitionService beanDefinitionService, Collection<Class<Monitored>> allMonitoredClasses) {
		this(parentTable, beanRegistrarService, beanDefinitionService);
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		this.setContent(horizontalLayout);
		this.monitoredComboBox = new ComboBox();
		this.monitoredComboBox.setCaption("Monitoring Type");
		Map<MonitoredUIItem, Layout> monitoredItems = new HashMap<MonitoredUIItem, Layout>();
		for (Class<Monitored> monitoredClass : allMonitoredClasses) {
			MonitoredUIItem item = new MonitoredUIItem(monitoredClass);
			monitoredItems.put(item, item.getLayout());
			this.monitoredComboBox.addItem(item);
		}
		Layout testSaveButtonsLayout = createTestSaveButtonsLayout();
		Layout comboBoxLayout = createComboBoxLayout(this.monitoredComboBox, monitoredItems, testSaveButtonsLayout);
		registerButtonListeners(testSaveButtonsLayout, this.monitoredComboBox);
		horizontalLayout.addComponents(comboBoxLayout, testSaveButtonsLayout);
		setModeNew();
	}

	/**
	 * For already existing Monitored components
	 * 
	 * @param parentTable
	 * @param beanRegistrarService
	 * @param beanDefinitionService
	 * @param monitored
	 */
	public MonitoredWindow(MonitoredTable parentTable, BeanRegistrarService beanRegistrarService, BeanDefinitionService beanDefinitionService, String monitoredId) {
		this(parentTable, beanRegistrarService, beanDefinitionService);
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		this.setContent(horizontalLayout);
		this.monitoredComboBox = new ComboBox();
		this.monitoredComboBox.setCaption("Monitoring Type");
		BeanDefinition monitoredBeanDefinition = this.beanRegistrarService.getBeanDefinition(monitoredId);
		this.beanDefinition = monitoredBeanDefinition;
		//Safe Casting
		MonitoredUIItem item = new MonitoredUIItem(monitoredBeanDefinition);
		Layout testSaveButtonsLayout = createTestSaveButtonsLayout();
		Layout comboBoxLayout = createComboBoxLayout(this.monitoredComboBox, Collections.singletonMap(item, item.getLayout()), testSaveButtonsLayout);
		this.monitoredComboBox.addItem(item);
		this.monitoredComboBox.select(item);
		registerButtonListeners(testSaveButtonsLayout, this.monitoredComboBox);
		horizontalLayout.addComponents(comboBoxLayout, testSaveButtonsLayout);
		this.idField = item.getIdField();
		setModeEdit();
	}

	protected void setModeEdit() {
		this.setCaption(" " + this.monitoredComboBox.getValue().toString() + " Monitored Application");
		this.idField.setEnabled(false);
		this.monitoredComboBox.setEnabled(false);

	}

	protected void setModeNew() {
		this.setCaption(" New Monitored Application");
	}

	private boolean isSaveButton(Component component) {
		return component instanceof Button && component.getId().equals(SAVE_BUTTON_CAPTION);
	}

	private boolean isTestButton(Component component) {
		return component instanceof Button && component.getId().equals(TEST_BUTTON_CAPTION);
	}

	private void registerButtonListeners(Layout newTestSaveButtonsLayout1, ComboBox monitoredType) {
		for (Component component : newTestSaveButtonsLayout1) {
			if (isTestButton(component)) {
				((Button)component).addClickListener(new TestButtonClickListener(monitoredType));
			} else if (isSaveButton(component)) {
				((Button)component).addClickListener(new SaveButtonClickListener(monitoredType));
			}
		}

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

	private Layout createComboBoxLayout(ComboBox monitoredType, Map<MonitoredUIItem, Layout> monitoredItems, Layout testSaveButtonsLayout) {
		VerticalLayout verticalLayout = new VerticalLayout(monitoredType);
		monitoredType.addListener(new MonitoredItemSetChangeListener(monitoredItems, verticalLayout, testSaveButtonsLayout));
		verticalLayout.setSpacing(true);
		MarginInfo marginInfo = new MarginInfo(true);
		verticalLayout.setMargin(marginInfo);
		return verticalLayout;
	}

	private class TestButtonClickListener implements ClickListener {

		private static final long serialVersionUID = 1L;

		private ComboBox monitoredType;

		public TestButtonClickListener(ComboBox monitoredType) {
			this.monitoredType = monitoredType;
		}

		@Override
		public void buttonClick(ClickEvent event) {
			MonitoredUIItem value = (MonitoredUIItem)this.monitoredType.getValue();
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
	}

	private class SaveButtonClickListener implements ClickListener {

		private static final long serialVersionUID = 1L;

		private ComboBox monitoredType;

		public SaveButtonClickListener(ComboBox monitoredType) {
			this.monitoredType = monitoredType;
		}

		@Override
		public void buttonClick(ClickEvent event) {

			MonitoredUIItem value = (MonitoredUIItem)this.monitoredType.getValue();
			if (value != null) {
				Notification notification = null;
				Window overwriteWindow = null;
				try {
					Constructor<? extends Monitored> constructor = value.getConstructor();
					Object[] arguments = value.getArguments();
					TextField id = (TextField)value.getLayout().iterator().next();
					BeanDefinition newBeanDefinition = MonitoredWindow.this.beanDefinitionService.createMonitoredBeanDefinition(id.getValue(), constructor, arguments);
					overwriteWindow = createOverwriteWindow(MonitoredWindow.this, newBeanDefinition);
					if (MonitoredWindow.this.beanDefinition != null) {
						MonitoredWindow.this.beanRegistrarService.saveBeanDefinition(newBeanDefinition, true);
					} else {
						MonitoredWindow.this.beanRegistrarService.saveBeanDefinition(newBeanDefinition, false);
					}
					MonitoredWindow.this.close();
					notification = new Notification("Monitored Application Saved Successfully");
					MonitoredWindow.this.parentTable.refreshTable();
					MonitoredWindow.this.beanDefinition = newBeanDefinition;
					MonitoredWindow.this.parentTable.addMonitoredWindow(newBeanDefinition.getAttribute("id"), MonitoredWindow.this);
					MonitoredWindow.this.idField = id;
					MonitoredWindow.this.setModeEdit();
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

		private Window createOverwriteWindow(Window newMonitoredWindow1, final BeanDefinition newBeanDefinition) {
			Window overwriteWindow = new Window(" Monitored with the same Id already exists ");
			overwriteWindow.setIcon(FontAwesome.INFO);
			overwriteWindow.setModal(true);
			overwriteWindow.setResizable(false);
			VerticalLayout verticalLayout = new VerticalLayout();
			HorizontalLayout horizontalLayout = new HorizontalLayout();
			Label label = new Label("Monitored with the same Id already exists, overwrite?");
			Button noButton = new Button("No");
			noButton.addClickListener(new ClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					overwriteWindow.close();
				}
			});
			Button yesButton = new Button("Yes");
			yesButton.addClickListener(new ClickListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					MonitoredWindow.this.beanRegistrarService.saveBeanDefinition(newBeanDefinition, true);
					overwriteWindow.close();
					newMonitoredWindow1.close();
					Notification notification = new Notification("Monitored Application Saved Successfully");
					notification.show(UI.getCurrent().getPage());
					MonitoredWindow.this.parentTable.refreshTable();
					MonitoredWindow.this.beanDefinition = newBeanDefinition;
					MonitoredWindow.this.parentTable.addMonitoredWindow(newBeanDefinition.getAttribute("id"), MonitoredWindow.this);
					MonitoredWindow.this.setModeEdit();
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
	}

	private class MonitoredItemSetChangeListener implements Listener {

		private static final long serialVersionUID = 1L;

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

}
