import syntaxtree.*;
import vtable.VTable;
import symboltablebuilder.*;
import symboltable.*;
import exceptions.*;
import semanticanalysis.SemancticAnalysis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static final String RED_TEXT = "\u001B[31m";
    public static final String GREEN_TEXT = "\u001B[32m";
    public static final String RESET_TEXT = "\u001B[0m";

    public static void main(String[] args) throws Exception {

        if (args.length < 1){
            System.err.println("Usage: java Main [file1] [file2] ... [fileN]");
            System.exit(1);
        }

        FileInputStream fis = null;

        // create a symbol table builder object
        SymbolTableBuilder eval = new SymbolTableBuilder();

        // create a semantic analysis object
        SemancticAnalysis analysis = new SemancticAnalysis();

        // check all files given for analysis
        for (int i = 0; i < args.length; i++) {

            // boolean to check if the file passed the semantic analysis or not
            boolean hasPassed = false;

            // clear the symbol table if it is not the first file
            if (i != 0) {
                SymbolTable.reset();
                VTable.reset();
            }

            // try catch block to catch any errors
            try {

                // create a file input stream to read the file
                fis = new FileInputStream(args[i]);

                // create a parser object to parse the file
                MiniJavaParser parser = new MiniJavaParser(fis);

                // create a node object to store the parsed file
                Goal root = parser.Goal();

                // fill the symbol table with the parsed file
                root.accept(eval, null);

                // analyze the parsed file
                root.accept(analysis, null);

                // set the boolean to true, if we reach this point the file passed the semantic analysis
                hasPassed = true;
            }
            catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }
            catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            }
            catch (MultipleVariableDefition ex) {
                System.err.println(ex.getMessage());
            }
            catch (MultipleClassDefinition ex) {
                System.err.println(ex.getMessage());
            }
            catch (MultipleMethodDefinition ex) {
                System.err.println(ex.getMessage());
            }
            catch (ClassNotFound ex) {
                System.err.println(ex.getMessage());
            }
            catch (TypeMissMatch ex) {
                System.err.println(ex.getMessage());
            }
            catch (UnknownType ex) {
                System.err.println(ex.getMessage());
            }
            finally {
                try {
                    if(fis != null) fis.close();
                }
                catch (IOException ex){
                    System.err.println(ex.getMessage());
                }
            }

            // check if the file passed the semantic analysis
            // and print the appropriate message
            // if the file passed calculate the offsets for the class fields and methods and store them in the vtable
            if  (hasPassed) {
                System.out.println("File " + args[i] + " : " + GREEN_TEXT + "Pass" + RESET_TEXT);

                VTable.computeOffests();
                VTable.printTable();

                // calculate the offsets for the class fields and methods and store them in the vtable
                // SymbolTable.calculateOffsets();

                // // print the vtable
                // VTable.printOffsets();
            }
            else System.out.println("File " + args[i] + " : " + RED_TEXT + "Fail" + RESET_TEXT);
        }
    }
}
