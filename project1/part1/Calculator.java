// File: Calculator.java
// implementation of the bitwise & and ^ calculator using an LL(1) recursive descent parser
// imports below
import java.io.InputStream;
import java.io.IOException;

// Grammar
//
// 1. exp         -> xorterm exprest
// 2. exprest     -> ^ xorterm exprest
// 3.             | ε
// 4. xorterm     -> andfactor xortermrest
// 5. xortermrest -> & andfactor xortermrest
// 6.             | ε
// 7. andfactor   -> (exp)
// 8.             | num
// 9 - 18. num    -> '0'..'9'
//
// Parse table
//
// -------------------------------------------------------------------
// |                   EOF      ^       &       (       )      0..9  |
// -------------------------------------------------------------------
// |exp             |   e   |   e   |   e   |   #1  |   e   |   #1   |
// |exprest         |   #3  |   #2  |   e   |   e   |   #3  |   e    |
// |xorterm         |   e   |   e   |   e   |   #4  |   e   |   #4   |
// |xortermrest     |   #6  |   #6  |   #5  |   e   |   #6  |   e    |
// |andfactor       |       |       |       |   #7  |       |   #8   |
// |num             |   e   |   e   |   e   |   e   |   e   | #9-#18 |
// -------------------------------------------------------------------

class Calculator {

    // input stream
    private final InputStream input;

    // look ahead symbol for ll(1) parser
    private int lookahead;

    // constructor
    public Calculator(InputStream input) throws IOException {

        // initialize input stream
        this.input = input;
        this.lookahead = this.input.read();

        // ignore whitespace
        // while(this.lookahead == ' ' || this.lookahead == '\t') {
        //     this.lookahead = this.input.read();
        // }
    }

    // matches the rule exp -> xorterm exprest
    public int evaluate() throws IOException, ParseError {

        int result = -1;

        // result of the input
        if (isDigit(this.lookahead) || this.lookahead == '(') {
            result = exp();
        }

        if (this.lookahead != -1 && this.lookahead != '\n') {
            throw new ParseError();
        }

        return result;
    }

    // checks if symbol is a digit
    private boolean isDigit(int symbol) {
        return symbol >= '0' && symbol <= '9';
    }

    // return the actual value of the digit, since it's given in ascii code
    private int digitValue(int symbol) {
        return symbol - '0';
    }

    // consumes the given symbol, if it matches with the one we're expecting, from the input stream
    private void consume(int symbol) throws IOException, ParseError {
        if (this.lookahead == symbol) {
            this.lookahead = this.input.read();

            // ignore whitespace
            // while(this.lookahead == ' ' || this.lookahead == '\t') {
            //     this.lookahead = this.input.read();
            // }
        }
        else {
            throw new ParseError();
        }
    }

    // matches the rule exp -> xorterm exprest
    private int exp() throws IOException, ParseError {

        if (isDigit(this.lookahead) || this.lookahead == '(') {

            // calculate the xorterm value first
            // since exprest can be the empty derivation
            // if it is the result is xorterm
            int xorterm = xorterm();

            // return the result of the exprest
            return exprest(xorterm);
        }
        else {
            throw new ParseError();
        }

    }

    // matches the rule xorterm -> andfactor xortermrest
    private int xorterm() throws IOException, ParseError {

        if (isDigit(this.lookahead) || this.lookahead == '(') {

            // calculate the andfactor value first
            // since xortermrest can be the empty derivation
            // if it is the result is andfactor
            int andfactor = andfactor();

            // return the result of the xortermrest
            return xortermrest(andfactor);
        }
        else {
            throw new ParseError();
        }
    }

    // matches the rule exprest -> ^ xorterm exprest
    //                          | ε
    private int exprest(int value) throws IOException, ParseError {

        if (this.lookahead == '^') {

            // consume the symbol
            consume('^');

            // calculate the xorterm value first
            // since exprest can be the empty derivation
            // if it is the result is xorterm
            int xorterm = xorterm();

            // return the result of the exprest
            int value2 = exprest(xorterm);

            // calculate the xor of between value and value2 and return it as the result
            return value ^ value2;
        }
        else if (this.lookahead == '\n' || this.lookahead == -1 || this.lookahead == ')') {

            // empty derivation, return the value as result
            return value;
        }
        else {
            throw new ParseError();
        }
    }

    // matches the rule andfactor   -> (exp)
    //                              | num
    private int andfactor() throws IOException, ParseError {

        if (this.lookahead == '(') {

            // consume the symbol
            consume('(');

            // calculate exp value
            int exp = exp();

            // consume the symbol
            consume(')');

            // return the result of exp
            return exp;
        }
        else if (isDigit(this.lookahead)) {

            // consume the symbol
            int num = digitValue(this.lookahead);
            consume(this.lookahead);

            return num;
        }
        else {
            throw new ParseError();
        }
    }

    // matches the rule xortermrest -> & andfactor xortermrest
    //                              | ε
    private int xortermrest(int value) throws IOException, ParseError {

        if (this.lookahead == '&') {

            // consume the symbol
            consume('&');

            // calculate the andfactor value first
            // since xortermrest can be the empty derivation
            // if it is the result is andfactor
            int andfactor = andfactor();

            // return the result of the xortermrest
            int value2 = xortermrest(andfactor);

            // calculate the and of between value and value2 and return it as the result
            return value & value2;
        }
        else if (this.lookahead == '\n' || this.lookahead == -1 || this.lookahead == ')' || this.lookahead == '^') {

            // empty derivation, return the value as result
            return value;
        }
        else {
            throw new ParseError();
        }
    }
}