package net.weichware.jbdaotest.uiserver;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {
    public MainView() {
        TextField textField = new TextField("Your name");
        Button button = new Button("Say hello",
                e -> Notification.show("Hello " + textField.getValue()));
        button.addClickShortcut(Key.ENTER);
        add(textField, button);
    }
}