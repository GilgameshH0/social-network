package social.network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import social.network.model.Group;
import social.network.model.GroupPost;

import java.util.Set;

public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    GroupPost findGroupPostById(Long id);
    Set<GroupPost> findAllGroupPostByOwner(Group group);
    void removeById(Long id);
    void removeAllByOwner(Group group);
}
