package ch.kobelnet.system.sensu_go;

import ch.kobelnet.system.sensu_go.dto.AuthResponse;
import ch.kobelnet.system.sensu_go.dto.Event;
import ch.kobelnet.system.sensu_go.dto.EventEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
public class SensuGoSender {
    private ObjectMapper mapper;

    private String apiBase;
    private String username;
    private String password;

    private List<String> createdEntities = new ArrayList<>();

    public SensuGoSender(String apiBase, String username, String password) {
        mapper = new ObjectMapper();
        this.apiBase = apiBase;
        this.username = username;
        this.password = password;
    }

    public boolean sendCheckResult(Event event) {

        try {

            String output = mapper.writeValueAsString(event);
            log.debug(output);

            AuthResponse authResponse = authenticate();

            ensureEntity(authResponse.getAccessToken(), event.getEntity());

            sendEvent(authResponse.getAccessToken(), output);


        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("failed to send check result: {}", e.getMessage());
            }
        }

        return true;
    }

    private void ensureEntity(String accessToken, EventEntity entity) {

        for (String entityName : createdEntities) {
            if (entityName.equals(entity.getMetadata().getName())) {
                return;
            }
        }

        try {

            String data = mapper.writeValueAsString(entity);

            URL url = new URL(apiBase + "/api/core/v2/namespaces/default/entities/" + entity.getMetadata().getName());
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            StringBuilder builder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                builder.append(output).append('\n');
            }

            log.debug("response: {}", builder.toString());

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            conn.disconnect();

            createdEntities.add(entity.getMetadata().getName());
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("failed to send entity: {}", e.getMessage());
            }
        }

    }

    private void sendEvent(String accessToken, String data) {

        try {

            URL url = new URL(apiBase + "/api/core/v2/namespaces/default/events");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            StringBuilder builder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                builder.append(output).append('\n');
            }

            log.debug("response: {}", builder.toString());

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            conn.disconnect();

        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("failed to send check event: {}", e.getMessage());
            }
        }

    }

    private AuthResponse authenticate() {

        AuthResponse authResponse = null;

        try {

            URL url = new URL(apiBase + "/auth");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));  //Java 8
            conn.setRequestProperty("Authorization", "Basic " + encoded);
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            StringBuilder builder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                builder.append(output).append('\n');
            }

            conn.disconnect();

            authResponse = mapper.readValue(builder.toString(), AuthResponse.class);


        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("failed to authenticate: {}", e.getMessage());
            }
        }
        return authResponse;
    }
}

