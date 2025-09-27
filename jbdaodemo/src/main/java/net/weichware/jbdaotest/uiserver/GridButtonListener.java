package net.weichware.jbdaotest.uiserver;

import com.vaadin.flow.component.ClickEvent;

public interface GridButtonListener<T> {
    void onComponentEvent(ClickEvent<?> event, T object);
}