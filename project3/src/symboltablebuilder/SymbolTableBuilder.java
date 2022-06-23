// package name
package symboltablebuilder;

// package imports
import visitor.*;
import syntaxtree.*;
import symboltable.*;
import exceptions.*;

// java libraries import
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class SymbolTableBuilder extends GJDepthFirst<String, String> {

    // checks for duplicate methods in the same class
    public void duplicateMethodCheck(ClassInformation newClass) throws Exception {

        // check if there is a method with the same name in the same class
        // if there is, throw an error
        List<MethodInformation> methodsDuplicateCheck = null;

        // iterate through all the pairs in the map of methods in of this class
        if (SymbolTable.classMethodsMap.containsKey(newClass) && SymbolTable.classMethodsMap.get(newClass) != null) {

            // store all the methods in a list
            for (MethodInformation method : SymbolTable.classMethodsMap.get(newClass)) {

                // check if the list is still null and allocate memory for it
                if (methodsDuplicateCheck == null) {
                    methodsDuplicateCheck = new ArrayList<>();
                }

                // check if there is a method with the name main
                // if there is, throw an error
                if (method.getMethodName().equals("main")) {
                    throw new MultipleMethodDefinition("Compilation Error\nCannot have more than one main methods in the file\nSecond definition found in class: " + newClass.getClassName());
                }

                // add the method to the list
                methodsDuplicateCheck.add(method);
            }
        }

        // if there are methods in the class create a set from the list constructed above
        // if the set has less elements than the list, there is a duplicate method
        if (methodsDuplicateCheck != null) {
            Set<MethodInformation> duplicatesSet = new HashSet<>(methodsDuplicateCheck);

            // check if the set has less elements than the list
            // if yes throw an error
            if (duplicatesSet.size() < methodsDuplicateCheck.size()) {
                throw new MultipleMethodDefinition("Compilation Error\nMultiple definition of methods in class: " + newClass.getClassName());
            }
        }
    }

    // creates a list of class fields, types and returns it in the calling method
    // also checks for duplicate fields in the same class
    public List<VariableInformation> classFieldCollection(Vector<Node> nodes, ClassInformation currentClass) throws Exception {
        // get all the class's field declarations
        // list to store all the pairs of field, type this class
        List<VariableInformation> classFields = null;

        // iterate through all the nodes of variable declaration in the ast
        for (Node varDeclaration : nodes) {

            // get the name and type of the variable in a single string
            // return format is "type name"
            String var = varDeclaration.accept(this, null);

            // check if there is a variable or not
            // if there is at least one variable allocate memory for a new list object
            if (var == null) {
                continue;
            }

            if (classFields == null) {
                classFields = new ArrayList<>();
            }

            // split the given string into the type and name
            String[] varStrings = var.split(" ");

            // check if there is a multiple defition of the same field
            // if there is, throw an error
            for (VariableInformation field : classFields) {
                if (field.getVarName().equals(varStrings[1])) {
                    throw new MultipleVariableDefition("Compilation Error\nMultiple definition of field: " + varStrings[1] + " in class: " + currentClass.getClassName());
                }
            }

            // create a new variable information object
            VariableInformation classField = new VariableInformation();

            // set the variable name
            classField.setVarName(varStrings[1]);

            // create a new pair to store the variables type
            CustomPair<ClassInformation, VariableInformation> classFieldPair = new CustomPair<>(currentClass, classField);

            // update symbol table with the field type
            SymbolTable.classFieldTypeMap.put(classFieldPair, varStrings[0]);

            // add the new field into the list
            classFields.add(classField);
        }

        return classFields;
    }

    // Main class visitor
    @Override
    public String visit(MainClass n, String argu) throws Exception {

        // get main class name
        String mainClassName = n.f1.accept(this, null);

        // create a new class information object
        ClassInformation mainClass = new ClassInformation();

        // set the name of the main class to the object
        mainClass.setClassName(mainClassName);

        // update symbol table, class name to class info object map
        SymbolTable.classesMap.put(mainClassName, mainClass);

        // set the class inheritance of the main class
        // main class cannot be a derived one, so no super class
        SymbolTable.classInheritenceMap.put(mainClass, null);

        // set the class fields of the main class
        // no fields in the main class
        SymbolTable.classFieldsMap.put(mainClass, null);

        // create a new method information object representing the main method
        MethodInformation mainMethod = new MethodInformation();

        // set the name of the main method, always "main" but we're converting n.f6 to
        // string
        // n.f6.toString is the same as passing the string "main", did not hard code a value
        mainMethod.setMethodName(n.f6.toString());

        // create a new list for the main class methods
        List<MethodInformation> mainClassMethods = new ArrayList<>();

        // add the main method to the list
        mainClassMethods.add(mainMethod);

        // add the list of methods to the symboltable
        SymbolTable.classMethodsMap.put(mainClass, mainClassMethods);

        // create a pair of class information and method information
        // this works as a scope, i.e. under what class is the method defined
        // and using this pair as a key we're storing the method's type
        CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(mainClass, mainMethod);

        // update the symbol table
        SymbolTable.classMethodTypeMap.put(classMethodPair, n.f5.toString());

        // update the method to parameter, type list map in the symbol table
        // get the name of the cli arguments array
        String cliArrayName = n.f11.accept(this, null);

        // create a new variable information object
        VariableInformation cliArray = new VariableInformation();

        // set the cli array name
        cliArray.setVarName(cliArrayName);

        // list to store all of the main method's parameters
        List<VariableInformation> mainMethodParameters = new ArrayList<>();

        // add the cli array to the list
        mainMethodParameters.add(cliArray);

        // add the list of parameters to the symbol table
        SymbolTable.methodParametersMap.put(classMethodPair, mainMethodParameters);

        // create a new pair of (class information, method information) , parameter information
        // in order to store the parameter type in the symbol table
        // this works as a scope, i.e. under what class and under what method the parameter defined
        CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> classMethodParameterPair = new CustomPair<>(classMethodPair, cliArray);

        // store the type of the cli array in the symbol table
        SymbolTable.methodParameterTypeMap.put(classMethodParameterPair, n.f8.toString() + n.f9.toString() + n.f10.toString());

        // get all the methods variable declaration if there are any
        // create a list to store all the variables of the main method
        List<VariableInformation> mainMethodVariables = null;

        // iterarate through all the variable declaration nodes in the ast
        for(Node varDeclaration : n.f14.nodes) {

            // get the name and type of the variable in a single string
            // return format is "type name"
            String var = varDeclaration.accept(this, null);

            // check if there is a variable or not
            // if there is at least one variable allocate memory for a new list object
            if (var == null) {
                continue;
            }

            if (mainMethodVariables == null) {
                mainMethodVariables = new ArrayList<>();
            }

            // split the given string into the type and name
            String[] varStrings = var.split(" ");

            // check if there is a multiple defition of the same variable
            // if there is, throw an error
            for (VariableInformation variable : mainMethodVariables) {

                // check for variables with the same name
                if (variable.getVarName().equals(varStrings[1])) {
                    throw new MultipleVariableDefition("Compilation Error\nMultiple definition of variable: " + varStrings[1] + " in main method");
                }
            }

            // create a new variable information object
            VariableInformation mainMethodVar = new VariableInformation();

            // set the variable name
            mainMethodVar.setVarName(varStrings[1]);

            // create a new pair to store the variable type
            CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> mainMethodVarType = new CustomPair<>(classMethodPair, mainMethodVar);

            // add the pair into the list
            mainMethodVariables.add(mainMethodVar);

            // add the type of the variable to the symbol table
            SymbolTable.methodVariableTypeMap.put(mainMethodVarType, varStrings[0]);
        }

        // check if there's a variable with the same name as the arguments array
        // if there is, throw an error
        if (mainMethodVariables != null) {
            for (VariableInformation variable : mainMethodVariables) {

                // check for variables with the same name
                if (variable.getVarName().equals(cliArrayName)) {
                    throw new MultipleVariableDefition("Compilation Error\nMultiple definition of parameter: " + cliArrayName + " in main method");
                }
            }
        }

        // update the symbol table
        SymbolTable.methodVariablesMap.put(classMethodPair, mainMethodVariables);

        return null;
    }

    // Class declaration visitor
    @Override
    public String visit(ClassDeclaration n, String argu) throws Exception {

        // get class name
        String className = n.f1.accept(this, null);

        // check if there is a class with this name and throw an exception if there is
        if (SymbolTable.classesMap.containsKey(className)) {
            throw new MultipleClassDefinition("Compilation Error\nMultiple definition of class: " + className);
        }

        // create new class object
        ClassInformation newClass = new ClassInformation();

        // set the new class's name
        newClass.setClassName(className);

        // update classes map
        SymbolTable.classesMap.put(className, newClass);

        // update symbol table
        // class declaration, does not inherit from one
        SymbolTable.classInheritenceMap.put(newClass, null);

        // get all the class's field declarations with the dedicated
        // method and then the update symbol table
        SymbolTable.classFieldsMap.put(newClass, classFieldCollection(n.f3.nodes, newClass));

        // initialize the class to a list of methods map to null
        SymbolTable.classMethodsMap.put(newClass, null);

        // iterate through all the nodes of method declaration in the ast
        for (Node methodDeclaration : n.f4.nodes) {

            // for each node visit the method declaration, there all the needed information
            // is store to the right maps
            methodDeclaration.accept(this, className);

        }

        // check for duplicated methods
        duplicateMethodCheck(newClass);

        return null;
    }

    // Class extends visitor
    @Override
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {

        // get derived class name
        String derivedClassName = n.f1.accept(this, null);

        // check if there is a class with this name and throw an exception if there is
        if (SymbolTable.classesMap.containsKey(derivedClassName)) {
            throw new MultipleClassDefinition("Compilation Error\nMultiple definition of class: " + derivedClassName);
        }

        // get super class name
        String superClassName = n.f3.accept(this, null);

        // check if there is an existing class with this name and throw an exception if there is not one
        if (!SymbolTable.classesMap.containsKey(superClassName)) {
            throw new ClassNotFound("Compilation Error\nClass: " + superClassName + " not found.\nCannot extend a non-existent class");
        }

        // check if the super class is the same as the derived class
        if (superClassName.equals(derivedClassName)) {
            throw new ClassNotFound("Compilation Error\nClass: " + derivedClassName + " cannot extend itself");
        }

        // create new class object
        ClassInformation newClass = new ClassInformation();

        // set the new class's name
        newClass.setClassName(derivedClassName);

        // update classes map
        SymbolTable.classesMap.put(derivedClassName, newClass);

        // update symbol table
        SymbolTable.classInheritenceMap.put(newClass, SymbolTable.classesMap.get(superClassName));

        // get all the class's field declarations with the dedicated
        // method and then the update symbol table
        SymbolTable.classFieldsMap.put(newClass, classFieldCollection(n.f5.nodes, newClass));

        // initialize the class to a list of methods map to null
        SymbolTable.classMethodsMap.put(newClass, null);

        // iterate through all the nodes of method declaration in the ast
        for (Node methodDeclaration : n.f6.nodes) {

            // for each node visit the method declaration, there all the needed information
            // is store to the right maps
            methodDeclaration.accept(this, derivedClassName);

        }

        // check for duplicate methods
        duplicateMethodCheck(newClass);

        // checking if there is a method with the same name in the super class
        // if there is and the type of the method is not the same or if the parameter types are not the same
        // and ordered in the same way throw an error

        // get derived class methods
        List<MethodInformation> derMethods = SymbolTable.classMethodsMap.get(newClass);

        // get the super class
        ClassInformation superClass = SymbolTable.classInheritenceMap.get(newClass);

        // pair of class and method to get the type of the method
        CustomPair<ClassInformation, MethodInformation> superClassMethodPair;
        CustomPair<ClassInformation, MethodInformation> derivedClassMethodPair;

        // check the method types if there are methods in the derived class
        if (derMethods != null) {

            // bottom up check to the top class
            while (superClass != null) {

                // get the super class methods
                List<MethodInformation> supMethods = SymbolTable.classMethodsMap.get(superClass);

                // boolean in order to check if the method was found or not
                boolean methodFound = false;

                for (MethodInformation method : derMethods) {

                    // get the index of the method in the super class if there is one
                    int indexSupList = supMethods.indexOf(method);

                    // check if the index returned is -1 which means that this method is not in the super class
                    if (indexSupList == -1) {
                        continue;
                    }

                    // get the super method type
                    superClassMethodPair = new CustomPair<>(superClass, supMethods.get(indexSupList));
                    String supType = SymbolTable.classMethodTypeMap.get(superClassMethodPair);

                    // get the derived method type
                    derivedClassMethodPair = new CustomPair<>(newClass, method);
                    String derType = SymbolTable.classMethodTypeMap.get(derivedClassMethodPair);

                    // get the parameters for both the super class and the derived class
                    // get the super class method parameters
                    List<VariableInformation> supParams = SymbolTable.methodParametersMap.get(superClassMethodPair);

                    // get the type of the super class method parameters, *IN ORDER*
                    List<String> supParamTypes = new ArrayList<>();

                    // create a custom pair for the key of the method parameters type map to get the correct method's types
                    CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> parametersTypeKey;

                    // get the super class method parameters types
                    // if there are any parameters
                    if (supParams != null) {

                        // get all the types
                        for (VariableInformation supParam : supParams) {
                            parametersTypeKey = new CustomPair<>(superClassMethodPair, supParam);
                            supParamTypes.add(SymbolTable.methodParameterTypeMap.get(parametersTypeKey));
                        }
                    }

                    // get the parameters for both the super class and the derived class
                    // get the super class method parameters
                    List<VariableInformation> derParams = SymbolTable.methodParametersMap.get(derivedClassMethodPair);

                    // get the type of the super class method parameters, *IN ORDER*
                    List<String> derParamTypes = new ArrayList<>();

                    // get the derived class method parameters types
                    // if there are any parameters
                    if (derParams != null) {

                        // get all the types
                        for (VariableInformation derParam : derParams) {
                            parametersTypeKey = new CustomPair<>(derivedClassMethodPair, derParam);
                            derParamTypes.add(SymbolTable.methodParameterTypeMap.get(parametersTypeKey));
                        }
                    }

                    // check if the super class method return type is the same as the derived class method return type
                    // also check if the super class method parameters are the same type *ORDERED* as the derived class method parameters
                    // if the methods don't have arguments then the first condition is true because we compare two empty lists
                    // depending only on the return type of the methods
                    if (!(supParamTypes.equals(derParamTypes) && supType.equals(derType))) {
                        throw new MultipleMethodDefinition("Compilation Error\nMultiple definition of inherited method " + method.getMethodName() + " in class: " + derivedClassName);
                    }

                    // set the found boolean to true
                    // if we reach this point the inherited method was found
                    // and was properly defined
                    methodFound = true;
                }

                // if found is true then break out of the while loop
                // no need to check the higher classes
                if (methodFound) {
                    break;
                }

                // get the super class of the super class
                superClass = SymbolTable.classInheritenceMap.get(superClass);
            }
        }

        return null;
    }

    // method declaration visitor
    @Override
    public String visit(MethodDeclaration n, String argu) throws Exception {

        // get method type
        String methodType = n.f1.accept(this, null);

        // get method name
        String methodName = n.f2.accept(this, null);

        // create a new method object
        MethodInformation newMethod = new MethodInformation();

        // set the method name
        newMethod.setMethodName(methodName);

        // create a new pair of method and type information
        CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(SymbolTable.classesMap.get(argu), newMethod);

        // update method name to type map
        if (SymbolTable.classMethodsMap.get(SymbolTable.classesMap.get(argu)) == null) {

            // create a new list object to store all the methods of the given class
            List<MethodInformation> methodLookupList = new ArrayList<>();

            // update the list with the first pair of method to type
            methodLookupList.add(newMethod);

            // update the symbol table
            SymbolTable.classMethodsMap.put(SymbolTable.classesMap.get(argu), methodLookupList);

            // update the type of the method
            SymbolTable.classMethodTypeMap.put(classMethodPair, methodType);
        }
        else {

            // update the symbol table
            SymbolTable.classMethodTypeMap.put(classMethodPair, methodType);
            SymbolTable.classMethodsMap.get(SymbolTable.classesMap.get(argu)).add(newMethod);
        }

        // get parameter list of the method
        List<VariableInformation> methodParameterList = null;

        // all the parameters are seperated with a whitespace
        String paramsString = n.f4.accept(this, null);

        if (paramsString != null) {

            // there are parameters, allocate memory for the list
            methodParameterList = new ArrayList<>();

            // split the parameters
            String[] parameters = paramsString.split(" ");

            // for each parameter
            for (String parameter : parameters) {

                // parameter type and name are seperated by the - character
                String[] paramStrings = parameter.split("-");

                // check if there is a multiple defition of the same field
                // if there is, throw an error
                for (VariableInformation par : methodParameterList) {
                    if (par.getVarName().equals(paramStrings[1])) {
                        throw new MultipleVariableDefition("Compilation Error\nMultiple definition of argument: " + paramStrings[1] + " in method: " + methodName + " of class: " + argu);
                    }
                }

                // create new field information object
                VariableInformation newParameter = new VariableInformation();

                // set the name of the parameter
                newParameter.setVarName(paramStrings[1]);

                // set the new parameter type
                // create a pair of (class, method) and parameter as key
                CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> parameterTypeKey = new CustomPair<>(classMethodPair, newParameter);

                // update list of method parameters
                methodParameterList.add(newParameter);

                // update the type of the parameter
                SymbolTable.methodParameterTypeMap.put(parameterTypeKey, paramStrings[0]);
            }

            // update the symbol table
            SymbolTable.methodParametersMap.put(classMethodPair, methodParameterList);
        }

        // get all the methods variable declarations
        // list to store all the variables of this method
        List<VariableInformation> methodVariables = null;

        // iterate through all the nodes of the method variable declaration in the ast
        for (Node varDeclaration : n.f7.nodes) {

            // get the name and type of the variable in a single string
            // return format is "type name"
            String var = varDeclaration.accept(this, null);

            // check if there is a variable or not
            // if there is at least one variable allocate memory for a new list object
            if (var == null) {
                continue;
            }
            else if (var != null && methodVariables == null) {
                methodVariables = new ArrayList<>();
            }

            // split the given string into the type and name
            String[] varStrings = var.split(" ");

            // check if there is a multiple defition of the same field
            // if there is, throw an error
            for (VariableInformation variable : methodVariables) {
                if (variable.getVarName().equals(varStrings[1])) {
                    throw new MultipleVariableDefition("Compilation Error\nMultiple definition of variable: " + varStrings[1] + " in method: " + methodName + " of class: " + argu);
                }
            }

            if (methodParameterList != null) {
                // check if the variable declaration is a method parameter
                // if it is, throw an error
                for (VariableInformation parameter : methodParameterList) {
                    if (parameter.getVarName().equals(varStrings[1])) {
                        throw new MultipleVariableDefition("Compilation Error\nMultiple definition of parameter: " + varStrings[1] + " in method : " + methodName + " of class: " + argu);
                    }
                }
            }

            // create a new variable information object
            VariableInformation methodVar = new VariableInformation();

            // set the variable name
            methodVar.setVarName(varStrings[1]);

            // add the pair into the list
            methodVariables.add(methodVar);

            // create a pair of (class, method) and variable as key
            CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> variableTypeKey = new CustomPair<>(classMethodPair, methodVar);

            // update the type of the variable
            SymbolTable.methodVariableTypeMap.put(variableTypeKey, varStrings[0]);
        }

        // update method to variables map
        SymbolTable.methodVariablesMap.put(classMethodPair, methodVariables);

        return null;

    }


    // formal parameter list visitor
    @Override
    public String visit(FormalParameterList n, String argu) throws Exception {
        return n.f0.accept(this, null) + " " + n.f1.accept(this, null);
    }

    // formal parameter visitor
    @Override
    public String visit(FormalParameter n, String argu) throws Exception {

        // get parameter type
        String type = n.f0.accept(this, null);

        // get parameter name
        String name = n.f1.accept(this, null);

        // return the field declaration string
        return type + "-" + name;
    }

    // formal parameter tail visitor
    @Override
    public String visit(FormalParameterTail n, String argu) throws Exception {

        String params = "";

        for (Node node : n.f0.nodes) params += node.accept(this, null) + " ";

        return params;
    }

    // formal parameter term visitor
    @Override
    public String visit(FormalParameterTerm n, String argu) throws Exception {

        return n.f1.accept(this, null);
    }

    // var declaration visitor
    @Override
    public String visit(VarDeclaration n, String argu) throws Exception {

        // get field type
        String type = n.f0.accept(this, null);

        // get field name
        String name = n.f1.accept(this, null);

        // return the field declaration string
        return type + " " + name;
    }

    // identifier visitor
    @Override
    public String visit(Identifier n, String argu) throws Exception {

        return n.f0.toString();
    }

    // boolean array type
    @Override
    public String visit(BooleanArrayType n, String argu) throws Exception {

        return "boolean[]";
    }

    // int array type
    @Override
    public String visit(IntegerArrayType n, String argu) throws Exception {

        return "int[]";
    }

    // boolean type
    @Override
    public String visit(BooleanType n, String argu) throws Exception {

        return "boolean";
    }

    // int type
    @Override
    public String visit(IntegerType n, String argu) throws Exception {

        return "int";
    }
}
