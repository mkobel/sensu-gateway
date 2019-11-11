package ch.kobelnet.system.sensu_core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {

    String id;
    String action;
    long timestamp;
    int occurrences;

    Check check;

    Client client;

}
