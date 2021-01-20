package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.UserGetResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.model.SuccessfulResponse;
import social.network.service.FriendService;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/send-friend-request/{id}")
    public ResponseEntity<?> sendFriendRequest(
            Principal principal,
            @PathVariable("id") Long id) throws SocialNetworkException {
        friendService.sendFriendRequest(principal.getName(), id);
        return ResponseEntity.ok(new SuccessfulResponse("You successfully sent friend request!"));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/send-request-to-remove-from-friend-list/{id}")
    public ResponseEntity<?> sendRequestToRemoveFromFriendList(
            Principal principal,
            @PathVariable("id") Long id) throws SocialNetworkException {
        friendService.sendRequestToRemoveFromFriendList(principal.getName(), id);
        return ResponseEntity.ok(new SuccessfulResponse("You successfully removed user with id:" + id + " from your friend list!"));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-all-friends")
    public Set<UserGetResponseDto> findAllFriends(Principal principal) throws SocialNetworkException {
        return friendService.findAllFriends(principal.getName());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-all-user-friends/{id}")
    public Set<UserGetResponseDto> findAllUserFriends(
            @PathVariable("id") Long id) throws SocialNetworkException {
        return friendService.findAllUserFriends(id);
    }
}
