package org.shepherd.vaadin.dashboard.view.monitored;

import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.annotation.MonitoredDisplayName;
import org.shepherd.monitored.annotation.ParamDisplayName;
import org.shepherd.monitored.annotation.UICreationPoint;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author davidm
 * @since Jan 1, 2015
 * @version 0.1.0
 */
public class MonitoredUIItem {

	private Class<Monitored> monitored;

	private Layout layout;

	public MonitoredUIItem(Class<Monitored> monitored) {
		this.monitored = monitored;
		List<Component> initializedComponents = initializeComponents();
		this.layout = new VerticalLayout(initializedComponents.toArray(new Component[initializedComponents.size()]));
		this.layout.setCaption(this.toString() + " Settings");
	}

	protected List<Component> initializeComponents() {
		List<Component> components = new ArrayList<Component>();
		Constructor<?>[] constructors = monitored.getConstructors();
		for (Constructor<?> constructor : constructors) {
			UICreationPoint uiCreationPoint = constructor.getAnnotation(UICreationPoint.class);
			if (uiCreationPoint != null) {
				for (ParamDisplayName paramDisplayName : uiCreationPoint.params()) {
					Component component = null;
					if (paramDisplayName.passwordField()) {
						component = new PasswordField(paramDisplayName.displayName());
					} else {
						component = new TextField(paramDisplayName.displayName());
					}
					components.add(component);
				}
			}
		}
		return components;
	}

	public Layout getLayout() {
		return this.layout;
	}

	@Override
	public String toString() {
		MonitoredDisplayName annotation = monitored.getAnnotation(MonitoredDisplayName.class);
		if (annotation != null) {
			return annotation.value();
		} else {
			return monitored.getSimpleName();
		}
	}
}
