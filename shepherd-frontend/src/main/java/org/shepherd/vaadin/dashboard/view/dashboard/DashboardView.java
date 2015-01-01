package org.shepherd.vaadin.dashboard.view.dashboard;

import org.shepherd.domain.User;
import org.shepherd.vaadin.dashboard.component.ProfilePreferencesWindow;
import org.shepherd.vaadin.dashboard.event.DashboardEvent.CloseOpenWindowsEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEvent.NotificationsCountUpdatedEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEvent.UserLoggedOutEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEventBus;
import org.shepherd.vaadin.dashboard.view.dashboard.DashboardEdit.DashboardEditListener;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@VaadinView(name = "Dashboard")
@UIScope
public final class DashboardView extends Panel implements View, DashboardEditListener {

	public static final String EDIT_ID = "dashboard-edit";
	public static final String TITLE_ID = "dashboard-title";

	private Label titleLabel;
	private NotificationsButton notificationsButton;
	private CssLayout dashboardPanels;
	private final VerticalLayout root;
	private Window notificationsWindow;
	
	private MenuItem settingsItem;

	public DashboardView() {
		addStyleName(ValoTheme.PANEL_BORDERLESS);
		setSizeFull();
		DashboardEventBus.register(this);

		root = new VerticalLayout();
		root.setSizeFull();
		root.setMargin(true);
		root.addStyleName("dashboard-view");
		setContent(root);
		Responsive.makeResponsive(root);

		root.addComponent(buildHeader());

		root.addComponent(buildSparklines());

		Component content = buildContent();
		root.addComponent(content);
		root.setExpandRatio(content, 1);

		// All the open sub-windows should be closed whenever the root layout
		// gets clicked.
		root.addLayoutClickListener(new LayoutClickListener() {

			@Override
			public void layoutClick(final LayoutClickEvent event) {
				DashboardEventBus.post(new CloseOpenWindowsEvent());
			}
		});
	}

	private Component buildSparklines() {
		CssLayout sparks = new CssLayout();
		sparks.addStyleName("sparks");
		sparks.setWidth("100%");
		Responsive.makeResponsive(sparks);

		//        SparklineChart s = new SparklineChart("Traffic", "K", "",
		//                DummyDataGenerator.chartColors[0], 22, 20, 80);
		//        sparks.addComponent(s);
		//
		//        s = new SparklineChart("Revenue / Day", "M", "$",
		//                DummyDataGenerator.chartColors[2], 8, 89, 150);
		//        sparks.addComponent(s);
		//
		//        s = new SparklineChart("Checkout Time", "s", "",
		//                DummyDataGenerator.chartColors[3], 10, 30, 120);
		//        sparks.addComponent(s);
		//
		//        s = new SparklineChart("Theater Fill Rate", "%", "",
		//                DummyDataGenerator.chartColors[5], 50, 34, 100);
		//        sparks.addComponent(s);

		return sparks;
	}

	private Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.addStyleName("viewheader");
		header.setSpacing(true);

		titleLabel = new Label("Dashboard");
		titleLabel.setId(TITLE_ID);
		titleLabel.setSizeUndefined();
		titleLabel.addStyleName(ValoTheme.LABEL_H1);
		titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		header.addComponent(titleLabel);

		notificationsButton = buildNotificationsButton();

		Component edit = buildEditButton();
		
		Component userMenuComponent = buildUserMenu();
		HorizontalLayout tools = new HorizontalLayout(userMenuComponent,notificationsButton, edit);
		tools.setSpacing(true);
		tools.addStyleName("toolbar");
		header.addComponent(tools);

