package net.weichware.jbdao.ui.dialog;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class EditDialog<T> extends Dialog {
    private final Map<String, Component> fieldMap = new HashMap<>();
    private final FormLayout formLayout;
    protected final EditDialogMode mode;

    public EditDialog(EditDialogMode mode, String name, T item, Runnable update) {
        super(mode.text + " " + name);
        this.mode = mode;

        setDraggable(true);
        setCloseOnOutsideClick(false);

        Button closeButton = new Button(new Icon("lumo", "cross"),(e) -> close());
        getHeader().add(closeButton);

        formLayout = new FormLayout();
        formLayout.setAutoResponsive(true);
        formLayout.setLabelsAside(true);
        formLayout.setMaxWidth("800px");
        add(formLayout);
        createFields(item);

        Button cancel = new Button("Cancel", (e)->{
            close();
        });
        cancel.setDisableOnClick(true);
        getFooter().add(cancel);

        Button ok = new Button("OK");
        ok.addClickListener((e)->{
            if (save()) {
                close();
                update.run();
            } else {
                ok.setEnabled(true);
            }
        });
        ok.setDisableOnClick(true);
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ok.addClickShortcut(Key.ENTER);
        getFooter().add(ok);
    }

    protected abstract void createFields(T item);

    protected abstract boolean save();

    private void addLabelField(String name, String value) {
        NativeLabel valueLabel = new NativeLabel(value);
        valueLabel.setId(name);
        formLayout.addFormItem(valueLabel, name);
    }

    protected void addDatePicker(String name, String label, LocalDate value, boolean enabled, boolean isMandatory) {
        DatePicker dateTimePicker = new DatePicker(value);
        dateTimePicker.setLocale(Locale.UK);
        dateTimePicker.setEnabled(enabled);
        if (value != null) {
            dateTimePicker.setId(name);
        }
        addField(name, label, isMandatory, dateTimePicker);
    }

    protected void addTextField(String name, String label, String value, boolean enabled, boolean isMandatory) {
        TextField textField = new TextField();
        textField.setWidthFull();
        textField.setEnabled(enabled);
        if (value != null && !value.trim().isEmpty()) {
            textField.setValue(value);
        }
        addField(name, label, isMandatory, textField);
    }

    protected void addNumberField(String name, String label, Double value, boolean enabled, boolean isMandatory) {
        NumberField numberField = new NumberField();
        numberField.setWidthFull();
        numberField.setEnabled(enabled);
        if (value != null) {
            numberField.setValue(value);
        }
        addField(name, label, isMandatory, numberField);
    }

    protected void addCheckBox(String name, String label, Boolean value, boolean enabled, boolean isMandatory) {
        Checkbox checkbox = new Checkbox();
        checkbox.setWidthFull();
        checkbox.setEnabled(enabled);
        if (value != null && value) {
            checkbox.setValue(true);
        }
        addField(name, label, isMandatory, checkbox);
    }

    protected void addComboBox(String name, String label, String value, boolean enabled, boolean isMandatory, List<String> items) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(items);
        comboBox.setWidthFull();
        comboBox.setEnabled(enabled);
        if (value != null && !value.isEmpty()) {
            comboBox.setValue(value);
        }
        addField(name, label, isMandatory, comboBox);
    }

    protected void addPasswordField(String name, String label, String value, boolean enabled, boolean isMandatory) {
        PasswordField passwordField = new PasswordField();
        passwordField.setWidthFull();
        passwordField.setEnabled(enabled);
        if (value != null) {
            passwordField.setValue(value);
        }
        addField(name, label, isMandatory, passwordField);
    }

    protected void addTextAreaField(String name, String label, String value, boolean enabled, boolean isMandatory) {
        TextArea textArea = new TextArea();
        textArea.setId(name);
        textArea.setWidthFull();
        textArea.setEnabled(enabled);
        if (value != null) {
            textArea.setValue(value);
        }
        addField(name, label, isMandatory, textArea);
    }

    private void addField(String name, String label, boolean isMandatory, Component component) {
        component.setId(name);
        formLayout.addFormItem(component, label + (isMandatory ? "*" : ""));
        fieldMap.put(name, component);
    }

    private Map<String, String> getFieldMap() {
        Map<String, String> map = new HashMap<>();
        for (String field : fieldMap.keySet()) {
            map.put(field, getValue(field));
        }
        return map;
    }

    public String getValue(String name) {
        Component component = fieldMap.get(name);
        if (component == null) {
            return null;
        }
        if (component instanceof TextField) {
            return ((TextField) component).getValue();
        } else if (component instanceof TextArea) {
            return ((TextArea) component).getValue();
        } else if (component instanceof PasswordField) {
            return ((PasswordField) component).getValue();
        } else if (component instanceof NumberField) {
            return ((NumberField) component).getValue() + "";
        } else if (component instanceof Checkbox) {
            return ((Checkbox) component).getValue() + "";
        } else if (component instanceof ComboBox) {
            return ((ComboBox<String>) component).getValue();
        } else if (component instanceof DatePicker) {
            return String.valueOf(((DatePicker) component).getValue());
        }
        return null;
    }

    protected Double getValueDouble(String name) {
        Component component = fieldMap.get(name);
        if (component == null) {
            return null;
        }
        if (component instanceof NumberField) {
            return ((NumberField) component).getValue();
        }
        throw new RuntimeException("Trying to get number from non NumberField"); //todo: implement custom Exception
    }

    protected Long getValueLong(String name) {
        Double value = getValueDouble(name);
        if (value!=null) {
            return value.longValue();
        }
        return null;
    }

    protected Integer getValueInteger(String name) {
        Double value = getValueDouble(name);
        if (value!=null) {
            return value.intValue();
        }
        return null;
    }

}
