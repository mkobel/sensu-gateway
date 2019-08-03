package ch.kobelnet.system.sensu_go.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Interface {
    String name;
    String mac;
    List<String> addresses;
}
