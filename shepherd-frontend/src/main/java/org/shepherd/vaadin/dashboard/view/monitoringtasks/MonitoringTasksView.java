package org.shepherd.vaadin.dashboard.view.monitoringtasks;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import org.shepherd.vaadin.dashboard.event.DashboardEvent.BrowserResizeEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEventBus;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@SuppressWarnings({ "serial", "unchecked" })
public final class MonitoringTasksView extends VerticalLayout implements View {

	private final Table table;
	private Button createReport;
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
	private static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");
	private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city", "theater", "room", "title", "seats" };

	public MonitoringTasksView() {
		setSizeFull();
		addStyleName("transactions");
		DashboardEventBus.register(this);

		addComponent(buildToolbar());

		table = buildTable();
		addComponent(table);
		setExpandRatio(table, 1);
	}

	@Override
	public void detach() {
		super.detach();
		// A new instance of TransactionsView is created every time it's
		// navigated to so we'll need to clean up references to it on detach.
		DashboardEventBus.unregister(this);
	}

	private Component buildToolbar() {
		HorizontalLayout header = new HorizontalLayout();
		header.addStyleName("viewheader");
		header.setSpacing(true);
		Responsive.makeResponsive(header);

		Label title = new Label("Monitoring Tasks");
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H1);
		title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		header.addComponent(title);

		//		createReport = buildCreateReport();
		//		HorizontalLayout tools = new HorizontalLayout(buildFilter(), createReport);
		//		tools.setSpacing(true);
		//		tools.addStyleName("toolbar");
		//		header.addComponent(tools);

		return header;
	}

	private Table buildTable() {
		final Table table = new Table() {

			@Override
			protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
				String result = super.formatPropertyValue(rowId, colId, property);
				if (colId.equals("time")) {
					result = DATEFORMAT.format(((Date)property.getValue()));
				} else if (colId.equals("price")) {
					if (property != null && property.getValue() != null) {
						return "$" + DECIMALFORMAT.format(property.getValue());
					} else {
						return "";
					}
				}
				return result;
			}
		};
		table.setSizeFull();
		table.addStyleName(ValoTheme.TABLE_BORDERLESS);
		table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		table.addStyleName(ValoTheme.TABLE_COMPACT);
		table.setSelectable(true);
		//
		//		table.setColumnCollapsingAllowed(true);
		//		table.setColumnCollapsible("time", false);
		//		table.setColumnCollapsible("price", false);
		//
		//		table.setColumnReorderingAllowed(true);
		//		table.setContainerDataSource(new TempTransactionsContainer(DashboardUI.getDataProvider().getRecentTransactions(200)));
		//		table.setSortContainerPropertyId("time");
		//		table.setSortAscending(false);
		//
		//		table.setColumnAlignment("Seats", Align.RIGHT);
		//		table.setColumnAlignment("Price", Align.RIGHT);
		//
		//		table.setVisibleColumns("time", "country", "city", "theater", "room", "title", "seats", "price");
		//		table.setColumnHeaders("Time", "Country", "City", "Theater", "Room", "Title", "Seats", "Price");
		//
		//		table.setFooterVisible(true);
		//		table.setColumnFooter("time", "Total");
		//
		//		table.setColumnFooter("price", "$" + DECIMALFORMAT.format(DashboardUI.getDataProvider().getTotalSum()));
		//
		//		// Allow dragging items to the reports menu
		//		table.setDragMode(TableDragMode.MULTIROW);
		//		table.setMultiSelect(true);

		table.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(final ValueChangeEvent event) {
				if (table.getValue() instanceof Set) {
					Set<Object> val = (Set<Object>)table.getValue();
					createReport.setEnabled(val.size() > 0);
				}
			}
		});
		table.setImmediate(true);

		return table;
	}

	private boolean defaultColumnsVisible() {
		boolean result = true;
		for (String propertyId : DEFAULT_COLLAPSIBLE) {
			if (table.isColumnCollapsed(propertyId) == Page.getCurrent().getBrowserWindowWidth() < 800) {
				result = false;
			}
		}
		return result;
	}

	@Subscribe
	public void browserResized(final BrowserResizeEvent event) {
		// Some columns are collapsed when browser window width gets small
		// enough to make the table fit better.
		if (defaultColumnsVisible()) {
			for (String propertyId : DEFAULT_COLLAPSIBLE) {
				table.setColumnCollapsed(propertyId, Page.getCurrent().getBrowserWindowWidth() < 800);
			}
		}
	}

	void createNewReportFromSelection() {
		//        UI.getCurrent().getNavigator()
		//                .navigateTo(DashboardViewType.REPORTS.getViewName());
		//		DashboardEventBus.post(new TransactionReportEvent((Collection<Transaction>)table.getValue()));
	}

	@Override
	public void enter(final ViewChangeEvent event) {}

}