package exceptions;

public class MultipleMethodDefinition extends Exception{

    String message;

    // returns multiple definition exception error
    public MultipleMethodDefinition(String message) {
        this.message = message;
    }
    public String getMessage() { return message; }
}
