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
    Metadata metadata;
    String command;
    long issued;
    long executed;
    float duration;
}
