
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20141202 (SVN rev 60)
//----------------------------------------------------

import java_cup.runtime.*;
import java_cup.runtime.XMLElement;

/** CUP v0.11b 20141202 (SVN rev 60) generated parser.
  */
@SuppressWarnings({"rawtypes"})
public class Parser extends java_cup.runtime.lr_parser {

 public final Class getSymbolContainer() {
    return sym.class;
}

  /** Default constructor. */
  public Parser() {super();}

  /** Constructor which sets the default scanner. */
  public Parser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner. */
  public Parser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\031\000\002\002\004\000\002\002\004\000\002\002" +
    "\003\000\002\002\002\000\002\004\004\000\002\004\003" +
    "\000\002\003\004\000\002\003\003\000\002\005\010\000" +
    "\002\005\007\000\002\006\005\000\002\006\003\000\002" +
    "\007\006\000\002\007\005\000\002\010\005\000\002\010" +
    "\003\000\002\011\003\000\002\012\003\000\002\013\013" +
    "\000\002\014\003\000\002\014\003\000\002\014\003\000" +
    "\002\014\005\000\002\014\003\000\002\014\004" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\060\000\014\002\ufffe\004\011\006\016\016\013\017" +
    "\007\001\002\000\032\002\uffed\004\uffed\005\uffed\006\uffed" +
    "\007\uffed\010\uffed\012\uffed\013\uffed\014\uffed\015\uffed\016" +
    "\uffed\017\uffed\001\002\000\032\002\uffea\004\uffea\005\uffea" +
    "\006\uffea\007\uffea\010\uffea\012\uffea\013\uffea\014\uffea\015" +
    "\uffea\016\uffea\017\uffea\001\002\000\004\002\062\001\002" +
    "\000\032\002\uffee\004\uffee\005\uffee\006\uffee\007\uffee\010" +
    "\uffee\012\uffee\013\uffee\014\uffee\015\uffee\016\uffee\017\uffee" +
    "\001\002\000\012\004\011\006\016\016\013\017\007\001" +
    "\002\000\004\011\050\001\002\000\004\002\uffff\001\002" +
    "\000\020\002\uffec\004\uffec\006\uffec\011\034\015\uffec\016" +
    "\uffec\017\uffec\001\002\000\012\004\ufffa\006\ufffa\016\ufffa" +
    "\017\ufffa\001\002\000\016\002\ufffc\004\011\006\016\015" +
    "\021\016\017\017\007\001\002\000\012\004\011\006\016" +
    "\016\017\017\007\001\002\000\034\002\uffec\004\uffec\005" +
    "\uffec\006\uffec\007\uffec\010\uffec\011\023\012\uffec\013\uffec" +
    "\014\uffec\015\uffec\016\uffec\017\uffec\001\002\000\032\002" +
    "\uffe9\004\uffe9\005\uffe9\006\uffe9\007\uffe9\010\uffe9\012\uffe9" +
    "\013\uffe9\014\uffe9\015\uffe9\016\uffe9\017\uffe9\001\002\000" +
    "\012\004\011\006\016\016\017\017\007\001\002\000\032" +
    "\002\uffeb\004\uffeb\005\uffeb\006\uffeb\007\uffeb\010\uffeb\012" +
    "\uffeb\013\uffeb\014\uffeb\015\uffeb\016\uffeb\017\uffeb\001\002" +
    "\000\014\004\011\006\016\012\025\016\017\017\007\001" +
    "\002\000\004\012\032\001\002\000\032\002\ufff4\004\ufff4" +
    "\005\ufff4\006\ufff4\007\ufff4\010\ufff4\012\ufff4\013\ufff4\014" +
    "\ufff4\015\ufff4\016\ufff4\017\ufff4\001\002\000\012\012\ufff1" +
    "\013\ufff1\014\ufff1\015\021\001\002\000\006\012\ufff2\014" +
    "\030\001\002\000\012\004\011\006\016\016\017\017\007" +
    "\001\002\000\004\012\ufff3\001\002\000\032\002\ufff5\004" +
    "\ufff5\005\ufff5\006\ufff5\007\ufff5\010\ufff5\012\ufff5\013\ufff5" +
    "\014\ufff5\015\ufff5\016\ufff5\017\ufff5\001\002\000\004\002" +
    "\ufffd\001\002\000\016\004\011\006\016\012\025\013\036" +
    "\016\017\017\007\001\002\000\004\013\045\001\002\000" +
    "\012\004\011\006\016\016\017\017\007\001\002\000\010" +
    "\012\ufff2\013\ufff6\014\040\001\002\000\012\004\011\006" +
    "\016\016\017\017\007\001\002\000\004\013\ufff7\001\002" +
    "\000\004\010\044\001\002\000\006\010\ufff0\015\021\001" +
    "\002\000\012\004\ufff8\006\ufff8\016\ufff8\017\ufff8\001\002" +
    "\000\012\004\011\006\016\016\017\017\007\001\002\000" +
    "\004\010\047\001\002\000\012\004\ufff9\006\ufff9\016\ufff9" +
    "\017\ufff9\001\002\000\012\004\011\006\016\016\017\017" +
    "\007\001\002\000\006\007\052\015\021\001\002\000\012" +
    "\004\011\006\016\016\017\017\007\001\002\000\006\012" +
    "\054\015\021\001\002\000\012\004\011\006\016\016\017" +
    "\017\007\001\002\000\006\005\056\015\021\001\002\000" +
    "\012\004\011\006\016\016\017\017\007\001\002\000\032" +
    "\002\uffef\004\uffef\005\uffef\006\uffef\007\uffef\010\uffef\012" +
    "\uffef\013\uffef\014\uffef\015\021\016\uffef\017\uffef\001\002" +
    "\000\004\002\000\001\002\000\012\004\ufffb\006\ufffb\016" +
    "\ufffb\017\ufffb\001\002\000\004\002\001\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\060\000\020\002\005\003\007\004\011\005\013\007" +
    "\003\013\004\014\014\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\014\004" +
    "\057\005\060\007\003\013\004\014\014\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\012\004\032\007\003\013\004\014\014\001\001" +
    "\000\010\007\003\013\004\014\017\001\001\000\002\001" +
    "\001\000\002\001\001\000\010\007\003\013\004\014\021" +
    "\001\001\000\002\001\001\000\014\007\003\010\023\011" +
    "\026\013\004\014\025\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\014\007" +
    "\003\010\030\011\026\013\004\014\025\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\016\006" +
    "\034\007\003\010\023\011\036\013\004\014\025\001\001" +
    "\000\002\001\001\000\012\007\003\012\041\013\004\014" +
    "\042\001\001\000\002\001\001\000\016\006\040\007\003" +
    "\010\030\011\036\013\004\014\025\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\012\007\003\012\045\013\004\014\042\001\001\000" +
    "\002\001\001\000\002\001\001\000\010\007\003\013\004" +
    "\014\050\001\001\000\002\001\001\000\010\007\003\013" +
    "\004\014\052\001\001\000\002\001\001\000\010\007\003" +
    "\013\004\014\054\001\001\000\002\001\001\000\010\007" +
    "\003\013\004\014\056\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$Parser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$Parser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$Parser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 0;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}


