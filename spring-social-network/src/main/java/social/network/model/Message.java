package social.network.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    private User fromUser;
    @ManyToOne(fetch = FetchType.LAZY)
    private User toUser;

    public Message(String text, User fromUser, User toUser) {
        this.text = text;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public Message() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message message = (Message) obj;
        if (!fromUser.equals(message.fromUser))
            return false;
        if (!toUser.equals(message.toUser))
            return false;
        return id.equals(message.id);
    }
}
