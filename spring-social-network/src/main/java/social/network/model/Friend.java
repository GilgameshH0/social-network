package social.network.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Friend friend = (Friend) obj;
        return friendIdentity.equals(friend.friendIdentity);
    }
}
