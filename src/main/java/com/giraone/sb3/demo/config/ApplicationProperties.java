package com.giraone.sb3.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties specific to application.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true) // TODO: false not working
public class ApplicationProperties {

    /** Log the configuration to the log on startup */
    private boolean showConfigOnStartup;
    /** URL (host/port) for second service */
    private RemoteService service2 = new RemoteService();

    public boolean isShowConfigOnStartup() {
        return showConfigOnStartup;
    }

    public void setShowConfigOnStartup(boolean showConfigOnStartup) {
        this.showConfigOnStartup = showConfigOnStartup;
    }

    public RemoteService getService2() {
        return service2;
    }

    public void setService2(RemoteService service2) {
        this.service2 = service2;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
            "showConfigOnStartup=" + showConfigOnStartup +
            ", service2=" + service2 +
            '}';
    }
}


