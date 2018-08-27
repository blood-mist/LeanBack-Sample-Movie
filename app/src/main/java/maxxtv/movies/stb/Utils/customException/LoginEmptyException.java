package maxxtv.movies.stb.Utils.customException;

public class LoginEmptyException extends Exception {
    public LoginEmptyException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public LoginEmptyException(String message, String exceptionMessage) {
        super(message);
        this.exceptionMessage = exceptionMessage;
    }

    public LoginEmptyException(String message, Throwable cause, String exceptionMessage) {
        super(message, cause);
        this.exceptionMessage = exceptionMessage;
    }

    public LoginEmptyException(Throwable cause, String exceptionMessage) {
        super(cause);
        this.exceptionMessage = exceptionMessage;
    }

    private String exceptionMessage;

}
