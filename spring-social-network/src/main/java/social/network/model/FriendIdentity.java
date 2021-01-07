package social.network.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class FriendIdentity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long firstUser;
    private Long secondUser;

    public FriendIdentity(Long firstUser, Long secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

    public FriendIdentity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendIdentity that = (FriendIdentity) o;
        return Objects.equals(firstUser, that.firstUser) && Objects.equals(secondUser, that.secondUser);
    }
}
