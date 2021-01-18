package social.network.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {SocialNetworkException.class})
    public ResponseEntity<Object> socialNetworkExceptionHandler(SocialNetworkException socialNetworkException) {

        switch (socialNetworkException.getErrorCode()) {
            case UsernameOrEmailAlreadyInUse, AlreadySentFriendRequest -> {
                HttpStatus status = HttpStatus.CONFLICT;
                ApiException apiException = new ApiException(
                        socialNetworkException.getMessage(),
                        socialNetworkException,
                        status,
                        LocalDateTime.now());
                return new ResponseEntity<>(apiException, status);
            }
            case WrongBearer -> {
                HttpStatus status = HttpStatus.UNAUTHORIZED;
                ApiException apiException = new ApiException(
                        socialNetworkException.getMessage(),
                        socialNetworkException,
                        status,
                        LocalDateTime.now());
                return new ResponseEntity<>(apiException, status);
            }
            case UserDoesNotExists, GroupDoesNotExist, PostDoesNotExists, CountryDoesNotExists -> {
                HttpStatus status = HttpStatus.NOT_FOUND;
                ApiException apiException = new ApiException(
                        socialNetworkException.getMessage(),
                        socialNetworkException,
                        status,
                        LocalDateTime.now());
                return new ResponseEntity<>(apiException, status);
            }
            case YouAreNotOwner -> {
                HttpStatus status = HttpStatus.FORBIDDEN;
                ApiException apiException = new ApiException(
                        socialNetworkException.getMessage(),
                        socialNetworkException,
                        status,
                        LocalDateTime.now());
                return new ResponseEntity<>(apiException, status);
            }
            default -> throw new IllegalStateException("Unexpected value: " + socialNetworkException.getErrorCode());
        }
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<Object> authenticationExceptionHandler(AuthenticationException authenticationException) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ApiException apiException = new ApiException(
                authenticationException.getMessage(),
                authenticationException,
                status,
                LocalDateTime.now());
        return new ResponseEntity<>(apiException, status);
    }
}
