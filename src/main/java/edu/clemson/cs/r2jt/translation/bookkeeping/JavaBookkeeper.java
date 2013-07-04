package edu.clemson.cs.r2jt.translation.bookkeeping;

import java.util.ArrayList;


/**
 *
 * @author Welch D
 */
public class JavaBookkeeper extends AbstractBookkeeper {

    /**
     * Construct a supervisor to manage Java modules undergoing 
     * translation.
     */
    public JavaBookkeeper(String name, Boolean isRealiz) {
        super(name, isRealiz);
    }

    @Override
    public void facAdd(String name, String concept, String realiz) {
        JavaFacilityDeclBook newFac;
        
    }

    /* FunctionBook Adders */

    @Override
    public void fxnAdd(String retType, String funcName) {
        FunctionBook f;
        f = new JavaFunctionBook(retType, funcName, isRealization);
        functionList.add(f);
        currentFunction = f;
    }


    
    @Override
    public String output() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }   
}
class JavaFunctionBook extends FunctionBook{
    
    JavaFunctionBook(String name, String returnType, boolean hasBody){
        super(name,returnType,hasBody);
    }
    
    /**
     * <p>Returns an unformatted (no newlines or tabs) Java string 
     * representation of <code>JavaFunctionBook</code>.</p>
     */
    @Override
    String getString() {
        StringBuilder finalFunc = new StringBuilder();
        finalFunc.append("public ").append(myReturnType).append(" ");
        finalFunc.append(myName).append("(");

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

class JavaFacilityDeclBook extends FacilityDeclBook{
    JavaFacilityDeclBook(String name, String concept, String realiz){
        super(name,concept,realiz);
    }

    @Override
    String getString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}