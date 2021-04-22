package nl.paulevers.spotsuserservice.repositories;

import nl.paulevers.spotsuserservice.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
