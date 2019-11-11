package ch.kobelnet.system;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Getter
@Setter
public class Config {

    private final static String CONFIG_FILE = "app.properties";

    boolean goToCoreEnabled = false;
    int goReceiverPort = 4446;
    String coreTargetHost = "localhost";
    int coreTargetPort = 3030;

    boolean coreToGoEnabled = false;
    int coreReceiverPort = 4445;
    String goApiBase;
    String goApiUsername;
    String goApiPassword;

    public Config() {

        Properties appProps = new Properties();
        try {
            appProps.load(Config.class.getResourceAsStream("/"+ CONFIG_FILE));
        } catch (IOException e) {
            if (log.isInfoEnabled()) {
                log.info("failed to load config from (compiled) resource {}: {}", CONFIG_FILE, e.getMessage());
            }
        }

        try {
            appProps.load(new FileInputStream(CONFIG_FILE));
        } catch (IOException e) {
            if (log.isInfoEnabled()) {
                log.info("failed to load config from {}: {}", CONFIG_FILE, e.getMessage());
            }
        }

        if (appProps.containsKey("core.to-go-enabled")) {
            this.coreToGoEnabled = Boolean.parseBoolean(appProps.getProperty("core.to-go-enabled"));
        }
        if (appProps.containsKey("go.to-core-enabled")) {
            this.goToCoreEnabled = Boolean.parseBoolean(appProps.getProperty("go.to-core-enabled"));
        }
        if (appProps.containsKey("core.receiver.port")) {
            this.coreReceiverPort = Integer.parseInt(appProps.getProperty("core.receiver.port"));
        }
        if (appProps.containsKey("core.target.host")) {
            this.coreTargetHost = appProps.getProperty("core.target.host");
        }
        if (appProps.containsKey("core.target.port")) {
            this.coreTargetPort = Integer.parseInt(appProps.getProperty("core.target.port"));
        }
        if (appProps.containsKey("go.receiver.port")) {
            this.goReceiverPort = Integer.parseInt(appProps.getProperty("go.receiver.port"));
        }
        if (appProps.containsKey("go.api.base")) {
            this.goApiBase = appProps.getProperty("go.api.base");
        }
        if (appProps.containsKey("go.api.username")) {
            this.goApiUsername = appProps.getProperty("go.api.username");
        }
        if (appProps.containsKey("go.api.password")) {
            this.goApiPassword = appProps.getProperty("go.api.password");
        }

    }
}
