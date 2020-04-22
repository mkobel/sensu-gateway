package ch.kobelnet.system.sensu_go.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    int high_flap_threshold;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    int low_flap_threshold;

    Metadata metadata;
    String command;
    long issued;
    long executed;
    float duration;
}
