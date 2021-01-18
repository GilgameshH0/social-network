package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.PostRequestDto;
import social.network.dto.PostResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.model.SuccessfulResponse;
import social.network.security.jwt.JwtUtils;
import social.network.service.GroupPostService;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/api/post")
public class GroupPostController {

    private final GroupPostService groupPostService;
    private final JwtUtils jwtUtils;
    public GroupPostController(GroupPostService groupPostService, JwtUtils jwtUtils) {
        this.groupPostService = groupPostService;
        this.jwtUtils = jwtUtils;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-post-on-group-wall/{groupId}")
    public ResponseEntity<?> createPostOnGroupWall(
            @RequestHeader("Authorization") String bearer,
            @RequestBody @Valid PostRequestDto postRequestDTO,
            @PathVariable("groupId") Long groupId) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        groupPostService.createPostOnGroupWall(username, postRequestDTO, groupId);
        return ResponseEntity.ok(new SuccessfulResponse("Post successfully created!"));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update-post-on-group-wall/{postId}")
    public ResponseEntity<?> updatePostOnGroupWall(
            @RequestHeader("Authorization") String bearer,
            @RequestBody @Valid PostRequestDto postRequestDTO,
            @PathVariable("postId") Long postId) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        groupPostService.updatePostOnGroupWall(username, postRequestDTO, postId);
        return ResponseEntity.ok(new SuccessfulResponse("Post successfully updated!"));
    }

    @GetMapping("/find-post-on-group-wall/{postId}")
    public PostResponseDto findPostOnGroupWall(@PathVariable("postId") Long postId) throws SocialNetworkException {
        return groupPostService.findPostOnGroupWall(postId);
    }

    @GetMapping("/find-all-posts-on-group-wall/{groupId}")
    public Set<PostResponseDto> findAllPostsOnGroupWall(@PathVariable("groupId") Long groupId) throws SocialNetworkException {
        return groupPostService.findAllPostsOnGroupWall(groupId);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/remove-post-on-group-wall-current-user/{postId}")
    public ResponseEntity<?> removePostFromUserWallCurrentUser(
            @RequestHeader("Authorization") String bearer,
            @PathVariable("postId") Long postId) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        groupPostService.removePostFromGroupWallCurrentUser(username, postId);
        return ResponseEntity.ok(new SuccessfulResponse("Post successfully removed!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove-post-on-group-wall/{postId}")
    public ResponseEntity<?> removePostFromGroupWall(@PathVariable("postId") Long postId) throws SocialNetworkException {
        groupPostService.removePostFromGroupWall(postId);
        return ResponseEntity.ok(new SuccessfulResponse("Post successfully removed!"));
    }
}
