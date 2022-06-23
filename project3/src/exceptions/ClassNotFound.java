package exceptions;

public class ClassNotFound extends Exception{

    String message;

    // returns class not found exception error
    public ClassNotFound(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
