package org.shepherd.vaadin.ui;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Common Yes No Window with some reasonable defaults
 * 
 * @author davidm
 * @since Jan 11, 2015
 * @version 0.1.0
 */
public class YesNoWindow extends Window {

	private static final long serialVersionUID = 1L;

	public YesNoWindow(String title, String message, ClickListener yesListener, ClickListener noListener) {
		super(" " + title);
		setIcon(FontAwesome.INFO);
		setModal(true);
		setResizable(false);
		VerticalLayout verticalLayout = new VerticalLayout();
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		Label label = new Label(message);
		Button noButton = new Button("No");
		if (noListener != null) {
			noButton.addClickListener(noListener);
		}
		noButton.addClickListener(new CloseWindowOnClickListener());
		Button yesButton = new Button("Yes");
		yesButton.addClickListener(yesListener);
		if (yesListener != null) {
			yesButton.addClickListener(new CloseWindowOnClickListener());
		}
		horizontalLayout.addComponents(yesButton, noButton);
		horizontalLayout.setSpacing(true);
		verticalLayout.addComponents(label, horizontalLayout);
		verticalLayout.setSpacing(true);
		verticalLayout.setMargin(true);
		setContent(verticalLayout);
	}

	private class CloseWindowOnClickListener implements ClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			YesNoWindow.this.close();
		}

	}

}
