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

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserPostService {

    private final UserPostRepository userPostRepository;
    private final UserRepository userRepository;
    private final UserPostMapper userPostMapper;

    public UserPostService(
            UserPostRepository userPostRepository,
            UserRepository userRepository,
            UserPostMapper userPostMapper) {
        this.userPostRepository = userPostRepository;
        this.userRepository = userRepository;
        this.userPostMapper = userPostMapper;
    }

    public void createPostOnUserWall(String username, PostRequestDto postRequestDTO) {
        User user = userRepository.findUserByUsername(username);
        UserPost post = new UserPost(postRequestDTO.getText(), user);
        userPostRepository.save(post);
        log.trace("Post successfully created!{}", post);
    }

    public void updatePostOnUserWall(String username, PostRequestDto postRequestDTO, Long postId) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        if (!userPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!");
        }
        UserPost post = userPostRepository.findUserPostById(postId);
        if (user != post.getOwner()) {
            throw new SocialNetworkException(ErrorCode.NotOwner, "you are not owner post with id:" + postId + "!");
        }
        post.setText(postRequestDTO.getText());
        userPostRepository.save(post);
        log.trace("Post successfully updated!{}", post);
    }

    public PostResponseDto findPostOnUserWall(Long postId) throws SocialNetworkException {
        if (!userPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!");
        }
        UserPost post = userPostRepository.findUserPostById(postId);
        return userPostMapper.toDto(post);
    }

    public Set<PostResponseDto> findAllPostsOnUserWall(Long userId) throws SocialNetworkException {
        if (!userRepository.existsById(userId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id:" + userId + " does not exists!");
        }
        User user = userRepository.findUserById(userId);
        Set<PostResponseDto> postList = findUserPostList(user);
        if (postList.size() == 0) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Posts from group with id:" + userId + " does not exists!");
        }
        return postList;
    }

    @Transactional
    public void removePostFromUserWallCurrentUser(String username, Long postId) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        if (!userPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!");
        }
        UserPost post = userPostRepository.findUserPostById(postId);
        if (user != post.getOwner()) {
            throw new SocialNetworkException(ErrorCode.NotOwner, "you are not owner post with id:" + postId + "!");
        }
        userPostRepository.removeById(postId);
        log.trace("Post with id: " + postId + " successfully removed!");
    }

    public void removePostFromUserWall(Long postId) throws SocialNetworkException {
        if (!userPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!");
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
