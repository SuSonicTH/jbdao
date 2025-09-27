package net.weichware.jbdaotest.uiserver;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.function.Predicate;

public class GridButton<T> {
    final String text;
    final VaadinIcon icon;
    final GridButtonListener<T> clickListener;
    final DisplayMode displayMode;
    final Predicate<T> isVisible;
    final Predicate<T> isEnabled;

    public GridButton(VaadinIcon icon, String text, GridButtonListener<T> clickListener) {
        this(DisplayMode.ICON, text, icon, clickListener, e -> true, e -> true);
    }

    public GridButton(DisplayMode displayMode, String text, VaadinIcon icon, GridButtonListener<T> clickListener, Predicate<T> isVisible, Predicate<T> isEnabled) {
        this.text = text;
        this.icon = icon;
        this.clickListener = clickListener;
        this.displayMode = displayMode;
        this.isVisible = isVisible;
        this.isEnabled = isEnabled;
    }

    public Button create(T item) {
        Button button;
        switch (displayMode) {
            case TEXT:
                button = new Button(text);
                break;
            case ICON:
                button = new Button(new Icon(icon));
                if (text != null) {
                    button.getElement().setProperty("title", text);
                }
                break;
            case ICON_AND_TEXT:
                button = new Button(text, new Icon(icon));
                break;
            case TEXT_AND_ICON:
                button = new Button(text, new Icon(icon));
                button.setIconAfterText(true);
                break;
            default:
                throw new IllegalStateException("Unknown DisplayMode:" + displayMode.name());
        }
        button.addClickListener(event -> clickListener.onComponentEvent(event, item));
        button.setEnabled(isEnabled.test(item));
        button.setVisible(isVisible.test(item));
        if (text != null) {
            button.setId("button_" + text.toLowerCase().replace(' ', '_'));
        }
        return button;
    }

    public enum DisplayMode {
        TEXT, ICON, ICON_AND_TEXT, TEXT_AND_ICON
    }
}
