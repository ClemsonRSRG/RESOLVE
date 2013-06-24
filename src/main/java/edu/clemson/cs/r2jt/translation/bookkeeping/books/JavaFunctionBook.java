package edu.clemson.cs.r2jt.translation.bookkeeping.books;

import java.util.ArrayList;

public class JavaFunctionBook implements FunctionBook {

    private String functionName;
    private String returnType;
    private Boolean hasBody;

    private ArrayList<String> parameterList;
    private ArrayList<String> varInitList;
    private StringBuilder allStmt;

    /**
     * <p>Constructs a <code>JavaFunctionBook</code> object 
     * that provides a container for some function named 
     * <code>name</code> with return type <code>type</code>. 
     * The function's parameters, variables, and 'stmts' will be 
     * added-to using standard @link Supervisor methods.</p>
     *
     * @param name The function's name.
     * @param type The function's return type.
     */
    public JavaFunctionBook(String type, String name, Boolean body) {
        this.hasBody = body;
        this.returnType = type;
        this.functionName = name;

        parameterList = new ArrayList();
        varInitList = new ArrayList();
        allStmt = new StringBuilder();
    }

    /** Adder Methods */

    @Override
    public void addParameter(String p) {
        parameterList.add(p);
    }

    @Override
    public void addVariable(String v) {
        varInitList.add(v);
    }

    @Override
    public void appendToStmt(String s) {
        allStmt.append(s);
    }

    /** Getter Methods */

    @Override
    public String getReturnType() {
        return returnType;
    }

    @Override
    public String getName() {
        return functionName;
    }

    /**
     * <p>Returns an unformatted (no newlines or tabs) Java string 
     * representation of <code>JavaFunctionBook</code>.</p>
     */
    @Override
    public String getString() {
        StringBuilder finalFunc = new StringBuilder();
        finalFunc.append("public ").append(returnType).append(" ");
        finalFunc.append(functionName).append("(");

        for (int i = 0; i < parameterList.size(); i++) {
            finalFunc.append(parameterList.get(i));
            if (i != parameterList.size() - 1) {
                finalFunc.append(", ");
            }
        }
        if (hasBody) {
            finalFunc.append(") {");

            if (allStmt != null) {
                finalFunc.append(allStmt);
            }
            //  if (!returnType.equals("void ")) {
            //      finalFunc.append("return ").append(functionName);
            //  }
            finalFunc.append("}");
        }
        else {
            finalFunc.append(");");
        }
        return finalFunc.toString();
    }
}
