package nl.paulevers.spotsuserservice.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import nl.paulevers.spotsuserservice.entities.User;
import nl.paulevers.spotsuserservice.classes.UserCreateRequest;
import nl.paulevers.spotsuserservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserRepository repository;


    @PostMapping(value="/user")
    public @ResponseBody
    ResponseEntity<String> createUser(@RequestBody UserCreateRequest request) {
        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword());
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
            String userId = userRecord.getUid();
            User user = new User();
            user.setId(userId);
            user.setEmail(request.getEmail());

            repository.save(user);
            return new ResponseEntity<>(userRecord.getUid(), HttpStatus.OK);
        } catch (FirebaseAuthException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }


    }
}
