package social.network.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Getter
@Setter
@ToString
public class SignUpUser {
    @NotEmpty(message = "Please, enter your username!")
    @Size(min = 4, max = 20)
    private String username;
    @NotEmpty(message = "Please, enter your password!")
    @Size(max = 120)
    private String password;
    @NotEmpty(message = "Please, enter your name!")
    @Size(max = 20)
    private String name;
    @NotEmpty(message = "Please, enter your surname!")
    @Size(max = 40)
    private String surname;
    @NotEmpty(message = "Please, enter your patronymic!")
    @Size(max = 40)
    private String patronymic;
    @NotEmpty(message = "Please, enter your gender!")
    private int gender;
    @NotEmpty(message = "Please, enter your birthdate!")
    private LocalDate birthdate;
    @NotEmpty(message = "Please, enter your country!")
    private String country;
    @Size(max = 50)
    @NotEmpty(message = "Please, enter your email!")
    @Email
    private String email;
}