package social.network.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import social.network.dto.PostResponseDto;
import social.network.model.GroupPost;

import java.util.Objects;

@Component
public class GroupPostMapper {
    private final ModelMapper mapper;

    public GroupPostMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public GroupPost toEntity(PostResponseDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, GroupPost.class);
    }

    public PostResponseDto toDto(GroupPost entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, PostResponseDto.class);
    }
}
