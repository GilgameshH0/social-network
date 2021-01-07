package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.exception.AlreadySentFriendRequestException;
import social.network.exception.UserDoesNotExistsException;
import social.network.exception.WrongBearerException;
import social.network.service.FriendService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/send-friend-request/{id}")
    public ResponseEntity<?> sendFriendRequest(@RequestHeader("Authorization") String bearer, @PathVariable("id") Long id) throws WrongBearerException, AlreadySentFriendRequestException, UserDoesNotExistsException {
        friendService.sendFriendRequest(bearer, id);
        return ResponseEntity.ok("You are successfully sent friend request!");
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/send-request-to-remove-from-friend-list/{id}")
    public ResponseEntity<?> sendRequestToRemoveFromFriendList(@RequestHeader("Authorization") String bearer, @PathVariable("id") Long id) throws WrongBearerException, AlreadySentFriendRequestException, UserDoesNotExistsException {
        friendService.sendRequestToRemoveFromFriendList(bearer, id);
        return ResponseEntity.ok("You are successfully sent friend request!");
    }
    
}
