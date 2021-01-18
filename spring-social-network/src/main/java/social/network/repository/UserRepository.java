package social.network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import social.network.model.User;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String Username);
    User findUserById(Long id);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    void removeUserByUsername(String username);
    void removeUserById(Long id);
    Boolean existsByCountry(String country);
    Set<User> findAllByCountry(String country);
}
