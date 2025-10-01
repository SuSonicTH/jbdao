package net.weichware.jbdao.ui.dialog;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;

public class ErrorDialog extends Dialog {

    public ErrorDialog(String title, String text) {
        super(title);

        setDraggable(true);
        setCloseOnOutsideClick(false);

        Button closeButton = new Button(new Icon("lumo", "cross"),(e) -> close());
        getHeader().add(closeButton);

        add(new Div(new Html("<div>" + text.replace("\n", "<br/>") + "</div>")));

        Button ok = new Button("OK", (e)->close());
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ok.addClickShortcut(Key.ENTER);
        getFooter().add(ok);
    }


}
