package irgeneration;

// package imports
import visitor.*;
import syntaxtree.*;
import symboltable.*;
import semanticanalysis.*;
import offsets.Offsets;

// library imports
import java.util.List;

public class LLVMCodeGeneration extends GJDepthFirst<String, SemanticAnalysis> {

    // current class and current method
    ClassInformation currentClass;
    MethodInformation currentMethod;

    public String getIdentifierType(String identifierName, CustomPair<ClassInformation, MethodInformation> argu) {

        // this represents the identifier we're working with
        VariableInformation methodVariable = null;

        // check if the identifier is a local variable and get it's type, i.e. it is in
        // the current scope of method and class
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
            CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> varTypePairKey = new CustomPair<>(
                    argu, methodVariable);
            return SymbolTable.methodVariableTypeMap.get(varTypePairKey);
        }

        // check if the identifier is a class parameter and get it's type, i.e. it is in
        // the current scope of method and class
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
            CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> paramTypePairKey = new CustomPair<>(
                    argu, methodVariable);
            return SymbolTable.methodParameterTypeMap.get(paramTypePairKey);
        }

        // check if the identifier is a class field and get it's type, i.e. it is in the
        // current scope of class
        List<VariableInformation> classFields = SymbolTable.classFieldsMap.get(argu.firstObj);

        boolean isClassField = false;

        if (classFields != null) {
            for (VariableInformation var : classFields) {
                if (var.getVarName().equals(identifierName)) {
                    methodVariable = var;
                    isClassField = true;
                    break;
                }
            }
        }

        if (isClassField) {
            CustomPair<ClassInformation, VariableInformation> fieldTypePairKey = new CustomPair<>(argu.firstObj,
                    methodVariable);
            return SymbolTable.classFieldTypeMap.get(fieldTypePairKey);
        }

        // check if the identifier is a class or not
        if (SymbolTable.classesMap.containsKey(identifierName)) {
            return identifierName;
        }

        // unknown type
        return null;
    }

    public VariableInformation getIdentifier(String identifierName,
            CustomPair<ClassInformation, MethodInformation> argu) {

        // check if the identifier is a local variable and get it's type, i.e. it is in
        // the current scope of method and class
        List<VariableInformation> methodVars = SymbolTable.methodVariablesMap.get(argu);

        if (methodVars != null) {
            for (VariableInformation var : methodVars) {
                if (var.getVarName().equals(identifierName)) {
                    return var;
                }
            }
        }

        // check if the identifier is a class parameter and get it's type, i.e. it is in
        // the current scope of method and class
        List<VariableInformation> methodParams = SymbolTable.methodParametersMap.get(argu);

        if (methodParams != null) {
            for (VariableInformation var : methodParams) {
                if (var.getVarName().equals(identifierName)) {
                    return var;
                }
            }
        }

        // check if the identifier is a class field and get it's type, i.e. it is in the
        // current scope of class
        List<VariableInformation> classFields = SymbolTable.classFieldsMap.get(argu.firstObj);

        if (classFields != null) {
            for (VariableInformation var : classFields) {
                if (var.getVarName().equals(identifierName)) {
                    return var;
                }
            }
        }

        // unknown type
        return null;
    }

    public String getLLVMSize(String identifierType) {

        switch (identifierType) {
            case "int":
                return "i32";
            case "boolean":
                return "i1";
            case "int[]":
            case "boolean[]":
                return "i32*";
            default:
                return "i8*";
        }
    }

    public String allocateLocalVariable(String idType, String idName) {

        // get the llvm size for the identifier type
        String llvmSize = getLLVMSize(idType);

        // allocate a local variable in the current method
        BuildTools.emit("\t%" + idName + " = alloca " + llvmSize);

        return null;
    }

    public boolean isMethodVar(String identifierName, CustomPair<ClassInformation, MethodInformation> classMethodPair) {

        // check if the identifier is a local variable
        List<VariableInformation> methodVars = SymbolTable.methodVariablesMap.get(classMethodPair);

        // only check if the method variables list is not null, i.e. empty
        if (methodVars != null) {
            for (VariableInformation var : methodVars) {
                if (var.getVarName().equals(identifierName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isMethodParam(String identifierName,
            CustomPair<ClassInformation, MethodInformation> classMethodPair) {

        // check if the identifier is a method parameter
        List<VariableInformation> methodParams = SymbolTable.methodParametersMap.get(classMethodPair);

        // only check if the method parameters list is not null, i.e. empty
        if (methodParams != null) {
            for (VariableInformation var : methodParams) {
                if (var.getVarName().equals(identifierName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isClassField(String identifierName) {

        // check if the identifier is a class field
        List<VariableInformation> classFields = SymbolTable.classFieldsMap.get(currentClass);

        // only check if the class fields list is not null, i.e. empty
        if (classFields != null) {
            for (VariableInformation var : classFields) {
                if (var.getVarName().equals(identifierName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void storeClassField(ClassInformation _class, String identifierName, String llvmSize, String expressionResultRegister) {

        // create a new register to store the pointer of the identifier
        String pointerRegister = BuildTools.newRegister("i8*");

        // int to store the offset of the inherited field
        int offset = -1;


        // get the offset of the identifier in the class fields
        List<CustomPair<VariableInformation, Integer>> fieldOffsets = Offsets.classFieldOffsetMap.get(_class);

        for (CustomPair<VariableInformation, Integer> fieldOffset : fieldOffsets) {
            if (fieldOffset.firstObj.getVarName().equals(identifierName)) {
                offset = fieldOffset.secondObj;
                break;
            }
        }

        // emit getelementptr instruction to get the pointer of the identifier
        // adding 8 since the vtable pointer in the start of the object is 8 bytes long
        // and all fields are after that
        BuildTools.emit("\t" + pointerRegister + " = getelementptr i8, i8* %this, i32 " + (offset + 8));

        // create a new register to bitcast the pointer to the correct type
        String classFieldCastedRegister = BuildTools.newRegister(llvmSize);

        // bitcast the pointer to the correct type
        BuildTools.emit(
                "\t" + classFieldCastedRegister + " = bitcast i8* " + pointerRegister + " to " + llvmSize + "*");

        // emit store instuction to store the result of the expression in the class
        // field register
        BuildTools.emit("\tstore " + llvmSize + " " + expressionResultRegister + ", " + llvmSize + "* "
                + classFieldCastedRegister);

        return;
    }

    public String loadClassField(ClassInformation _class, String identifierName, String identifierSize) {

        // create new object register
        String identifierPointerRegister = BuildTools.newRegister("i8*");

        // list to the pairs of variables and offsets
        List<CustomPair<VariableInformation, Integer>> variableOffsetPairs = Offsets.classFieldOffsetMap
                .get(_class);

        // initialize the offset to 8, since theres a pointer to the vtable in the
        // begining of the object
        int offset = 8;

        // loop through the pairs of variables and offsets
        for (CustomPair<VariableInformation, Integer> fieldOffestPair : variableOffsetPairs) {

            // check if the name of the identifier is the same as the name of the field
            if (fieldOffestPair.firstObj.getVarName().equals(identifierName)) {
                offset += fieldOffestPair.secondObj;
                break;
            }
        }

        // get the element pointer with getelementptr
        BuildTools.emit("\t" + identifierPointerRegister + " = getelementptr i8, i8* %this, i32 " + offset);

        // create new register for the field
        String identifierFieldPointerRegister = BuildTools.newRegister(identifierSize);

        // bit cast from i8* to the type of the field
        BuildTools.emit("\t" + identifierFieldPointerRegister + " = bitcast i8* " + identifierPointerRegister
                + " to " + identifierSize + "*");

        // create new register for the field value
        String identifierFieldValueRegister = BuildTools.newRegister(identifierSize);

        // load the value of the field into the register
        BuildTools.emit("\t" + identifierFieldValueRegister + " = load " + identifierSize + ", " + identifierSize
                + "* " + identifierFieldPointerRegister);

        // return the register with the field value
        return identifierFieldValueRegister;
    }

    @Override
    public String visit(Goal n, SemanticAnalysis argu) throws Exception {

        // build the argu object for the visitor
        ClassInformation mainClass = SymbolTable.classesMap.get(n.f0.f1.f0.toString());

        // get the first element of the method list of the main class since there should
        // be only one item in this list, the main method
        MethodInformation mainMethod = SymbolTable.classMethodsMap.get(mainClass).get(0);

        // set the current class and method to the main class and method
        currentClass = mainClass;
        currentMethod = mainMethod;

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

    @Override
    public String visit(MainClass n, SemanticAnalysis argu) throws Exception {

        // reset the counters for register and labels
        BuildTools.resetCounters();

        // write the main method signature to the LL file
        BuildTools.emit("define i32 @main() {");

        // accept all the variable declarations of the main method
        for (Node variableDeclaration : n.f14.nodes) {
            variableDeclaration.accept(this, argu);
        }

        // for each statement inside the main method
        for (Node statement : n.f15.nodes) {

            // accept the statement
            // argu is the current scope, i.e. the class name and the method name
            // since this is the main method, argu is provided by the main method
            statement.accept(this, argu);
        }

        // main method must return 0, emit to .ll file
        BuildTools.emit("\tret i32 0");

        BuildTools.emit("}");

        // emit a new line to the .ll file
        // pretty printing
        BuildTools.emit("");

        // nothing to return
        return null;
    }

    @Override
    public String visit(ClassDeclaration n, SemanticAnalysis argu) throws Exception {

        // get class name
        String className = n.f1.f0.toString();

        // get the class information from the symbol table
        ClassInformation classInfo = SymbolTable.classesMap.get(className);

        // we can skip the variable declarations since they are already done
        // set the current class to the current class
        currentClass = classInfo;

        // accept all the method declarations
        // iterate through the method declartations
        for (Node methodDeclaration : n.f4.nodes) {
            // reset the counters for register and labels
            BuildTools.resetCounters();

            methodDeclaration.accept(this, argu);
        }

        // nothing to return
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n, SemanticAnalysis argu) throws Exception {

        // get class name
        String className = n.f1.f0.toString();

        // get the class information from the symbol table
        ClassInformation classInfo = SymbolTable.classesMap.get(className);

        // we can skip the variable declarations since they are already done
        // set the current class to the current class
        currentClass = classInfo;

        // accept all the method declarations
        // iterate through the method declartations
        for (Node methodDeclaration : n.f6.nodes) {

            // reset the counters for register and labels
            BuildTools.resetCounters();

            methodDeclaration.accept(this, argu);
        }

        // nothing to return
        return null;
    }

    @Override
    public String visit(MethodDeclaration n, SemanticAnalysis argu) throws Exception {


        // get the name of the method
        String methodName = n.f2.f0.toString();

        // get the method information object
        MethodInformation method = null;

        // iterate through the list of methods in the class to find the right object
        List<MethodInformation> classMethods = SymbolTable.classMethodsMap.get(currentClass);

        for (MethodInformation methodInfo : classMethods) {
            if (methodInfo.getMethodName().equals(methodName)) {
                method = methodInfo;
                break;
            }
        }

        // set the current method to the method found above
        currentMethod = method;

        // create new class method pair to find the type of the method
        CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(currentClass, method);

        // get the method type from the symbol table
        String methodType = SymbolTable.classMethodTypeMap.get(classMethodPair);

        // build the signature of the method
        String methodSignature = "define " + getLLVMSize(methodType) + " @" + currentClass.getClassName() + "."
                + methodName + "(i8* %this";

        // get the list of the method parameters
        List<VariableInformation> methodParams = SymbolTable.methodParametersMap.get(classMethodPair);

        if (methodParams != null) {

            // iterate through the method parameters
            for (VariableInformation param : methodParams) {

                // new pair of classMethod pair and variable information object
                CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> classMethodVarPair = new CustomPair<>(
                        classMethodPair, param);

                methodSignature += ", " + getLLVMSize(SymbolTable.methodParameterTypeMap.get(classMethodVarPair))
                        + " %." + param.getVarName();
            }
        }

        // finish the method signature
        methodSignature += ") {";

        // emit the method signature to the .ll file
        BuildTools.emit(methodSignature);

        // allocate memory for every method parameter, if it has
        if (methodParams != null) {

            // iterate through the method parameters
            for (VariableInformation param : methodParams) {

                // new pair of classMethod pair and variable information object
                CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> classMethodVarPair = new CustomPair<>(
                        classMethodPair, param);

                // emit alloca instruction to allocate memory for the method parameter
                BuildTools.emit("\t%" + param.getVarName() + " = alloca "
                        + getLLVMSize(SymbolTable.methodParameterTypeMap.get(classMethodVarPair)));

                // emit store instruction to store the method parameter in the allocated memory
                BuildTools.emit("\tstore " + getLLVMSize(SymbolTable.methodParameterTypeMap.get(classMethodVarPair))
                        + " %." + param.getVarName() + ", "
                        + getLLVMSize(SymbolTable.methodParameterTypeMap.get(classMethodVarPair)) + "* %"
                        + param.getVarName());
            }
        }

        // accept all the variable declarations
        // iterate through the variable declarations
        for (Node varDeclaration : n.f7.nodes) {
            varDeclaration.accept(this, argu);
        }

        // accept all statements in the method
        for (Node statement : n.f8.nodes) {
            statement.accept(this, argu);
        }

        // get the result of the return expression of the method
        String returnResultRegister = n.f10.accept(this, argu);

        // emit the return instruction to return the result of the method
        BuildTools.emit("\tret " + getLLVMSize(methodType) + " " + returnResultRegister);

        BuildTools.emit("}");

        // emit new line
        // pretty print
        BuildTools.emit("");

        // nothing to return
        return null;
    }

    @Override
    public String visit(VarDeclaration n, SemanticAnalysis argu) throws Exception {

        // get the type of the identifier
        String identifierType = n.f0.accept(this, argu);

        // get the name of the identifier
        String identifierName = n.f1.f0.toString();

        // if we're in the main method or not
        // if we are every type declaration is a variable declaration
        if (currentMethod.getMethodName().equals("main")) {

            // allocate the local variable in the main method
            allocateLocalVariable(identifierType, identifierName);
        } else {

            // we're not in the main method so we're in a class method
            // create a custom pair of class and method to use as a key for the symbol table
            // maps
            CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(currentClass,
                    currentMethod);

            // list of variables in the current method
            List<VariableInformation> methodVars = SymbolTable.methodVariablesMap.get(classMethodPair);

            // list of class fields in the current class
            List<VariableInformation> classFields = SymbolTable.classFieldsMap.get(currentClass);

            // boolean to know if the variable is allocated or not
            boolean isAllocated = false;

            // check if the identifier is a local variable first
            if (methodVars != null && !isAllocated) {
                for (VariableInformation var : methodVars) {
                    if (var.getVarName().equals(identifierName)) {
                        allocateLocalVariable(identifierType, identifierName);
                        isAllocated = true;
                        break;
                    }
                }
            }

            // check if the identifier is a class field next
            if (classFields != null && !isAllocated) {
                for (VariableInformation var : classFields) {
                    if (var.getVarName().equals(identifierName)) {
                        allocateLocalVariable(identifierType, identifierName);
                        isAllocated = true;
                        break;
                    }
                }
            }

        }

        // statement nothing to return
        return null;
    }

    @Override
    public String visit(Type n, SemanticAnalysis argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(ArrayType n, SemanticAnalysis argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(BooleanArrayType n, SemanticAnalysis argu) throws Exception {

        return "boolean[]";
    }

    @Override
    public String visit(IntegerArrayType n, SemanticAnalysis argu) throws Exception {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, SemanticAnalysis argu) throws Exception {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, SemanticAnalysis argu) throws Exception {
        return "int";
    }

    @Override
    public String visit(Statement n, SemanticAnalysis argu) throws Exception {

        // accept the statement, this will produce code for the statement
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(Block n, SemanticAnalysis argu) throws Exception {

        // accept each of the statements in the block
        for (Node statment : n.f1.nodes) {
            statment.accept(this, argu);
        }

        return null;
    }

    @Override
    public String visit(AssignmentStatement n, SemanticAnalysis argu) throws Exception {

        // custom pair of current class and method to use as a key for the symbol table
        // maps
        CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(this.currentClass,
                this.currentMethod);

        // get the name of the identifier
        String identifierName = n.f0.f0.toString();

        // get the type of the identifier
        String identifierType = getIdentifierType(n.f0.f0.toString(), classMethodPair);

        // boolean to know if the variable is an inherited field
        Boolean isInheritedField = false;

        // pair of class and variable information object of the inherited field
        CustomPair<ClassInformation, VariableInformation> inheritedFieldPair = null;

        // if the identifier type is null check if the identifier is a super class field
        if (identifierType == null) {

            // get super class of the current class
            ClassInformation superClass = SymbolTable.classInheritenceMap.get(this.currentClass);

            // if the super class is not null
            if (superClass != null) {

                // check every class in the inheritence tree
                while (superClass != null) {

                    // get the class fields
                    List<VariableInformation> classFields = SymbolTable.classFieldsMap.get(superClass);

                    // if the class fields is not null
                    if (classFields != null) {

                        // check every class field
                        for (VariableInformation var : classFields) {

                            // if the class field name matches the identifier name
                            if (var.getVarName().equals(identifierName)) {

                                // custom pair of class info and var info to use as key for the map below
                                inheritedFieldPair = new CustomPair<>(superClass, var);

                                // set the identifier type to the class field type
                                identifierType = SymbolTable.classFieldTypeMap.get(inheritedFieldPair);

                                // set the isInheritedField to true
                                isInheritedField = true;

                                // break out of the loop
                                break;
                            }
                        }
                    }

                    if (isInheritedField) {
                        break;
                    }

                    // get the super class of the current class
                    superClass = SymbolTable.classInheritenceMap.get(superClass);
                }
            }
        }

        // get llvm size of the identifier type
        String llvmSize = getLLVMSize(identifierType);

        // get the expression result
        String expressionResultRegister = n.f2.accept(this, argu);

        // check if we're in the main method or not
        if (isInheritedField) {

            // call store class field with the super class
            storeClassField(inheritedFieldPair.firstObj, identifierName, llvmSize, expressionResultRegister);
        }
        else if (currentMethod.getMethodName().equals("main") || isMethodVar(identifierName, classMethodPair)
                || isMethodParam(identifierName, classMethodPair)) {

            // emit store instruction to store the expression result in the local variable
            BuildTools.emit(
                    "\tstore " + llvmSize + " " + expressionResultRegister + ", " + llvmSize + "* %" + identifierName);
        } else if (isClassField(identifierName)) {

            // call store class field with the the current class
            storeClassField(currentClass, identifierName, llvmSize, expressionResultRegister);
        }

        return null;
    }

    @Override
    public String visit(ArrayAssignmentStatement n, SemanticAnalysis argu) throws Exception {

        // get the name of the identifier
        String identifierName = n.f0.accept(this, argu);

        // since it's an array the size is i32*
        String arraySizeRegister = BuildTools.newRegister("i32");

        // load the array size into the array size register
        BuildTools.emit("\t" + arraySizeRegister + " = load i32, i32* " + identifierName);

        // get the result of the expression acting as the index
        String indexResultRegister = n.f2.accept(this, argu);

        // create register for greater than or equal to zero check
        String zeroCheckRegister = BuildTools.newRegister("i1");

        // check if the index is greater than or equal to zero
        BuildTools.emit("\t" + zeroCheckRegister + " = icmp sge i32 " + indexResultRegister + ", 0");

        // inbound check
        String inboundCheckRegister = BuildTools.newRegister("i1");

        // check if the index is less than the array size
        BuildTools.emit(
                "\t" + inboundCheckRegister + " = icmp slt i32 " + indexResultRegister + ", " + arraySizeRegister);

        // combine these two checks
        String andRegister = BuildTools.newRegister("i1");
        BuildTools.emit("\t" + andRegister + " = and i1 " + zeroCheckRegister + ", " + inboundCheckRegister);

        // labels for out of bounds check
        String arrErrorLabel = BuildTools.newLabel("arrError", false);
        String oobLabel = BuildTools.newLabel("oob", false);
        String nszLabel = BuildTools.newLabel("nsz", false);
        String oobLabelOk = BuildTools.newLabel("oobOk", true);

        // add branch instruction to ll file
        BuildTools.emit("\tbr i1 " + andRegister + ", label %" + oobLabelOk + ", label %" + arrErrorLabel);

        // add oob label
        BuildTools.emit(arrErrorLabel + ":");

        // add branch instruction to ll file, check if the size index is negative or
        // greater than the length
        BuildTools.emit("\tbr i1 " + inboundCheckRegister + ", label %" + nszLabel + ", label %" + oobLabel);

        // add oob label
        BuildTools.emit(oobLabel + ":");

        // add error message
        BuildTools.emit("\tcall void @throw_oob()");

        // branch to oob ok label
        BuildTools.emit("\tbr label %" + oobLabelOk);

        // add nsz label
        BuildTools.emit(nszLabel + ":");

        // add error message
        BuildTools.emit("\tcall void @throw_nsz()");

        // branch to oob ok label
        BuildTools.emit("\tbr label %" + oobLabelOk);

        // add oob ok label to ll file
        BuildTools.emit(oobLabelOk + ":");

        // add one to the index register, create a new register to store the actual
        // index
        String indexRegister = BuildTools.newRegister("i32");
        BuildTools.emit("\t" + indexRegister + " = add i32 " + indexResultRegister + ", 1");

        // get the result of the expression
        String expressionResultRegister = n.f5.accept(this, argu);

        // check if the expression result is in a register or not
        if (BuildTools.registerMap.containsKey(expressionResultRegister)) {

            // if it is, check the type of the expression result
            // if it is i1 then we need to convert it to i32 using zext
            if (BuildTools.registerMap.get(expressionResultRegister).getRegisterType().equals("i1")) {

                // create a new register to store the zext result
                String zextRegister = BuildTools.newRegister("i32");

                // emit the zext instruction
                BuildTools.emit("\t" + zextRegister + " = zext i1 " + expressionResultRegister + " to i32");

                // make expression result register the zext register
                expressionResultRegister = zextRegister;
            }
        } else {

            // if it is not, check if the expression result is a boolean literal, i.e. true,
            // false
            if (expressionResultRegister.equals("true")) {

                // convert the boolean literal to i32 using zext
                String trueRegister = BuildTools.newRegister("i32");

                // emit the zext instruction
                BuildTools.emit("\t" + trueRegister + " = zext i1 true to i32");

                expressionResultRegister = trueRegister;
            } else if (expressionResultRegister.equals("false")) {

                // convert the boolean literal to i32 using zext
                String falseRegister = BuildTools.newRegister("i32");

                // emit the zext instruction
                BuildTools.emit("\t" + falseRegister + " = zext i1 false to i32");

                expressionResultRegister = falseRegister;
            }
        }

        // get the pointer to the index + 1 element of the array
        // create a new register for the pointer
        String pointerRegister = BuildTools.newRegister("i32*");

        // calculate the pointer to the index + 1 element of the array
        BuildTools.emit(
                "\t" + pointerRegister + " = getelementptr i32, i32* " + identifierName + ", i32 " + indexRegister);

        // store the result of the expression in the pointer
        BuildTools.emit("\tstore i32 " + expressionResultRegister + ", i32* " + pointerRegister);

        return null;
    }

    @Override
    public String visit(IfStatement n, SemanticAnalysis argu) throws Exception {

        // accept the expression and get the result register
        String expressionResultRegister = n.f2.accept(this, argu);

        // check if the expression result is in a register or not
        if (BuildTools.registerMap.containsKey(expressionResultRegister)) {

            // if it is, check the type of the expression result
            // if it is i32 then we need to convert it to i1 using trunc
            if (BuildTools.registerMap.get(expressionResultRegister).getRegisterType().equals("i32")) {

                // create a new register to store the zext result
                String truncRegister = BuildTools.newRegister("i1");

                // emit the trunc instruction
                BuildTools.emit("\t" + truncRegister + " = trunc i32 " + expressionResultRegister + " to i1");

                // make expression result register the zext register
                expressionResultRegister = truncRegister;
            }
        }

        // create if else exit labels
        String ifLabel = BuildTools.newLabel("ifLabel", false);
        String elseLabel = BuildTools.newLabel("elseLabel", false);
        String exitLabel = BuildTools.newLabel("ifStmtExitLabel", true);

        // emit the branch instruction to ll file
        BuildTools.emit("\tbr i1 " + expressionResultRegister + ", label %" + ifLabel + ", label %" + elseLabel);

        // emit the if label
        BuildTools.emit(ifLabel + ":");

        // accept the if statement
        n.f4.accept(this, argu);

        // emit the branch to exit instruction to ll file
        BuildTools.emit("\tbr label %" + exitLabel);

        // emit the else label
        BuildTools.emit(elseLabel + ":");

        // accept the else statement
        n.f6.accept(this, argu);

        // emit the branch to exit instruction to ll file
        BuildTools.emit("\tbr label %" + exitLabel);

        // emit the exit label
        BuildTools.emit(exitLabel + ":");

        return null;
    }

    @Override
    public String visit(WhileStatement n, SemanticAnalysis argu) throws Exception {

        // create while, while loop and while exit label
        String whileLabel = BuildTools.newLabel("whileLabel", false);
        String whileLoopLabel = BuildTools.newLabel("whileLoopLabel", false);
        String whileExitLabel = BuildTools.newLabel("whileExitLabel", true);

        // emit the branch to while instruction to ll file
        BuildTools.emit("\tbr label %" + whileLabel);

        // emit the while label
        BuildTools.emit(whileLabel + ":");

        // accept the expression and get the result register
        String expressionResultRegister = n.f2.accept(this, argu);

        // emit the branch instruction to ll file
        BuildTools.emit(
                "\tbr i1 " + expressionResultRegister + ", label %" + whileLoopLabel + ", label %" + whileExitLabel);

        // emit the while loop label
        BuildTools.emit(whileLoopLabel + ":");

        // accept the while statement
        n.f4.accept(this, argu);

        // emit the branch to while instruction to ll file
        BuildTools.emit("\tbr label %" + whileLabel);

        // emit the while exit label
        BuildTools.emit(whileExitLabel + ":");

        return null;
    }

    @Override
    public String visit(PrintStatement n, SemanticAnalysis argu) throws Exception {

        // accept the expression and get the result register
        String expressionResultRegister = n.f2.accept(this, argu);

        // add the print instruction to the LL file
        BuildTools.emit("\t" + "call void (i32) @print_int(i32 " + expressionResultRegister + ")");

        return null;
    }

    @Override
    public String visit(Expression n, SemanticAnalysis argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(AndExpression n, SemanticAnalysis argu) throws Exception {

        // create and entry label, and label, and exit label
        String andLabel0 = BuildTools.newLabel("andLabel", true);
        String andLabel1 = BuildTools.newLabel("andLabel", true);
        String andLabel2 = BuildTools.newLabel("andLabel", true);
        String andLabel3 = BuildTools.newLabel("andLabel", true);

        // accept the left clause and get the result register
        String leftClauseResultRegister = n.f0.accept(this, argu);

        // check if the expression result is in a register or not
        if (BuildTools.registerMap.containsKey(leftClauseResultRegister)) {

            // if it is, check the type of the expression result
            // if it is i32 then we need to convert it to i1 using trunc
            if (BuildTools.registerMap.get(leftClauseResultRegister).getRegisterType().equals("i32")) {

                // create a new register to store the zext result
                String truncRegister = BuildTools.newRegister("i1");

                // emit the trunc instruction
                BuildTools.emit("\t" + truncRegister + " = trunc i32 " + leftClauseResultRegister + " to i1");

                // make expression result register the zext register
                leftClauseResultRegister = truncRegister;
            }
        }

        // emit the branch instruction to ll file
        BuildTools.emit("\tbr i1 " + leftClauseResultRegister + ", label %" + andLabel1 + ", label %" + andLabel0);

        // emit the and entry label to ll file
        BuildTools.emit(andLabel0 + ":");

        // emit branch to exit label to ll file
        BuildTools.emit("\tbr label %" + andLabel3);

        // emit the and right label to ll file
        BuildTools.emit(andLabel1 + ":");

        // accept the right clause and get the result register
        String rightClauseResultRegister = n.f2.accept(this, argu);

        // check if the expression result is in a register or not
        if (BuildTools.registerMap.containsKey(rightClauseResultRegister)) {

            // if it is, check the type of the expression result
            // if it is i32 then we need to convert it to i1 using trunc
            if (BuildTools.registerMap.get(rightClauseResultRegister).getRegisterType().equals("i32")) {

                // create a new register to store the zext result
                String truncRegister = BuildTools.newRegister("i1");

                // emit the trunc instruction
                BuildTools.emit("\t" + truncRegister + " = trunc i32 " + rightClauseResultRegister + " to i1");

                // make expression result register the zext register
                rightClauseResultRegister = truncRegister;
            }
        }

        // emit the branch instruction to ll file
        BuildTools.emit("\tbr label %" + andLabel2);

        // emit the and label 2 to ll file
        BuildTools.emit(andLabel2 + ":");

        // emit the branch to exit label to ll file
        BuildTools.emit("\tbr label %" + andLabel3);

        // emit the and exit label to ll file
        BuildTools.emit(andLabel3 + ":");

        // create register for the result of the expression
        String resultRegister = BuildTools.newRegister("i1");

        // emit the phi instruction to ll file
        BuildTools.emit("\t" + resultRegister + " = phi i1 [0, %" + andLabel0 + "], [" + rightClauseResultRegister
                + ", %" + andLabel2 + "]");

        // return the result register
        return resultRegister;

    }

    @Override
    public String visit(CompareExpression n, SemanticAnalysis argu) throws Exception {

        // accept the left and right primary expressions and get the result registers
        String leftPrimaryResultRegister = n.f0.accept(this, argu);
        String rightPrimaryResultRegister = n.f2.accept(this, argu);

        // create new register for the result of the comparison
        String resultRegister = BuildTools.newRegister("i1");

        // emit the compare instruction to ll file
        BuildTools.emit("\t" + resultRegister + " = icmp slt i32 " + leftPrimaryResultRegister + ", "
                + rightPrimaryResultRegister);

        // return the result register
        return resultRegister;
    }

    @Override
    public String visit(PlusExpression n, SemanticAnalysis argu) throws Exception {

        // accept the left and right primary expressions and get the result registers
        String leftPrimaryResultRegister = n.f0.accept(this, argu);
        String rightPrimaryResultRegister = n.f2.accept(this, argu);

        // create new register for the result of the addition
        String resultRegister = BuildTools.newRegister("i32");

        // emit the add instruction to ll file
        BuildTools.emit(
                "\t" + resultRegister + " = add i32 " + leftPrimaryResultRegister + ", " + rightPrimaryResultRegister);

        // return the result register
        return resultRegister;
    }

    @Override
    public String visit(MinusExpression n, SemanticAnalysis argu) throws Exception {

        // accept the left and right primary expressions and get the result registers
        String leftPrimaryResultRegister = n.f0.accept(this, argu);
        String rightPrimaryResultRegister = n.f2.accept(this, argu);

        // create new register for the result of the subtraction
        String resultRegister = BuildTools.newRegister("i32");

        // emit the sub instruction to ll file
        BuildTools.emit(
                "\t" + resultRegister + " = sub i32 " + leftPrimaryResultRegister + ", " + rightPrimaryResultRegister);

        // return the result register
        return resultRegister;
    }

    @Override
    public String visit(TimesExpression n, SemanticAnalysis argu) throws Exception {

        // accept the left and right primary expressions and get the result registers
        String leftPrimaryResultRegister = n.f0.accept(this, argu);
        String rightPrimaryResultRegister = n.f2.accept(this, argu);

        // create new register for the result of the multiplication
        String resultRegister = BuildTools.newRegister("i32");

        // emit the mul instruction to ll file
        BuildTools.emit(
                "\t" + resultRegister + " = mul i32 " + leftPrimaryResultRegister + ", " + rightPrimaryResultRegister);

        // return the result register
        return resultRegister;
    }

    @Override
    public String visit(ArrayLookup n, SemanticAnalysis argu) throws Exception {

        // accept the first primary expression which is the array and get the result
        // register
        String arrayResultRegister = n.f0.accept(this, argu);

        // create new register for the array size
        String arraySizeRegister = BuildTools.newRegister("i32");

        // get the array size from the array result register
        BuildTools.emit("\t" + arraySizeRegister + " = load i32, i32* " + arrayResultRegister);

        // accept the second primary expression which is the index and get the result
        // register
        String indexResultRegister = n.f2.accept(this, argu);

        // create register for greater than or equal to zero check
        String zeroCheckRegister = BuildTools.newRegister("i1");

        // check if the index is greater than or equal to zero
        BuildTools.emit("\t" + zeroCheckRegister + " = icmp sge i32 " + indexResultRegister + ", 0");

        // inbound check
        String inboundCheckRegister = BuildTools.newRegister("i1");

        // check if the index is less than the array size
        BuildTools.emit(
                "\t" + inboundCheckRegister + " = icmp slt i32 " + indexResultRegister + ", " + arraySizeRegister);

        // combine these two checks
        String andRegister = BuildTools.newRegister("i1");
        BuildTools.emit("\t" + andRegister + " = and i1 " + zeroCheckRegister + ", " + inboundCheckRegister);

        // labels for out of bounds check
        String arrErrorLabel = BuildTools.newLabel("arrError", false);
        String oobLabel = BuildTools.newLabel("oob", false);
        String nszLabel = BuildTools.newLabel("nsz", false);
        String oobLabelOk = BuildTools.newLabel("oobOk", true);

        // add branch instruction to ll file
        BuildTools.emit("\tbr i1 " + andRegister + ", label %" + oobLabelOk + ", label %" + arrErrorLabel);

        // add oob label
        BuildTools.emit(arrErrorLabel + ":");

        // add branch instruction to ll file, check if the size index is negative or
        // greater than the length
        BuildTools.emit("\tbr i1 " + inboundCheckRegister + ", label %" + nszLabel + ", label %" + oobLabel);

        // add oob label
        BuildTools.emit(oobLabel + ":");

        // add error message
        BuildTools.emit("\tcall void @throw_oob()");

        // branch to oob ok label
        BuildTools.emit("\tbr label %" + oobLabelOk);

        // add nsz label
        BuildTools.emit(nszLabel + ":");

        // add error message
        BuildTools.emit("\tcall void @throw_nsz()");

        // branch to oob ok label
        BuildTools.emit("\tbr label %" + oobLabelOk);

        // add oob ok label to ll file
        BuildTools.emit(oobLabelOk + ":");

        // add one to the index register, create a new register to store the actual
        // index
        String indexRegister = BuildTools.newRegister("i32");
        BuildTools.emit("\t" + indexRegister + " = add i32 " + indexResultRegister + ", 1");

        // get the pointer to the index + 1 element of the array
        // create a new register for the pointer
        String pointerRegister = BuildTools.newRegister("i32*");

        // calculate the pointer to the index + 1 element of the array
        BuildTools.emit("\t" + pointerRegister + " = getelementptr i32, i32* " + arrayResultRegister + ", i32 "
                + indexRegister);

        // create result register
        String resultRegister = BuildTools.newRegister("i32");

        // load the value of the index + 1 element of the array into the result register
        BuildTools.emit("\t" + resultRegister + " = load i32, i32* " + pointerRegister);

        return resultRegister;
    }

    @Override
    public String visit(ArrayLength n, SemanticAnalysis argu) throws Exception {

        // accept the primary expression
        String arrayRegister = n.f0.accept(this, argu);

        // create new register to store the length of the array
        String arrayLengthRegister = BuildTools.newRegister("i32");

        // load the length of the array into the register
        BuildTools.emit("\t" + arrayLengthRegister + " = load i32, i32* " + arrayRegister);

        return arrayLengthRegister;
    }

    @Override
    public String visit(MessageSend n, SemanticAnalysis argu) throws Exception {

        // get the result register of the primary expression
        String primaryResultRegister = n.f0.accept(this, argu);

        ClassInformation classInfo = null;

        // getting the correct class if the primary expression is not this
        if (!primaryResultRegister.equals("%this")) {

            // using the semantic analysis visitor for the primary expression
            // since it's easier to get the type of the expression and the class that the information is
            // since every method is visible to every class
            CustomPair<ClassInformation, MethodInformation> pair = new CustomPair<>(currentClass, currentMethod);
            String primaryExpressionType = argu.visit(n.f0, pair);

            // get the class information of the method that we want to call
            classInfo = SymbolTable.classesMap.get(primaryExpressionType);
        }

        // get the name of the method to call
        String methodName = n.f2.f0.toString();

        // create a new register to load the object from the primary expression
        String objectRegister = BuildTools.newRegister("i8*");

        // emit comment to ll file
        BuildTools.emit("\t; " + (classInfo == null ? currentClass.getClassName() : classInfo.getClassName()) + ":" + n.f2.f0.toString());

        // load the object
        BuildTools.emit("\t" + objectRegister + " = bitcast i8* " + primaryResultRegister + " to i8***");

        // create a new register to store the vtable of the object
        String vtableRegister = BuildTools.newRegister("i8**");

        // load the vtable of the object into the register
        BuildTools.emit("\t" + vtableRegister + " = load i8**, i8*** " + objectRegister);

        // create a new register to store the function pointer
        String arrayFunctionRegister = BuildTools.newRegister("i8*");

        // get the offset of the method in the vtable
        List<CustomPair<MethodInformation, Integer>> methodOffsets = Offsets.classMethodOffsetMap
                .get((classInfo == null ? currentClass : classInfo));

        // get the offset of the method in the vtable
        MethodInformation method = null;

        for (CustomPair<MethodInformation, Integer> pair : methodOffsets) {
            if (pair.firstObj.getMethodName().equals(methodName)) {
                method = pair.firstObj;
                break;
            }
        }

        // if the method is null check for the method in the superclass
        if (method == null) {

            // get the superclass of the class that the method is in
            ClassInformation superClass = SymbolTable.classInheritenceMap.get((classInfo == null ? currentClass : classInfo));

            // get the method from the superclass
            // iterate through all the super classes until found
            // or until the super class is null
            while (superClass != null) {

                // get super class method offsets
                List<CustomPair<MethodInformation, Integer>> superClassMethodOffsets = Offsets.classMethodOffsetMap.get(superClass);

                // method found in the super class
                // break out of the while loop
                boolean doBreak = false;

                // smae check as in line 1288 -> 1293
                for (CustomPair<MethodInformation, Integer> pair : superClassMethodOffsets) {

                    if (pair.firstObj.getMethodName().equals(methodName)) {

                        method = pair.firstObj;

                        // the new class info variable is the super class
                        classInfo = superClass;

                        // set the break flag to true
                        doBreak = true;
                        break;
                    }
                }

                // if found break out of the while loop
                if (doBreak) {
                    break;
                }

                // continue to the next super class
                superClass = SymbolTable.classInheritenceMap.get(superClass);
            }

            // check if we have this with an inherited method
            if (primaryResultRegister.equals("%this")) {
                
            }
        }

        CustomPair<ClassInformation, MethodInformation> pair = new CustomPair<>(
                (classInfo == null ? currentClass : classInfo), method);

        // load the function pointer from the vtable into the register
        BuildTools.emit("\t" + arrayFunctionRegister + " = getelementptr i8*, i8** " + vtableRegister + ", i32 "
                + (BuildTools.vtalbeMap.get(pair) / 8));

        // create a new register to store the function pointer
        String functionRegister = BuildTools.newRegister("i8*");

        // load the function pointer into the register
        BuildTools.emit("\t" + functionRegister + " = load i8*, i8** " + arrayFunctionRegister);

        // create a new register to cast the function pointer to the correct signature
        String functionPointerRegister = BuildTools
                .newRegister(BuildTools.methodSignatureInVtableMap.get(pair).split(" ")[0]);

        // cast the function pointer to the correct signature
        BuildTools.emit("\t" + functionPointerRegister + " = bitcast i8* " + functionRegister + " to "
                + BuildTools.methodSignatureInVtableMap.get(pair) + "*");

        // create a new register to store the result of the function call
        String resultRegister = BuildTools.newRegister(BuildTools.methodSignatureInVtableMap.get(pair).split(" ")[0]);

        // first accept the expression list and get the code for that
        String expressionList = n.f4.accept(this, argu);

        // emit call instuction to call the function
        BuildTools.emit("\t" + resultRegister + " = call "
                + BuildTools.registerMap.get(resultRegister).getRegisterType() + " " + functionPointerRegister + "(i8* "
                + primaryResultRegister + (expressionList == null ? "" : ", " + expressionList) + ")");

        // return the result register
        return resultRegister;

    }

    @Override
    public String visit(ExpressionList n, SemanticAnalysis argu) throws Exception {

        // custom pair of current class and current method
        CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(currentClass, currentMethod);

        // accept the expression and get the result register
        String expressionResultRegister = n.f0.accept(this, argu);

        // expression result type
        String expressionResultType = "";

        // check if expression result is a register to get the type
        // check if it is a local variable or a method parameter to get the type
        // if it is not a local variable or method parameter then check if it is a class
        // field in order to load it from the object
        // if it is not a class field check if it is a litteral to get the type
        if (BuildTools.registerMap.containsKey(expressionResultRegister)) {
            expressionResultType = BuildTools.registerMap.get(expressionResultRegister).getRegisterType();
        } else if (isMethodVar(expressionResultRegister, classMethodPair)) {

            // list of method variables to get the correct variable object
            List<VariableInformation> methodVars = SymbolTable.methodVariablesMap.get(classMethodPair);

            // get the type of the method parameter
            String variableType = "";

            // loop through the method variables to get the correct variable object
            for (VariableInformation variable : methodVars) {
                if (variable.getVarName().equals(expressionResultRegister)) {

                    // pair of classMethod and variable information object to get the type of the
                    // variable
                    CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> variablePair = new CustomPair<>(
                            classMethodPair, variable);

                    // get the type and break out of the loop
                    variableType = SymbolTable.methodVariableTypeMap.get(variablePair);
                    break;
                }
            }

            // set the type of the expression result register to the variable type
            expressionResultType = getLLVMSize(variableType);
        } else if (isMethodParam(expressionResultRegister, classMethodPair)) {

            // list of method variables to get the correct variable object
            List<VariableInformation> methodParams = SymbolTable.methodParametersMap.get(classMethodPair);

            // get the type of the method parameter
            String parameterType = "";

            // loop through the method variables to get the correct variable object
            for (VariableInformation variable : methodParams) {
                if (variable.getVarName().equals(expressionResultRegister)) {

                    // pair of classMethod and variable information object to get the type of the
                    // variable
                    CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> variablePair = new CustomPair<>(
                            classMethodPair, variable);

                    // get the type and break out of the loop
                    parameterType = SymbolTable.methodVariableTypeMap.get(variablePair);
                    break;
                }
            }

            // set the type of the expression result register to the variable type
            expressionResultType = getLLVMSize(parameterType);
        } else if (isClassField(expressionResultRegister)) {

            // get the variable information object of the class field
            VariableInformation variable = getIdentifier(expressionResultRegister, classMethodPair);

            // custom pair of class information and variable information object to get the
            // type of the variable
            CustomPair<ClassInformation, VariableInformation> classFieldPair = new CustomPair<>(currentClass, variable);

            // get the type of the class field
            String classFieldType = SymbolTable.classFieldTypeMap.get(classFieldPair);
            expressionResultType = getLLVMSize(classFieldType);

            // create new register to load the value of the class field into
            String classFieldPointerRegister = BuildTools.newRegister("i8*");

            // get the offset and add 8 since we stored the vtable in the first 8 bytes of
            // the object
            int offset = 8;

            // list of variable and offset pairs for the current class
            List<CustomPair<VariableInformation, Integer>> classFields = Offsets.classFieldOffsetMap.get(currentClass);

            // iterate through the class fields to get the correct offset
            for (CustomPair<VariableInformation, Integer> field : classFields) {
                if (field.firstObj.getVarName().equals(variable.getVarName())) {
                    offset += field.secondObj;
                    break;
                }
            }

            // get the pointer to the class field using getelementptr
            BuildTools.emit("\t" + classFieldPointerRegister + " = getelementptr i8, i8* " + expressionResultRegister
                    + ", i32 " + offset);

            // create a new register to bitcast the pointer to the correct type
            String classFieldCastedRegister = BuildTools.newRegister(expressionResultType);

            // bitcast the pointer to the correct type
            BuildTools.emit("\t" + classFieldCastedRegister + " = bitcast i8* " + classFieldPointerRegister + " to "
                    + expressionResultType + "*");

            // create a new register to load the value of the class field into
            String classFieldValueRegister = BuildTools.newRegister(expressionResultType);

            // load the value of the class field into the register
            BuildTools.emit("\t" + classFieldValueRegister + " = load " + expressionResultType + ", "
                    + expressionResultType + "* " + classFieldCastedRegister);

            // set the expression result register to the class field value register
            expressionResultRegister = classFieldValueRegister;
        }
        else if (expressionResultRegister.equals("%this")) {
            expressionResultType = "i8*";
        }
        else {
            expressionResultType = (expressionResultRegister.equals("true") || expressionResultRegister.equals("false"))
                    ? "i1"
                    : "i32";
        }

        // get the tail of the expression list
        String expressionTail = n.f1.accept(this, argu);

        // return the result register and it's type and the expression tail string
        return expressionResultType + " " + expressionResultRegister + expressionTail;

    }

    @Override
    public String visit(ExpressionTail n, SemanticAnalysis argu) throws Exception {

        // result string of the expression tail
        String expressionTail = "";

        // accept all the expression terms and get the resulting string
        for (Node node : n.f0.nodes) {
            expressionTail += node.accept(this, argu);
        }

        return expressionTail;
    }

    @Override
    public String visit(ExpressionTerm n, SemanticAnalysis argu) throws Exception {
        // custom pair of current class and current method
        CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(currentClass, currentMethod);

        // accept the expression and get the result register
        String expressionResultRegister = n.f1.accept(this, argu);

        // expression result type
        String expressionResultType = "";

        // check if expression result is a register to get the type
        // check if it is a local variable or a method parameter to get the type
        // if it is not a local variable or method parameter then check if it is a class
        // field in order to load it from the object
        // if it is not a class field check if it is a litteral to get the type
        if (BuildTools.registerMap.containsKey(expressionResultRegister)) {
            expressionResultType = BuildTools.registerMap.get(expressionResultRegister).getRegisterType();
        } else if (isMethodVar(expressionResultRegister, classMethodPair)) {

            // list of method variables to get the correct variable object
            List<VariableInformation> methodVars = SymbolTable.methodVariablesMap.get(classMethodPair);

            // get the type of the method parameter
            String variableType = "";

            // loop through the method variables to get the correct variable object
            for (VariableInformation variable : methodVars) {
                if (variable.getVarName().equals(expressionResultRegister)) {

                    // pair of classMethod and variable information object to get the type of the
                    // variable
                    CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> variablePair = new CustomPair<>(
                            classMethodPair, variable);

                    // get the type and break out of the loop
                    variableType = SymbolTable.methodVariableTypeMap.get(variablePair);
                    break;
                }
            }

            // set the type of the expression result register to the variable type
            expressionResultType = getLLVMSize(variableType);
        } else if (isMethodParam(expressionResultRegister, classMethodPair)) {

            // list of method variables to get the correct variable object
            List<VariableInformation> methodParams = SymbolTable.methodParametersMap.get(classMethodPair);

            // get the type of the method parameter
            String parameterType = "";

            // loop through the method variables to get the correct variable object
            for (VariableInformation variable : methodParams) {
                if (variable.getVarName().equals(expressionResultRegister)) {

                    // pair of classMethod and variable information object to get the type of the
                    // variable
                    CustomPair<CustomPair<ClassInformation, MethodInformation>, VariableInformation> variablePair = new CustomPair<>(
                            classMethodPair, variable);

                    // get the type and break out of the loop
                    parameterType = SymbolTable.methodVariableTypeMap.get(variablePair);
                    break;
                }
            }

            // set the type of the expression result register to the variable type
            expressionResultType = getLLVMSize(parameterType);
        } else if (isClassField(expressionResultRegister)) {

            // get the variable information object of the class field
            VariableInformation variable = getIdentifier(expressionResultRegister, classMethodPair);

            // custom pair of class information and variable information object to get the
            // type of the variable
            CustomPair<ClassInformation, VariableInformation> classFieldPair = new CustomPair<>(currentClass, variable);

            // get the type of the class field
            String classFieldType = SymbolTable.classFieldTypeMap.get(classFieldPair);
            expressionResultType = getLLVMSize(classFieldType);

            // create new register to load the value of the class field into
            String classFieldPointerRegister = BuildTools.newRegister("i8*");

            // get the offset and add 8 since we stored the vtable in the first 8 bytes of
            // the object
            int offset = 8;

            // list of variable and offset pairs for the current class
            List<CustomPair<VariableInformation, Integer>> classFields = Offsets.classFieldOffsetMap.get(currentClass);

            // iterate through the class fields to get the correct offset
            for (CustomPair<VariableInformation, Integer> field : classFields) {
                if (field.firstObj.getVarName().equals(variable.getVarName())) {
                    offset += field.secondObj;
                    break;
                }
            }

            // get the pointer to the class field using getelementptr
            BuildTools.emit("\t" + classFieldPointerRegister + " = getelementptr i8, i8* " + expressionResultRegister
                    + ", i32 " + offset);

            // create a new register to bitcast the pointer to the correct type
            String classFieldCastedRegister = BuildTools.newRegister(expressionResultType);

            // bitcast the pointer to the correct type
            BuildTools.emit("\t" + classFieldCastedRegister + " = bitcast i8* " + classFieldPointerRegister + " to "
                    + expressionResultType + "*");

            // create a new register to load the value of the class field into
            String classFieldValueRegister = BuildTools.newRegister(expressionResultType);

            // load the value of the class field into the register
            BuildTools.emit("\t" + classFieldValueRegister + " = load " + expressionResultType + ", "
                    + expressionResultType + "* " + classFieldCastedRegister);

            // set the expression result register to the class field value register
            expressionResultRegister = classFieldValueRegister;
        }
        else if (expressionResultRegister.equals("%this")) {
            expressionResultType = "i8*";
        }
        else {
            expressionResultType = (expressionResultRegister.equals("true") || expressionResultRegister.equals("false"))
                    ? "i1"
                    : "i32";
        }

        // return the result register and it's type and the expression tail string
        return ", " + expressionResultType + " " + expressionResultRegister;
    }

    @Override
    public String visit(Clause n, SemanticAnalysis argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(PrimaryExpression n, SemanticAnalysis argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(IntegerLiteral n, SemanticAnalysis argu) throws Exception {
        return n.f0.toString();
    }

    @Override
    public String visit(TrueLiteral n, SemanticAnalysis argu) throws Exception {
        return "true";
    }

    @Override
    public String visit(FalseLiteral n, SemanticAnalysis argu) throws Exception {
        return "false";
    }

    @Override
    public String visit(Identifier n, SemanticAnalysis argu) throws Exception {

        // get identifier name
        String identifierName = n.f0.toString();

        // create a custom pair of class method to get the identifier type
        CustomPair<ClassInformation, MethodInformation> classMethodPair = new CustomPair<>(currentClass, currentMethod);

        // get identifier type
        String identifierType = getIdentifierType(identifierName, classMethodPair);

        // boolean to check if the identifier is an inherited class field
        Boolean isInheritedField = false;
        CustomPair<ClassInformation, VariableInformation> inheritedFieldPair = null;

        // if the identifier type is null check if the identifier is a super class field
        if (identifierType == null) {

            // get super class of the current class
            ClassInformation superClass = SymbolTable.classInheritenceMap.get(this.currentClass);

            // if the super class is not null
            if (superClass != null) {

                // check every class in the inheritence tree
                while (superClass != null) {

                    // get the class fields
                    List<VariableInformation> classFields = SymbolTable.classFieldsMap.get(superClass);

                    // if the class fields is not null
                    if (classFields != null) {

                        // check every class field
                        for (VariableInformation var : classFields) {

                            // if the class field name matches the identifier name
                            if (var.getVarName().equals(identifierName)) {

                                // custom pair of class info and var info to use as key for the map below
                                inheritedFieldPair = new CustomPair<>(superClass, var);

                                // set the identifier type to the class field type
                                identifierType = SymbolTable.classFieldTypeMap.get(inheritedFieldPair);

                                // set the isInheritedField to true
                                isInheritedField = true;

                                // break out of the loop
                                break;
                            }
                        }
                    }

                    if (isInheritedField) {
                        break;
                    }

                    // get the super class of the current class
                    superClass = SymbolTable.classInheritenceMap.get(superClass);
                }
            }
        }

        // get the llvm size of the identifier type
        String identifierSize = getLLVMSize(identifierType);

        // result register
        String resultRegister = "";

        // check if the identifier is an inherited class field
        if (isInheritedField) {

            // call load field and set the result register to the register containing the value of the
            // identifier
            resultRegister = loadClassField(inheritedFieldPair.firstObj, identifierName, identifierSize);
        }
        else if (isMethodVar(identifierName, classMethodPair) || isMethodParam(identifierName, classMethodPair)) {

            // create a new register to load the value of the identifier
            String identifierRegister = BuildTools.newRegister(identifierSize);

            // load the value of the identifier into the register
            BuildTools.emit("\t" + identifierRegister + " = load " + identifierSize + ", " + identifierSize + "* %"
                    + identifierName);

            // set the result register to the register containing the value of the
            // identifier
            resultRegister = identifierRegister;
        } else if (isClassField(identifierName)) {

            // call load class field and set the result register to the register containing the value of the
            // identifier
            resultRegister = loadClassField(currentClass, identifierName, identifierSize);
        }

        return resultRegister;
    }

    @Override
    public String visit(ThisExpression n, SemanticAnalysis argu) throws Exception {
        return "%this";
    }

    @Override
    public String visit(ArrayAllocationExpression n, SemanticAnalysis argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(BooleanArrayAllocationExpression n, SemanticAnalysis argu) throws Exception {

        // create a new register to get the result of the expression
        String expressionResultRegister = n.f3.accept(this, argu);

        // add one to the expression result register, since we want to store the size of
        // the array at the first index
        // create a new register to get the result of the addition
        String additionResultRegister = BuildTools.newRegister("i32");

        BuildTools.emit("\t" + additionResultRegister + " = add i32 " + expressionResultRegister + ", 1");

        // create a register for icmp result
        String icmpResultRegister = BuildTools.newRegister("i1");

        // check if the expression is an integer higher than 0
        BuildTools.emit("\t" + icmpResultRegister + " = icmp sgt i32 " + additionResultRegister + ", 1");

        // create labels
        String arrayAllocationNegative = BuildTools.newLabel("arr_neg_sz", false);
        String arrayAllocationOk = BuildTools.newLabel("arr_ok_sz", true);

        // add branch instruction to ll file
        BuildTools.emit("\tbr i1 " + icmpResultRegister + ", label %" + arrayAllocationOk + ", label %"
                + arrayAllocationNegative);

        // add the labels to the ll file
        BuildTools.emit(arrayAllocationNegative + ":");

        // throw negative size exception
        BuildTools.emit("\tcall void @throw_nsz()");

        // br to ok label
        BuildTools.emit("\tbr label %" + arrayAllocationOk);

        // add ok label to ll file
        BuildTools.emit(arrayAllocationOk + ":");

        // allocate the array
        String arrayRegister = BuildTools.newRegister("i8*");

        BuildTools.emit("\t" + arrayRegister + " = call i8* @calloc(i32 " + additionResultRegister + ", i32 4)");

        // cast the returned pointer of the calloc call to i32*
        String arrayRegister32 = BuildTools.newRegister("i32*");

        BuildTools.emit("\t" + arrayRegister32 + " = bitcast i8* " + arrayRegister + " to i32*");

        // store the size of the array at the first index
        BuildTools.emit("\tstore i32 " + expressionResultRegister + ", i32* " + arrayRegister32);

        return arrayRegister32;
    }

    @Override
    public String visit(IntegerArrayAllocationExpression n, SemanticAnalysis argu) throws Exception {

        // create a new register to get the result of the expression
        String expressionResultRegister = n.f3.accept(this, argu);

        // add one to the expression result register, since we want to store the size of
        // the array at the first index
        // create a new register to get the result of the addition
        String additionResultRegister = BuildTools.newRegister("i32");

        BuildTools.emit("\t" + additionResultRegister + " = add i32 " + expressionResultRegister + ", 1");

        // create a register for icmp result
        String icmpResultRegister = BuildTools.newRegister("i1");

        // check if the expression is an integer higher than 0
        BuildTools.emit("\t" + icmpResultRegister + " = icmp sgt i32 " + additionResultRegister + ", 1");

        // create labels
        String arrayAllocationNegative = BuildTools.newLabel("arr_neg_sz", false);
        String arrayAllocationOk = BuildTools.newLabel("arr_ok_sz", true);

        // add branch instruction to ll file
        BuildTools.emit("\tbr i1 " + icmpResultRegister + ", label %" + arrayAllocationOk + ", label %"
                + arrayAllocationNegative);

        // add the labels to the ll file
        BuildTools.emit(arrayAllocationNegative + ":");

        // throw negative size exception
        BuildTools.emit("\tcall void @throw_nsz()");

        // br to ok label
        BuildTools.emit("\tbr label %" + arrayAllocationOk);

        // add ok label to ll file
        BuildTools.emit(arrayAllocationOk + ":");

        // allocate the array
        String arrayRegister = BuildTools.newRegister("i8*");

        BuildTools.emit("\t" + arrayRegister + " = call i8* @calloc(i32 " + additionResultRegister + ", i32 4)");

        // cast the returned pointer of the calloc call to i32*
        String arrayRegister32 = BuildTools.newRegister("i32*");

        BuildTools.emit("\t" + arrayRegister32 + " = bitcast i8* " + arrayRegister + " to i32*");

        // store the size of the array at the first index
        BuildTools.emit("\tstore i32 " + expressionResultRegister + ", i32* " + arrayRegister32);

        return arrayRegister32;
    }

    @Override
    public String visit(AllocationExpression n, SemanticAnalysis argu) throws Exception {

        // get the class name
        String className = n.f1.f0.toString();

        // get the class information object
        ClassInformation classInfo = SymbolTable.classesMap.get(className);

        // get the size of the object and add 8 cause we need a pointer to the vtable
        int objectSize = Offsets.classLastOffestsMap.get(classInfo).firstObj + 8;

        // get vtable type
        String vtableType = BuildTools.classVtableType.get(classInfo);

        // create a new register to get the result of the allocation
        String objectRegister = BuildTools.newRegister("i8*");

        // allocate the object, using calloc
        // one object of object size
        BuildTools.emit("\t" + objectRegister + " = call i8* @calloc(i32 1, i32 " + objectSize + ")");

        // bitcast i8* to i8*** to store the vtable pointer
        String bitcastedRegister = BuildTools.newRegister("i8***");

        // emit bitcast instruction to ll file
        BuildTools.emit("\t" + bitcastedRegister + " = bitcast i8* " + objectRegister + " to i8***");

        // get the address of the first element of the vtable of the class
        String vtableAddressRegister = BuildTools.newRegister("i8**");

        // emit getelementptr instruction to ll file
        BuildTools.emit("\t" + vtableAddressRegister + " = getelementptr " + vtableType + ", " + vtableType + "* @."
                + className + "_vtable, i32 0, i32 0");

        // emit store instruction of the vtable address in the bitcasted register
        BuildTools.emit("\tstore i8** " + vtableAddressRegister + ", i8*** " + bitcastedRegister);

        // return the object register
        return objectRegister;
    }

    @Override
    public String visit(NotExpression n, SemanticAnalysis argu) throws Exception {

        // accept the clause and negate the result
        String clauseResultRegister = n.f1.accept(this, argu);

        // check if the expression result is in a register or not
        if (BuildTools.registerMap.containsKey(clauseResultRegister)) {

            // if it is, check the type of the expression result
            // if it is i32 then we need to convert it to i1 using trunc
            if (BuildTools.registerMap.get(clauseResultRegister).getRegisterType().equals("i32")) {

                // create a new register to store the zext result
                String truncRegister = BuildTools.newRegister("i1");

                // emit the trunc instruction
                BuildTools.emit("\t" + truncRegister + " = trunc i32 " + clauseResultRegister + " to i1");

                // make expression result register the zext register
                clauseResultRegister = truncRegister;
            }
        }

        // negate the result of the clause using xor
        // get the register for the result of the clause
        String notExpressionResultRegister = BuildTools.newRegister("i1");

        // emit the xor instruction
        BuildTools.emit("\t" + notExpressionResultRegister + " = xor i1 " + clauseResultRegister + ", 1");

        // return the register for the result of the not expression
        return notExpressionResultRegister;
    }

    @Override
    public String visit(BracketExpression n, SemanticAnalysis argu) throws Exception {

        // accept the expression and get the result register
        String expressionResultRegister = n.f1.accept(this, argu);

        return expressionResultRegister;
    }
}
