package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.GroupRequestDto;
import social.network.dto.GroupResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.model.SuccessfulResponse;
import social.network.service.GroupService;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-group")
    public ResponseEntity<?> createGroup(@RequestHeader("Authorization") String bearer, @RequestBody @Valid GroupRequestDto groupRequestDto) throws SocialNetworkException {
        groupService.createGroup(bearer, groupRequestDto);
        return ResponseEntity.ok(new SuccessfulResponse("You successfully created group!"));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/remove-group-current-user/{id}")
    public ResponseEntity<?> removeGroupCurrentUser(@RequestHeader("Authorization") String bearer, @PathVariable("id") Long id) throws SocialNetworkException {
        groupService.removeGroupCurrentUser(bearer, id);
        return ResponseEntity.ok(new SuccessfulResponse("You successfully removed your group with id: " + id + "!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove-group/{id}")
    public ResponseEntity<?> removeGroup(@PathVariable("id") Long id) throws SocialNetworkException {
        groupService.removeGroup(id);
        return ResponseEntity.ok(new SuccessfulResponse("You successfully removed group with id: " + id + "!"));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/join-group/{id}")
    public ResponseEntity<?> joinGroup(@RequestHeader("Authorization") String bearer, @PathVariable("id") Long id) throws SocialNetworkException {
        groupService.joinGroup(bearer, id);
        return ResponseEntity.ok(new SuccessfulResponse("You successfully joined to group with id: " + id + "!"));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/leave-group/{id}")
    public ResponseEntity<?> leaveGroup(@RequestHeader("Authorization") String bearer, @PathVariable("id") Long id) throws SocialNetworkException {
        groupService.leaveGroup(bearer, id);
        return ResponseEntity.ok(new SuccessfulResponse("You successfully left from group with id: " + id + "!"));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/find-groups-current-user")
    public Set<GroupResponseDto> findGroupsCurrentUser(@RequestHeader("Authorization") String bearer) throws SocialNetworkException {
        return groupService.findGroupsCurrentUser(bearer);
    }

    @GetMapping("/find-user-groups/{id}")
    public Set<GroupResponseDto> findGroups( @PathVariable("id") Long id) throws SocialNetworkException {
        return groupService.findGroups(id);
    }
}
