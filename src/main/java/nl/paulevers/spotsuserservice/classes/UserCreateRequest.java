package nl.paulevers.spotsuserservice.classes;

import lombok.Data;

@Data
public class UserCreateRequest {
    String password;
    String email;
    String name;
}
