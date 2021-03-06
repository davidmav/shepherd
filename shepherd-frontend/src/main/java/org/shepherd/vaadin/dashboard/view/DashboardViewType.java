package org.shepherd.vaadin.dashboard.view;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

import org.shepherd.vaadin.dashboard.view.dashboard.DashboardView;
import org.shepherd.vaadin.dashboard.view.login.LoginView;
import org.shepherd.vaadin.dashboard.view.monitored.MonitoredView;
import org.shepherd.vaadin.dashboard.view.monitoringtasks.MonitoringTasksView;
import org.shepherd.vaadin.dashboard.view.settings.SettingsView;

public enum DashboardViewType {
	DASHBOARD("Dashboard", DashboardView.class, FontAwesome.HOME, true),
	MONITORED("Monitored", MonitoredView.class, FontAwesome.CLOUD, false),
	MONITORINGTASKS("Monitoring Tasks", MonitoringTasksView.class, FontAwesome.EYE, false),
	SETTINGS("Settings", SettingsView.class, FontAwesome.WRENCH, false),
	LOGIN("Login", LoginView.class, FontAwesome.USER, false);

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
		return this.stateful;
	}

	public String getViewName() {
		return this.viewName;
	}

	public Class<? extends View> getViewClass() {
		return this.viewClass;
	}

	public Resource getIcon() {
		return this.icon;
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
