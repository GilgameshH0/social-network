package social.network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import social.network.model.Friend;
import social.network.model.FriendIdentity;

public interface FriendRepository extends JpaRepository<Friend, FriendIdentity> {
    Friend findByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(Long firstUserId, Long secondUserId);
    Boolean existsFriendByFriendIdentity_FirstUserAndFriendIdentity_SecondUser(Long firstUserId, Long secondUserId);
}
