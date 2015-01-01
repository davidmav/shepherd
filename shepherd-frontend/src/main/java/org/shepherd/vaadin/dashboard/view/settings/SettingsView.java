package org.shepherd.vaadin.dashboard.view.settings;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import org.shepherd.vaadin.dashboard.event.DashboardEvent.ReportsCountUpdatedEvent;
import org.shepherd.vaadin.dashboard.event.DashboardEventBus;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.navigator.VaadinView;

@SuppressWarnings("serial")
@VaadinView(name = "Settings")
@UIScope
public final class SettingsView extends TabSheet implements View, CloseHandler {

	public static final String CONFIRM_DIALOG_ID = "confirm-dialog";

	public SettingsView() {
		setSizeFull();
		addStyleName("settings");
		addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		setCloseHandler(this);
		DashboardEventBus.register(this);

		addTab(buildGeneralSettings());
		addTab(buildLDAPSettings());
		addTab(buildSMTPSettings());
	}

	private Component buildSMTPSettings() {
		final VerticalLayout smtpSettings = new VerticalLayout();
		smtpSettings.setSizeFull();
		smtpSettings.setCaption("SMTP");

		VerticalLayout titleAndDrafts = new VerticalLayout();
		titleAndDrafts.setSizeUndefined();
		titleAndDrafts.setSpacing(true);
		titleAndDrafts.addStyleName("smtp");
		smtpSettings.addComponent(titleAndDrafts);
		smtpSettings.setComponentAlignment(titleAndDrafts, Alignment.MIDDLE_CENTER);

		Label draftsTitle = new Label("SMTP Settings");
		draftsTitle.addStyleName(ValoTheme.LABEL_H1);
		draftsTitle.setSizeUndefined();
		titleAndDrafts.addComponent(draftsTitle);
		titleAndDrafts.setComponentAlignment(draftsTitle, Alignment.TOP_CENTER);

		//		titleAndDrafts.addComponent(buildDraftsList());

		return smtpSettings;
	}

	private Component buildLDAPSettings() {
		final VerticalLayout ldapSettings = new VerticalLayout();
		ldapSettings.setSizeFull();
		ldapSettings.setCaption("LDAP");

		VerticalLayout titleAndDrafts = new VerticalLayout();
		titleAndDrafts.setSizeUndefined();
		titleAndDrafts.setSpacing(true);
		titleAndDrafts.addStyleName("ldap");
		ldapSettings.addComponent(titleAndDrafts);
		ldapSettings.setComponentAlignment(titleAndDrafts, Alignment.MIDDLE_CENTER);

		Label draftsTitle = new Label("LDAP Settings");
		draftsTitle.addStyleName(ValoTheme.LABEL_H1);
		draftsTitle.setSizeUndefined();
		titleAndDrafts.addComponent(draftsTitle);
		titleAndDrafts.setComponentAlignment(draftsTitle, Alignment.TOP_CENTER);

		//		titleAndDrafts.addComponent(buildDraftsList());

		return ldapSettings;
	}

	private Component buildGeneralSettings() {
		final VerticalLayout generalSettings = new VerticalLayout();
		generalSettings.setSizeFull();
		generalSettings.setCaption("General Settings");

		VerticalLayout titleAndDrafts = new VerticalLayout();
		titleAndDrafts.setSizeUndefined();
		titleAndDrafts.setSpacing(true);
		titleAndDrafts.addStyleName("generalsettings");
		generalSettings.addComponent(titleAndDrafts);
		generalSettings.setComponentAlignment(titleAndDrafts, Alignment.MIDDLE_CENTER);

		Label draftsTitle = new Label("General Settings");
		draftsTitle.addStyleName(ValoTheme.LABEL_H1);
		draftsTitle.setSizeUndefined();
		titleAndDrafts.addComponent(draftsTitle);
		titleAndDrafts.setComponentAlignment(draftsTitle, Alignment.TOP_CENTER);

		//		titleAndDrafts.addComponent(buildDraftsList());

		return generalSettings;
	}

	@Override
	public void onTabClose(final TabSheet tabsheet, final Component tabContent) {
		Label message = new Label("You have not saved this report. Do you want to save or discard any changes you've made to this report?");
		message.setWidth("25em");

		final Window confirmDialog = new Window("Unsaved Changes");
		confirmDialog.setId(CONFIRM_DIALOG_ID);
		confirmDialog.setCloseShortcut(KeyCode.ESCAPE, null);
		confirmDialog.setModal(true);
		confirmDialog.setClosable(false);
		confirmDialog.setResizable(false);

		VerticalLayout root = new VerticalLayout();
		root.setSpacing(true);
		root.setMargin(true);
		confirmDialog.setContent(root);

		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth("100%");
		footer.setSpacing(true);

		root.addComponents(message, footer);

		Button ok = new Button("Save", new ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				confirmDialog.close();
				removeComponent(tabContent);
				DashboardEventBus.post(new ReportsCountUpdatedEvent(getComponentCount() - 1));
				Notification.show("The report was saved as a draft", "Actually, the report was just closed and deleted forever. As this is only a demo, it doesn't persist any data.",
						Type.TRAY_NOTIFICATION);
			}
		});
		ok.addStyleName(ValoTheme.BUTTON_PRIMARY);

		Button discard = new Button("Discard Changes", new ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				confirmDialog.close();
				removeComponent(tabContent);
				DashboardEventBus.post(new ReportsCountUpdatedEvent(getComponentCount() - 1));
			}
		});
		discard.addStyleName(ValoTheme.BUTTON_DANGER);

		Button cancel = new Button("Cancel", new ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				confirmDialog.close();
			}
		});

		footer.addComponents(discard, cancel, ok);
		footer.setExpandRatio(discard, 1);

		getUI().addWindow(confirmDialog);
		confirmDialog.focus();
	}

	@Override
	public void enter(final ViewChangeEvent event) {}

	public enum ReportType {
		MONTHLY,
		EMPTY,
		TRANSACTIONS
	}

}
