package ch.kobelnet.system.sensu_go.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EventEntity {

    @JsonProperty("entity_class")
    String entityClass;

    @JsonProperty("sensu_agent_version")
    String sensuAgentVersion;

    Metadata metadata;

    EventEntitySystem system;

    List<String> subscriptions;

}
