package social.network.exception;

public class WrongBearerException extends Exception{
    public WrongBearerException(String bearer) {
        super("Bearer is " + bearer + " wrong!");
    }
}
