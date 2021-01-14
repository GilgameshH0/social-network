package social.network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import social.network.model.Group;
import social.network.model.User;

import java.util.Set;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Boolean existsByIdAndOwner(Long id, User owner);
    void removeById(Long id);
    Group findGroupById(Long id);
    Set<Group> findAllGroupByOwner(User user);
    Set<Group> findGroupBySubscribersContains(User user);
}
