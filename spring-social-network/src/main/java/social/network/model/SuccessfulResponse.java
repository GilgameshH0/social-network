package social.network.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class SuccessfulResponse {
    private final HttpStatus httpStatus = HttpStatus.OK;
    private final String message;
    public SuccessfulResponse(String message) {
        this.message = message;
    }
}
