package ch.kobelnet.system;

import ch.kobelnet.system.sensu_core.SensuCoreReceiver;
import ch.kobelnet.system.sensu_core.SensuCoreSender;
import ch.kobelnet.system.sensu_core.dto.Check;
import ch.kobelnet.system.sensu_go.SensuGoReceiver;
import ch.kobelnet.system.sensu_go.SensuGoSender;
import ch.kobelnet.system.sensu_go.dto.*;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Gateway {

    // note: receiving client must allow connection, check bind parameter if sending from external host

    public static void main(String[] args) {
        Gateway gateway = new Gateway();
        Config config = new Config();

        if (config.goToCoreEnabled) {
            gateway.goToCore(config.getGoReceiverPort(), config.getCoreTargetHost(), config.getCoreTargetPort());
        }
        if (config.coreToGoEnabled) {
            gateway.coreToGo(config.getCoreReceiverPort(), config.getGoApiBase(), config.getGoApiUsername(), config.getGoApiPassword());
        }
    }

    private void goToCore(int receiverPort, String targetHost, int targetPort) {
        SensuGoReceiver receiver;
        SensuCoreSender sender = new SensuCoreSender(targetHost, targetPort);
        try {
            receiver = new SensuGoReceiver(receiverPort, event -> {
                if (!(event.getEntity().getMetadata().getLabels() != null && event.getEntity().getMetadata().getLabels().containsKey("monitoring") && event.getEntity().getMetadata().getLabels().get("monitoring").equals("sensu-core"))) {
                    Check res = convertGoEventToCoreCheck(event);
                    return sender.sendCheckResult(res);
                } else {
                    return false;
                }
            });
            receiver.start();
        } catch (SocketException e) {
            if (log.isErrorEnabled()) {
                log.error("go-to-core server startup failed: {}", e.getMessage());
            }
        }
    }

    private void coreToGo(int receiverPort, String apiBase, String apiUsername, String apiPassword) {
        SensuCoreReceiver receiver;
        SensuGoSender sender = new SensuGoSender(apiBase, apiUsername, apiPassword);
        try {
            receiver = new SensuCoreReceiver(receiverPort, event -> {
                if (!event.getCheck().getTags().contains("monitoring:sensu-go")) {
                    Event res = convertCoreEventToGoEvent(event);
                    return sender.sendCheckResult(res);
                } else {
                    return false;
                }
            });
            receiver.start();
        } catch (SocketException e) {
            if (log.isErrorEnabled()) {
                log.error("core-to-go server startup failed: {}", e.getMessage());
            }
        }
    }

    private Check convertGoEventToCoreCheck(Event event) {
        Check res = new Check();
        res.setName(event.getCheck().getMetadata().getName());
        res.setHandlers(event.getCheck().getHandlers());
        res.setCommand(event.getCheck().getCommand());
        res.setOutput(event.getCheck().getOutput());
        res.setStatus(event.getCheck().getStatus());
        res.setSource(event.getEntity().getMetadata().getName());
        //res.setSubscribers(event.getCheck().getSubscriptions());
        res.setInterval(event.getCheck().getInterval());
        res.setAuto_resolve(true);
        if (event.getCheck().getStatus() == 0) {
            res.setForce_resolve(true);
        }
        List<String> tags = new ArrayList<>();
        boolean containsMonitoringTag = false;
        if (event.getCheck().getMetadata().getLabels() != null) {
            for (Map.Entry<String, String> label : event.getCheck().getMetadata().getLabels().entrySet()) {
                tags.add(label.getKey() + ":" + label.getValue());
                if (label.getKey().equals("monitoring")) {
                    containsMonitoringTag = true;
                }
            }
        }
        if (!containsMonitoringTag) {
            tags.add("monitoring:sensu-go");
        }
        res.setTags(tags);
        //res.setLow_flap_threshold(event.getCheck().getLow_flap_threshold());
        //res.setHigh_flap_threshold(event.getCheck().getHigh_flap_threshold());
        return res;
    }

    private Event convertCoreEventToGoEvent(ch.kobelnet.system.sensu_core.dto.Event event) {
        Event evt = new Event();
        evt.setTimestamp(event.getTimestamp());
        EventEntity entity = new EventEntity();
        entity.setEntityClass("proxy");
        entity.setSensuAgentVersion(event.getClient().getVersion());
        Metadata metadata = new Metadata();
        metadata.setName(event.getClient().getName());
        metadata.setNamespace("default");
        Map<String, String> labels = new HashMap<>();
        labels.put("environment", event.getClient().getEnvironment());
        labels.put("management", "puppet");
        labels.put("monitoring", "sensu-core");
        labels.put("tags", String.join(",", event.getClient().getTags()));
        metadata.setLabels(labels);
        entity.setMetadata(metadata);
        EventEntitySystem system = new EventEntitySystem();
        system.setHostname(event.getClient().getName());
        entity.setSystem(system);
        evt.setEntity(entity);
        EventCheck check = new EventCheck();
        check.setOutput(event.getCheck().getOutput());
        check.setStatus(event.getCheck().getStatus());
        check.setHandlers(event.getCheck().getHandlers());
        check.setInterval(event.getCheck().getInterval());
        check.setIssued(event.getCheck().getIssued());
        check.setExecuted(event.getCheck().getExecuted());
        check.setDuration(event.getCheck().getDuration());
        metadata = new Metadata();
        metadata.setName(event.getCheck().getName());
        metadata.setNamespace("default");
        labels = new HashMap<>();
        labels.put("tags", String.join(",", event.getCheck().getTags()));
        metadata.setLabels(labels);
        check.setMetadata(metadata);
        check.setCommand(event.getCheck().getCommand());
        evt.setCheck(check);

        return evt;
    }

}
