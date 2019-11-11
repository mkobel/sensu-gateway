package ch.kobelnet.system.sensu_go.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Metadata {
    String name;
    String namespace = "default";
    Map<String, String> labels;
}
