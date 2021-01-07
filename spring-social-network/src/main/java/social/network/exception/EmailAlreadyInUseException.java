package social.network.exception;

public class EmailAlreadyInUseException extends Exception{
    public EmailAlreadyInUseException() {
        super("Email is already taken!");
    }
}
