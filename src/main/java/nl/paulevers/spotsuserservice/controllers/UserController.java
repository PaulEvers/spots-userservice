package nl.paulevers.spotsuserservice.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import nl.paulevers.spotsuserservice.entities.User;
import nl.paulevers.spotsuserservice.classes.UserCreateRequest;
import nl.paulevers.spotsuserservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class UserController {
    @Autowired
    private UserRepository repository;

    @GetMapping(value="/user")
    public @ResponseBody
    ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
        try {
            FirebaseToken decodedToken = null;
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();

            if(uid != "" && uid != null) {
                User user = repository.findById(uid).get();
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

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
            user.setName(request.getName());

            repository.save(user);
            return new ResponseEntity<>(userRecord.getUid(), HttpStatus.OK);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping(value="/user/{id}")
    public @ResponseBody
    ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @PathVariable String id) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            Map<String, Object> claims = decodedToken.getClaims();
            if (Boolean.FALSE.equals(claims.get("admin")) || !claims.containsKey("admin")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            repository.deleteById(id);
            FirebaseAuth.getInstance().deleteUser(id);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
