package social.network.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import social.network.dto.PostRequestDto;
import social.network.dto.PostResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.model.SuccessfulResponse;
import social.network.service.UserPostService;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/api/post")
public class UserPostController {
    private final UserPostService userPostService;

    public UserPostController(UserPostService userPostService) {
        this.userPostService = userPostService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-post-on-user-wall")
    public ResponseEntity<?> createPostOnUserWall(@RequestHeader("Authorization") String bearer, @RequestBody @Valid PostRequestDto postRequestDTO) throws SocialNetworkException {
        userPostService.createPostOnUserWall(bearer, postRequestDTO);
        return ResponseEntity.ok(new SuccessfulResponse("Post successfully created!"));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update-post-on-user-wall/{postId}")
    public ResponseEntity<?> updatePostOnUserWall(@RequestHeader("Authorization") String bearer, @RequestBody @Valid PostRequestDto postRequestDTO, @PathVariable("postId") Long postId) throws SocialNetworkException {
        userPostService.updatePostOnUserWall(bearer, postRequestDTO, postId);
        return ResponseEntity.ok(new SuccessfulResponse("Post successfully updated!"));
    }

    @GetMapping("/find-post-on-user-wall/{postId}")
    public PostResponseDto findPostOnUserWall(@PathVariable("postId") Long postId) throws SocialNetworkException {
        return userPostService.findPostOnUserWall(postId);
    }

    @GetMapping("/find-all-posts-on-user-wall/{userId}")
    public Set<PostResponseDto> findAllPostsOnUserWall(@PathVariable("userId") Long userId) throws SocialNetworkException {
        return userPostService.findAllPostsOnUserWall(userId);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/remove-post-on-user-wall/{postId}")
    public ResponseEntity<?> removePostFromUserWallCurrentUser(@RequestHeader("Authorization") String bearer, @PathVariable("postId") Long postId) throws SocialNetworkException {
        userPostService.removePostFromUserWallCurrentUser(bearer, postId);
        return ResponseEntity.ok(new SuccessfulResponse("Post successfully removed!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove-post-on-user-wall-by-user-id/{postId}")
    public ResponseEntity<?> removePostFromUserWall(@PathVariable("postId") Long postId) throws SocialNetworkException {
        userPostService.removePostFromUserWall(postId);
        return ResponseEntity.ok(new SuccessfulResponse("Post successfully removed!"));
    }
}
