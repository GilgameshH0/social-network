package social.network.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PostRequestDto {
    @NotNull(message = "Please, write name of group!")
    private String text;

    public PostRequestDto(@NotNull(message = "Please, write your text!") String text) {
        this.text = text;
    }

    public PostRequestDto() {
    }
}
