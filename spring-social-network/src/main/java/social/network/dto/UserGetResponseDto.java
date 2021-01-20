package social.network.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import social.network.model.Gender;

import java.time.LocalDate;

@Getter
@Setter
public class UserGetResponseDto {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String patronymic;
    private Gender gender;
    private LocalDate birthdate;
    private String country;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserGetResponseDto userGetResponseDto = (UserGetResponseDto) obj;
        if (!username.equals(userGetResponseDto.username)) {
            return false;
        }
        return id.equals(userGetResponseDto.id);
    }
}
