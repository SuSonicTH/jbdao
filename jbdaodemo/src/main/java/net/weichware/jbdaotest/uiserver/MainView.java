package net.weichware.jbdaotest.uiserver;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import net.weichware.jbdaotest.CustomerPage;

@Route("")
public class MainView extends VerticalLayout {
    public MainView() {
        TextField textField = new TextField("Your name");
        Button button = new Button("Say hello",
                e -> Notification.show("Hello " + textField.getValue()));
        button.addClickShortcut(Key.ENTER);
        Button customer = new Button("Customer",
                e -> openPage(e, CustomerPage.Name));
        add(textField, button, customer);
    }

    protected void openPage(ClickEvent<?> clickEvent, String route) {
        if (clickEvent.isCtrlKey()) {
            UI.getCurrent().getPage().open(route);
        } else {
            UI.getCurrent().getPage().setLocation(route);
        }
    }
}