package irgeneration;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import symboltable.ClassInformation;
import symboltable.CustomPair;
import symboltable.MethodInformation;

public class BuildTools {

    // output file
    public static PrintStream outFile;

    // label counter
    public static int labelCounter;

    // register counter
    public static int registerCounter;

    // register name to register information object map
    public static Map<String, RegisterInformation> registerMap = new HashMap<>();

    // vtable for classes
    public static Map<CustomPair<ClassInformation, MethodInformation>, Integer> vtalbeMap = new HashMap<>();

    // vtable type for classes
    public static Map<ClassInformation, String> classVtableType = new HashMap<>();

    // method signature in vtable map
    public static Map<CustomPair<ClassInformation, MethodInformation>, String> methodSignatureInVtableMap = new HashMap<>();

    // build the .ll file name from the .java file name
    public static String buildLLFile(String fileName) {

        // create a new file object
        File minijavaFile = new File(fileName);

        // build the string of the .ll file
        String llfile = minijavaFile.getName().substring(0, minijavaFile.getName().indexOf(".")) + ".ll";

        // return .ll file string
        return llfile;
    }

    // emit llvm code to the .ll file
    public static void emit(String text) { outFile.append(text + "\n"); }

    // create new register
    public static String newRegister(String registerType) {

        // create the register name
        String register = "%_" + registerCounter;

        // increment the register counter
        registerCounter++;

        // create a new register information object
        RegisterInformation registerInfo = new RegisterInformation();

        // set register name and register type
        registerInfo.setRegisterName(register);
        registerInfo.setRegisterType(registerType);

        // add the register information object to the map
        registerMap.put(register, registerInfo);

        // return the name
        return register;
    }

    // create new label
    public static String newLabel(String labelName, boolean doIncrement) {
        // create the label name
        String label = labelName + labelCounter;

        if (doIncrement) {

            // increment the label counter
            labelCounter++;
        }

        // return the label name
        return label;
    }

    // trivial emits to .ll file
    public static void addStandardDeclarations() {

        // emit a new line to the .ll file
        // pretty printing
        emit("\n");

        // declare the calloc function
        emit("declare i8* @calloc(i32, i32)");

        // declare the printf function
        emit("declare i32 @printf(i8*, ...)");

        // declare the exit function
        emit("declare void @exit(i32)");

        // emit a new line to the .ll file
        // pretty printing
        emit("\n");
    }

    public static void addStandardStrings() {

        // emit the string for int printing
        emit("@_cint = constant [4 x i8] c\"%d\\0a\\00\"");

        // emit the string for index out of bound error
        emit("@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"");

        // emit the string for negative array size error
        emit("@_cNSZ = constant [21 x i8] c\"Negative Array Size\\0a\\00\"");

        // emit a new line to the .ll file
        // pretty printing
        emit("\n");
    }

    public static void addPrintFunction() {

        // emit the function signature for the print function
        emit("define void @print_int(i32 %i) {");

        // emit every line seperated by a new line
        emit("\t%_str = bitcast [4 x i8]* @_cint to i8*");
        emit("\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)");
        emit("\tret void");

        // emit the function end tag
        emit("}");

        // emit a new line to the .ll file
        // pretty printing
        emit("\n");
    }

    public static void addOOBFunction() {

        // emit the function signature for the print function
        emit("define void @throw_oob() {");

        // emit every line seperated by a new line
        emit("\t%_str = bitcast [15 x i8]* @_cOOB to i8*");
        emit("\tcall i32 (i8*, ...) @printf(i8* %_str)");
        emit("\tcall void @exit(i32 1)");
        emit("\tret void");

        // emit the function end tag
        emit("}");

        // emit a new line to the .ll file
        // pretty printing
        emit("\n");
    }

    public static void addNSZFunction() {

        // emit the function signature for the print function
        emit("define void @throw_nsz() {");

        // emit every line seperated by a new line
        emit("\t%_str = bitcast [21 x i8]* @_cNSZ to i8*");
        emit("\tcall i32 (i8*, ...) @printf(i8* %_str)");
        emit("\tcall void @exit(i32 1)");
        emit("\tret void");

        // emit the function end tag
        emit("}");

        // emit a new line to the .ll file
        // pretty printing
        emit("\n");
    }

    public static void makeAllTrivialEmits() {

        // invokes all the methods above
        addStandardDeclarations();
        addStandardStrings();
        addPrintFunction();
        addOOBFunction();
        addNSZFunction();

    }

    // reset the register and label counters
    public static void resetCounters() {
        labelCounter = 0;
        registerCounter = 0;
    }

    // reset the three maps and prepare for a new compilation
    public static void resetMaps() {
        registerMap.clear();
        vtalbeMap.clear();
        classVtableType.clear();
    }

    // get the signature of the method
    public static String getMethodSignature(ClassInformation _class, MethodInformation method) {

        // create a new pair of class and method to use as a key for the map
        CustomPair<ClassInformation, MethodInformation> key = new CustomPair<>(_class, method);

        // return the signature
        return methodSignatureInVtableMap.get(key);
    }
}
