package social.network.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class MessageDTO {
    @NotEmpty(message = "Please, write your message!")
    private String text;
    @NotEmpty(message = "Please, enter user id, which you want to send the message!")
    private Long toUserId;
}
