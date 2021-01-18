package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.UserGetResponseDto;
import social.network.dto.UserLogInRequestDto;
import social.network.dto.UserSignUpAndUpdateRequestDto;
import social.network.exception.SocialNetworkException;
import social.network.model.SuccessfulResponse;
import social.network.security.jwt.JwtUtils;
import social.network.service.UserService;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid UserSignUpAndUpdateRequestDto userSignUpAndUpdateRequestDto) throws SocialNetworkException {
        userService.registerUser(userSignUpAndUpdateRequestDto);
        return ResponseEntity.ok(new SuccessfulResponse("User registered successfully!"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid UserLogInRequestDto userLogInRequestDto) throws SocialNetworkException {
        return ResponseEntity.ok(userService.authenticateUser(userLogInRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUser(@PathVariable("id") Long id) throws SocialNetworkException {
        return ResponseEntity.ok(userService.findUser(id));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update-current-user")
    public ResponseEntity<?> updateCurrentUser(
            @RequestHeader("Authorization") String bearer,
            @RequestBody UserSignUpAndUpdateRequestDto userSignUpAndUpdateRequestDto) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        userService.updateCurrentUser(username, userSignUpAndUpdateRequestDto);
        return ResponseEntity.ok(new SuccessfulResponse("Account successfully updated!"));
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserSignUpAndUpdateRequestDto userSignUpAndUpdateRequestDto) throws SocialNetworkException {
        userService.updateUser(userSignUpAndUpdateRequestDto, id);
        return ResponseEntity.ok(new SuccessfulResponse("User with id:"+ id + " updated successfully!"));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/remove-current-user")
    public ResponseEntity<?> removeCurrentUser(@RequestHeader("Authorization") String bearer) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        userService.removeCurrentUser(username);
        return ResponseEntity.ok(new SuccessfulResponse("Account is successfully deleted"));
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @DeleteMapping("/remove-user/{id}")
    public ResponseEntity<?> removeUser(@PathVariable("id") Long id) throws SocialNetworkException {
        userService.removeUser(id);
        return ResponseEntity.ok(new SuccessfulResponse("User with id:"+ id + " deleted successfully!"));
    }

    @GetMapping("/find-by-country/{country}")
    public Set<UserGetResponseDto> findAllUserByCountry(@PathVariable("country") String country) throws SocialNetworkException {
        return userService.findAllUserByCountry(country);
    }
}
