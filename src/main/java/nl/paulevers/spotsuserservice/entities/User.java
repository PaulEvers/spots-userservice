package nl.paulevers.spotsuserservice.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Document(collection = "users")
public class User {
    @Id
    String id;
    String name;
    String email;
    long userCreated = new Date().getTime(); // Unix
    Set<String> favoriteSpotsIds = new HashSet<>();

    public boolean likeSpot(String id) {
        return favoriteSpotsIds.add(id);
    }

    public boolean dislikeSpot(String id) { return favoriteSpotsIds.remove(id); }
}
