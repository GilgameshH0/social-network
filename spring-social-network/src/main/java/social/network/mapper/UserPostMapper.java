package social.network.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import social.network.dto.PostResponseDto;
import social.network.model.UserPost;

import java.util.Objects;

@Component
public class UserPostMapper {
    private final ModelMapper mapper;

    public UserPostMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public UserPost toEntity(PostResponseDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, UserPost.class);
    }

    public PostResponseDto toDto(UserPost entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, PostResponseDto.class);
    }
}