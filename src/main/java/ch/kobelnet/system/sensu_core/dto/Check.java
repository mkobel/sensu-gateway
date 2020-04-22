package ch.kobelnet.system.sensu_core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Check {

    String name;
    String output;
    int status;

    String type = "standard";
    String command;
    boolean standalone = true;
    List<String> subscribers;
    boolean publish = false;
    int interval;
    int timeout;
    //    int ttl;
    int ttl_status;
    boolean auto_resolve;
    boolean force_resolve;
    boolean handle;
    String handler;
    List<String> handlers;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    int low_flap_threshold;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    int high_flap_threshold;

    String source;

    List<String> tags;

    long issued;
    long executed;
    float duration;


}
