// File: Main.java
// bitwise & and ^ calculator using an LL(1) recursive descent parser

// imports below
import java.io.IOException;

class Main {
    public static void main(String[] args) throws IOException {

        // instantiate the calculator
        Calculator calc = new Calculator(System.in);

        // try catch block for parse error handling
        try {
            System.out.println(calc.evaluate());
        }
        catch (IOException | ParseError e) {
            System.err.println(e.getMessage());
        }

    }
}
