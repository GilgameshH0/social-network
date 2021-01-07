package social.network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import social.network.model.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllMessagesByFromUserId(Long fromUserId);
    List<Message> findAllMessagesByToUserId(Long toUserId);
}
