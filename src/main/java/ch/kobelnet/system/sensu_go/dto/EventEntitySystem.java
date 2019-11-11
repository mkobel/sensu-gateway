package ch.kobelnet.system.sensu_go.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventEntitySystem {

    String hostname;
    String os;
    String platform;

    @JsonProperty("platform_family")
    String platformFamily;

    @JsonProperty("platform_version")
    String platformVersion;


}
