package net.weichware.jbdao.ui.dialog;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;

public class ConfirmationDialog extends BaseDialog {

    public ConfirmationDialog(String title, String text, ComponentEventListener<ClickEvent<Button>> okEvent, ComponentEventListener<ClickEvent<Button>> cancelEvent) {
        super(title, cancelEvent);
        addContent(createDivFromText(text));
        addCancelButton("Cancel");
        addFooterButton("OK", true, okEvent);
    }

    public ConfirmationDialog(String title, String text, ComponentEventListener<ClickEvent<Button>> okEvent) {
        this(title, text, okEvent, null);
    }

}
