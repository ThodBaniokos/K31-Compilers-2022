package exceptions;

public class TypeMissMatch extends Exception {

    String message;

    // returns multiple definition exception error
    public TypeMissMatch(String message) {
        this.message = message;
    }
    public String getMessage() { return message; }
}