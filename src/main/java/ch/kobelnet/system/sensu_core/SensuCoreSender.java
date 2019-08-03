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
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
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
            connect();
            response = sendMessage(output);
            disconnect();
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

    private void connect() throws IOException {
        clientSocket = new Socket(host, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    private String sendMessage(String msg) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("send check result: {}", msg);
        }
        out.println(msg);
        String response = in.readLine();
        if (log.isDebugEnabled()) {
            log.debug("received response: {}", response);
        }
        return response;
    }

    private void disconnect() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
