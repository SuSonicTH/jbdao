package net.weichware.jbdao.ui.dialog;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.HashMap;
import java.util.Map;

public class BaseDialog extends Dialog {
    private final VerticalLayout contentLayout;
    private final HorizontalLayout footerLayout;
    private final ComponentEventListener<ClickEvent<Button>> closeEvent;
    private final Map<String, Button> footerButton = new HashMap<>();

    public BaseDialog(String title) {
        this(title, null);
    }

    BaseDialog(String title, ComponentEventListener<ClickEvent<Button>> closeEvent) {
        this.closeEvent = closeEvent;
        createHeader(title);
        contentLayout = createContent();
        footerLayout = createFooter();
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
    }

    private void createHeader(String titleText) {
        HorizontalLayout header = new HorizontalLayout();
        H3 title = new H3(titleText);

        header.add(title, createCloseButton());
        header.setFlexGrow(1, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        add(header);
    }

    private VerticalLayout createContent() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        add(content);
        return content;
    }

    private Button createCloseButton() {
        Button close = new Button();
        close.setIcon(VaadinIcon.CLOSE.create());
        close.addClickListener(event -> {
            close();
            if (closeEvent != null) {
                closeEvent.onComponentEvent(event);
            }
        });
        return close;
    }

    private HorizontalLayout createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setPadding(false);
        add(footer);
        return footer;
    }

    protected void addFooterButton(String text, ComponentEventListener<ClickEvent<Button>> clickEvent) {
        addFooterButton(text, false, clickEvent);
    }

    protected void addCancelButton(String text) {
        addFooterButton(text, false, true, closeEvent);
    }

    protected void addFooterButton(String text, boolean isPrimary, ComponentEventListener<ClickEvent<Button>> clickEvent) {
        addFooterButton(text, isPrimary, true, clickEvent);
    }

    protected void addFooterButton(String text, boolean isPrimary, boolean closeOnPress, ComponentEventListener<ClickEvent<Button>> clickEvent) {
        Button button = new Button(text);
        button.setId("button_" + text.replace(' ', '_'));
        button.addClickListener(event -> {
            if (closeOnPress) {
                close();
            }
            if (clickEvent != null) {
                clickEvent.onComponentEvent(event);
            }
        });
        button.setDisableOnClick(true);
        if (isPrimary) {
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            button.addClickShortcut(Key.ENTER);
        }
        footerLayout.add(button);
        footerButton.put(text, button);
    }

    public void enableButton(String text) {
        Button button = footerButton.get(text);
        if (button != null) {
            button.setEnabled(true);
        }
    }

    protected void addContent(Component... components) {
        contentLayout.add(components);
    }

    protected Div createDivFromText(String text) {
        return new Div(new Html("<div>" + text.replace("\n", "<br/>") + "</div>"));
    }
}
