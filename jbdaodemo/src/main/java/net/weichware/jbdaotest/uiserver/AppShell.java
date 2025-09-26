package net.weichware.jbdaotest.uiserver;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
@PWA(
        name = "JBDAO Demo",
        shortName = "JBDAO",
        manifestPath = "manifest.json",
        description = "JBDAO Demo application"
)
public class AppShell implements AppShellConfigurator {
}