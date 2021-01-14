package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.UserGetResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.model.SuccessfulResponse;
import social.network.service.FriendService;

import java.util.Set;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/send-friend-request/{id}")
    public ResponseEntity<?> sendFriendRequest(@RequestHeader("Authorization") String bearer, @PathVariable("id") Long id) throws SocialNetworkException {
        friendService.sendFriendRequest(bearer, id);
        return ResponseEntity.ok(new SuccessfulResponse("You successfully sent friend request!"));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/send-request-to-remove-from-friend-list/{id}")
    public ResponseEntity<?> sendRequestToRemoveFromFriendList(@RequestHeader("Authorization") String bearer, @PathVariable("id") Long id) throws SocialNetworkException {
        friendService.sendRequestToRemoveFromFriendList(bearer, id);
        return ResponseEntity.ok(new SuccessfulResponse("You successfully removed user with id:" + id + " from your friend list!"));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-all-friends")
    public Set<UserGetResponseDto> findAllFriends(@RequestHeader("Authorization") String bearer) throws SocialNetworkException {
        return friendService.findAllFriends(bearer);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-all-user-friends/{id}")
    public Set<UserGetResponseDto> findAllUserFriends(@RequestHeader("Authorization") String bearer, @PathVariable("id") Long id) throws SocialNetworkException {
        return friendService.findAllUserFriends(bearer, id);
    }
}
