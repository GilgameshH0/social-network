package social.network.service;

import org.springframework.stereotype.Service;
import social.network.exception.AlreadySentFriendRequestException;
import social.network.exception.UserDoesNotExistsException;
import social.network.exception.WrongBearerException;
import social.network.model.Friend;
import social.network.model.FriendIdentity;
import social.network.model.FriendStatus;
import social.network.model.User;
import social.network.repository.FriendRepository;
import social.network.repository.UserRepository;
import social.network.security.jwt.JwtUtils;

@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository, JwtUtils jwtUtils) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public void sendFriendRequest(String bearer, Long id) throws WrongBearerException, AlreadySentFriendRequestException, UserDoesNotExistsException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)){
            throw new WrongBearerException(bearer);
        }
        User user = userRepository.findUserByUsername(username);
        if (friendRepository.existsFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(user.getId(), id)){
           throw new AlreadySentFriendRequestException();
        }
        if (friendRepository.existsFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(id, user.getId())){
            Friend firstFriend = new Friend(new FriendIdentity(user.getId(), id), FriendStatus.CONFIRMED);
            friendRepository.save(firstFriend);
            Friend secondFriend = new Friend(new FriendIdentity(id, user.getId()), FriendStatus.CONFIRMED);
            friendRepository.save(secondFriend);
        }else {
            Friend friend = new Friend(new FriendIdentity(user.getId(), id), FriendStatus.REQUESTED);
            friendRepository.save(friend);
        }
    }


    public void sendRequestToRemoveFromFriendList(String bearer, Long id) throws WrongBearerException {
        String username = jwtUtils.getUsernameFromTokenString(bearer);
        if (!userRepository.existsByUsername(username)){
            throw new WrongBearerException(bearer);
        }
    }
}
