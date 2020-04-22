package ch.kobelnet.system.sensu_go.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Check {
    String command;
    List<String> handlers;
    Integer high_flap_threshold;
    int interval;
    Integer low_flap_threshold;
    boolean publish;
    // runtime_assets
    List<String> subscriptions;
    String proxy_entity_name;
    // check_hooks
    boolean stdin;
    // subdue
    int ttl;
    int timeout;
    boolean round_robin;
    float duration;
    long executed;
    List<HistoryItem> history;
    long issued;
    String output;
    String state;
    int status;
    long total_state_change;
    long last_ok;
    long occurrences;
    long occurrences_watermark;
    String output_metric_format;
    // output_metric_handlers;
    // env_vars;
    Metadata metadata;
}
