package social.network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import social.network.model.Friend;
import social.network.model.FriendIdentity;
import social.network.model.User;

import java.util.Set;

public interface FriendRepository extends JpaRepository<Friend, FriendIdentity> {
    @Query("select case when count(t) > 0 then true else false end from Friend t where t.friendIdentity.firstUser = ?1 and t.friendIdentity.secondUser = ?2")
    Boolean existsByUsers(User firstUser, User secondUser);
    @Query("select t from Friend t where t.friendIdentity.firstUser = ?1 and t.friendIdentity.secondUser = ?2")
    Friend findFriendByUsers(User firstUser, User secondUser);
    @Modifying
    @Query("delete from Friend t where t.friendIdentity.firstUser = ?1 and t.friendIdentity.secondUser = ?2")
    void removeFriendByUsers(User firstUser, User secondUser);
    @Query("select t from Friend t where t.friendIdentity.firstUser = ?1")
    Set<Friend> findAllFriends(User firstUser);
}
