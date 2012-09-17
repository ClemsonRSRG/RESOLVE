/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

import java.util.ArrayList;
import java.util.List;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.translation.PrettyCTranslationInfo.Function;

/**
 *
 * @author Mark T
 */
public class PrettyCTranslationInfo {

    int lineCount;
    ArrayList<Function> funcList;
    Function currentFunc;
    String name;

    public PrettyCTranslationInfo(String name) {
        lineCount = 0;
        this.name = name;
    }

    class Function {

        String returnType;
        String functionName;
        List<String> params;

        List<String> varInit;
        List<String> stmts;

        private String getFunctionString() {
            StringBuffer retBuf = new StringBuffer();

            retBuf.append(returnType).append(" ").append(functionName).append(
                    "(");
            int size = params.size() - 1;
            for (int i = 0; i <= size; i++) {
                retBuf.append(params.get(i));
                if (!(i == size)) {
                    retBuf.append(", ");
                }
            }
            retBuf.append("){");
            if (!returnType.equals("void"))
                ;//TODO: Handle returns
            for (String a : varInit) {
                retBuf.append(a).append(";");
            }
            if (!returnType.equals("void")) {
                retBuf.append("return ").append(functionName).append(";");
            }
            retBuf.append("}");

            return retBuf.toString();

        }
    }

    /* Function methods */

    /**
     * <p>Creates a new function and adds to list. Sets it to current function.</p>
     * @param newFuncName
     */

    public void addFunction(PosSymbol newFuncName) {
        Function newFunc = new Function();
        newFunc.functionName = stringFromSym(newFuncName); //obvious 'void' problem
        funcList.add(newFunc);
        newFunc.params = new ArrayList<String>();
        newFunc.returnType = "void";
        newFunc.stmts = new ArrayList<String>();
        newFunc.varInit = new ArrayList<String>();
        currentFunc = newFunc;
    }

    /**
     * <p>Adds variable to current function's list </p>
     * @param varName
     */
    public void addParamToFunc(String parm) {
        if (!(currentFunc == null)) {
            currentFunc.params.add(parm);
        }
    }

    /**
     * <p>Adds 'complete' statement to Function. (A 'complete' statement is 
     * considered thus if it has no statements for parents.)</p>
     * @param data
     */
    public void addToStmts(String data) {
        if (!(currentFunc == null)) {
            currentFunc.stmts.add(data);
        }
    }

    public void appendToFuncVarInit(String data) {
        if (!(currentFunc == null)) {
            currentFunc.varInit.add(data);
        }
    }

    //TODO: Function returns
    /*public void setFuncReturnType(String ret){
        if(!(currentFunc == null)) {
            currentFunc.returnType = ret;
        }
    }*/

    /* End of Function methods */

    public String stringFromSym(PosSymbol pos) {
        int n = pos.getLocation().getPos().getLine() - lineCount;
        String retString = pos.getName();
        while (n > 0) {
            retString = "\n" + retString;
            n--;
            lineCount++;
        }
        return retString;
    }
}
