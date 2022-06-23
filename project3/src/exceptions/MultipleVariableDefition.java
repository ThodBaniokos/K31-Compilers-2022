package exceptions;

public class MultipleVariableDefition extends Exception {

    String message;

    // returns multiple definition exception error
    public MultipleVariableDefition(String message) {
        this.message = message;
    }
    public String getMessage() { return message; }
}
