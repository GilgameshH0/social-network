package social.network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import social.network.dto.PostRequestDto;
import social.network.dto.PostResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.mapper.UserPostMapper;
import social.network.model.ErrorCode;
import social.network.model.User;
import social.network.model.UserPost;
import social.network.repository.UserPostRepository;
import social.network.repository.UserRepository;
import social.network.security.jwt.JwtUtils;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserPostService {

    private final UserPostRepository userPostRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserPostMapper userPostMapper;

    public UserPostService(UserPostRepository userPostRepository, UserRepository userRepository, JwtUtils jwtUtils, UserPostMapper userPostMapper) {
        this.userPostRepository = userPostRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.userPostMapper = userPostMapper;
    }

    public void createPostOnUserWall(String bearer, PostRequestDto postRequestDTO) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
        User user = userRepository.findUserByUsername(username);
        UserPost post = new UserPost(postRequestDTO.getText(), user);
        userPostRepository.save(post);
        log.trace("Post successfully created!{}", post);
    }

    public void updatePostOnUserWall(String bearer, PostRequestDto postRequestDTO, Long postId) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
        User user = userRepository.findUserByUsername(username);
        if (!userPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.PostDoesNotExists, "Post with id:" + postId + " does not exists!");
        }
        UserPost post = userPostRepository.findUserPostById(postId);
        if (user != post.getOwner()) {
            throw new SocialNetworkException(ErrorCode.YouAreNotOwner, "you are not owner post with id:" + postId + "!");
        }
        post.setText(postRequestDTO.getText());
        userPostRepository.save(post);
        log.trace("Post successfully updated!{}", post);
    }

    public PostResponseDto findPostOnUserWall(Long postId) throws SocialNetworkException {
        if (!userPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.PostDoesNotExists, "Post with id:" + postId + " does not exists!");
        }
        UserPost post = userPostRepository.findUserPostById(postId);
        return userPostMapper.toDto(post);
    }

    public Set<PostResponseDto> findAllPostsOnUserWall(Long userId) throws SocialNetworkException {
        if (!userRepository.existsById(userId)) {
            throw new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + userId + " does not exists!");
        }
        User user = userRepository.findUserById(userId);
        return findUserPostList(user);
    }

    @Transactional
    public void removePostFromUserWallCurrentUser(String bearer, Long postId) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
        User user = userRepository.findUserByUsername(username);
        if (!userPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.PostDoesNotExists, "Post with id:" + postId + " does not exists!");
        }
        UserPost post = userPostRepository.findUserPostById(postId);
        if (user != post.getOwner()) {
            throw new SocialNetworkException(ErrorCode.YouAreNotOwner, "you are not owner post with id:" + postId + "!");
        }
        userPostRepository.removeById(postId);
        log.trace("Post with id: " + postId + " successfully removed!");
    }

    public void removePostFromUserWall(Long postId) throws SocialNetworkException {
        if (!userPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.PostDoesNotExists, "Post with id:" + postId + " does not exists!");
        }
        userPostRepository.removeById(postId);
        log.trace("Post with id: " + postId + " successfully removed!");
    }

    private Set<PostResponseDto> findUserPostList(User user) {
        Set<UserPost> posts = userPostRepository.findAllUserPostByOwner(user);
        Set<PostResponseDto> postResponseDtoList = new HashSet<>();
        for (UserPost post : posts) {
            PostResponseDto postResponseDto = userPostMapper.toDto(post);
            postResponseDtoList.add(postResponseDto);
        }
        return postResponseDtoList;
    }
}
