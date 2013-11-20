package edu.clemson.cs.r2jt.translation.bookkeeping;

public class JavaFacilityBookkeeper extends JavaBookkeeper {

    public JavaFacilityBookkeeper(String name, Boolean hasBody) {
        super(name, hasBody);
    }

    @Override
    public String output() {

        boolean standardMainDefined = false;
        StringBuilder documentBuilder = new StringBuilder();

        for (String imp : myImports) {
            documentBuilder.append(imp);
        }
        documentBuilder.append("public class ").append(myModuleName)
                .append("{");

        for (AbstractFacilityDecBook facility : myFacilities) {
            documentBuilder.append(facility.getString());
        }

        for (AbstractFunctionBook func : myFunctions) {
            if (func.myName.equals("main")) {
                standardMainDefined = true;
            }
            else if (!func.myName.equals("Main")) {
                standardMainDefined = true;
            }
            documentBuilder.append(func.getString());
        }

        if (!standardMainDefined) {
            documentBuilder.append("public static void main(String args[]) {");
            documentBuilder.append(myModuleName).append(" ").append("start");
            documentBuilder.append(" = new ").append(myModuleName)
                    .append("();");
            documentBuilder.append("start.Main(); }");
        }

        documentBuilder.append("}");
        return documentBuilder.toString();
    }
}
