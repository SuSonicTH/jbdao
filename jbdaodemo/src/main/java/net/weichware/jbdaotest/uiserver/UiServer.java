package net.weichware.jbdaotest.uiserver;


import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.startup.ServletContextListeners;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.net.MalformedURLException;
import java.net.URL;

public class UiServer {
    private static final Logger log = LoggerFactory.getLogger(UiServer.class);
    public static final int PORT = 8080;
    private final Server server;

    public UiServer() {
        server = new Server();
        setPropertyIfProductionMode();
    }

    public void start() throws Exception {
        log.info("Creating ui server");
        configureHandler();
        configureConnectors();
        log.info("Starting ui server");
        server.start();
    }

    private void configureHandler() throws ServletException {
        final WebAppContext context = new WebAppContext();
        context.setBaseResource(findWebRoot());
        context.setContextPath("/");
        context.addServlet(VaadinServlet.class, "/*");
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*\\.jar|.*/classes/.*");
        context.setConfigurations(new Configuration[]{
                new AnnotationConfiguration(),
                new WebInfConfiguration(),
                new WebXmlConfiguration(),
                new MetaInfConfiguration(),
                new FragmentConfiguration(),
                new EnvConfiguration(),
                new PlusConfiguration(),
                new JettyWebXmlConfiguration()
        });
        context.setConfigurationDiscovered(true);
        context.getServletContext().setExtendedListenerTypes(true);
        context.addEventListener(new ServletContextListeners());
        context.setErrorHandler(new ErrorHandler());
        server.setHandler(context);
        WebSocketServerContainerInitializer.configureContext(context);
    }

    private static Resource findWebRoot() {
        // don't look up directory as a resource, it's unreliable: https://github.com/eclipse/jetty.project/issues/4173#issuecomment-539769734
        // instead we'll look up the /webapp/ROOT and retrieve the parent folder from that.
        final URL f = UiServer.class.getResource("/webapp/ROOT");
        if (f == null) {
            throw new IllegalStateException("Invalid state: the resource /webapp/ROOT doesn't exist, has webapp been packaged in as a resource?");
        }
        final String url = f.toString();
        if (!url.endsWith("/ROOT")) {
            throw new RuntimeException("Parameter url: invalid value " + url + ": doesn't end with /ROOT");
        }

        URL webRoot;
        try {
            webRoot = new URL(url.substring(0, url.length() - 5));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return Resource.newResource(webRoot);
    }

    private void configureConnectors() {
        HttpConfiguration https = new HttpConfiguration();
        //https.addCustomizer(new SecureRequestCustomizer());
        //SslContextFactory sslContextFactory = new SslContextFactory();
        //sslContextFactory.setKeyStorePath("keystore.jks");
        //sslContextFactory.setKeyStorePassword(uiServerConfig.getKeyStorePassword());
        //sslContextFactory.setKeyManagerPassword(uiServerConfig.getKeyStorePassword());

        ServerConnector sslConnector = new ServerConnector(server,
                //new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https));
        sslConnector.setPort(PORT);

        server.addConnector(sslConnector);
    }

    private void setPropertyIfProductionMode() {
        final String probe = "META-INF/maven/com.vaadin/flow-server-production-mode/pom.xml";
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader.getResource(probe) != null) {
            System.setProperty("vaadin.productionMode", "true");
        }
    }

    public void stop() throws Exception {
        log.info("Stopping ui server");
        server.stop();
    }

}
