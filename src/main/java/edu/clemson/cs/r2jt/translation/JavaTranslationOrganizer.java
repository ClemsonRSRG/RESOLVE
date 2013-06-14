/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Welch D
 */
public class JavaTranslationOrganizer {

    private StringBuffer header;
    private StringBuffer rest;

    Function currentFunction;
    ArrayList<Function> funcList;
    ArrayList<String> globalVarList;

    public JavaTranslationOrganizer() {
        funcList = new ArrayList();
        header = new StringBuffer();
        rest = new StringBuffer();
    }
	
	class Header {

  
    }

    class Function {

        StringBuffer functionBody;
        String retTyandName;
        String returnType;

        List<String> parameters;
        List<String> statements;
        List<String> varInit;
    }

    public void addFunction(String modifier, PosSymbol retType, PosSymbol name) {
        Function newFunc = new Function();

        newFunc.returnType = "void ";
        if (retType != null) {
            newFunc.returnType = retType.getName().toString();
        }

        newFunc.retTyandName = modifier + newFunc.returnType + name.toString();
        funcList.add(newFunc);

        newFunc.functionBody = new StringBuffer();
        newFunc.parameters = new ArrayList<String>();
        newFunc.statements = new ArrayList<String>();
        newFunc.varInit = new ArrayList<String>();
        currentFunction = newFunc;
    }

    public void appendToCurrFunction(String line) {
        currentFunction.functionBody.append(line);
    }

    public void appendToCurrParamList(String line) {
        // if (currentFunction != null) { }
        currentFunction.parameters.add(line);
    }

    public void appendToHeader(String line) {
        header.append(line);
    }

    public void appendToRest(String line) {
        rest.append(line);
    }

    public String formParameter(PosSymbol type, PosSymbol par) {
        // remember to add in Integer_Template.<type>, etc, etc
        return type.getName().toString() + " " + par.getName().toString();
    }

    @Override
    public String toString() {
        return header.toString() + rest.toString();

    }

}
