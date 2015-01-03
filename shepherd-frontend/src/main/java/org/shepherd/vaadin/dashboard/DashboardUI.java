package org.shepherd.vaadin.dashboard;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import org.shepherd.domain.User;
import org.shepherd.monitored.service.UserService;
import org.shepherd.vaadin.dashboard.event.DashboardEvent.BrowserResizeEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEvent.CloseOpenWindowsEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEvent.UserLoggedOutEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEvent.UserLoginRequestedEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEventBus;
import org.shepherd.vaadin.dashboard.view.MainView;
import org.shepherd.vaadin.dashboard.view.dashboard.DashboardView;
import org.shepherd.vaadin.dashboard.view.login.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.vaadin.spring.VaadinUI;
import org.vaadin.spring.navigator.SpringViewProvider;

import java.util.Locale;

@Title("Shepherd Monitor")
@SuppressWarnings("serial")
@ComponentScan(basePackages = { "org.shepherd" })
@EnableAutoConfiguration
@ImportResource("META-INF/bootstrap/*")
@VaadinUI
@Theme("dashboard")
public final class DashboardUI extends UI {

	@SuppressWarnings("unused")
	private static ApplicationContext applicationContext;

	@Autowired
	private UserService userService;

	@Autowired
	private SpringViewProvider springViewProvider;

	public static void main(String[] args) {
		DashboardUI.applicationContext = SpringApplication.run(DashboardUI.class, args);
	}

	/*
	 * This field stores an access to the dummy backend layer. In real
	 * applications you most likely gain access to your beans trough lookup or
	 * injection; and not in the UI but somewhere closer to where they're
	 * actually accessed.
	 */
	private final DashboardEventBus dashboardEventbus = new DashboardEventBus();

	@Override
	protected void init(final VaadinRequest request) {
		setLocale(Locale.US);

		DashboardEventBus.register(this);
		Responsive.makeResponsive(this);

		updateContent();

		// Some views need to be aware of browser resize events so a
		// BrowserResizeEvent gets fired to the event but on every occasion.
		Page.getCurrent().addBrowserWindowResizeListener(new BrowserWindowResizeListener() {

			@Override
			public void browserWindowResized(final BrowserWindowResizeEvent event) {
				DashboardEventBus.post(new BrowserResizeEvent());
			}
		});
	}

	/**
	 * Updates the correct content for this UI based on the current user status. If the user is logged in with appropriate
	 * privileges, main view is shown. Otherwise login view is shown.
	 */
	private void updateContent() {

		User user = (User)VaadinSession.getCurrent().getAttribute(User.class.getName());

		if (user != null && "admin".equals(user.getRole())) {
			//Authenticated user
			//			user = getDataProvider().authenticate("David Mavashev", "");
			VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
			setContent(new MainView());

			removeStyleName(LoginView.STYLE_NAME);
			addStyleName(MainView.STYLE_NAME);

			Navigator navigator = getNavigator();
			navigator.addProvider(springViewProvider);
			navigator.navigateTo(DashboardView.ID);
		} else {
			setContent(new LoginView());
			addStyleName(LoginView.STYLE_NAME);
		}
	}

	@Subscribe
	public void userLoginRequested(final UserLoginRequestedEvent event) throws Exception {
		User user = authenticate(event.getUserName(), event.getPassword());
		if (user != null) {
			VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
			updateContent();
		}
	}

	//dummy authentication
	private User authenticate(String userName, String password) throws Exception {

		User user = this.userService.authenticate(userName, password);

		if (!"admin".equals(userName) || !password.equals("admin")) {
			getErrorNotification();
			return null;
		}
		return user;
	}

	@Subscribe
	public void userLoggedOut(final UserLoggedOutEvent event) {
		// When the user logs out, current VaadinSession gets closed and the
		// page gets reloaded on the login screen. Do notice the this doesn't
		// invalidate the current HttpSession.
		VaadinSession.getCurrent().close();
		Page.getCurrent().reload();
	}

	@Subscribe
	public void closeOpenWindows(final CloseOpenWindowsEvent event) {
		for (Window window : getWindows()) {
			window.close();
		}
	}

	public static DashboardEventBus getDashboardEventbus() {
		return ((DashboardUI)getCurrent()).dashboardEventbus;
	}

	public void getErrorNotification() {
		Notification notification = new Notification("Wrong credentials");
		notification.setDescription("Username or password are incorrect");
		notification.setHtmlContentAllowed(true);
		notification.setStyleName("tray dark small closable login-help");
		notification.setPosition(Position.BOTTOM_CENTER);
		notification.show(Page.getCurrent());
		notification.setIcon(FontAwesome.INFO);
		notification.setDelayMsec(2000);
	}
}
