package nl.paulevers.spotsuserservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SpotLikedEvent implements Serializable {
    String spotId;
}
