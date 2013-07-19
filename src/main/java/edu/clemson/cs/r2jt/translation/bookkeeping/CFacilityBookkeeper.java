/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.bookkeeping;

/**
 * @author Welch D
 */
public class CFacilityBookkeeper extends CBookkeeper {

    public CFacilityBookkeeper(String name, Boolean hasBody) {
        super(name, hasBody);
    }

    @Override
    public String output() {
        StringBuilder translateDoc = new StringBuilder();

        for (String imp : importList) {
            translateDoc.append(imp);
        }
        translateDoc.append("public class ");
        translateDoc.append(moduleName).append(" {");

        // Now print all functions 
        for (FunctionBook func : functionList) {
            translateDoc.append(func.getString());
        }

        translateDoc.append("}");
        return translateDoc.toString();
    }

}
