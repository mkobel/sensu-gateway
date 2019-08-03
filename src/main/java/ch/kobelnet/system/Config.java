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

    int receiverPort = 4445;
    String targetHost = "localhost";
    int targetPort = 3030;


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

        if (appProps.containsKey("receiver.port")) {
            this.receiverPort = Integer.parseInt(appProps.getProperty("receiver.port"));
        }
        if (appProps.containsKey("target.host")) {
            this.targetHost = appProps.getProperty("target.host");
        }
        if (appProps.containsKey("target.port")) {
            this.targetPort = Integer.parseInt(appProps.getProperty("target.port"));
        }

    }
}
