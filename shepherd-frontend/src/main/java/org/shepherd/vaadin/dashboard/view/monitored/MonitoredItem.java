package org.shepherd.vaadin.dashboard.view.monitored;

import com.vaadin.ui.Component;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

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
public class MonitoredItem {

	private Class<Monitored> monitored;

	private List<Component> components;

	public MonitoredItem(Class<Monitored> monitored) {
		this.monitored = monitored;
		this.components = initializeComponents();
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

	public List<Component> getComponents() {
		return components;
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
