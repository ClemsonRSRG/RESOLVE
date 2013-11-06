package edu.clemson.cs.r2jt.translation.bookkeeping;

public class JavaFacilityBookkeeper extends JavaBookkeeper {

    public JavaFacilityBookkeeper(String name, Boolean hasBody) {
        super(name, hasBody);
    }

    @Override
    public String output() {

        boolean standardMainDefined = false;
        StringBuilder documentBuilder = new StringBuilder();

        for (String imp : myImportList) {
            documentBuilder.append(imp);
        }
        documentBuilder.append("public class ");
        documentBuilder.append(myModuleName).append(" {");

        for (AbstractFacilityDecBook facility : myFacilityList) {
            documentBuilder.append(facility.getString());
        }

        for (AbstractFunctionBook func : myFunctionList) {
            if (func.name.equals("main")) {
                standardMainDefined = true;
            }
            else if (!func.name.equals("Main")) {
                standardMainDefined = true;
            }
            documentBuilder.append(func.getString());
        }

        if (!standardMainDefined) {
            documentBuilder.append("public static void main(String args[]) {");
            documentBuilder.append(myModuleName).append(" ").append("start");
            documentBuilder.append(" = new ").append(myModuleName)
                    .append("();");
            documentBuilder.append("start.Main(); }"); // TODO : parameters??
        }

        documentBuilder.append("}");
        return documentBuilder.toString();
    }
}
