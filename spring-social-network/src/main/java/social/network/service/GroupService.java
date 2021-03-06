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

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupPostRepository groupPostRepository;
    private final GroupMapper groupMapper;

    public GroupService(
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupPostRepository groupPostRepository,
            GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupPostRepository = groupPostRepository;
        this.groupMapper = groupMapper;
    }

    public void createGroup(String username, GroupRequestDto groupRequestDto) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        Group group = new Group(groupRequestDto.getName(), user);
        groupRepository.save(group);
        log.trace("Group successfully created!{}", group);
    }

    @Transactional
    public void removeGroupCurrentUser(String username, Long id) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        if (!groupRepository.existsByIdAndOwner(id, user)) {
            throw new SocialNetworkException(ErrorCode.NotOwner, "You are not owner of group with id: " + id);
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
            throw new SocialNetworkException(ErrorCode.NotExists, "Group with id: " + id + " does not exists!");
        }
        Group group = groupRepository.findGroupById(id);
        groupPostRepository.removeAllByOwner(group);
        log.trace("All post from group with id: " + id + " successfully removed!");
        groupRepository.removeById(id);
        log.trace("Group with id: " + id + " successfully removed!");
    }

    public void joinGroup(String username, Long id) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        if (!groupRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Group with id: " + id + " does not exists!");
        }
        Group group = groupRepository.findGroupById(id);
        Set<User> subscribes = group.getSubscribers();
        if (subscribes.contains(user)) {
            throw new SocialNetworkException(ErrorCode.AlreadySubscribed, "You already joined to group with id:" + id + "!");
        }
        subscribes.add(user);
        group.setSubscribers(subscribes);
        groupRepository.save(group);
    }

    public void leaveGroup(String username, Long id) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        if (!groupRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "Group with id: " + id + " does not exists!");
        }
        Group group = groupRepository.findGroupById(id);
        Set<User> subscribes = group.getSubscribers();
        if (!subscribes.contains(user)) {
            throw new SocialNetworkException(ErrorCode.NotSubscribed, "You dont subscribed to group with id: " + id + " !");
        }
        subscribes.remove(user);
        group.setSubscribers(subscribes);
        groupRepository.save(group);
    }

    public Set<GroupResponseDto> findGroupsCurrentUser(String username) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        Set<GroupResponseDto> groupList = findGroupList(user);
        if (groupList.size() == 0) {
            throw new SocialNetworkException(ErrorCode.NotExists, "You are not subscribed on any group!");
        }
        return groupList;
    }

    public Set<GroupResponseDto> findGroups(Long id) throws SocialNetworkException {
        if (!userRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id:" + id + " does not exists!");
        }
        User user = userRepository.findUserById(id);
        Set<GroupResponseDto> groupList = findGroupList(user);
        if (groupList.size() == 0) {
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id:" + id + " does not subscribed on any group!");
        }
        return groupList;
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
