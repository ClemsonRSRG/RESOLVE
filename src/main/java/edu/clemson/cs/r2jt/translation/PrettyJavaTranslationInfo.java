package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.translation.PrettyJavaTranslationInfo.Function;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 
 */
public class PrettyJavaTranslationInfo {

    int lineCount;
    ArrayList<Function> funcList;
    ArrayList<String> globalVarsList;
    Function currentFunc;
    String name;

    public PrettyJavaTranslationInfo(String newName) {
        lineCount = 1;
        name = newName;
        funcList = new ArrayList<Function>();
        globalVarsList = new ArrayList<String>();
    }

    class Function {

        String[] returnType;
        String functionName;
        String returnName;
        List<String> params;

        List<String> varInit;
        List<String> stmts;
        StringBuffer allStmt;

        private String getFunctionString() {
            StringBuilder retBuf = new StringBuilder();

            retBuf.append(functionName).append("(");
            int size = params.size() - 1;
            for (int i = 0; i <= size; i++) {
                retBuf.append(params.get(i));
                if (!(i == size)) {
                    retBuf.append(", ");
                }
            }
            retBuf.append("){");
            if (!returnType[0].equals("void ")) {
                retBuf.append(returnType[0]).append(returnName).append(
                        returnType[1]).append(";");
            }
            /*for (String a : varInit) {
                retBuf.append(a).append(";");
            }*/
            for (String a : stmts) {
                //retBuf.append(a).append(";");

            }
            if (allStmt != null) {
                retBuf.append(allStmt);
            }
            if (!returnType[0].equals("void ")) {
                retBuf.append("return ").append(returnName).append(";");
            }
            retBuf.append(" }");

            return retBuf.toString();

        }
    }

    /* Function methods */

    /**
     * <p>Creates a new function and adds to funcList. Sets it to 
     * currentFunc.</p>
     * @param newFuncName name of function
     * @param newReturnTy type of return value from function
     */

    public void addFunction(PosSymbol newFuncName, PosSymbol newReturnTy) {
        Function newFunc = new Function();

        newFunc.returnType = new String[2];
        newFunc.returnType[0] = "void ";
        if (newReturnTy != null) {
            newFunc.returnType = getVarType(newReturnTy.getName());
        }
        newFunc.returnName = newFuncName.getName();
        String temp = "public static " + newFunc.returnType[0];
        newFunc.functionName = stringFromSym(newFuncName, temp);
        String te = newFunc.functionName.trim();
        funcList.add(newFunc);
        newFunc.params = new ArrayList<String>();
        newFunc.stmts = new ArrayList<String>();
        newFunc.varInit = new ArrayList<String>();
        newFunc.allStmt = new StringBuffer();
        currentFunc = newFunc;
    }

    public void appendToStmt(String append) {
        currentFunc.allStmt.append(append);
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
            currentFunc.allStmt.append(data);
            currentFunc.varInit.add(data);
        }
        else {
            globalVarsList.add(data);
        }
    }

    //TODO: Function returns
    /*public void setFuncReturnType(String ret){
        if(!(currentFunc == null)) {
            currentFunc.returnType = ret;
        }
    }*/

    /* End of Function methods */

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        //ret.append(buildHeaderComment());
        int temp;
        //temp = fileName.indexOf(".");
        //name = fileName.substring(0, temp);
        //global variables
        String nameNoExt = name.substring(0, name.lastIndexOf('.'));

        ret.append("public static class ").append(nameNoExt).append("{  ");
        for (int j = 0; j < globalVarsList.size(); j++) {
            ret.append(globalVarsList.get(j));
        }
        //functions
        for (int j = 0; j < funcList.size(); j++) {
            Function tFunc = funcList.get(j);
            ret.append(tFunc.getFunctionString());
        }
        ret.append(" }");
        return ret.toString();
    }

    public String stringFromSym(PosSymbol pos, String prepend) {
        int n = pos.getLocation().getPos().getLine() - lineCount;
        String retString;
        if (prepend != null) {
            retString = prepend + pos.getName();
        }
        else {
            retString = pos.getName();
        }
        return stringWithLines(retString, n);
    }

    public String getCVarsWithLines(PosSymbol pos, String prepend) {
        String[] typeString = getVarType(pos.getName());
        if (prepend != null) {
            typeString[0] = prepend + typeString[0];
        }
        int n = pos.getLocation().getPos().getLine() - lineCount;
        return stringWithLines(typeString[0], n);
    }

    public String[] getVarType(String ty) {
        String[] typeString = { null, null };
        if (ty.equals("Integer")) {
            typeString[0] = "int ";
            typeString[1] = "= 0";
        }
        else if (ty.equals("Boolean")) {
            typeString[0] = "int ";
            typeString[1] = "= 0";
        }
        else if (ty.equals("Character")) {
            typeString[0] = "char ";
            typeString[1] = "= /0";
        }
        else {
            typeString[0] = ty;
            typeString[1] = "= /0";
        }

        return typeString;
    }

    public String stringWithLines(String toAdd, int lines) {
        StringBuffer retString;
        retString = new StringBuffer("");
        int count = 0;
        while (count < lines) {
            retString.append("\n");
            count++;
            lineCount++;
        }
        retString.append(toAdd);
        return retString.toString();
    }

    public void increaseLineStatementBuffer(int position) {
        StringBuilder retString = new StringBuilder();
        int n = position - lineCount;
        int count = 0;
        while (count < n) {
            retString.append("\n");
            count++;
            lineCount++;
        }
        addToStmts(retString.toString());
        currentFunc.allStmt.append(retString.toString());
    }
}
