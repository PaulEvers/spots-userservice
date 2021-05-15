package nl.paulevers.spotsuserservice.events;

import lombok.Data;

import java.io.Serializable;

@Data
public class MqEvent implements Serializable {
    EventType eventType;
    Object data;
}
