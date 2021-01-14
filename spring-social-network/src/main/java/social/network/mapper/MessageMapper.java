package social.network.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import social.network.dto.MessageSendRequestDto;
import social.network.model.Message;

import java.util.Objects;

@Component
public class MessageMapper {
    private final ModelMapper mapper;

    public MessageMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Message toEntity(MessageSendRequestDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, Message.class);
    }

    public MessageSendRequestDto toDto(Message entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, MessageSendRequestDto.class);
    }
}
