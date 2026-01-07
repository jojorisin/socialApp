package se.jensen.johanna.socialapp.exception;

public class PasswordMisMatchException extends RuntimeException {
    public PasswordMisMatchException(String message) {
        super(message);
    }

    public PasswordMisMatchException() {
        super("Password has to match.");
    }
}
