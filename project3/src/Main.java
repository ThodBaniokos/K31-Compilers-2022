import syntaxtree.*;
import offsets.*;
import symboltablebuilder.*;
import symboltable.*;
import exceptions.*;
import irgeneration.BuildTools;
import irgeneration.LLVMCodeGeneration;
import irgeneration.VTableBuilder;
import llfilebuilder.*;
import semanticanalysis.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class Main {

    public static final String redText = "\u001B[31m";
    public static final String greenText = "\u001B[32m";
    public static final String resetText = "\u001B[0m";
    public static final String outPath = "../llvm-output/";

    public static void main(String[] args) throws Exception {

        if (args.length < 1){
            System.err.println("Usage: java Main [file1] [file2] ... [fileN]");
            System.exit(1);
        }

        FileInputStream fis = null;

        // create a symbol table builder object
        SymbolTableBuilder eval = new SymbolTableBuilder();

        // create a semantic analysis object
        SemanticAnalysis analysis = new SemanticAnalysis();

        VTableBuilder vTableGenerator = new VTableBuilder();

        LLVMCodeGeneration codeGenerator = new LLVMCodeGeneration();

        // check all files given for analysis
        for (int i = 0; i < args.length; i++) {

            // boolean to check if the file passed the semantic analysis or not
            boolean hasPassed = false;

            // clear the symbol table, offsets and maps in build tools if it is not the first file
            if (i != 0) {
                SymbolTable.reset();
                Offsets.reset();
                BuildTools.resetMaps();
            }

            // try catch block to catch any errors
            try {

                // create a file input stream to read the file
                fis = new FileInputStream(args[i]);

                // create the .ll file string
                String llfile = LLFileBuilder.buildLLFile(args[i]);

                // create the output file
                PrintStream outFile = new PrintStream(outPath + llfile);

                // set the current output file to the ll file builder object
                BuildTools.outFile = outFile;

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

                // compute the offsets
                Offsets.computeOffests();

                // generate the llvm intermidiate code for the vtable
                root.accept(vTableGenerator, null);

                // generate the llvm intermidiate code for the program
                root.accept(codeGenerator, analysis);
            }
            catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
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
            catch (NullPointerException ex) {
                ex.printStackTrace();
                System.err.println(args[i] + " : " + ex.getMessage());
            }
            catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                System.err.println(args[i] + " : " + ex.getMessage());
            }
            finally {
                try {
                    if(fis != null) fis.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                    System.err.println(ex.getMessage());
                }
            }

            // check if the file passed the semantic analysis
            // and print the appropriate message
            // if the file passed calculate the offsets for the class fields and methods and store them in the vtable
            if (hasPassed) System.out.println("File " + args[i] + " : " + greenText + "Pass" + resetText);
            else System.out.println("File " + args[i] + " : " + redText + "Fail" + resetText);
        }
    }
}
