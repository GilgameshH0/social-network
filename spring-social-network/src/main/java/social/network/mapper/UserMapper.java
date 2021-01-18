package social.network.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import social.network.dto.UserGetResponseDto;
import social.network.dto.UserSignUpAndUpdateRequestDto;
import social.network.model.User;

import java.util.Objects;

@Component
public class UserMapper {
    private final ModelMapper mapper;

    public UserMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public User toEntity(UserGetResponseDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, User.class);
    }

    public User toEntity(UserSignUpAndUpdateRequestDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, User.class);
    }

    public UserGetResponseDto toDto(User entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, UserGetResponseDto.class);
    }
}