  /** Scan to get the next Symbol. */
  public java_cup.runtime.Symbol scan()
    throws java.lang.Exception
    {
 return s.next_token(); 
    }


    // Connect this parser to a scanner!
    Scanner s;
    Parser(Scanner s){ this.s=s; }


/** Cup generated class to encapsulate user supplied action code.*/
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
class CUP$Parser$actions {
  private final Parser parser;

  /** Constructor */
  CUP$Parser$actions(Parser parser) {
    this.parser = parser;
  }

  /** Method 0 with the actual generated action code for actions 0 to 300. */
  public final java_cup.runtime.Symbol CUP$Parser$do_action_part00000000(
    int                        CUP$Parser$act_num,
    java_cup.runtime.lr_parser CUP$Parser$parser,
    java.util.Stack            CUP$Parser$stack,
    int                        CUP$Parser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$Parser$result;

      /* select the action based on the action number */
      switch (CUP$Parser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // $START ::= program EOF 
            {
              Object RESULT =null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object start_val = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		RESULT = start_val;
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("$START",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          /* ACCEPT */
          CUP$Parser$parser.done_parsing();
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // program ::= method_decl_list main_body 
            {
              Object RESULT =null;
		int mdlleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int mdlright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object mdl = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		int mbleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int mbright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object mb = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		  System.out.println("public class Main {");
                                                            System.out.println("\tpublic static void main(String[] args) {");
                                                            System.out.println("\t\t" + mb);
                                                            System.out.println("\t}");
                                                            System.out.println("\n\t" + mdl);
                                                            System.out.println("}"); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("program",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // program ::= main_body 
            {
              Object RESULT =null;
		int mbleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int mbright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object mb = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		  System.out.println("public class Main {");
                                                            System.out.println("\tpublic static void main(String[] args) {");
                                                            System.out.println("\t\t" + mb);
                                                            System.out.println("\t}");
                                                            System.out.println("}"); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("program",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // program ::= 
            {
              Object RESULT =null;
		  System.out.println("public class Main {");
                                                            System.out.println("\tpublic static void main(String[] args) {");
                                                            System.out.println("\t}");
                                                            System.out.println("}"); 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("program",0, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // main_body ::= expr main_body 
            {
              Object RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object e = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		int mbleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int mbright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object mb = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = "System.out.println(" + e + ");\n\t\t" + mb; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("main_body",2, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // main_body ::= expr 
            {
              Object RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object e = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		  RESULT = "System.out.println(" + e + ");"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("main_body",2, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // method_decl_list ::= method_decl_list method_decl 
            {
              Object RESULT =null;
		int mdlleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int mdlright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object mdl = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		int mdleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int mdright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object md = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = mdl + "\n" + "\t" + md; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("method_decl_list",1, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // method_decl_list ::= method_decl 
            {
              Object RESULT =null;
		int mdleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int mdright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object md = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = md; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("method_decl_list",1, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // method_decl ::= IDENTIFIER LPAREN dec_arg_list METHOD_DEC method_body RBRACE 
            {
              Object RESULT =null;
		int idleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-5)).left;
		int idright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-5)).right;
		Object id = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-5)).value;
		int dalleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).left;
		int dalright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).right;
		Object dal = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-3)).value;
		int mbleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int mbright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object mb = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = "public static String " + id + "(" + dal + ") {\n\t\t" + mb + "\n\t}\n"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("method_decl",3, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-5)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // method_decl ::= IDENTIFIER LPAREN METHOD_DEC method_body RBRACE 
            {
              Object RESULT =null;
		int idleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-4)).left;
		int idright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-4)).right;
		Object id = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-4)).value;
		int mbleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int mbright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object mb = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = "public static String " + id + "() {\n\t\t" + mb + "\n\t}\n"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("method_decl",3, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-4)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 10: // dec_arg_list ::= arg COMMA dec_arg_list 
            {
              Object RESULT =null;
		int daleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int daright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Object da = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int dalleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int dalright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object dal = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = "String " + da + ", " + dal; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("dec_arg_list",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 11: // dec_arg_list ::= arg 
            {
              Object RESULT =null;
		int daleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int daright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object da = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = "String " + da; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("dec_arg_list",4, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 12: // method_call ::= IDENTIFIER LPAREN call_arg_list RPAREN 
            {
              Object RESULT =null;
		int idleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).left;
		int idright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)).right;
		Object id = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-3)).value;
		int calleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int calright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object cal = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		 RESULT = id + "(" + cal + ")"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("method_call",5, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-3)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 13: // method_call ::= IDENTIFIER LPAREN RPAREN 
            {
              Object RESULT =null;
		int idleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int idright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Object id = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		 RESULT = id + "()"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("method_call",5, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 14: // call_arg_list ::= arg COMMA call_arg_list 
            {
              Object RESULT =null;
		int aleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int aright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Object a = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int calleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int calright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object cal = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = a + ", " + cal; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_arg_list",6, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 15: // call_arg_list ::= arg 
            {
              Object RESULT =null;
		int aleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int aright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object a = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = a; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("call_arg_list",6, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 16: // arg ::= expr 
            {
              Object RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object e = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = e; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("arg",7, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 17: // method_body ::= expr 
            {
              Object RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object e = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = "return " + e + ";"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("method_body",8, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 18: // if_stmt ::= IF LPAREN expr PREFIX expr RPAREN expr ELSE expr 
            {
              Object RESULT =null;
		int e1left = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-6)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-6)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-6)).value;
		int e2left = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-4)).left;
		int e2right = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-4)).right;
		Object e2 = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-4)).value;
		int ibleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int ibright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Object ib = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int ebleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int ebright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object eb = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = "(" + e2 + ".startsWith(" + e1 + ") ? " + ib + " : " + eb + ")"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("if_stmt",9, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-8)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 19: // expr ::= STRING_LITERAL 
            {
              Object RESULT =null;
		int sleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int sright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		String s = (String)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = "\"" + s + "\""; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr",10, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 20: // expr ::= method_call 
            {
              Object RESULT =null;
		int mcleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int mcright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object mc = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = mc; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr",10, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 21: // expr ::= IDENTIFIER 
            {
              Object RESULT =null;
		int idleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int idright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object id = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = id; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr",10, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 22: // expr ::= expr CONCAT expr 
            {
              Object RESULT =null;
		int e1left = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		Object e1 = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int e2left = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int e2right = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object e2 = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = e1 + " + " + e2; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr",10, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 23: // expr ::= if_stmt 
            {
              Object RESULT =null;
		int isleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int isright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object is = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = is; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr",10, ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 24: // expr ::= REVERSE expr 
            {
              Object RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()).right;
		Object e = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.peek()).value;
		 RESULT = "new StringBuilder(" + e + ").reverse().toString()"; 
              CUP$Parser$result = parser.getSymbolFactory().newSymbol("expr",10, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)), ((java_cup.runtime.Symbol)CUP$Parser$stack.peek()), RESULT);
            }
          return CUP$Parser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number "+CUP$Parser$act_num+"found in internal parse table");

        }
    } /* end of method */

  /** Method splitting the generated action code into several parts. */
  public final java_cup.runtime.Symbol CUP$Parser$do_action(
    int                        CUP$Parser$act_num,
    java_cup.runtime.lr_parser CUP$Parser$parser,
    java.util.Stack            CUP$Parser$stack,
    int                        CUP$Parser$top)
    throws java.lang.Exception
    {
              return CUP$Parser$do_action_part00000000(
                               CUP$Parser$act_num,
                               CUP$Parser$parser,
                               CUP$Parser$stack,
                               CUP$Parser$top);
    }
}

}
