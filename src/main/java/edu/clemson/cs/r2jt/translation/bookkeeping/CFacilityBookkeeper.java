package edu.clemson.cs.r2jt.translation.bookkeeping;

public class CFacilityBookkeeper extends CBookkeeper {

    public CFacilityBookkeeper(String name, Boolean hasBody) {
        super(name, hasBody);
    }

    @Override
    public String output() {
        StringBuilder translateDoc = new StringBuilder();

        for (String imp : myImportList) {
            translateDoc.append(imp);
        }
        translateDoc.append("public class ");
        translateDoc.append(myModuleName).append(" {");

        // Now print all functions
        for (AbstractFunctionBook func : myFunctionList) {
            translateDoc.append(func.getString());
        }

        translateDoc.append("}");
        return translateDoc.toString();
    }
}
