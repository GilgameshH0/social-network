package social.network.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import social.network.dto.UserGetResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.mapper.UserMapper;
import social.network.model.*;
import social.network.repository.FriendRepository;
import social.network.repository.UserRepository;
import social.network.security.jwt.JwtUtils;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository, UserMapper userMapper) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public void sendFriendRequest(String username, Long id) throws SocialNetworkException {
        User firstUser = userRepository.findUserByUsername(username);
        if (!userRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id:" + id + " does not exists!");
        }
        User secondUser = userRepository.findUserById(id);
        if (friendRepository.existsByUsers(firstUser, secondUser)) {
            throw new SocialNetworkException(ErrorCode.AlreadySentFriendRequest, "you are already sent friend request to user with id:" + id);
        }
        if (friendRepository.existsByUsers(secondUser, firstUser)) {
            Friend firstFriend = new Friend(new FriendIdentity(firstUser, secondUser), FriendStatus.CONFIRMED);
            friendRepository.save(firstFriend);
            Friend secondFriend = new Friend(new FriendIdentity(secondUser, firstUser), FriendStatus.CONFIRMED);
            friendRepository.save(secondFriend);
            log.trace("Friend request confirmed with user with id:" + firstUser.getId() + "and user with id:" + secondUser.getId() + "!");
        } else {
            Friend friend = new Friend(new FriendIdentity(firstUser, secondUser), FriendStatus.REQUESTED);
            friendRepository.save(friend);
            log.trace("Friend request sent to user with id:" + id + " !");
        }
    }

    @Transactional
    public void sendRequestToRemoveFromFriendList(String username, Long id) throws SocialNetworkException {
        User firstUser = userRepository.findUserByUsername(username);
        if (!userRepository.existsById(id)) {
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id:" + id + " does not exists!");
        }
        User secondUser = userRepository.findUserById(id);
        if (!friendRepository.existsByUsers(firstUser, secondUser)) {
            throw new SocialNetworkException(ErrorCode.NotYourFriend, "User with id:" + id + " is not your Friend!");
        }
        if (friendRepository.findFriendByUsers(firstUser, secondUser).getFriendStatus() == FriendStatus.REQUESTED) {
            friendRepository.removeFriendByUsers(firstUser, secondUser);
        } else {
            friendRepository.removeFriendByUsers(firstUser, secondUser);
            Friend friend = friendRepository.findFriendByUsers(secondUser, firstUser);
            friend.setFriendStatus(FriendStatus.REQUESTED);
            friendRepository.save(friend);
        }
        log.trace("Request to remove from friend list sent to user with id:" + id + " !");
    }

    public Set<UserGetResponseDto> findAllFriends(String username) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        Set<UserGetResponseDto> friendList = findFriendList(user);
        if (friendList.size() == 0){
            throw new SocialNetworkException(ErrorCode.NotExists, "You have not friends!");
        }
        return friendList;
    }

    public Set<UserGetResponseDto> findAllUserFriends(Long id) throws SocialNetworkException {
        User user = userRepository.findUserById(id);
        Set<UserGetResponseDto> friendList = findFriendList(user);
        if (friendList.size() == 0){
            throw new SocialNetworkException(ErrorCode.NotExists, "User with id: " + id + " does not have friends!");
        }
        return friendList;
    }

    private Set<UserGetResponseDto> findFriendList(User user) {
        Set<Friend> friendList = friendRepository.findAllFriends(user);
        Set<UserGetResponseDto> userGetResponseDtoList = new HashSet<>();
        for (Friend friend : friendList) {
            UserGetResponseDto userGetResponseDto = userMapper.toDto(friend.getFriendIdentity().getSecondUser());
            userGetResponseDtoList.add(userGetResponseDto);
        }
        return userGetResponseDtoList;
    }
}
