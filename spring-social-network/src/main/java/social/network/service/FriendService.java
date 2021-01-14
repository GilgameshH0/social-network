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
    private final JwtUtils jwtUtils;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository, UserMapper userMapper, JwtUtils jwtUtils) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
    }

    public void sendFriendRequest(String bearer, Long id) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
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
    public void sendRequestToRemoveFromFriendList(String bearer, Long id) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
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

    public Set<UserGetResponseDto> findAllFriends(String bearer) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
        User user = userRepository.findUserByUsername(username);
        return getFriendList(user);
    }

    public Set<UserGetResponseDto> findAllUserFriends(String bearer, Long id) throws SocialNetworkException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)) {
            throw new SocialNetworkException(ErrorCode.WrongBearer, "Bearer " + bearer + "is wrong!");
        }
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
