package net.weichware.jbdao.ui.dialog;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;

public class ConfirmationDialog extends Dialog {

    public ConfirmationDialog(String title, String text, ComponentEventListener<ClickEvent<Button>> okEvent, ComponentEventListener<ClickEvent<Button>> cancelEvent) {
        super(title);
        setDraggable(true);
        setCloseOnOutsideClick(false);
        Button closeButton = new Button(new Icon("lumo", "cross"),(e) -> close());
        getHeader().add(closeButton);

        add(new Div(new Html("<div>" + text.replace("\n", "<br/>") + "</div>")));

        Button cancel = new Button("Cancel", (e)->{
            if (cancelEvent!=null) {
                cancelEvent.onComponentEvent(e);
            }
            close();
        });
        cancel.setDisableOnClick(true);
        getFooter().add(cancel);

        Button ok = new Button("OK");
        ok.addClickListener((e)->{
            okEvent.onComponentEvent(e);
            close();
        });
        ok.setDisableOnClick(true);
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ok.addClickShortcut(Key.ENTER);
        getFooter().add(ok);
    }

    public ConfirmationDialog(String title, String text, ComponentEventListener<ClickEvent<Button>> okEvent) {
        this(title, text, okEvent, null);
    }

}
