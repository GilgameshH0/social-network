package social.network.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MessageSendRequestDto {
    @NotNull(message = "Please, write your message!")
    private String text;
    @NotNull(message = "Please, enter user id, which you want to send the message!")
    private Long toUserId;

    public MessageSendRequestDto() {
    }
}
