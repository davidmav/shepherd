package org.shepherd.vaadin.dashboard.view.monitored;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.TypedStringValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author davidm
 * @since Jan 1, 2015
 * @version 0.1.0
 */
public class MonitoredUIItem {

	private Class<Monitored> monitored;

	private Constructor<? extends Monitored> uiCreationPointConstructor;

	private Layout layout;

	public MonitoredUIItem(Class<Monitored> monitored) {

		this.monitored = monitored;
		List<Component> initializedComponents = initializeComponents(Collections.emptyMap());
		this.layout = new VerticalLayout(initializedComponents.toArray(new Component[initializedComponents.size()]));
		this.layout.setCaption(this.toString() + " Settings");
	}

	@SuppressWarnings("unchecked")
	//Safe casting
	public MonitoredUIItem(BeanDefinition monitoredBeanDefinition) {

		try {
			this.monitored = (Class<Monitored>)Class.forName(monitoredBeanDefinition.getBeanClassName());
		} catch (ClassNotFoundException e) {
			//Should never happen
		}
		ConstructorArgumentValues constructorArgumentValues = monitoredBeanDefinition.getConstructorArgumentValues();
		List<Component> initializedComponents = initializeComponents(constructorArgumentValues.getIndexedArgumentValues());
		this.layout = new VerticalLayout(initializedComponents.toArray(new Component[initializedComponents.size()]));
		this.layout.setCaption(this.toString() + " Settings");
	}

	public TextField getIdField() {
		return (TextField)this.layout.iterator().next();
	}

	@SuppressWarnings("unchecked")
	protected List<Component> initializeComponents(Map<Integer, ValueHolder> valueMap) {
		List<Component> components = new ArrayList<Component>();
		Constructor<?>[] constructors = this.monitored.getConstructors();
		for (Constructor<?> constructor : constructors) {
			UICreationPoint uiCreationPoint = constructor.getAnnotation(UICreationPoint.class);
			if (uiCreationPoint != null) {

				int i = 0;

				for (ParamDisplayName paramDisplayName : uiCreationPoint.params()) {

					@SuppressWarnings("rawtypes")
					AbstractField component = null;
					String displayName = paramDisplayName.displayName();

					Parameter parameter = constructor.getParameters()[i];
					ValueHolder valueHolder = valueMap.get(i);
					Object value = null;
					if (valueHolder != null && valueHolder.getValue() != null) {
						if (valueHolder.getValue() instanceof TypedStringValue) {
							value = ((TypedStringValue)valueHolder.getValue()).getValue();
						} else {
							value = valueHolder.getValue();
						}
					}
					if (parameter.getType().isEnum()) {
						component = getComboForEnum(displayName, parameter);

					} else if (paramDisplayName.passwordField()) {
						component = new PasswordField(displayName);
					} else {
						component = new TextField(displayName);
					}
					if (component != null && value != null) {
						component.setValue(value.toString());
						if (component instanceof ComboBox) {
							@SuppressWarnings("rawtypes")
							Enum<?> enumValue = Enum.valueOf((Class<? extends Enum>)((ComboBox)component).getItemIds().iterator().next().getClass(), value.toString());
							((ComboBox)component).select(enumValue);
						}
					}
					component.setId(parameter.getName());
					components.add(component);
					i++;
				}
				this.uiCreationPointConstructor = (Constructor<? extends Monitored>)constructor;
				break;
			}
		}
		return components;
	}

	private ComboBox getComboForEnum(String caption, Parameter parameter) {
		ComboBox component = null;
		if (parameter.getType().isEnum()) {
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
		return this.uiCreationPointConstructor.newInstance(arguments);
	}

	@SuppressWarnings("rawtypes")
	public Object[] getArguments() {
		validateInput();
		Object[] args = new Object[this.uiCreationPointConstructor.getParameterCount()];
		int i = 0;
		Iterator<Component> iterator = this.layout.iterator();
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
		MonitoredDisplayName annotation = this.monitored.getAnnotation(MonitoredDisplayName.class);
		if (annotation != null) {
			return annotation.value();
		} else {
			return this.monitored.getSimpleName();
		}
	}
}
