package social.network.dto;

import lombok.Getter;
import lombok.Setter;
import social.network.model.Gender;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Getter
@Setter

public class UserSignUpAndUpdateRequestDto {
    @NotNull(message = "Please, enter your username!")
    @Size(min = 4, max = 20)
    private String username;
    @NotNull(message = "Please, enter your password!")
    @Size(max = 120)
    private String password;
    @NotNull(message = "Please, enter your name!")
    @Size(max = 20)
    private String name;
    @NotNull(message = "Please, enter your surname!")
    @Size(max = 40)
    private String surname;
    @NotNull(message = "Please, enter your patronymic!")
    @Size(max = 40)
    private String patronymic;
    @NotNull(message = "Please, enter your gender!")
    private Gender gender;
    @NotNull(message = "Please, enter your birthdate!")
    private LocalDate birthdate;
    @NotNull(message = "Please, enter your country!")
    private String country;
    @Size(max = 50)
    @NotNull(message = "Please, enter your email!")
    @Email
    private String email;

    public UserSignUpAndUpdateRequestDto() {
    }
}