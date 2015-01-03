package org.shepherd.vaadin.dashboard.view.monitored;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import org.apache.commons.beanutils.ConvertUtils;
import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.annotation.MonitoredDisplayName;
import org.shepherd.monitored.annotation.ParamDisplayName;
import org.shepherd.monitored.annotation.UICreationPoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

	private Constructor uiCreationPointConstructor;

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
				this.uiCreationPointConstructor = constructor;
				break;
			}
		}
		return components;
	}

	public Layout getLayout() {
		return this.layout;
	}

	public Monitored createNewMonitoredInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] args = new Object[this.uiCreationPointConstructor.getParameterCount()];
		int i = 0;
		for (Component component : layout) {
			AbstractTextField textField = (AbstractTextField)component;
			Class argType = this.uiCreationPointConstructor.getParameterTypes()[i];
			if (argType != String.class) {
				Object converted = ConvertUtils.convert(textField.getValue(), argType);
				if (converted == String.class) {
					throw new MonitoredArgumentClassNotSupportedException(argType);
				} else {
					args[i] = converted;
				}
			} else {
				args[i] = textField.getValue();
			}
			i++;
		}
		return (Monitored)uiCreationPointConstructor.newInstance(args);
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
