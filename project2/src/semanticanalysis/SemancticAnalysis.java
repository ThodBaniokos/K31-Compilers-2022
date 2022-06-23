package semanticanalysis;

// package imports
import visitor.*;
import syntaxtree.*;
import symboltable.*;
import exceptions.*;

// java libraries import
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SemancticAnalysis extends GJDepthFirst<String, CustomPair<ClassInformation, MethodInformation>> {

    public String getIdentifierType(String identifierName, CustomPair<ClassInformation, MethodInformation> argu) {

        // this represents the identifier we're working with
        VariableInformation methodVariable = null;

        // check if the identifier is a local variable and get it's type, i.e. it is in the current scope of method and class
        List<VariableInformation> methodVars = SymbolTable.methodVariablesMap.get(argu);

        boolean isLocalVar = false;

        if (methodVars != null) {
            for (VariableInformation var : methodVars) {
                if (var.getVarName().equals(identifierName)) {
                    methodVariable = var;
                    isLocalVar = true;
                    break;
                }
            }
        }

        if (isLocalVar) {
            CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> varTypePairKey = new CustomPair<>(argu, methodVariable);
            return SymbolTable.methodVariableTypeMap.get(varTypePairKey);
        }

        // check if the identifier is a class parameter and get it's type, i.e. it is in the current scope of method and class
        List<VariableInformation> methodParams = SymbolTable.methodParametersMap.get(argu);

        boolean isMethodParam = false;

        if (methodParams != null) {
            for (VariableInformation var : methodParams) {
                if (var.getVarName().equals(identifierName)) {
                    methodVariable = var;
                    isMethodParam = true;
                    break;
                }
            }
        }

        if (isMethodParam) {
            CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> paramTypePairKey = new CustomPair<>(argu, methodVariable);
            return SymbolTable.methodParameterTypeMap.get(paramTypePairKey);
        }

        // check if the identifier is a class field and get it's type, i.e. it is in the current scope of class
        List<VariableInformation> classFields = SymbolTable.classFieldsMap.get(argu.firstObj);

        boolean isClassField = false;

        if (classFields != null) {
            for(VariableInformation var : classFields) {
                if (var.getVarName().equals(identifierName)) {
                    methodVariable = var;
                    isClassField = true;
                    break;
                }
            }
        }

        if (isClassField) {
            CustomPair<ClassInformation, VariableInformation> fieldTypePairKey = new CustomPair<>(argu.firstObj, methodVariable);
            return SymbolTable.classFieldTypeMap.get(fieldTypePairKey);
        }

        // check if the identifier is a class or not
        if (SymbolTable.classesMap.containsKey(identifierName)) {
            return identifierName;
        }

        // unknown type
        return null;
    }

    public VariableInformation getIdentifier(String identifierName, CustomPair<ClassInformation, MethodInformation> argu) {

        // check if the identifier is a local variable and get it's type, i.e. it is in the current scope of method and class
        List<VariableInformation> methodVars = SymbolTable.methodVariablesMap.get(argu);

        if (methodVars != null) {
            for (VariableInformation var : methodVars) {
                if (var.getVarName().equals(identifierName)) {
                    return var;
                }
            }
        }

        // check if the identifier is a class parameter and get it's type, i.e. it is in the current scope of method and class
        List<VariableInformation> methodParams = SymbolTable.methodParametersMap.get(argu);

        if (methodParams != null) {
            for (VariableInformation var : methodParams) {
                if (var.getVarName().equals(identifierName)) {
                    return var;
                }
            }
        }

        // check if the identifier is a class field and get it's type, i.e. it is in the current scope of class
        List<VariableInformation> classFields = SymbolTable.classFieldsMap.get(argu.firstObj);

        if (classFields != null) {
            for(VariableInformation var : classFields) {
                if (var.getVarName().equals(identifierName)) {
                    return var;
                }
            }
        }

        // unknown type
        return null;
    }

    /**
    * Goal visitor method.
    * Performs semantic analysis on the goal node which includes checking the following:
    * 1. The main class.
    * 2. All the other type declarations, i.e. the other Classes.
    */
    @Override
    public String visit(Goal n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // build the argu object for the visitor
        ClassInformation mainClass = SymbolTable.classesMap.get(n.f0.f1.f0.toString());

        // get the first element of the method list of the main class since there should be only one item in this list, the main method
        MethodInformation mainMethod = SymbolTable.classMethodsMap.get(mainClass).get(0);

        argu = new CustomPair<>(mainClass, mainMethod);

        // perform the semantic analysis on the main class
        n.f0.accept(this, argu);

        // perform the semantic analysis on the type declarations
        // classes methods etc.
        for (Node node : n.f1.nodes) {

            // accept the type declarations
            node.accept(this, argu);
        }

        // nothing to return
        return null;
    }

    /**
     * Main class visitor method.
     * Just accepts all the statements in the main method.
     * Stronger semantic analysis is deeper.
     */
    @Override
    public String visit(MainClass n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // for each statement inside the main method
        for(Node statement : n.f15.nodes) {

            // accept the statement
            // argu is the current scope, i.e. the class name and the method name
            // since this is the main method, argu is provided by the main method
            statement.accept(this, argu);
        }

        // nothing to return
        return null;
    }

    /**
     * Class declaration visitor method.
     * Skips everything and goes towards the method declarations since they are the only ones with statements to check.
     * Everything else was checked in the symbol table builing phase.
     */
    @Override
    public String visit(ClassDeclaration n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the class name
        String className = n.f1.accept(this, argu);

        // get the class information object
        ClassInformation classInfo = SymbolTable.classesMap.get(className);

        // construct the new argu with the class information
        CustomPair<ClassInformation, MethodInformation> newArgu = new CustomPair<>(classInfo, null);

        // skiping variables declarations, i.e. class fields since we've already visited them
        // going towards methods declarations
        // for each method declaration
        for(Node methodDecl : n.f4.nodes) {

            // accept the method declaration
            methodDecl.accept(this, newArgu);
        }

        // nothing to return
        return null;
    }

    /**
     * Class extends declaration visitor method.
     * Essentially the same as the class declaration visitor method.
     */
    @Override
    public String visit(ClassExtendsDeclaration n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the derived class name
        // we already know wich class is the super class
        // since we have the symbol table inheritance map
        // built in the previous phase
        String className = n.f1.accept(this, argu);

        // get the class information object
        ClassInformation classInfo = SymbolTable.classesMap.get(className);

        // construct the new argu with the class information
        CustomPair<ClassInformation, MethodInformation> newArgu = new CustomPair<>(classInfo, null);

        // skiping variables declarations, i.e. class fields since we've already visited them
        // going towards methods declarations
        // for each method declaration
        for(Node methodDecl : n.f6.nodes) {

            // accept the method declaration
            methodDecl.accept(this, newArgu);
        }

        // nothing to return
        return null;
    }

    /**
     * Method declaration visitor method.
     * Checks the method declaration and the statements inside it.
     * Skips vaiable declarations and parameter list declerations since these were already visited in the symbol table building phase.
     * Also checks the return type of the method and the type of the expression in the return statement.
     */
    @Override
    public String visit(MethodDeclaration n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the method type and name
        String methodName = n.f2.f0.toString();

        // get the method information object
        MethodInformation methodInfo = null;
        List<MethodInformation> methodInfoList = SymbolTable.classMethodsMap.get(argu.firstObj);

        // iterate over the list of methods to find the desired method
        for(MethodInformation methodIterator : methodInfoList) {

            // found the method
            // break from the loop
            if(methodIterator.getMethodName().equals(methodName)) {
                methodInfo = methodIterator;
                break;
            }
        }

        // build the argu with the new method information
        CustomPair<ClassInformation, MethodInformation> newArgu = new CustomPair<>(argu.firstObj, methodInfo);

        // skiping parameters declarations and variables declarations since these were already visited
        // in the previous phase of the symbol table building, going towards the statements

        // proceeding towards the statements
        for(Node statement : n.f8.nodes) {

            // accept the statement
            statement.accept(this, newArgu);
        }

        // find the return type of the return expression
        String returnType = n.f10.accept(this, newArgu);

        // check if the return type is the same as the method return type
        if(!returnType.equals(SymbolTable.classMethodTypeMap.get(newArgu))) {
            throw new TypeMissMatch("Return value of method " + methodName + " is not the same as the method return type.\nReturn value type : " + returnType + "\nMethod return type : " + SymbolTable.classMethodTypeMap.get(newArgu));
        }

        // nothing to return
        return null;
    }

    /**
    * The visitor method for the statements.
    * The statment can be one of the above, we accept the statement and continueing the semantic analysis deeper.
    */
    @Override
    public String visit(Statement n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // accept the statement, has to be one of the above
        // all of them don't need a check at this point of the code
        return n.f0.accept(this, argu);
    }

    /**
    * Block visitor method.
    * Accept all the statements inside the block.
    */
    @Override
    public String visit(Block n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // for each statement inside the block
        for (Node statement : n.f1.nodes) {

            // accept the statement
            statement.accept(this, argu);
        }

        // nothing to return
        return null;
    }

    /**
    * Assignment statement visitor.
    * Checks the type of the left hand side and the right hand side.
    * The types of the identifier and expression must match to accept the statement.
    */
    @Override
    public String visit(AssignmentStatement n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the identifier name
        String identifierType = n.f0.accept(this, argu);

        // get the expression type
        String exprType = n.f2.accept(this, argu);

        // check if either of the types is null, i.e. unknown types
        // if so, throw an exception
        if(identifierType == null || exprType == null) {
            throw new UnknownType("Unknown type in assignment statement.");
        }

        // check if the type of the expression is a class and if it has a super class
        // in order to check inheritance between the two types
        if(SymbolTable.classesMap.containsKey(exprType) && SymbolTable.classInheritenceMap.get(SymbolTable.classesMap.get(exprType)) != null) {

            // check if the identifier type is the same as the expression type, i.e. the same class
            if(identifierType.equals(exprType)) return null;


            // get the super class information object
            ClassInformation superClassInfo = null;

            // get the derived class information object
            ClassInformation derivedClassInfo = SymbolTable.classesMap.get(exprType);

            // check if the identifier is a class type
            if(SymbolTable.classesMap.containsKey(identifierType)) {

                // get the object
                superClassInfo = SymbolTable.classesMap.get(identifierType);
            }

            boolean isInherited = false;

            // iterate till there's no super class
            while(derivedClassInfo != null) {

                // check if the parent of the derived class is the super class is the identifier type
                if (superClassInfo.equals(SymbolTable.classInheritenceMap.get(derivedClassInfo))) {

                    // the parent is the super class
                    // update the boolean flag and exit the loop
                    isInherited = true;
                    break;
                }

                // change the derived class to the parent in order to bottom up check
                derivedClassInfo = SymbolTable.classInheritenceMap.get(derivedClassInfo);
            }

            // check if the identifier is a class type and if it is inherited from the expression type
            // if not throw an error
            if (!isInherited) {
                throw new TypeMissMatch("Class " + exprType + " is not derived from class " + identifierType + ".");
            }

            // no need to perform the check below since the types are inherited and compatible
            return null;
        }

        // check if the types are compatible
        if(!identifierType.equals(exprType)) {
            throw new TypeMissMatch("Type missmatch in assignment statement.\nIdentifier " + n.f0.f0.toString() + " is of type : " + identifierType + "\nExpression type : " + exprType);
        }

        // nothing to return
        return null;
    }

    /**
    * Array assignment statement visitor.
    * Checks the type of the left hand side and the right hand side.
    * The types of the identifier and expression must match to accept the statement.
    * Also the identifier must be an array.
    */
    @Override
    public String visit(ArrayAssignmentStatement n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get array name
        String arrayType = n.f0.accept(this, argu);

        if (!Pattern.compile("\\[\\]").matcher(arrayType).find()) {
            throw new TypeMissMatch("Array assignment statement type missmatch.\nIdentifier : " + n.f0.toString() + " is not an array.");
        }

        // get the expression type of the array index
        String indexType = n.f2.accept(this, argu);

        // check if the index type is an integer
        if(!indexType.equals("int")) {
            throw new TypeMissMatch("Array assignment statement type missmatch.\nArray index must be of type int.");
        }

        // get the expression type of the assignment
        String exprType = n.f5.accept(this, argu);

        // check if the expression type is the same as the array type
        if(!arrayType.contains(exprType)) {
            throw new TypeMissMatch("Array assignment statement type missmatch.\nArray is of type : " + arrayType + "\nExpression is of type : " + exprType);
        }

        // nothing to return
        return null;
    }

    /**
    * If statement visitor.
    * Checks the type of the condition expression.
    * The condition expression must be of type boolean.
    */
    @Override
    public String visit(IfStatement n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the expression type
        String exprType = n.f2.accept(this, argu);

        // check if the expression type is boolean
        if(!exprType.equals("boolean")) {
            throw new TypeMissMatch("If statement type missmatch.\nExpression is not of type boolean.");
        }

        // accept the if statement
        n.f4.accept(this, argu);

        // accept the else statement
        n.f6.accept(this, argu);

        // nothing to return
        return null;
    }

    /**
    * While statement visitor.
    * Checks the type of the condition expression.
    * The condition expression must be of type boolean.
    */
    @Override
    public String visit(WhileStatement n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the expression type
        String exprType = n.f2.accept(this, argu);

        // check if the expression type is boolean
        if(!exprType.equals("boolean")) {
            throw new TypeMissMatch("While statement type missmatch.\nExpression is not of type boolean.");
        }

        // accept the while statement
        n.f4.accept(this, argu);

        // nothing to return
        return null;
    }

    /**
    * Print statement visitor.
    * Checks the type of the expression.
    * The expression must be of type int in order to print in stdout.
    */
    @Override
    public String visit(PrintStatement n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the expression type
        String exprType = n.f2.accept(this, argu);

        // check if the expression type is an integer
        if(!exprType.equals("int")) {
            throw new TypeMissMatch("Print statement type missmatch.\nExpression is not of type int.");
        }

        // nothing to return
        return null;
    }

    /**
    * Expression visitor.
    * Calls the visitor method for right expression from the list of the available ones.
    */
    @Override
    public String visit(Expression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // accept the expression, the type will be computed deeper in the analysis
        return n.f0.accept(this, argu);
    }

    /**
    * And expression visitor.
    * Checks the type of the left and right expressions.
    * The expressions must be of type boolean.
    */
    @Override
    public String visit(AndExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the expressions types
        String firstClauseType = n.f0.accept(this, argu);
        String secondClauseType = n.f2.accept(this, argu);

        // check are the same type and if they are check if the type is boolean
        // if not throw an exception
        if (!(firstClauseType.equals(secondClauseType) && firstClauseType.equals("boolean"))) {
            throw new TypeMissMatch("Logical and expression type missmatch.\nBoth clauses must be of type boolean.");
        }

        // return the type of the expression
        // since this is a logical and expression, the type is boolean
        return "boolean";
    }

    /**
    * Compare expression visitor.
    * Checks the type of the left and right expressions.
    * The expressions must be of type int.
    */
    @Override
    public String visit(CompareExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the type of the primary expressions
        String firstPrimExpr = n.f0.accept(this, argu);
        String secondPrimExpr = n.f2.accept(this, argu);

        // check if they are the same and if they are check if they are ints
        // if not throw an exception
        if (!(firstPrimExpr.equals(secondPrimExpr) && firstPrimExpr.equals("int"))) {
            throw new TypeMissMatch("Compare expression type missmatch.\nComparison is allowed between integers.");
        }

        // return the type of the expression, if we reach this point then the type is boolean
        return "boolean";
    }

    /**
    * Plus expression visitor.
    * Checks the type of the left and right expressions.
    * The expressions must be of type int.
    */
    @Override
    public String visit(PlusExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the type of both the primary expressions
        String firstPrimExpr = n.f0.accept(this, argu);
        String secondPrimExpr = n.f2.accept(this, argu);

        // check if they are the same and if they are check if they are ints
        // if not throw an exception
        if (!(firstPrimExpr.equals(secondPrimExpr) && firstPrimExpr.equals("int"))) {
            throw new TypeMissMatch("Plus expression type missmatch.\nAddition is allowed only between integers.");
        }

        // return the type of the expression, if we reach this point then the type is int
        return "int";
    }

    /**
    * Minus expression visitor.
    * Checks the type of the left and right expressions.
    * The expressions must be of type int.
    */
    @Override
    public String visit(MinusExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the type of both the primary expressions
        String firstPrimExpr = n.f0.accept(this, argu);
        String secondPrimExpr = n.f2.accept(this, argu);

        // check if they are the same and if they are check if they are ints
        // if not throw an exception
        if (!(firstPrimExpr.equals(secondPrimExpr) && firstPrimExpr.equals("int"))) {
            throw new TypeMissMatch("Minus expression type missmatch.\nSubtraction is allowed only between integers.");
        }

        // return the type of the expression, if we reach this point then the type is int
        return "int";
    }

    /**
    * Times expression visitor.
    * Checks the type of the left and right expressions.
    * The expressions must be of type int.
    */
    @Override
    public String visit(TimesExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the type of both the primary expressions
        String firstPrimExpr = n.f0.accept(this, argu);
        String secondPrimExpr = n.f2.accept(this, argu);

        // check if they are the same and if they are check if they are ints
        // if not throw an exception
        if (!(firstPrimExpr.equals(secondPrimExpr) && firstPrimExpr.equals("int"))) {
            throw new TypeMissMatch("Times expression type missmatch.\nMultiplication is allowed only between integers.");
        }

        // return the type of the expression, if we reach this point then the type is int
        return "int";
    }

    /**
    * Array lookup expression visitor.
    * Checks the type of the first and second primary expressions.
    * The first expression must be an array.
    * The second expression must be of type int since it is the index of the array lookup.
    */
    @Override
    public String visit(ArrayLookup n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // since this is an array lookup we're getting the name of the identifier
        String identifierType = n.f0.accept(this, argu);

        // check if the identifier is an array with this simple regex
        if (!Pattern.compile("\\[\\]").matcher(identifierType).find()) {
            throw new TypeMissMatch("Array lookup expression type missmatch.\nGiven primary experssion is not an array.");
        }

        // get the type of the primary expression inside the square brackets
        String primExprType = n.f2.accept(this, argu);

        // check if the type of the primary expression is int or not
        if (!primExprType.equals("int")) {
            throw new TypeMissMatch("Array lookup expression type missmatch.\nArray index must be of type int.");
        }

        // return the correct type of the expression, if the array is of type int then the type is int
        // if the array is of type boolean then the type is boolean
        return (identifierType.equals("int[]")) ? "int" : "boolean";
    }

    /**
    * Array length expression visitor.
    * Checks the type of the first primary expression.
    * The first expression must be an array.
    */
    @Override
    public String visit(ArrayLength n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // since this is an array lookup we're getting the name of the identifier
        String identifierType = n.f0.accept(this, argu);

        // check if the identifier is an array with this simple regex
        if (!Pattern.compile("\\[\\]").matcher(identifierType).find()) {
            throw new TypeMissMatch("Array length expression type missmatch.\nGiven primary experssion is not an array.");
        }

        // return the type of this expression, the type is int since we're getting the length of an array
        return "int";
    }

    /**
    * Method call expression visitor.
    * Checks the type of the first primary expression.
    * The first expression must be a class or the this expression.
    * The second expression must be a method of the class type from the primary expression.
    * The expression list must have the same number of parameters as the method has and the same types *OREDERED*.
    */
    @Override
    public String visit(MessageSend n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the type of the primary expression
        String primExprType = n.f0.accept(this, argu);

        // check if the primary expression is the this expression

        // check if it is an existing class
        if (!SymbolTable.classesMap.containsKey(primExprType)) {
            throw new TypeMissMatch("Message send expression type missmatch.\nClass " + primExprType + " does not exist.");
        }

        // get the class information
        ClassInformation classInfo = SymbolTable.classesMap.get(primExprType);

        // get the method name
        String methodName = n.f2.f0.toString();

        // MethodInformation variable to store the actual method
        MethodInformation methodInfo = null;

        // while the class information is not null
        // this is used because the method could be inherited from a parent class
        while (classInfo != null) {

            // check if the class found above has a method with the name of the identifier
            // list of methods to store all the class methods
            List<MethodInformation> classMethods = SymbolTable.classMethodsMap.get(classInfo);

            methodInfo = null;

            // check if this class has any methods declared or not
            // if not continue to the next class if this class has a parent class
            if (classMethods != null) {

                // iterate through the list of methods to find the method with the name of the identifier
                for (MethodInformation method : classMethods) {

                    // method found so set the methodInfo variable to the method
                    // break out of the loop
                    if (method.getMethodName().equals(methodName)) {
                        methodInfo = method;
                        break;
                    }
                }
            }

            // if we found the method we can break out of the loop
            if (methodInfo != null) break;

            // get the parent class of the class information to continue the search
            classInfo = SymbolTable.classInheritenceMap.get(classInfo);
        }

        // check if the method variable is null
        // if it is then the method does not exist in the class
        // throw an exception
        if (methodInfo == null) {
            throw new TypeMissMatch("Message send expression type missmatch.\nMethod " + methodName + " does not exist in class " + primExprType + ".");
        }

        // create a pair of class information and method information to get the needed information
        // from the correct maps stored in the SymbolTable
        CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(classInfo, methodInfo);

        // get the type of the method
        String methodType = SymbolTable.classMethodTypeMap.get(classMethodPair);

        // get the list of parameters of the method
        List<VariableInformation> methodParameters = SymbolTable.methodParametersMap.get(classMethodPair);

        // store the type of the method parameters *ORDERED*
        List<String> methodParametersTypes = new ArrayList<>();

        // check if the method has parameters
        if (!(methodParameters == null)) {
            for (VariableInformation parameter : methodParameters) {
                methodParametersTypes.add(SymbolTable.methodParameterTypeMap.get(new CustomPair<>(classMethodPair, parameter)));
            }
        }

        // get the list of expressions
        String expressionList = n.f4.accept(this, argu);

        // check every possible state of the expression list and the method parameters
        // if there are parameters and given arguments check the number of arguments and the types *OREDERED*
        // if there are no arguments given and the method takes parameters throw an exception
        // if there are arguments and the method takes no parameters throw an exception
        if (expressionList != null && methodParameters != null) {

            // split all the expressions into a list
            String [] expressions = expressionList.split(" ");

            // check if the number of arguments is correct
            if (expressions.length != methodParametersTypes.size()) {
                throw new TypeMissMatch("Message send expression type missmatch.\nMethod " + methodName + " in class " + primExprType + " has " + methodParametersTypes.size() + " parameters but " + expressions.length + " arguments were given.");
            }

            // iterate through all the types and check if they are the same *OREDERED*
            for (int i = 0; i < expressions.length; i++) {

                // boolean flag to check for inheritance tree
                boolean isInherited = false;

                // check if the expression is a class that has inherited from another class
                if (SymbolTable.classesMap.containsKey(expressions[i]) && SymbolTable.classInheritenceMap.get(SymbolTable.classesMap.get(expressions[i])) != null) {

                    // bottom up check this argument to see if it is an inherited object
                    ClassInformation derClassInfo = SymbolTable.classesMap.get(expressions[i]);

                    // while the class information is not null
                    while (derClassInfo != null) {

                        // check if the argument is an inherited object
                        if (methodParametersTypes.get(i).equals(SymbolTable.classInheritenceMap.get(derClassInfo).getClassName())) {

                            isInherited = true;
                            break;
                        }

                        // get the parent class of the class information to continue the search
                        derClassInfo = SymbolTable.classInheritenceMap.get(derClassInfo);
                    }
                }

                // check if the expression is an inherited object
                // if it is then we can proceed to the next parameter
                if (isInherited) continue;

                // check if the type of the expression is the same as the type of the method parameter
                if (!methodParametersTypes.get(i).equals(expressions[i])) {
                    throw new TypeMissMatch("Message send expression type missmatch.\nIn method : " + methodName + " in class " + primExprType + " there are different argument types than expected.");
                }
            }
        }
        else if (expressionList == null && methodParameters != null) {
            throw new TypeMissMatch("Message send expression type missmatch.\nMethod " + methodName + " in class " + primExprType + " has " + methodParametersTypes.size() + " parameters but no arguments were given.");
        }
        else if (expressionList != null && methodParameters == null) {
            throw new TypeMissMatch("Message send expression type missmatch.\nMethod " + methodName + " in class " + primExprType + " takes no parameters.");
        }

        // the type of the message send is the type of the method
        return methodType;
    }

    /**
    * Expression list visitor.
    * Gets the types of all the expressions.
    * Returns the types in a string separated by spaces.
    */
    @Override
    public String visit(ExpressionList n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the type of the expression
        String exprType = n.f0.accept(this, argu);

        // get the type of the expression tail types
        String exprTailTypes = n.f1.accept(this, argu);

        // return only the types of the expression list
        return exprType + " " + exprTailTypes;
    }

    /**
    * Expression tail visitor.
    * Gets the types of all the expressions.
    * Returns the types in a string separated by spaces.
    */
    @Override
    public String visit(ExpressionTail n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // string to store the types
        String types = "";

        // accept the expression term
        for (Node expr : n.f0.nodes) {

            // add the types to the string separated by spaces
            types += expr.accept(this, argu) + " ";
        }

        // return the types
        return types;
    }

    /**
    * Expression term visitor.
    * Gets the type of the expression.
    * Returns the type.
    */
    @Override
    public String visit(ExpressionTerm n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // this is just an expression so accept it and get it's type
        return n.f1.accept(this, argu);
    }

    /**
    * Primary expression visitor.
    * Calls one of the many primary expressions.
    * Returns the type of the primary expression.
    */
    @Override
    public String visit(PrimaryExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // accept the primary expression and get the type
        return n.f0.accept(this, argu);
    }

    /**
    * Integer literal visitor.
    * Returns the type of the integer literal which is int.
    */
    @Override
    public String visit(IntegerLiteral n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {
        return "int";
    }

    /**
    * True literal visitor.
    * Returns the type of the true literal which is boolean.
    */
    @Override
    public String visit(TrueLiteral n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {
        return "boolean";
    }

    /**
    * False literal visitor.
    * Returns the type of the false literal which is boolean.
    */
    @Override
    public String visit(FalseLiteral n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {
        return "boolean";
    }

    /**
    * Identifier visitor.
    * Gets the type of the identifier from the SymbolTable, using the argu.
    * Argu is a pair of class information and method information which represents the scope.
    * Returns the type of the identifier.
    */
    @Override
    public String visit(Identifier n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the identifier name
        String identifierName = n.f0.toString();

        // get the identifier type
        String identifierType = getIdentifierType(identifierName, argu);

        // check if the type is null, this could mean that a variable is inherited from another class
        // check if the variable is inherited or not, if not throw an exception
        if (identifierType == null) {

            // get the class information of the class that the variable is inherited from
            ClassInformation derivedClass = SymbolTable.classesMap.get(argu.firstObj.getClassName());
            ClassInformation superClass = SymbolTable.classInheritenceMap.get(derivedClass);

            // check if the class has a parent class that inherits the variable we're checking
            if (superClass == null) {
                throw new UnknownType("Unknown type.\nVariable " + identifierName + " is not defined.");
            }

            // boolean to check if the variable is an inherited super class field
            boolean isField = false;

            // variable information object to store the field
            VariableInformation superClassField = null;

            // check if the identifier is a field of the super class until there's no super class to check
            while(superClass != null) {

                // get the fields of the super class
                List<VariableInformation> superClassFields = SymbolTable.classFieldsMap.get(superClass);
                isField = false;

                // check if the super class has fields or not
                // if not continue higher to the class hierarchy if there's one
                if (superClassFields != null) {

                    // check if the identifier is a field of the super class
                    for (VariableInformation field : superClassFields) {

                        // check if the identifier is a field of the super class
                        if (field.getVarName().equals(identifierName)) {

                            // update the boolean to true
                            // and update the field variable information object
                            isField = true;
                            superClassField = field;
                            break;
                        }
                    }
                }

                // if the identifier is a field of the super class
                // we can break the loop
                if (isField) break;

                // get the super class of the current class
                superClass = SymbolTable.classInheritenceMap.get(superClass);
            }

            // if the identifier is not a field of the super class
            // throw an exception
            if (!isField) {
                throw new UnknownType("Unknown type.\nVariable " + identifierName + " is not defined.");
            }

            // get the type of the field
            return SymbolTable.classFieldTypeMap.get(new CustomPair<ClassInformation, VariableInformation>(superClass, superClassField));

        }

        // check if the identifier type is String[] and if it is then
        // throw an exception, cannot use main method string array argument
        if (identifierType.equals("String[]")) {
            throw new TypeMissMatch("Cannot use main method string array argument.");
        }

        // return the identifier type
        return identifierType;
    }

    /**
    * This expression visitor.
    * Returns the type of the this expression which is the type of the class.
    */
    @Override
    public String visit(ThisExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the class information from argu
        ClassInformation classInfo = argu.firstObj;

        // return the type of this expression, the type is the class name
        return classInfo.getClassName();
    }

    /**
    * Array allocation expression visitor.
    * Gets the type of the array allocation expression.
    * Returns the type of the array allocation expression.
    */
    @Override
    public String visit(ArrayAllocationExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // accept the type of the array allocation expression
        // the type will be either int[] or boolean[]
        return n.f0.accept(this, argu);
    }

    /**
    * Boolean array allocation expression visitor.
    * Returns the type of the boolean array allocation expression which is boolean[].
    * Also checks the type of the expression representing the size of the array.
    * The type must be int.
    */
    @Override
    public String visit(BooleanArrayAllocationExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the type of the expression inside the square brackets
        String exprType = n.f3.accept(this, argu);

        // check if the type of the expression is int or not
        // if it is not then throw an exception
        if (!exprType.equals("int")) {
            throw new TypeMissMatch("Boolean array allocation expression type missmatch.\nArray allocation size must be of type int.");
        }

        // return the type of the array, since we're in a boolean array allocation expression the type is boolean[]
        return "boolean[]";
    }

    /**
    * Integer array allocation expression visitor.
    * Returns the type of the integer array allocation expression which is int[].
    * Also checks the type of the expression representing the size of the array.
    * The type must be int.
    */
    @Override
    public String visit(IntegerArrayAllocationExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the type of the expression inside the square brackets
        String exprType = n.f3.accept(this, argu);

        // check if the type of the expression is int or not
        // if it is not then throw an exception
        if (!exprType.equals("int")) {
            throw new TypeMissMatch("Integer array allocation expression type missmatch.\nArray allocation size must be of type int.");
        }

        // return the type of the array, since we're in a int array allocation expression the type is int[]
        return "int[]";
    }

    /**
    * Allocation expression visitor.
    * Returns the type of the allocation expression which is the class type.
    */
    @Override
    public String visit(AllocationExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the identifier/class name
        String identifierType = n.f1.accept(this, argu);

        // check if the class exists or not in the symbol table
        if (!SymbolTable.classesMap.containsKey(identifierType)) {
            throw new ClassNotFound("Allocation expression type missmatch.\nClass " + identifierType + " does not exist.");
        }

        // return the type of the object which is the class name, i.e. if the class is A then the type of the object is also A
        return identifierType;
    }

    /**
    * Clause visitor.
    * Returns the type of the clause which is the type of the primary expression or the not expression.
    */
    @Override
    public String visit(Clause n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the expression type
        String expr = n.f0.accept(this, argu);

        // return the type of the clause found above
        return expr;
    }

    /**
    * Not expression visitor.
    * Returns the type of the not expression which is boolean.
    * Also checks if the type of the clause is boolean or not.
    */
    @Override
    public String visit(NotExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // get the clause type
        String clauseType = n.f1.accept(this, argu);

        // check if the clause type is boolean or not
        if (!clauseType.equals("boolean")) {
            throw new TypeMissMatch("Not expression type missmatch.\nClause in not expression must be of type boolean.");
        }

        // return the type of the not expression, the type is boolean
        return "boolean";
    }

    /**
    * Bracket expression visitor.
    * Returns the type of the bracket expression which is the type of the expression.
    */
    @Override
    public String visit(BracketExpression n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        // accept the expression and return it's type
        return n.f1.accept(this, argu);
    }

    // boolean array type
    @Override
    public String visit(BooleanArrayType n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        return "boolean[]";
    }

    // int array type
    @Override
    public String visit(IntegerArrayType n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        return "int[]";
    }

    // boolean type
    @Override
    public String visit(BooleanType n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        return "boolean";
    }

    // int type
    @Override
    public String visit(IntegerType n, CustomPair<ClassInformation, MethodInformation> argu) throws Exception {

        return "int";
    }
}
