package social.network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import social.network.model.Friend;
import social.network.model.FriendIdentity;
import social.network.model.User;

import java.util.Set;

public interface FriendRepository extends JpaRepository<Friend, FriendIdentity> {
    Boolean existsFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(User firstUser, User secondUser);
    Friend findFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(User firstUser, User secondUser);
    void removeFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(User firstUser, User secondUser);
    Set<Friend> findAllByFriendIdentity_FirstUser(User firstUser);
}
