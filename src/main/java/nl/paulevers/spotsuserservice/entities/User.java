package nl.paulevers.spotsuserservice.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "users")
public class User {
    @Id
    String id;
    String name;
    String email;
    long userCreated = new Date().getTime(); // Unix
    List<String> favoriteSpotsIds = new ArrayList<>();
}
