package social.network.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class GroupRequestDto {
    @NotNull(message = "Please, write name of group!")
    private String name;

    public GroupRequestDto(@NotNull(message = "Please, write name of group!") String name) {
        this.name = name;
    }

    public GroupRequestDto() {
    }
}
