package exceptions;

public class UnknownType extends Exception {

    String message;

    // returns multiple definition exception error
    public UnknownType(String message) {
        this.message = message;
    }
    public String getMessage() { return message; }
}
