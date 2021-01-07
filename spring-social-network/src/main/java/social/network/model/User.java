package social.network.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String username;
    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;
    @NotBlank
    @Size(max = 20)
    private String name;
    @NotBlank
    @Size(max = 40)
    private String surname;
    @NotBlank
    @Size(max = 40)
    private String patronymic;
    @NotBlank
    private int gender;
    @NotBlank
    private LocalDate birthdate;
    @NotBlank
    private String country;
    @Column(name = "email")
    @Size(max = 50)
    @NotBlank
    @Email
    private String email;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    public User() {
    }

    public User(@NotBlank @Size(max = 20) String username, @NotBlank @Size(max = 120) String password, @NotBlank @Size(max = 20) String name, @NotBlank @Size(max = 40) String surname, @NotBlank @Size(max = 40) String patronymic, @NotBlank int gender, @NotBlank LocalDate birthdate, @NotBlank String country, @Size(max = 50) @NotBlank @Email String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.gender = gender;
        this.birthdate = birthdate;
        this.country = country;
        this.email = email;
    }
}
