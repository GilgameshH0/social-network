package social.network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import social.network.model.User;
import social.network.model.UserPost;

import java.util.Set;

public interface UserPostRepository extends JpaRepository<UserPost, Long> {
    UserPost findUserPostById(Long id);
    Set<UserPost> findAllUserPostByOwner(User user);
    void removeById(Long id);
    void removeAllByOwner(User user);
}
