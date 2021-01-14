package social.network.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String text;

    public PostResponseDto(String text) {
        this.text = text;
    }

    public PostResponseDto() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PostResponseDto postResponseDto = (PostResponseDto) obj;
        return id.equals(postResponseDto.id);
    }
}
