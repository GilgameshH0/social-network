package social.network.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupResponseDto {
    private Long id;
    private String name;

    public GroupResponseDto() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroupResponseDto groupResponseDto = (GroupResponseDto) obj;
        return id.equals(groupResponseDto.id);
    }
}
