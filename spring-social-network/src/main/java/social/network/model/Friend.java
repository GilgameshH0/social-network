package social.network.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "friend")
public class Friend {
    @EmbeddedId
    private FriendIdentity friendIdentity;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendStatus friendStatus;

    public Friend(FriendIdentity friendIdentity, FriendStatus friendStatus) {
        this.friendIdentity = friendIdentity;
        this.friendStatus = friendStatus;
    }

    public Friend() {

    }
}
