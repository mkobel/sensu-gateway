package ch.kobelnet.system.sensu_go.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryItem {

    int status;
    long executed;

}
