package ch.kobelnet.system.sensu_core;

import ch.kobelnet.system.sensu_core.dto.Check;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class SensuCoreSender {

    private ObjectMapper mapper;
    private String host;
    private int port;

    public SensuCoreSender(String host, int port) {
        mapper = new ObjectMapper();
        this.host = host;
        this.port = port;
    }

    public boolean sendCheckResult(Check check) {
        String response = null;
        try {
            String output = mapper.writeValueAsString(check);
            try (Socket clientSocket = new Socket(host, port);
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                response = sendMessage(output, writer, reader);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("failed to send check result: {}", e.getMessage());
            }
        }
        if (response != null && response.trim().equalsIgnoreCase("ok")) {
            return true;
        } else {
            if (log.isWarnEnabled()) {
                log.warn("failed to send check result. Response was: {}", response);
            }
        }
        return false;
    }

    private String sendMessage(String msg, PrintWriter writer, BufferedReader reader) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("send check result: {}", msg);
        }
        writer.println(msg);
        String response = reader.readLine();
        if (log.isDebugEnabled()) {
            log.debug("received response: {}", response);
        }
        return response;
    }
}
