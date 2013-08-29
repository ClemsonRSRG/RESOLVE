package edu.clemson.cs.r2jt.translation.bookkeeping;

/**
 * @author Welch D
 */
public class JavaFacilityBookkeeper extends JavaBookkeeper {

    public JavaFacilityBookkeeper(String name, Boolean hasBody) {
        super(name, hasBody);
    }

    @Override
    public String output() {
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
            documentBuilder.append(func.getString());
        }

        documentBuilder.append("}");
        return documentBuilder.toString();
    }

}
