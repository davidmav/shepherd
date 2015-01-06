package org.shepherd.vaadin.dashboard.component;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Iterator;

/*
 * This component extends a CustomField and implements all the necessary
 * functionality so that it can be used just like any other Field.
 */
@SuppressWarnings({ "serial", "unchecked" })
public final class OptionalSelect<T> extends CustomField<T> {

	private final CheckBox checkBox;
	private final ComboBox comboBox;
	private final HorizontalLayout content;

	@Override
	protected Component initContent() {
		return this.content;
	}

	public OptionalSelect() {
		this.content = new HorizontalLayout();
		this.content.setSpacing(true);
		this.content.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		this.content.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		this.comboBox = new ComboBox();
		this.comboBox.setTextInputAllowed(false);
		this.comboBox.setNullSelectionAllowed(false);
		this.comboBox.addStyleName(ValoTheme.COMBOBOX_SMALL);
		this.comboBox.setWidth(10.0f, Unit.EM);
		this.comboBox.setEnabled(false);
		this.comboBox.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
				setValue((T)event.getProperty().getValue());
			}
		});
		this.content.addComponent(this.comboBox);

		this.checkBox = new CheckBox("Subscribe to newsletter", false);
		this.checkBox.setPropertyDataSource(new MethodProperty<Boolean>(this.comboBox, "enabled"));
		this.checkBox.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(final com.vaadin.data.Property.ValueChangeEvent event) {
				if ((Boolean)event.getProperty().getValue()) {
					if (OptionalSelect.this.comboBox.getValue() == null) {
						Iterator<?> iterator = OptionalSelect.this.comboBox.getItemIds().iterator();
						if (iterator.hasNext()) {
							OptionalSelect.this.comboBox.setValue(iterator.next());
						}
					}
				} else {
					setValue(null);
				}
			}
		});

		this.content.addComponent(this.checkBox, 0);
	}

	@Override
	protected void setInternalValue(final T newValue) {
		super.setInternalValue(newValue);
		this.comboBox.setValue(newValue);
		this.checkBox.setValue(newValue != null);
	}

	public void addOption(final T itemId, final String caption) {
		this.comboBox.addItem(itemId);
		this.comboBox.setItemCaption(itemId, caption);
	}

	@Override
	public Class<? extends T> getType() {
		return (Class<? extends T>)Object.class;
	}

}
