import java_cup.runtime.*;

%%
/* ----------------- Options and Declarations Section----------------- */

/*
   The name of the class JFlex will create will be Scanner.
   Will write the code to the file Scanner.java.
*/
%class Scanner
// %debug

/*
  The current line number can be accessed with the variable yyline
  and the current column number with the variable yycolumn.
*/
%line
%column

/*
   Will switch to a CUP compatibility mode to interface with a CUP
   generated parser.
*/
%cup
%unicode

/*
  Declarations

  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.
*/

%{
    StringBuffer stringBuffer = new StringBuffer();

    /**
        The following two methods create java_cup.runtime.Symbol objects
    **/
    private Symbol symbol(int type) {
       return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/*
  Macro Declarations

  These declarations are regular expressions that will be used latter
  in the Lexical Rules Section.
*/

/* A line terminator is a \r (carriage return), \n (line feed), or
   \r\n. */
LineTerminator = \r|\n|\r\n

/* White space is a line terminator, space, tab, or line feed. */
WhiteSpace     = {LineTerminator} | [ \t\f]

/*
   An identifier is a sequence of letters, digits, or underscores.
   Must start with an underscore or a letter, either upper or lower case
*/

identifier    = [a-zA-Z_][a-zA-Z_0-9]*

/* A literal integer is is a number beginning with a number between
   one and nine followed by zero or more numbers between zero and nine
   or just a zero.  */

%state STRING

%%
/* ------------------------Lexical Rules Section---------------------- */

<YYINITIAL> {
/* keywords */
"if"      { return symbol(sym.IF); }
"else"    { return symbol(sym.ELSE); }
"prefix"  { return symbol(sym.PREFIX); }
"reverse" { return symbol(sym.REVERSE); }

/* seperators */
 ")" + {WhiteSpace} + "{" { return symbol(sym.METHOD_DEC); }
 "}"      { return symbol(sym.RBRACE); }
 "("      { return symbol(sym.LPAREN); }
 ")"      { return symbol(sym.RPAREN); }
 ","      { return symbol(sym.COMMA); }

/* operators */
 "+"      { return symbol(sym.CONCAT); }

/* string literal is starting with a double quote */
 \"             { stringBuffer.setLength(0); yybegin(STRING); }
 {identifier}   { return symbol(sym.IDENTIFIER, yytext()); }
 {WhiteSpace}   { /* just skip what was found, do nothing */ }
}


<STRING> {
      \"                             { yybegin(YYINITIAL);
                                       return symbol(sym.STRING_LITERAL, stringBuffer.toString()); }
      [^\n\r\"\\]+                   { stringBuffer.append( yytext() ); }
      \\t                            { stringBuffer.append('\t'); }
      \\n                            { stringBuffer.append('\n'); }

      \\r                            { stringBuffer.append('\r'); }
      \\\"                           { stringBuffer.append('\"'); }
      \\                             { stringBuffer.append('\\'); }
}

/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }
