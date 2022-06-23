package vtable;

// custom packages
import symboltable.*;

// java libraries
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class VTable {

    /**
     * Class to field offset map
     * Usage : pair of class information and variable information to offset
     */
    public static Map<ClassInformation, List<CustomPair<VariableInformation, Integer>>> classFieldOffsetMap = new LinkedHashMap<>();

    /**
     * Class to field offset map
     * Usage : pair of class information and variable information to offset
     */
    public static Map<ClassInformation, List<CustomPair<MethodInformation, Integer>>> classMethodOffsetMap = new LinkedHashMap<>();

    /**
     * Class to last offsets map
     * Usage : class information to pair of integers, first is the field offset second the method offset.
     */
    public static Map<ClassInformation, CustomPair<Integer, Integer>> classLastOffestsMap = new LinkedHashMap<>();

    public static void computeOffests() {

        // loop counter
        int i = 0;

        // iterate though all the classes
        for (ClassInformation classInformation : SymbolTable.classesMap.values()) {

            // if the counter is 0, then we are in the first class, the main class, we dont want to print something for it
            if (i++ == 0) continue;

            int fieldStartingOffset = 0;
            int methodStartingOffset = 0;

            // super class if exists
            ClassInformation superClass = SymbolTable.classInheritenceMap.get(classInformation);

            if (superClass != null) {

                // get the super class last offsets
                fieldStartingOffset = classLastOffestsMap.get(superClass).firstObj;
                methodStartingOffset = classLastOffestsMap.get(superClass).secondObj;
            }

            // get the list of the methods for the class
            List<MethodInformation> methods = SymbolTable.classMethodsMap.get(classInformation);

            // get the list of the fields for the class
            List<VariableInformation> fields = SymbolTable.classFieldsMap.get(classInformation);

            // list to store the pairs of fields and offsets
            List<CustomPair<VariableInformation, Integer>> fieldOffsets = null;

            // if the class has fields
            if (fields != null) {

                // initialize the list
                fieldOffsets = new ArrayList<>();

                // iterate through the fields
                for (VariableInformation field : fields) {

                    // add offset of the field to the list and then update the starting offset
                    fieldOffsets.add(new CustomPair<>(field, fieldStartingOffset));

                    // get the type of the field
                    String fieldType = SymbolTable.classFieldTypeMap.get(new CustomPair<>(classInformation, field));

                    // switch case on the type
                    switch (fieldType) {

                        // the type is boolean
                        // size of boolean is 1 bytes
                        case "boolean":
                            fieldStartingOffset++;
                            break;

                        // the type is int
                        // size of int is 4 bytes
                        case "int":
                            fieldStartingOffset += 4;
                            break;

                        // the type is array of ints or booleans
                        // size of array is 8 bytes
                        case "int[]":
                        case "boolean[]":
                            fieldStartingOffset += 8;
                            break;
                    }

                    // if the field type is inside this map in the symbol table then it is a class object so it is a pointer
                    if (SymbolTable.classesMap.containsKey(fieldType)) fieldStartingOffset += 8;
                }
            }

            // add the list to the map
            classFieldOffsetMap.put(classInformation, fieldOffsets);

            // list to store the pairs of methods and offsets
            List<CustomPair<MethodInformation, Integer>> methodOffsets = null;

            // if the class has methods
            if (methods != null) {

                // initialize the list
                methodOffsets = new ArrayList<>();

                // iterate through all the methods
                for (MethodInformation method : methods) {

                    // used in order to keep track of the immediate super class of this one
                    ClassInformation tempSuperClass = superClass;

                    // boolean to keep track if the method is inherited or not
                    boolean isInherited = false;

                    // if this method also exists in the super class method list or higher in the
                    // class inheritance hierarchy dont compute the offset
                    while (tempSuperClass != null) {

                        // check if the current method is in the method list of the super class
                        // no need to perform extra checks since this file has already passed
                        // the semantic check
                        if (SymbolTable.classMethodsMap.get(tempSuperClass).contains(method)) {
                            isInherited = true;
                            break;
                        }

                        // continue higher in the inheritance hierarchy
                        tempSuperClass = SymbolTable.classInheritenceMap.get(tempSuperClass);
                    }

                    // no need to compute the offset break out of the loop
                    if (isInherited) continue;

                    // add offset of the method to the list and then update the starting offset
                    methodOffsets.add(new CustomPair<>(method, methodStartingOffset));

                    // methods are pointers and have a fixed size of 8 bytes
                    methodStartingOffset += 8;

                }
            }

            // add the list to the map
            classMethodOffsetMap.put(classInformation, methodOffsets);

            // add the last number of the offsets to the map
            // used if the class is a derived one to know where to
            // start calculating the next offsets
            CustomPair<Integer, Integer> offsetPairs = new CustomPair<>(fieldStartingOffset, methodStartingOffset);

            // update the corresponding map
            classLastOffestsMap.put(classInformation, offsetPairs);

        }

    }

    public static void printTable() {

        // loop counter
        int i = 0;

        // iterate though all the classes
        for (ClassInformation classInformation : SymbolTable.classesMap.values()) {

            // if the counter is 0, then we are in the first class, the main class, we dont want to print something for it
            if (i++ == 0) continue;

            // get the lists of offsets for the class
            List<CustomPair<VariableInformation, Integer>> fieldOffsets = classFieldOffsetMap.get(classInformation);
            List<CustomPair<MethodInformation, Integer>> methodOffsets = classMethodOffsetMap.get(classInformation);

            // print the class name
            System.out.println("-----------Class " + classInformation.getClassName() + "-----------");

            // start printing the fields
            System.out.println("--Variables---");

            // if the class has fields
            if (fieldOffsets != null) {

                // iterate through the fields
                for (CustomPair<VariableInformation, Integer> field : fieldOffsets) {

                    // print the offset
                    System.out.println(classInformation.getClassName() + "." + field.firstObj.getVarName() + " : " + field.secondObj);
                }

            }

            // start printing the fields
            System.out.println("---Methods---");

            // if the class has fields
            if (methodOffsets != null) {

                // iterate through the fields
                for (CustomPair<MethodInformation, Integer> method : methodOffsets) {

                    // print the offset
                    System.out.println(classInformation.getClassName() + "." + method.firstObj.getMethodName() + " : " + method.secondObj);
                }

            }

            // pretty printing
            System.out.println();

        }

    }

    public static void reset() {

        // clear all the tables to get the correct offsets for the other files
        classFieldOffsetMap.clear();
        classMethodOffsetMap.clear();
        classLastOffestsMap.clear();
    }
}
