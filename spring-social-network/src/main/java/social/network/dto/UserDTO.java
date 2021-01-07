package social.network.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class UserDTO {
    private String username;
    private String name;
    private String surname;
    private String patronymic;
    private int gender;
    private LocalDate birthdate;
    private String country;
}
