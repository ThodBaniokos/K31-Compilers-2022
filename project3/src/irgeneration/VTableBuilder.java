package irgeneration;

// package imports
import visitor.*;
import syntaxtree.*;
import symboltable.*;

import java.util.ArrayList;
import java.util.List;
public class VTableBuilder extends GJDepthFirst<String, String> {

    public String addMethodToVTable(ClassInformation classInfo, MethodInformation methodInfo, String vtableString) {

        // get the type of the method
        CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(classInfo, methodInfo);
        String methodType = SymbolTable.classMethodTypeMap.get(classMethodPair);
        String methodSignature = "";

        switch (methodType) {
            case "int":
                vtableString += "i8* bitcast (i32 (i8*";
                methodSignature += "i32 (i8*";
                break;
            case "boolean":
                vtableString += "i8* bitcast (i1 (i8*";
                methodSignature += "i1 (i8*";
                break;
            case "int[]":
                vtableString += "i8* bitcast (i32* (i8*";
                methodSignature += "i32* (i8*";
                break;
            case "boolean[]":
                vtableString += "i8* bitcast (i32* (i8*";
                methodSignature += "i32* (i8*";
                break;
            default:
                vtableString += "i8* bitcast (i8* (i8*";
                methodSignature += "i8* (i8*";
                break;
        }

        // get method parameter types
        List<VariableInformation> methodParameters = SymbolTable.methodParametersMap.get(classMethodPair);

        if (methodParameters != null) {
            for (VariableInformation parameter : methodParameters) {

                CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> parameterTypeKey = new CustomPair<>(classMethodPair, parameter);

                String parameterType = SymbolTable.methodParameterTypeMap.get(parameterTypeKey);

                switch (parameterType) {
                    case "int":
                        vtableString += ", i32";
                        methodSignature += ", i32";
                        break;
                    case "boolean":
                        vtableString += ", i1";
                        methodSignature += ", i1";
                        break;
                    case "int[]":
                    case "boolean[]":
                        vtableString += ", i32*";
                        methodSignature += ", i32*";
                        break;
                    default:
                        vtableString += ", i8*";
                        methodSignature += ", i8*";
                        break;
                }
            }
        }

        vtableString += ")* @" + classInfo.getClassName() + "." + methodInfo.getMethodName() + " to i8*)";
        methodSignature += ")";

        // add method signature to the map
        BuildTools.methodSignatureInVtableMap.put(classMethodPair, methodSignature);

        return vtableString;
    }

    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    public String visit(Goal n, String argu) throws Exception {

        // build the vtable for the main class
        n.f0.accept(this, argu);

        // build the vtable for the other classes
        for (Node node : n.f1.nodes) {
            node.accept(this, argu);
        }

        // make all the trivial emits after the vtable declarations
        BuildTools.makeAllTrivialEmits();

        return null;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    @Override
    public String visit(MainClass n, String argu) throws Exception {

        // build the v-table for the main class first
        String mainClassName = n.f1.f0.toString();

        // build the string to put in the .ll file
        String vtableString = "@." + mainClassName + "_vtable = global [0 x i8*] []";

        // emit the vtable string to the .ll file
        BuildTools.emit(vtableString);

        return null;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    public String visit(ClassDeclaration n, String argu) throws Exception {

        // get class name
        String className = n.f1.f0.toString();

        // Class info object to store the class info
        ClassInformation classInfo = SymbolTable.classesMap.get(className);

        // get the method list
        List<MethodInformation> methodList = SymbolTable.classMethodsMap.get(classInfo);

        // build the type of the vtable string and store it in the vtable type map
        int vtableSize = (methodList != null) ? methodList.size() : 0;
        String vtableTypeString = "[" + vtableSize + " x i8*]";
        BuildTools.classVtableType.put(classInfo, vtableTypeString);

        // build the v-table for the class, get the lentgh of the v-table from the methods list in the symbol table
        String vtableString = "@." + className + "_vtable = global " + vtableTypeString +" [";

        // build the v-table for the class with the method
        if (methodList != null) {

            for (int i = 0; i < methodList.size(); i++) {
                MethodInformation method = methodList.get(i);
                vtableString = addMethodToVTable(classInfo, method, vtableString);
                if (i != methodList.size() - 1) {
                    vtableString += ", ";
                }

                // key for vtable map
                CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(classInfo, method);

                // add the vtable entry to the map
                // this means that the vtable entry is the address of the method in the vtable
                // i * 8 is the offset of the method in the vtable, i.e. if we have 3 methods, the first method is at offset 0, the second at offset 8, and the third at offset 16
                // 8 is the size of the function pointer, i is the method counter
                BuildTools.vtalbeMap.put(classMethodPair, i * 8);
            }
        }

        // close the v-table string
        vtableString += "]";

        // emit the v-table string to the .ll file
        BuildTools.emit(vtableString);

        return null;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {

        // get class name
        String className = n.f1.f0.toString();

        // Class info object to store the class info
        ClassInformation classInfo = SymbolTable.classesMap.get(className);

        // get the method list
        List<MethodInformation> methodList = SymbolTable.classMethodsMap.get(classInfo);

        // list to store list of class and methods of super classes inherited by this class
        List<CustomPair<ClassInformation, List<MethodInformation>>> inheritedClassMethods = new ArrayList<>();

        // get the super class
        ClassInformation superClass = SymbolTable.classInheritenceMap.get(classInfo);

        // while the super class is not null, i.e. there are classes above this class in the inheritance tree
        while (superClass != null) {

            // get the method list of the super class
            List<MethodInformation> superClassMethodList = SymbolTable.classMethodsMap.get(superClass);

            // add the super class and method list to the list of inherited classes and methods
            inheritedClassMethods.add(new CustomPair<>(superClass, new ArrayList<>(superClassMethodList)));

            // get the super class of the super class
            superClass = SymbolTable.classInheritenceMap.get(superClass);
        }

        String vtableStringTail = "";
        int vtableLength = 0;

        // build the v-table for the class, first are the objects methods, next the inherited ones
        // also delete any overridden methods from the inherited methods list
        if (methodList != null) {

            for (int i = 0; i < methodList.size(); i++) {

                MethodInformation method = methodList.get(i);

                // check if the method is overridden and remove it from the super class/classes method list
                for (CustomPair<ClassInformation, List<MethodInformation>> classMethodList : inheritedClassMethods) {

                    if (classMethodList.secondObj.contains(method)) {
                        classMethodList.secondObj.remove(method);
                        break;
                    }
                }

                // add the method to the v-table string in order to emit it later
                vtableStringTail = addMethodToVTable(classInfo, method, vtableStringTail);
                if (i != methodList.size() - 1) {
                    vtableStringTail += ", ";
                }

                // key for vtable map
                CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(classInfo, method);

                // add the vtable entry to the map
                // this means that the vtable entry is the address of the method in the vtable
                // i * 8 is the offset of the method in the vtable, i.e. if we have 3 methods, the first method is at offset 0, the second at offset 8, and the third at offset 16
                // 8 is the size of the function pointer, i is the method counter
                BuildTools.vtalbeMap.put(classMethodPair, i * 8);
            }

            vtableLength = methodList.size();
        }

        // do the same for every super class
        for (CustomPair<ClassInformation, List<MethodInformation>> classMethodList : inheritedClassMethods) {

            if (classMethodList.secondObj.size() > 0) {

                if (methodList != null) vtableStringTail += ", ";

                for (int i = 0; i < classMethodList.secondObj.size(); i++) {

                    MethodInformation method = classMethodList.secondObj.get(i);
                    vtableStringTail = addMethodToVTable(classMethodList.firstObj, method, vtableStringTail);
                    if (i != classMethodList.secondObj.size() - 1) {
                        vtableStringTail += ", ";
                    }

                    // key for vtable map
                    CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(classMethodList.firstObj, method);

                    // add the vtable entry to the map
                    // this means that the vtable entry is the address of the method in the vtable
                    // i * 8 is the offset of the method in the vtable, i.e. if we have 3 methods, the first method is at offset 0, the second at offset 8, and the third at offset 16
                    // 8 is the size of the function pointer, i is the method counter
                    BuildTools.vtalbeMap.put(classMethodPair, (i + (methodList != null ? methodList.size() : 0)) * 8);
                }
            }

            vtableLength += classMethodList.secondObj.size();
        }

        vtableStringTail += "]";

        // build the v-table for the class, get the lentgh of the v-table from the methods list in the symbol table
        String vtableStringHead = "@." + className + "_vtable = global [" + vtableLength + " x i8*] [";

        // add the type of the vtable to the vtable type map
        BuildTools.classVtableType.put(classInfo, "[" + vtableLength + " x i8*]");

        BuildTools.emit(vtableStringHead + vtableStringTail);

        return null;
    }

}
