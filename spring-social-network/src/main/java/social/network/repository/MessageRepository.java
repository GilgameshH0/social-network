package social.network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import social.network.model.Message;

import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Set<Message> findAllMessagesByFromUserId(Long fromUserId);
    Set<Message> findAllMessagesByToUserId(Long toUserId);
}
