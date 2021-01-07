package social.network.exception;

public class AlreadySentFriendRequestException extends Exception{

    public AlreadySentFriendRequestException() {
        super("you are already sent friend request to this user!");
    }
}
