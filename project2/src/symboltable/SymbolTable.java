package symboltable;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

public class SymbolTable {

    /**
     * Clears all the maps in the symbol table to make room for the next file to be analized.
     */
    public static void reset() {

        // clear all the maps in this symbol table class
        classesMap.clear();
        classInheritenceMap.clear();
        classMethodsMap.clear();
        classFieldsMap.clear();
        classMethodTypeMap.clear();
        classFieldTypeMap.clear();
        methodParametersMap.clear();
        methodVariablesMap.clear();
        methodParameterTypeMap.clear();
        methodVariableTypeMap.clear();

    }

    /**
     * Class name to class information object map
     * Usage : class name (String) -> class information (ClassInformation Object)
     */
    public static Map<String, ClassInformation> classesMap = new LinkedHashMap<>();

    /**
     * Derived to super class map
     * Usage : class information (ClassInformation object) -> (ClassInformation object)
     */
    public static Map<ClassInformation, ClassInformation> classInheritenceMap = new LinkedHashMap<>();

    /**
     * Class information to method information map
     * Usage : class information (ClassInformation object) -> method information (MethodInformation object)
     */
    public static Map<ClassInformation, List<MethodInformation>> classMethodsMap = new LinkedHashMap<>();

    /**
     * Class information to field information map
     * Usage : class information (ClassInformation object) -> field information (FieldInformation object)
     */
    public static Map<ClassInformation, List<VariableInformation>> classFieldsMap = new LinkedHashMap<>();

    /**
     * Class method to type map
     * Usage : class information (ClassInformation object), method information (MethodInformation object) -> type (String)
     */
    public static Map<CustomPair<ClassInformation, MethodInformation>, String> classMethodTypeMap = new LinkedHashMap<>();

    /**
     * Class field to type map
     * Usage : class information (ClassInformation object), field information (VariableInformation object) -> type (String)
     */
    public static Map<CustomPair<ClassInformation, VariableInformation>, String> classFieldTypeMap = new LinkedHashMap<>();

    /**
     * method parameters map
     * Usage : class information (ClassInformation object), method information (MethodInformation object) -> list of parameterers (String)
     */
    public static Map<CustomPair<ClassInformation, MethodInformation>, List<VariableInformation>> methodParametersMap = new LinkedHashMap<>();

    /**
     * method variables map
     * Usage : class information (ClassInformation object), method information (MethodInformation object) -> list of variables (String)
     */
    public static Map<CustomPair<ClassInformation, MethodInformation>, List<VariableInformation>> methodVariablesMap = new LinkedHashMap<>();

    /**
     * Method parameter type map
     * Usage : (class information (ClassInformation object), method information (MethodInformation object)), parameter (VariableInformation) -> type (String)
     */
    public static Map<CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation>, String> methodParameterTypeMap = new LinkedHashMap<>();

    /**
     * Method variable type map
     * Usage : (class information (ClassInformation object), method information (MethodInformation object)), parameter name (VariableInformation) -> type (String)
     */
    public static Map<CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation>, String> methodVariableTypeMap = new LinkedHashMap<>();

}
