package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.UserDTO;
import social.network.exception.EmailAlreadyInUseException;
import social.network.exception.UserDoesNotExistsException;
import social.network.exception.UsernameAlreadyInUseException;
import social.network.exception.WrongBearerException;
import social.network.dto.auth.LogInUser;
import social.network.dto.auth.SignUpUser;
import social.network.service.UserService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpUser signUpUser) throws EmailAlreadyInUseException, UsernameAlreadyInUseException {
        userService.registerUser(signUpUser);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LogInUser logInUser) {
        return ResponseEntity.ok(userService.authenticateUser(logInUser));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> findUser(@PathVariable("id") Long id) throws UserDoesNotExistsException {
        return ResponseEntity.ok(userService.findUser(id));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/user/update-current-user")
    public ResponseEntity<?> updateCurrentUser(@RequestHeader("Authorization") String bearer, @RequestBody SignUpUser signUpUser) throws WrongBearerException {
        userService.updateCurrentUser(bearer, signUpUser);
        return ResponseEntity.ok("Account successfully updated!");
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PutMapping("/user/update-user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @Valid @RequestBody SignUpUser signUpUser) throws UserDoesNotExistsException {
        userService.updateUser(signUpUser, id);
        return ResponseEntity.ok("User with id:"+ id + " updated successfully!");
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/user/remove-current-user")
    public ResponseEntity<?> removeCurrentUser(@RequestHeader("Authorization") String bearer) throws WrongBearerException {
        userService.removeCurrentUser(bearer);
        return ResponseEntity.ok("Account is successfully deleted");
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @DeleteMapping("/user/remove-user/{id}")
    public ResponseEntity<?> removeUser(@PathVariable("id") Long id) throws UserDoesNotExistsException {
        userService.removeUser(id);
        return ResponseEntity.ok("User with id:"+ id + " deleted successfully!");
    }

    @GetMapping("/user/find-by-country/{country}")
    public List<UserDTO> findUser(@PathVariable("country") String country) {
        return userService.findAllUserByCountry(country);
    }
}
