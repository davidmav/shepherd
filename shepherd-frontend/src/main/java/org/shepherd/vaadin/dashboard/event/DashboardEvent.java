package org.shepherd.vaadin.dashboard.event;

import org.shepherd.vaadin.dashboard.view.DashboardViewType;

/*
 * Event bus events used in Dashboard are listed here as inner classes.
 */
public abstract class DashboardEvent {

	public static final class UserLoginRequestedEvent {

		private final String userName, password;

		public UserLoginRequestedEvent(final String userName, final String password) {
			this.userName = userName;
			this.password = password;
		}

		public String getUserName() {
			return this.userName;
		}

		public String getPassword() {
			return this.password;
		}
	}

	public static class BrowserResizeEvent {

	}

	public static class UserLoggedOutEvent {

	}

	public static class NotificationsCountUpdatedEvent {
	}

	public static final class ReportsCountUpdatedEvent {

		private final int count;

		public ReportsCountUpdatedEvent(final int count) {
			this.count = count;
		}

		public int getCount() {
			return this.count;
		}

	}

	public static final class PostViewChangeEvent {

		private final DashboardViewType view;

		public PostViewChangeEvent(final DashboardViewType view) {
			this.view = view;
		}

		public DashboardViewType getView() {
			return this.view;
		}
	}

	public static class CloseOpenWindowsEvent {
	}

	public static class ProfileUpdatedEvent {
	}

}
