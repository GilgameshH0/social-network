package social.network.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotEmpty
    private String text;
    @JoinTable(	name = "user",
            joinColumns = @JoinColumn(name = "id"))
    private Long fromUserId;
    @JoinTable(	name = "user",
            joinColumns = @JoinColumn(name = "id"))
    private Long toUserId;
}
