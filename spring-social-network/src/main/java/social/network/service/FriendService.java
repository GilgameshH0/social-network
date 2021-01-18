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
            throw new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + id + " does not exists!");
        }
        User secondUser = userRepository.findUserById(id);
        if (friendRepository.existsFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(firstUser, secondUser)) {
            throw new SocialNetworkException(ErrorCode.AlreadySentFriendRequest, "you are already sent friend request to user with id:" + id);
        }
        if (friendRepository.existsFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(secondUser, firstUser)) {
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
            throw new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + id + " does not exists!");
        }
        User secondUser = userRepository.findUserById(id);
        if (friendRepository.findFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(firstUser, secondUser).getFriendStatus() == FriendStatus.REQUESTED) {
            friendRepository.removeFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(firstUser, secondUser);
        } else {
            friendRepository.removeFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(firstUser, secondUser);
            Friend friend = friendRepository.findFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(secondUser, firstUser);
            friend.setFriendStatus(FriendStatus.REQUESTED);
            friendRepository.save(friend);
        }
        log.trace("Request to remove from friend list sent to user with id:" + id + " !");
    }

    public Set<UserGetResponseDto> findAllFriends(String username) throws SocialNetworkException {
        User user = userRepository.findUserByUsername(username);
        return getFriendList(user);
    }

    public Set<UserGetResponseDto> findAllUserFriends(String username, Long id) throws SocialNetworkException {
        User user = userRepository.findUserById(id);
        return getFriendList(user);
    }

    private Set<UserGetResponseDto> getFriendList(User user) {
        Set<Friend> friendList = friendRepository.findAllByFriendIdentity_FirstUser(user);
        Set<UserGetResponseDto> userGetResponseDtoList = new HashSet<>();
        for (Friend friend : friendList) {
            UserGetResponseDto userGetResponseDto = userMapper.toDto(friend.getFriendIdentity().getSecondUser());
            userGetResponseDtoList.add(userGetResponseDto);
        }
        return userGetResponseDtoList;
    }
}
