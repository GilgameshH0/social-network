package social.network.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class FriendIdentity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    private User firstUser;
    @ManyToOne(fetch = FetchType.LAZY)
    private User secondUser;

    public FriendIdentity(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

    public FriendIdentity() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FriendIdentity friendIdentity = (FriendIdentity) obj;
        if (!firstUser.equals(friendIdentity.firstUser)) {
            return false;
        }
        return secondUser.equals(friendIdentity.secondUser);
    }
}
