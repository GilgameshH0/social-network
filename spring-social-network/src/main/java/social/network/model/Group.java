package social.network.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "community")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> subscribers;

    public Group(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public Group() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group group = (Group) obj;
        return id.equals(group.id);
    }
}
