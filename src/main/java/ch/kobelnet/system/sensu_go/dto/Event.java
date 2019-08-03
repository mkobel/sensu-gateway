package ch.kobelnet.system.sensu_go.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {

    long timestamp;
    Entity entity;
    Check check;
    Metadata metadata;

}
