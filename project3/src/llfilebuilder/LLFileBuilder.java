package llfilebuilder;

import java.io.File;
import java.io.PrintStream;

public class LLFileBuilder {

    public static PrintStream outFile;

    public static String buildLLFile(String fileName) {

        // create a new file object
        File minijavaFile = new File(fileName);

        // build the string of the .ll file
        String llfile = minijavaFile.getName().substring(0, minijavaFile.getName().indexOf(".")) + ".ll";

        // return .ll file string
        return llfile;
    }
}
