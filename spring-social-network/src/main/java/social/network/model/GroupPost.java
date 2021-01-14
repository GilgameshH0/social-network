package social.network.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "group_post")
public class GroupPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    private Group owner;

    public GroupPost(String text, Group owner) {
        this.text = text;
        this.owner = owner;
    }

    public GroupPost() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroupPost groupPost = (GroupPost) obj;
        return id.equals(groupPost.id);
    }
}