		return header;
	}

	private User getCurrentUser() {
		return (User)VaadinSession.getCurrent().getAttribute(User.class.getName());
	}
	
	private Component buildUserMenu() {
		
		final MenuBar settings = new MenuBar();
		final User user = getCurrentUser();
		
		settings.addStyleName("user-menu");
		settingsItem = settings.addItem("", new ThemeResource("img/profile-pic-300px.png"), null);
		settingsItem.addItem("Edit Profile", new Command() {

			@Override
			public void menuSelected(final MenuItem selectedItem) {
				ProfilePreferencesWindow.open(user, false);
			}
		});
		settingsItem.addItem("Preferences", new Command() {

			@Override
			public void menuSelected(final MenuItem selectedItem) {
				ProfilePreferencesWindow.open(user, true);
			}
		});
		settingsItem.addSeparator();
		settingsItem.addItem("Sign Out", new Command() {

			@Override
			public void menuSelected(final MenuItem selectedItem) {
				DashboardEventBus.post(new UserLoggedOutEvent());
			}
		});
		return settings;
	}

	private NotificationsButton buildNotificationsButton() {
		NotificationsButton result = new NotificationsButton();
		result.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				openNotificationsPopup(event);
			}
		});
		return result;
	}

	private Component buildEditButton() {
		Button result = new Button();
		result.setId(EDIT_ID);
		result.setIcon(FontAwesome.EDIT);
		result.addStyleName("icon-edit");
		result.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		result.setDescription("Edit Dashboard");
		result.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				getUI().addWindow(new DashboardEdit(DashboardView.this, titleLabel.getValue()));
			}
		});
		return result;
	}

	private Component buildContent() {
		dashboardPanels = new CssLayout();
		dashboardPanels.addStyleName("dashboard-panels");
		Responsive.makeResponsive(dashboardPanels);

		//		dashboardPanels.addComponent(buildTopGrossingMovies());
		//		dashboardPanels.addComponent(buildNotes());
		//		dashboardPanels.addComponent(buildTop10TitlesByRevenue());
		//		dashboardPanels.addComponent(buildPopularMovies());

		return dashboardPanels;
	}

	//	private Component buildNotes() {
	//		TextArea notes = new TextArea("Notes");
	//		notes.setValue("Remember to:\n· Zoom in and out in the Sales view\n· Filter the transactions and drag a set of them to the Reports tab\n· Create a new report\n· Change the schedule of the movie theater");
	//		notes.setSizeFull();
	//		notes.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
	//		Component panel = createContentWrapper(notes);
	//		panel.addStyleName("notes");
	//		return panel;
	//	}

	private void openNotificationsPopup(final ClickEvent event) {
		VerticalLayout notificationsLayout = new VerticalLayout();
		notificationsLayout.setMargin(true);
		notificationsLayout.setSpacing(true);

		Label title = new Label("Notifications");
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		notificationsLayout.addComponent(title);

		//		Collection<DashboardNotification> notifications = DashboardUI.getDataProvider().getNotifications();
		DashboardEventBus.post(new NotificationsCountUpdatedEvent());

		//		for (DashboardNotification notification : notifications) {
		//			VerticalLayout notificationLayout = new VerticalLayout();
		//			notificationLayout.addStyleName("notification-item");
		//
		//			Label titleLabel = new Label(notification.getFirstName() + " " + notification.getLastName() + " " + notification.getAction());
		//			titleLabel.addStyleName("notification-title");
		//
		//			Label timeLabel = new Label(notification.getPrettyTime());
		//			timeLabel.addStyleName("notification-time");
		//
		//			Label contentLabel = new Label(notification.getContent());
		//			contentLabel.addStyleName("notification-content");
		//
		//			notificationLayout.addComponents(titleLabel, timeLabel, contentLabel);
		//			notificationsLayout.addComponent(notificationLayout);
		//		}

		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth("100%");
		Button showAll = new Button("View All Notifications", new ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				Notification.show("Not implemented in this demo");
			}
		});
		showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		showAll.addStyleName(ValoTheme.BUTTON_SMALL);
		footer.addComponent(showAll);
		footer.setComponentAlignment(showAll, Alignment.TOP_CENTER);
		notificationsLayout.addComponent(footer);

		if (notificationsWindow == null) {
			notificationsWindow = new Window();
			notificationsWindow.setWidth(300.0f, Unit.PIXELS);
			notificationsWindow.addStyleName("notifications");
			notificationsWindow.setClosable(false);
			notificationsWindow.setResizable(false);
			notificationsWindow.setDraggable(false);
			notificationsWindow.setCloseShortcut(KeyCode.ESCAPE, null);
			notificationsWindow.setContent(notificationsLayout);
		}

		if (!notificationsWindow.isAttached()) {
			notificationsWindow.setPositionY(event.getClientY() - event.getRelativeY() + 40);
			getUI().addWindow(notificationsWindow);
			notificationsWindow.focus();
		} else {
			notificationsWindow.close();
		}
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		//		notificationsButton.updateNotificationsCount(null);
	}

	@Override
	public void dashboardNameEdited(final String name) {
		titleLabel.setValue(name);
	}

	public static final class NotificationsButton extends Button {

		private static final String STYLE_UNREAD = "unread";
		public static final String ID = "dashboard-notifications";

		public NotificationsButton() {
			setIcon(FontAwesome.BELL);
			setId(ID);
			addStyleName("notifications");
			addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			DashboardEventBus.register(this);
		}

		//		@Subscribe
		//		public void updateNotificationsCount(final NotificationsCountUpdatedEvent event) {
		//			setUnreadCount(DashboardUI.getDataProvider().getUnreadNotificationsCount());
		//		}

		public void setUnreadCount(final int count) {
			setCaption(String.valueOf(count));

			String description = "Notifications";
			if (count > 0) {
				addStyleName(STYLE_UNREAD);
				description += " (" + count + " unread)";
			} else {
				removeStyleName(STYLE_UNREAD);
			}
			setDescription(description);
		}
	}

}
