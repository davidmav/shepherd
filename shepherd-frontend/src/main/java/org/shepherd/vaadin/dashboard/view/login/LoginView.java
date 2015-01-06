package org.shepherd.vaadin.dashboard.view.login;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import org.shepherd.vaadin.dashboard.event.DashboardEvent.UserLoginRequestedEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEventBus;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

@SuppressWarnings("serial")
@VaadinView(name = "Login")
@UIScope
public class LoginView extends VerticalLayout implements View {

	public static final String STYLE_NAME = "loginview";
	private TextField username;

	private PasswordField password;

	private Button loginButton;

	public LoginView() {
		setSizeFull();

		Component loginForm = buildLoginForm();
		addComponent(loginForm);
		setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
	}

	private Component buildLoginForm() {
		final VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.setSizeUndefined();
		loginPanel.setSpacing(true);
		Responsive.makeResponsive(loginPanel);
		loginPanel.addStyleName("login-panel");

		loginPanel.addComponent(buildLabels());
		loginPanel.addComponent(buildFields());
		loginPanel.addComponent(new CheckBox("Remember me", true));
		return loginPanel;
	}

	private Component buildFields() {
		HorizontalLayout fields = new HorizontalLayout();
		fields.setSpacing(true);
		fields.addStyleName("fields");

		this.username = new TextField("Username");
		this.username.setIcon(FontAwesome.USER);
		this.username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

		this.password = new PasswordField("Password");
		this.password.setIcon(FontAwesome.LOCK);
		this.password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

		this.loginButton = new Button("Sign In");
		this.loginButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.loginButton.setClickShortcut(KeyCode.ENTER);
		this.loginButton.focus();

		fields.addComponents(this.username, this.password, this.loginButton);
		fields.setComponentAlignment(this.loginButton, Alignment.BOTTOM_LEFT);

		this.loginButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				DashboardEventBus.post(new UserLoginRequestedEvent(LoginView.this.username.getValue(), LoginView.this.password.getValue()));
			}
		});
		return fields;
	}

	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label welcome = new Label("Welcome");
		welcome.setSizeUndefined();
		welcome.addStyleName(ValoTheme.LABEL_H4);
		welcome.addStyleName(ValoTheme.LABEL_COLORED);
		labels.addComponent(welcome);

		Label title = new Label("Shepherd Monitor Dashboard");
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_LIGHT);
		labels.addComponent(title);
		return labels;
	}

	@Override
	public void enter(ViewChangeEvent event) {}

}
