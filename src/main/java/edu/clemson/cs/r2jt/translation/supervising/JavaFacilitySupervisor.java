/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.supervising;

/**
 * @author Welch D
 */
public class JavaFacilitySupervisor extends JavaSupervisor {

    public JavaFacilitySupervisor(String name) {
        super(name);
    }

    // Take the newlines out of this eventually...
    @Override
    public StringBuilder output() {
        StringBuilder translateDoc = new StringBuilder();

        for (String imp : importList) {
            translateDoc.append(imp);
            translateDoc.append("\n");
        }
        translateDoc.append("public class ");
        translateDoc.append(moduleName).append(" {\n");

        // Now print all functions 
        for (JavaEmployeeFunction func : functionList) {
            translateDoc.append(func.getString());
            translateDoc.append("\n");
        }

        translateDoc.append("}");
        return translateDoc;
    }

}
