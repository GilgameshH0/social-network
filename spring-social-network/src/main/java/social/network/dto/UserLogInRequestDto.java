package social.network.dto;

import lombok.Getter;
import lombok.Setter;
import social.network.model.User;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserLogInRequestDto user = (UserLogInRequestDto) obj;
        if (!username.equals(user.username)) {
            return false;
        }
        return password.equals(user.password);
    }
}