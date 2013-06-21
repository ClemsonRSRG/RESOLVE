package edu.clemson.cs.r2jt.translation.supervising;

import java.util.ArrayList;

public class JavaEmployeeFunction implements EmployeeFunction {

    private String functionName;
    private String returnType;
    private Boolean isConcept; // take a look first...

    private ArrayList<String> parameterList;
    private ArrayList<String> varInitList;
    private StringBuilder allStmt;

    /**
     * <p>Constructs a <code>JavaEmployeeFunction</code> object 
     * that provides a container for some function named 
     * <code>name</code> with return type <code>type</code>. 
     * The function's parameters, variables, and 'stmts' will be 
     * added-to using standard @link Supervisor methods.</p>
     *
     * @param name The function's name.
     * @param type The function's return type.
     */
    public JavaEmployeeFunction(String type, String name) {
        this.returnType = type;
        this.functionName = name;
        //this.isConcept = false;

        parameterList = new ArrayList();
        varInitList = new ArrayList();
        allStmt = new StringBuilder();
    }

    // ===========================================================
    // Adder Methods
    // ===========================================================

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

    // ===========================================================
    // Getter Methods
    // ===========================================================

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
     * representation of <code>JavaEmployeeFunction</code>.</p>
     */

    @Override
    public String getString() {
        StringBuilder finalFunc = new StringBuilder();
        finalFunc.append("public ").append(returnType);
        finalFunc.append(functionName).append("(");

        for (int i = 0; i < parameterList.size(); i++) {
            finalFunc.append(parameterList.get(i));
            if (i != parameterList.size() - 1) {
                finalFunc.append(", ");
            }
        }
        finalFunc.append(") {");

        if (allStmt != null) {
            finalFunc.append(allStmt);
        }
        //  if (!returnType.equals("void ")) {
        //      finalFunc.append("return ").append(functionName);
        //  }
        finalFunc.append("}");
        return finalFunc.toString();
    }
}
