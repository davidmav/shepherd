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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import org.shepherd.vaadin.dashboard.view.monitored.table.MonitoredTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import javax.annotation.PostConstruct;

@SuppressWarnings("serial")
@VaadinView(name = "Monitored")
@UIScope
public class MonitoredView extends VerticalLayout implements View {

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

		@Override
		public void buttonClick(ClickEvent event) {
			MonitoredView.this.monitoredTable.addNewMonitored();
		}

	}

	@Override
	public void enter(final ViewChangeEvent event) {}

}
