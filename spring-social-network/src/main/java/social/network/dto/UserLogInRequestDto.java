package social.network.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserLogInRequestDto {
    @NotNull(message = "Please, enter your username!")
    private String username;
    @NotNull(message = "Please, enter your password!")
    private String password;

    public UserLogInRequestDto() {
    }
}