package edu.clemson.cs.r2jt.translation.bookkeeping;

import edu.clemson.cs.r2jt.translation.bookkeeping.books.JavaFunctionBook;
import java.util.ArrayList;

/**
 *
 * @author Welch D
 */
public class JavaConceptBookkeeper extends JavaBookkeeper {

    private ArrayList<String> constructorList;

    //private ArrayList<String> interfaceList;
    public JavaConceptBookkeeper(String name, Boolean hasBody) {
        super(name, hasBody);
    }

    // Avoid newlines in all translation-related output/printing 
    // procedures.
    @Override
    public String output() {
        StringBuilder translateDoc = new StringBuilder();

        for (String imp : importList) {
            translateDoc.append(imp);
        }
        translateDoc.append("public interface ").append(moduleName);
        translateDoc.append(" extends RESOLVE_INTERFACE {");

        // print constructors/interfaces
        /*for (String constructor : constructorList) {
            translateDoc.append(constructor.toString());
        }*/

        // Now print all functions 
        for (JavaFunctionBook operDecl : functionList) {
            translateDoc.append(operDecl.getString());
        }

        translateDoc.append("}");
        return translateDoc.toString();
    }

}
