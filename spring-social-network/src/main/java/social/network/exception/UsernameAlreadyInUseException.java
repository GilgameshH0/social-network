package social.network.exception;

public class UsernameAlreadyInUseException extends Exception{
    public UsernameAlreadyInUseException(String username) {
        super("Username " + username + " is already taken!");
    }
}
