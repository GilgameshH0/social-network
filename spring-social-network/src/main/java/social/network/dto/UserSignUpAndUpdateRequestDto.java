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
    private String username;
    @Size(max = 120)
    private String password;
    @Size(max = 20)
    private String name;
    @Size(max = 40)
    private String surname;
    @Size(max = 40)
    private String patronymic;
    private Gender gender;
    private LocalDate birthdate;
    private String country;
    private String email;

    public UserSignUpAndUpdateRequestDto() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserSignUpAndUpdateRequestDto user = (UserSignUpAndUpdateRequestDto) obj;
        if (!username.equals(user.username)) {
            return false;
        }
        return password.equals(user.password);
    }
}