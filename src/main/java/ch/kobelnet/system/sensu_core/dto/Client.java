package ch.kobelnet.system.sensu_core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Client {

    long timestamp;
    String version;
    Socket socket;
    List<String> subscriptions;
    List<String> tags;
    String environment;
    String address;
    String name;

}
