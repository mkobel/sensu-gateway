package ch.kobelnet.system.sensu_go.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensuSystem {
    String hostname;
    String os;
    String platform;
    String platform_family;
    String platform_version;
    Network network;
    String arch;
}
