package org.shepherd.vaadin.dashboard.view;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

import org.shepherd.vaadin.dashboard.view.dashboard.DashboardView;
import org.shepherd.vaadin.dashboard.view.reports.ReportsView;
import org.shepherd.vaadin.dashboard.view.sales.SalesView;
import org.shepherd.vaadin.dashboard.view.schedule.ScheduleView;
import org.shepherd.vaadin.dashboard.view.transactions.TransactionsView;

public enum DashboardViewType {
	DASHBOARD("dashboard", DashboardView.class, FontAwesome.HOME, true),
	SALES("monitored", SalesView.class, FontAwesome.BAR_CHART_O, false),
	TRANSACTIONS("monitoring tasks", TransactionsView.class, FontAwesome.TABLE, false),
	REPORTS("reports", ReportsView.class, FontAwesome.FILE_TEXT_O, true),
	SCHEDULE("schedule", ScheduleView.class, FontAwesome.CALENDAR_O, false);

	private final String viewName;
	private final Class<? extends View> viewClass;
	private final Resource icon;
	private final boolean stateful;

	private DashboardViewType(final String viewName, final Class<? extends View> viewClass, final Resource icon, final boolean stateful) {
		this.viewName = viewName;
		this.viewClass = viewClass;
		this.icon = icon;
		this.stateful = stateful;
	}

	public boolean isStateful() {
		return stateful;
	}

	public String getViewName() {
		return viewName;
	}

	public Class<? extends View> getViewClass() {
		return viewClass;
	}

	public Resource getIcon() {
		return icon;
	}

	public static DashboardViewType getByViewName(final String viewName) {
		DashboardViewType result = null;
		for (DashboardViewType viewType : values()) {
			if (viewType.getViewName().equals(viewName)) {
				result = viewType;
				break;
			}
		}
		return result;
	}

}
