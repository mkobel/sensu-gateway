package ch.kobelnet.system.sensu_go.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventCheck {

    String output;
    String state;
    int status;
    List<String> handlers;
    int interval;
    Integer high_flap_threshold;
    Integer low_flap_threshold;
    Metadata metadata;
    String command;
    long issued;
    long executed;
    float duration;
}
