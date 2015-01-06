package org.shepherd.vaadin.dashboard.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import org.shepherd.domain.User;
import org.shepherd.vaadin.dashboard.event.DashboardEvent.CloseOpenWindowsEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEvent.ProfileUpdatedEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEventBus;

@SuppressWarnings("serial")
public class ProfilePreferencesWindow extends Window {

	public static final String ID = "profilepreferenceswindow";

	private final BeanFieldGroup<User> fieldGroup;
	/*
	 * Fields for editing the User object are defined here as class members.
	 * They are later bound to a FieldGroup by calling
	 * fieldGroup.bindMemberFields(this). The Fields' values don't need to be
	 * explicitly set, calling fieldGroup.setItemDataSource(user) synchronizes
	 * the fields with the user object.
	 */
	@PropertyId("firstName")
	private TextField firstNameField;
	@PropertyId("lastName")
	private TextField lastNameField;
	@PropertyId("title")
	private ComboBox titleField;
	@PropertyId("male")
	private OptionGroup sexField;
	@PropertyId("email")
	private TextField emailField;
	@PropertyId("location")
	private TextField locationField;
	@PropertyId("phone")
	private TextField phoneField;
	@PropertyId("newsletterSubscription")
	private OptionalSelect<Integer> newsletterField;
	@PropertyId("website")
	private TextField websiteField;
	@PropertyId("bio")
	private TextArea bioField;

	private ProfilePreferencesWindow(final User user, final boolean preferencesTabOpen) {
		addStyleName("profile-window");
		setId(ID);
		Responsive.makeResponsive(this);

		setModal(true);
		setCloseShortcut(KeyCode.ESCAPE, null);
		setResizable(false);
		setClosable(false);
		setHeight(90.0f, Unit.PERCENTAGE);

		VerticalLayout content = new VerticalLayout();
		content.setSizeFull();
		content.setMargin(new MarginInfo(true, false, false, false));
		setContent(content);

		TabSheet detailsWrapper = new TabSheet();
		detailsWrapper.setSizeFull();
		detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
		detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		content.addComponent(detailsWrapper);
		content.setExpandRatio(detailsWrapper, 1f);

		detailsWrapper.addComponent(buildProfileTab());
		detailsWrapper.addComponent(buildPreferencesTab());

		if (preferencesTabOpen) {
			detailsWrapper.setSelectedTab(1);
		}

		content.addComponent(buildFooter());

		this.fieldGroup = new BeanFieldGroup<User>(User.class);
		this.fieldGroup.bindMemberFields(this);
		this.fieldGroup.setItemDataSource(user);
	}

	private Component buildPreferencesTab() {
		VerticalLayout root = new VerticalLayout();
		root.setCaption("Preferences");
		root.setIcon(FontAwesome.COGS);
		root.setSpacing(true);
		root.setMargin(true);
		root.setSizeFull();

		Label message = new Label("Not implemented in this demo");
		message.setSizeUndefined();
		message.addStyleName(ValoTheme.LABEL_LIGHT);
		root.addComponent(message);
		root.setComponentAlignment(message, Alignment.MIDDLE_CENTER);

		return root;
	}

	private Component buildProfileTab() {
		HorizontalLayout root = new HorizontalLayout();
		root.setCaption("Profile");
		root.setIcon(FontAwesome.USER);
		root.setWidth(100.0f, Unit.PERCENTAGE);
		root.setSpacing(true);
		root.setMargin(true);
		root.addStyleName("profile-form");

		VerticalLayout pic = new VerticalLayout();
		pic.setSizeUndefined();
		pic.setSpacing(true);
		Image profilePic = new Image(null, new ThemeResource("img/profile-pic-300px.jpg"));
		profilePic.setWidth(100.0f, Unit.PIXELS);
		pic.addComponent(profilePic);

		Button upload = new Button("Change…", new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Not implemented in this demo");
			}
		});
		upload.addStyleName(ValoTheme.BUTTON_TINY);
		pic.addComponent(upload);

		root.addComponent(pic);

		FormLayout details = new FormLayout();
		details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		root.addComponent(details);
		root.setExpandRatio(details, 1);

		this.firstNameField = new TextField("First Name");
		details.addComponent(this.firstNameField);
		this.lastNameField = new TextField("Last Name");
		details.addComponent(this.lastNameField);

		this.titleField = new ComboBox("Title");
		this.titleField.setInputPrompt("Please specify");
		this.titleField.addItem("Mr.");
		this.titleField.addItem("Mrs.");
		this.titleField.addItem("Ms.");
		this.titleField.setNewItemsAllowed(true);
		details.addComponent(this.titleField);

		this.sexField = new OptionGroup("Sex");
		this.sexField.addItem(Boolean.FALSE);
		this.sexField.setItemCaption(Boolean.FALSE, "Female");
		this.sexField.addItem(Boolean.TRUE);
		this.sexField.setItemCaption(Boolean.TRUE, "Male");
		this.sexField.addStyleName("horizontal");
		details.addComponent(this.sexField);

		Label section = new Label("Contact Info");
		section.addStyleName(ValoTheme.LABEL_H4);
		section.addStyleName(ValoTheme.LABEL_COLORED);
		details.addComponent(section);

		this.emailField = new TextField("Email");
		this.emailField.setWidth("100%");
		this.emailField.setRequired(true);
		this.emailField.setNullRepresentation("");
		details.addComponent(this.emailField);

		this.locationField = new TextField("Location");
		this.locationField.setWidth("100%");
		this.locationField.setNullRepresentation("");
		this.locationField.setComponentError(new UserError("This address doesn't exist"));
		details.addComponent(this.locationField);

		this.phoneField = new TextField("Phone");
		this.phoneField.setWidth("100%");
		this.phoneField.setNullRepresentation("");
		details.addComponent(this.phoneField);

		this.newsletterField = new OptionalSelect<Integer>();
		this.newsletterField.addOption(0, "Daily");
		this.newsletterField.addOption(1, "Weekly");
		this.newsletterField.addOption(2, "Monthly");
		details.addComponent(this.newsletterField);

		section = new Label("Additional Info");
		section.addStyleName(ValoTheme.LABEL_H4);
		section.addStyleName(ValoTheme.LABEL_COLORED);
		details.addComponent(section);

		this.websiteField = new TextField("Website");
		this.websiteField.setInputPrompt("http://");
		this.websiteField.setWidth("100%");
		this.websiteField.setNullRepresentation("");
		details.addComponent(this.websiteField);

		this.bioField = new TextArea("Bio");
		this.bioField.setWidth("100%");
		this.bioField.setRows(4);
		this.bioField.setNullRepresentation("");
		details.addComponent(this.bioField);

		return root;
	}

	private Component buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);

		Button ok = new Button("OK");
		ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
		ok.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					ProfilePreferencesWindow.this.fieldGroup.commit();
					// Updated user should also be persisted to database. But
					// not in this demo.

					Notification success = new Notification("Profile updated successfully");
					success.setDelayMsec(2000);
					success.setStyleName("bar success small");
					success.setPosition(Position.BOTTOM_CENTER);
					success.show(Page.getCurrent());

					DashboardEventBus.post(new ProfileUpdatedEvent());
					close();
				} catch (CommitException e) {
					Notification.show("Error while updating profile", Type.ERROR_MESSAGE);
				}

			}
		});
		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
		return footer;
	}

	public static void open(final User user, final boolean preferencesTabActive) {
		DashboardEventBus.post(new CloseOpenWindowsEvent());
		Window w = new ProfilePreferencesWindow(user, preferencesTabActive);
		UI.getCurrent().addWindow(w);
		w.focus();
	}
}
