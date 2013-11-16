package edu.clemson.cs.r2jt.translation.bookkeeping;

public class JavaConceptBookkeeper extends JavaBookkeeper {

    public JavaConceptBookkeeper(String name, Boolean hasBody) {
        super(name, hasBody);
    }

    // Avoid newlines in translation-related output procedures.
    @Override
    public String output() {
        StringBuilder translateDoc = new StringBuilder();

        for (String i : myImports) {
            translateDoc.append(i);
        }
        translateDoc.append("public interface ").append(myModuleName);
        translateDoc.append(" extends RESOLVE_INTERFACE {");

        // Print constructors/interfaces.
        for (String constructor : myConstructors) {
            translateDoc.append("interface ").append(constructor);
            translateDoc.append(" extends RTYPE { }");

            translateDoc.append(constructor);
            translateDoc.append(" create").append(constructor);
            translateDoc.append("();");
        }

        // Now print all operation declarations.
        for (AbstractFunctionBook operation : myFunctions) {
            translateDoc.append(operation.getString());
        }

        translateDoc.append("}");
        return translateDoc.toString();
    }
}
