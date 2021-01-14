package social.network.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import social.network.dto.GroupResponseDto;
import social.network.model.Group;

import java.util.Objects;

@Component
public class GroupMapper {
    private final ModelMapper mapper;

    public GroupMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Group toEntity(Group dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, Group.class);
    }

    public GroupResponseDto toDto(Group entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, GroupResponseDto.class);
    }
}
