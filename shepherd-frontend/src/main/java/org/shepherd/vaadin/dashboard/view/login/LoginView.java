package org.shepherd.vaadin.dashboard.view.login;

import org.shepherd.vaadin.dashboard.event.DashboardEvent.UserLoginRequestedEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEventBus;

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

@SuppressWarnings("serial")
public class LoginView extends VerticalLayout implements View{

	public static final String STYLE_NAME = "loginview";
	private  TextField username;

	private  PasswordField password;

	private  Button loginButton;

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

		username = new TextField("Username");
		username.setIcon(FontAwesome.USER);
		username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

		password = new PasswordField("Password");
		password.setIcon(FontAwesome.LOCK);
		password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

		loginButton = new Button("Sign In");
		loginButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		loginButton.setClickShortcut(KeyCode.ENTER);
		loginButton.focus();

		fields.addComponents(username, password, loginButton);
		fields.setComponentAlignment(loginButton, Alignment.BOTTOM_LEFT);

		loginButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				DashboardEventBus.post(new UserLoginRequestedEvent(username
						.getValue(), password.getValue()));
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
