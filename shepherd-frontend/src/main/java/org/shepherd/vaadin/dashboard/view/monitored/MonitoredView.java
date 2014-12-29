package org.shepherd.vaadin.dashboard.view.monitored;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class MonitoredView extends VerticalLayout implements View {

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

	@Override
	public void enter(final ViewChangeEvent event) {}

}
