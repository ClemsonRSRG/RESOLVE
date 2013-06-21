/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.supervising;

import java.util.ArrayList;

/**
 *
 * @author Welch D
 */
public abstract class JavaSupervisor implements Supervisor {

    private JavaEmployeeFunction currentFunction;

    protected String moduleName;
    protected ArrayList<String> importList;
    protected ArrayList<JavaEmployeeFunction> functionList;

    public JavaSupervisor(String name) {
        moduleName = name;
        importList = new ArrayList();
        functionList = new ArrayList();
    }

    @Override
    public void addUses(String usesName) {
        importList.add(usesName);
    }

    @Override
    public void fxnAdd(String retType, String funcName) {
        JavaEmployeeFunction f;
        f = new JavaEmployeeFunction(retType, funcName);
        functionList.add(f);
        currentFunction = f;
    }

    @Override
    public void fxnAddParam(String parName) {
        currentFunction.addParameter(parName);
    }

    @Override
    public void fxnAddVarDecl(String varName) {
        currentFunction.addVariable(varName);
    }

    @Override
    public void fxnAppendTo(String stmt) {
        currentFunction.appendToStmt(stmt);
    }

    @Override
    public void fxnEnd() {}
}