package social.network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import social.network.dto.GroupRequestDto;
import social.network.dto.GroupResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.mapper.GroupMapper;
import social.network.model.ErrorCode;
import social.network.model.Group;
import social.network.model.User;
import social.network.repository.GroupPostRepository;
import social.network.repository.GroupRepository;
import social.network.repository.UserRepository;
import social.network.security.jwt.JwtUtils;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupPostRepository groupPostRepository;
    private final JwtUtils jwtUtils;
    private final GroupMapper groupMapper;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, GroupPostRepository groupPostRepository, JwtUtils jwtUtils, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupPostRepository = groupPostRepository;
        this.jwtUtils = jwtUtils;
        this.groupMapper = groupMapper;
    }

    public void createGroup(String bearer, GroupRequestDto groupRequestDto) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
        User user = userRepository.findUserByUsername(username);
        Group group = new Group(groupRequestDto.getName(), user);
        groupRepository.save(group);
        log.trace("Group successfully created!{}", group);
    }

    @Transactional
    public void removeGroupCurrentUser(String bearer, Long id) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
        User user = userRepository.findUserByUsername(username);
        if (!groupRepository.existsByIdAndOwner(id, user)) {
            throw new SocialNetworkException(ErrorCode.YouAreNotOwner, "You are not owner of group with id: " + id);
        }
        Group group = groupRepository.findGroupById(id);
        groupPostRepository.removeAllByOwner(group);
        log.trace("All post from group with id: " + id + " successfully removed!");
        groupRepository.removeById(id);
        log.trace("Group with id: " + id + " successfully removed!");
    }

    @Transactional
    public void removeGroup(Long id) throws SocialNetworkException {
        if (!groupRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.GroupDoesNotExist, "Group with id: " + id + " does not exists!");
        }
        Group group = groupRepository.findGroupById(id);
        groupPostRepository.removeAllByOwner(group);
        log.trace("All post from group with id: " + id + " successfully removed!");
        groupRepository.removeById(id);
        log.trace("Group with id: " + id + " successfully removed!");
    }

    public void joinGroup(String bearer, Long id) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
        User user = userRepository.findUserByUsername(username);
        if (!groupRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.GroupDoesNotExist, "Group with id: " + id + " does not exists!");
        }
        Group group = groupRepository.findGroupById(id);
        Set<User> subscribes = group.getSubscribers();
        subscribes.add(user);
        group.setSubscribers(subscribes);
        groupRepository.save(group);
    }

    public void leaveGroup(String bearer, Long id) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
        User user = userRepository.findUserByUsername(username);
        if (!groupRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.GroupDoesNotExist, "Group with id: " + id + " does not exists!");
        }
        Group group = groupRepository.findGroupById(id);
        Set<User> subscribes = group.getSubscribers();
        subscribes.remove(user);
        group.setSubscribers(subscribes);
        groupRepository.save(group);
    }

    public Set<GroupResponseDto> findGroupsCurrentUser(String bearer) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
        User user = userRepository.findUserByUsername(username);
        return findGroupList(user);
    }

    public Set<GroupResponseDto> findGroups(Long id) throws SocialNetworkException {
        if (!userRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + id + " does not exists!");
        }
        User user = userRepository.findUserById(id);
        return findGroupList(user);
    }

    private Set<GroupResponseDto> findGroupList(User user) {
        Set<Group> groups = groupRepository.findGroupBySubscribersContains(user);
        Set<GroupResponseDto> groupResponseDtoList = new HashSet<>();
        for (Group group : groups) {
            GroupResponseDto groupResponseDto = groupMapper.toDto(group);
            groupResponseDtoList.add(groupResponseDto);
        }
        return groupResponseDtoList;
    }

}
