package nl.paulevers.spotsuserservice.classes;

import lombok.Data;
import nl.paulevers.spotsuserservice.entities.User;

@Data
public class UserCreateRequest {
    String password;
    String email;
}
