package exceptions;

public class MultipleClassDefinition extends Exception {

    String message;

    // returns multiple definition exception error
    public MultipleClassDefinition(String message) {
        this.message = message;
    }
    public String getMessage() { return message; }
}
