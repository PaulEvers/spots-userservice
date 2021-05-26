package nl.paulevers.spotsuserservice.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import nl.paulevers.spotsuserservice.classes.UserLikeRequest;
import nl.paulevers.spotsuserservice.entities.User;
import nl.paulevers.spotsuserservice.classes.UserCreateRequest;
import nl.paulevers.spotsuserservice.entities.UserCreatedInfo;
import nl.paulevers.spotsuserservice.events.EventType;
import nl.paulevers.spotsuserservice.events.MqEvent;
import nl.paulevers.spotsuserservice.repositories.UserRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class UserController {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private UserRepository repository;

    @GetMapping(value="/user")
    public @ResponseBody
    ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
        try {
            FirebaseToken decodedToken = decodeToken(token);
            String uid = decodedToken.getUid();

            if(!uid.equals("")) {
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

    @PostMapping(value="/user/liked")
    public @ResponseBody
    ResponseEntity<?> likeSpot(@RequestHeader("Authorization") String token, @RequestBody UserLikeRequest request) {
        try {
            FirebaseToken decodedToken = decodeToken(token);
            String uid = decodedToken.getUid();

            if(!uid.equals("")) {
                User user = repository.findById(uid).get();
                String spotId = request.getSpotId();
                // If spot is not yet previously liked
                if(user.likeSpot(spotId)) {
                    repository.save(user);
                    MqEvent event = new MqEvent();
                    event.setEventType(EventType.SpotLikedEvent);
                    event.setData(spotId);
                    rabbitTemplate.convertAndSend("spotsQueue", event);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                return new ResponseEntity<>("Spot is already liked", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value="/user/liked")
    public @ResponseBody
    ResponseEntity<?> unlikeSpot(@RequestHeader("Authorization") String token, @RequestBody UserLikeRequest request) {
        try {
            FirebaseToken decodedToken = decodeToken(token);
            String uid = decodedToken.getUid();

            if(!uid.equals("")) {
                User user = repository.findById(uid).get();
                String spotId = request.getSpotId();

                // If spot is removed
                if(user.dislikeSpot(spotId)) {
                    repository.save(user);
                    MqEvent event = new MqEvent();
                    event.setEventType(EventType.SpotUnlikedEvent);
                    event.setData(spotId);
                    rabbitTemplate.convertAndSend("spotsQueue", event);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                return new ResponseEntity<>("Spot is already unliked", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/user/created")
    public @ResponseBody
    ResponseEntity<?> getUsersCreationDate() {
        try {
                List<User> users = repository.findAll();
                List<UserCreatedInfo> userCreatedInfos = new ArrayList<>();
                users.forEach((user) -> {
                    UserCreatedInfo userCreatedInfo = new UserCreatedInfo();
                    userCreatedInfo.setId(user.getId());
                    userCreatedInfo.setUserCreated(user.getUserCreated());

                    userCreatedInfos.add(userCreatedInfo);
                });
                return new ResponseEntity<>(userCreatedInfos, HttpStatus.OK);

        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    private FirebaseToken decodeToken(String token) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(token);
    }
}
