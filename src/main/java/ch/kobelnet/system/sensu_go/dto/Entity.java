package ch.kobelnet.system.sensu_go.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Entity {

    String entity_class;
    SensuSystem system;
    List<String> subscriptions;
    long last_seen;
    boolean deregister;
    // deregistration
    String user;
    List<String> redact;
    Metadata metadata;
}
