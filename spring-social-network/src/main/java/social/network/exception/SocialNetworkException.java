package social.network.exception;

import social.network.model.ErrorCode;

public class SocialNetworkException extends Exception{
    private final ErrorCode errorCode;
    private final String message;

    public SocialNetworkException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
