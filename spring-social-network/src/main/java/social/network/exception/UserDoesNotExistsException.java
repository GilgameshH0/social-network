package social.network.exception;

public class UserDoesNotExistsException extends Exception{
    public UserDoesNotExistsException(Long id) {
        super("User with id:" + id + " does not exists!");
    }
}
