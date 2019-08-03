package ch.kobelnet.system;

import ch.kobelnet.system.sensu_core.SensuCoreSender;
import ch.kobelnet.system.sensu_core.dto.Check;
import ch.kobelnet.system.sensu_go.SensuGoReceiver;
import ch.kobelnet.system.sensu_go.dto.Event;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketException;

@Slf4j
public class SensuGateway {

    // create handler in sensu:
    //
    // note: receiving client must allow connection, check bind parameter if sending from external host

    public static void main(String[] args) {
        SensuGateway gateway = new SensuGateway();
        Config config = new Config();
        gateway.server(config.getReceiverPort(), config.getTargetHost(), config.getTargetPort());
    }

    private void server(int receiverPort, String targetHost, int targetPort) {
        SensuGoReceiver receiver;
        SensuCoreSender sender = new SensuCoreSender(targetHost, targetPort);
        try {
            receiver = new SensuGoReceiver(receiverPort, event -> {
                Check res = convertGoEventToCoreCheck(event);
                return sender.sendCheckResult(res);
            });
            receiver.start();
        } catch (SocketException e) {
            if (log.isErrorEnabled()) {
                log.error("server startup failed: {}", e.getMessage());
            }
        }
    }

    private Check convertGoEventToCoreCheck(Event event) {
        Check res = new Check();
        res.setName(event.getCheck().getMetadata().getName());
        res.setCommand(event.getCheck().getCommand());
        res.setOutput(event.getCheck().getOutput());
        res.setStatus(event.getCheck().getStatus());
        res.setSource(event.getEntity().getMetadata().getName());
        res.setSubscribers(event.getCheck().getSubscriptions());
        res.setInterval(event.getCheck().getInterval());
        res.setAuto_resolve(true);
        if (event.getCheck().getStatus() == 0) {
            res.setForce_resolve(true);
        }
        res.setLow_flap_threshold(event.getCheck().getLow_flap_threshold());
        res.setHigh_flap_threshold(event.getCheck().getHigh_flap_threshold());
        return res;
    }

}
