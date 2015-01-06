package org.shepherd.vaadin.dashboard.view.monitored;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.shepherd.monitored.Monitored;
import org.shepherd.monitored.annotation.MonitoredDisplayName;
import org.shepherd.monitored.annotation.ParamDisplayName;
import org.shepherd.monitored.annotation.UICreationPoint;
import org.shepherd.monitored.utils.EnumConverter;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

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

				int enumIndex = 0;
				
				for (ParamDisplayName paramDisplayName : uiCreationPoint.params()) {

					Component component = null;
					String displayName = paramDisplayName.displayName();
					
					Parameter parameter = constructor.getParameters()[enumIndex];

					if(parameter.getType().isEnum()){
						component = getComboForEnum(component, displayName, parameter);
					} else	if (paramDisplayName.passwordField()) {
						component = new PasswordField(displayName);
					} else {
						component = new TextField(displayName);
					}
					components.add(component);
				}
				this.uiCreationPointConstructor = constructor;
				break;
			}
		}
		return components;
	}

	private Component getComboForEnum(Component component, String caption, Parameter parameter) {


		if(parameter.getType().isEnum()){
			Object[] enumConstants = parameter.getType().getEnumConstants();
			component = new ComboBox(caption, Arrays.asList(enumConstants));
		}
		return component;
	}


	public Layout getLayout() {
		return this.layout;
	}

	public Monitored createNewMonitoredInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] arguments = getArguments();
		return (Monitored)uiCreationPointConstructor.newInstance(arguments);
	}

	@SuppressWarnings("rawtypes")
	public Object[] getArguments() {
		validateInput();
		Object[] args = new Object[this.uiCreationPointConstructor.getParameterCount()];
		int i = 0;
		Iterator<Component> iterator = layout.iterator();
		while (iterator.hasNext()) {
			
			Component component = iterator.next();
			
			AbstractField field = (AbstractField)component;
			Class argType = this.uiCreationPointConstructor.getParameterTypes()[i];
			
			if (argType != String.class) {
				Object converted = ConvertUtils.convert(field.getValue(), argType);
				if (converted == String.class) {
					throw new MonitoredArgumentClassNotSupportedException(argType);
				} else {
					args[i] = converted;
				}
			} else {
				args[i] = field.getValue();
			}
			i++;
		}
		return args;
	}

	protected void validateInput() throws IllegalArgumentException {
		// TODO create input validation here
	}

	public Constructor<? extends Monitored> getConstructor() {
		return this.uiCreationPointConstructor;
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
