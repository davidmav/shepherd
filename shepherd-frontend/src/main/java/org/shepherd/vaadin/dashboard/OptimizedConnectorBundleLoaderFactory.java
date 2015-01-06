package org.shepherd.vaadin.dashboard;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.vaadin.client.ui.button.ButtonConnector;
import com.vaadin.client.ui.csslayout.CssLayoutConnector;
import com.vaadin.client.ui.label.LabelConnector;
import com.vaadin.client.ui.orderedlayout.HorizontalLayoutConnector;
import com.vaadin.client.ui.orderedlayout.VerticalLayoutConnector;
import com.vaadin.client.ui.panel.PanelConnector;
import com.vaadin.client.ui.passwordfield.PasswordFieldConnector;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.client.ui.window.WindowConnector;
import com.vaadin.server.widgetsetutils.ConnectorBundleLoaderFactory;
import com.vaadin.shared.ui.Connect.LoadStyle;

import java.util.HashSet;
import java.util.Set;

public final class OptimizedConnectorBundleLoaderFactory extends ConnectorBundleLoaderFactory {

	private final Set<String> eagerConnectors = new HashSet<String>();
	{
		this.eagerConnectors.add(PasswordFieldConnector.class.getName());
		this.eagerConnectors.add(VerticalLayoutConnector.class.getName());
		this.eagerConnectors.add(HorizontalLayoutConnector.class.getName());
		this.eagerConnectors.add(ButtonConnector.class.getName());
		this.eagerConnectors.add(UIConnector.class.getName());
		this.eagerConnectors.add(CssLayoutConnector.class.getName());
		this.eagerConnectors.add(TextFieldConnector.class.getName());
		this.eagerConnectors.add(PanelConnector.class.getName());
		this.eagerConnectors.add(LabelConnector.class.getName());
		this.eagerConnectors.add(WindowConnector.class.getName());
	}

	@Override
	protected LoadStyle getLoadStyle(final JClassType connectorType) {
		if (this.eagerConnectors.contains(connectorType.getQualifiedBinaryName())) {
			return LoadStyle.EAGER;
		} else {
			// Loads all other connectors immediately after the initial view has
			// been rendered
			return LoadStyle.DEFERRED;
		}
	}
}