package net.weichware.jbdaotest.uiserver;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("")
@PageTitle(MainView.PAGE_TITLE)
@Theme(value = Lumo.class, variant = Lumo.DARK)
@PWA(
        name = "JBDAO Demo",
        shortName = "JBDAO",
        manifestPath = "manifest.json",
        description = "JBDAO Demo application"
)
public class MainView extends VerticalLayout {
    public static final String PAGE_TITLE = "Main";

    public MainView() {
        add(new Label(PAGE_TITLE));
    }

}
