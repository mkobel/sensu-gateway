package ch.kobelnet.system.sensu_go;

import ch.kobelnet.system.sensu_go.dto.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.function.Function;

@Slf4j
public class SensuGoReceiver extends Thread {

    private final int MAX_ERROR_COUNT = 100;
    private ServerSocket socket;
    private ObjectMapper mapper;
    private Function<Event, Boolean> sendFunction;

    public SensuGoReceiver(int receiverPort, Function<Event, Boolean> sendFunction) throws IOException {
        socket = new ServerSocket(receiverPort);
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.sendFunction = sendFunction;
        if (log.isInfoEnabled()) {
            log.info("listening on port {} for incoming messages", receiverPort);
        }
    }

    public void run() {
        int errorCount = 0;

        if (log.isDebugEnabled()) {
            log.debug("receiver is now running");
        }

        while (errorCount < MAX_ERROR_COUNT) {
            try {
                new SensuGoClientHandler(socket.accept()).start();
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("failed create client handler: {}", e.getMessage(), e);
                }
                errorCount++;
            }
        }
        if (log.isInfoEnabled()) {
            log.info("close receiver with errorCount={}", errorCount);
        }
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            log.error("failed to close connection: {}", e.getMessage());
        }
    }

    private class SensuGoClientHandler extends Thread {
        private Socket clientSocket;


        public SensuGoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {

            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("received an event: {}", inputLine);
                    }
                    Event event = mapper.readValue(inputLine, Event.class);
                    boolean result = sendFunction.apply(event);
                    if (log.isDebugEnabled()) {
                        log.debug("event forwarded with result: {}", result);
                    }
                    out.println("ACK");
                }
            } catch (IOException e) {
                log.error("communication error: {}", e.getMessage());
            }

            try {
                if (clientSocket.isConnected()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                log.error("failed to close connection: {}", e.getMessage());
            }
        }
    }
}