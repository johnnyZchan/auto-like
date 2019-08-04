package com.johnny.tools.autolike.listeners;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;

public class LogbackContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String profile = System.getProperty("spring.profiles.active", "development");
        String fileName = "logback-" + profile + ".xml";

        InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            configurator.doConfigure(configStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (configStream != null) {
                    configStream.close();
                }
            } catch (Exception e2) {}
        }
    }
}
