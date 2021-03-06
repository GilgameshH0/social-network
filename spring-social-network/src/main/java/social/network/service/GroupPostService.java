package social.network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import social.network.dto.PostRequestDto;
import social.network.dto.PostResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.mapper.GroupPostMapper;
import social.network.model.ErrorCode;
import social.network.model.Group;
import social.network.model.GroupPost;
import social.network.model.User;
import social.network.repository.GroupPostRepository;
import social.network.repository.GroupRepository;
import social.network.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class GroupPostService {
    private final GroupPostRepository groupPostRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupPostMapper groupPostMapper;

    public GroupPostService(
            GroupPostRepository groupPostRepository,
            UserRepository userRepository,
            GroupRepository groupRepository,
            GroupPostMapper groupPostMapper) {
        this.groupPostRepository = groupPostRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupPostMapper = groupPostMapper;
    }

    public void createPostOnGroupWall(String username, PostRequestDto postRequestDTO, Long groupId) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        if (!groupRepository.existsById(groupId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Group with id: " + groupId + " does not exists!");
        }
        Set<Group> groups = groupRepository.findAllGroupByOwner(user);
        boolean groupFlag = false;
        for (Group currentGroup : groups) {
            if (currentGroup.getId().equals(groupId)) {
                groupFlag = true;
                break;
            }
        }
        if (!groupFlag) {
            throw new SocialNetworkException(ErrorCode.NotOwner, "You are not owner of group with id: " + groupId);
        }
        Group group = groupRepository.findGroupById(groupId);
        GroupPost post = new GroupPost(postRequestDTO.getText(), group);
        groupPostRepository.save(post);
        log.trace("Post successfully created!{}", post);
    }

    public void updatePostOnGroupWall(String username, PostRequestDto postRequestDTO, Long postId) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        if (!groupRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!");
        }
        GroupPost post = groupPostRepository.findGroupPostById(postId);
        Set<Group> groups = groupRepository.findAllGroupByOwner(user);
        boolean groupFlag = false;
        for (Group currentGroup : groups) {
            if (currentGroup.equals(post.getOwner())) {
                groupFlag = true;
                break;
            }
        }
        if (!groupFlag) {
            throw new SocialNetworkException(ErrorCode.NotOwner, "You are not owner of group with id: " + post.getOwner().getId());
        }
        post.setText(postRequestDTO.getText());
        groupPostRepository.save(post);
        log.trace("Post successfully updated!{}", post);
    }

    public PostResponseDto findPostOnGroupWall(Long postId) throws SocialNetworkException {
        if (!groupRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!");
        }
        GroupPost post = groupPostRepository.findGroupPostById(postId);
        return groupPostMapper.toDto(post);
    }

    public Set<PostResponseDto> findAllPostsOnGroupWall(Long groupId) throws SocialNetworkException {
        if (!groupRepository.existsById(groupId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Group with id: " + groupId + " does not exists!");
        }
        Group group = groupRepository.findGroupById(groupId);
        Set<PostResponseDto> postList = findGroupPostList(group);
        if (postList.size() == 0) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Posts from group with id:" + groupId + " don't exists!");
        }
        return postList;
    }

    @Transactional
    public void removePostFromGroupWallCurrentUser(String username, Long postId) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        if (!groupPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!");
        }
        GroupPost post = groupPostRepository.findGroupPostById(postId);
        Set<Group> groups = groupRepository.findAllGroupByOwner(user);
        boolean groupFlag = false;
        for (Group currentGroup : groups) {
            if (currentGroup.equals(post.getOwner())) {
                groupFlag = true;
                break;
            }
        }
        if (!groupFlag) {
            throw new SocialNetworkException(ErrorCode.NotOwner, "You are not owner of group with id: " + post.getOwner().getId());
        }
        groupPostRepository.removeById(postId);
        log.trace("Post with id:" + postId + " removed!");
    }

    @Transactional
    public void removePostFromGroupWall(Long postId) throws SocialNetworkException {
        if (!groupPostRepository.existsById(postId)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!");
        }
        groupPostRepository.removeById(postId);
        log.trace("Post with id:" + postId + " removed!");
    }

    private Set<PostResponseDto> findGroupPostList(Group group) {
        Set<GroupPost> posts = groupPostRepository.findAllGroupPostByOwner(group);
        Set<PostResponseDto> postResponseDtoList = new HashSet<>();
        for (GroupPost post : posts) {
            PostResponseDto postResponseDto = groupPostMapper.toDto(post);
            postResponseDtoList.add(postResponseDto);
        }

        return postResponseDtoList;
    }
}
