package social.network.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class LogInUser {
    @NotEmpty(message = "Please, enter your username!")
    private String username;
    @NotEmpty(message = "Please, enter your password!")
    private String password;
}