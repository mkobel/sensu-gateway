package ch.kobelnet.system.sensu_go;

import ch.kobelnet.system.sensu_go.dto.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.function.Function;

@Slf4j
public class SensuGoReceiver extends Thread {

    private final int MAX_ERROR_COUNT = 100;
    private DatagramSocket socket;
    private byte[] buf = new byte[4096];
    private ObjectMapper mapper;
    private Function<Event, Boolean> sendFunction;

    public SensuGoReceiver(int receiverPort, Function<Event, Boolean> sendFunction) throws SocketException {
        socket = new DatagramSocket(receiverPort);
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

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                packet = new DatagramPacket(buf, buf.length, address, port);

                String rawEvent = new String(packet.getData(), 0, packet.getLength());

                if (log.isDebugEnabled()) {
                    log.debug("received event: {}", rawEvent);
                }
                Event event = mapper.readValue(rawEvent, Event.class);
                boolean result = sendFunction.apply(event);
                if (log.isDebugEnabled()) {
                    log.debug("event forwarded with result: {}", result);
                }

            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("failed to process event: {}", e.getMessage());
                }
                errorCount++;
            }
        }
        if (log.isInfoEnabled()) {
            log.info("close receiver with errorCount={}", errorCount);
        }
        socket.close();
    }
}