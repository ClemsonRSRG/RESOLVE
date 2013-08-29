package edu.clemson.cs.r2jt.translation.bookkeeping;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Welch D
 */
public class JavaBookkeeper extends AbstractBookkeeper {

    public JavaBookkeeper(String moduleName, Boolean isRealiz) {
        super(moduleName, isRealiz);
    }

    // -----------------------------------------------------------
    //   FacilityBook methods
    // -----------------------------------------------------------

    @Override
    public void facAdd(String name, String concept, String realiz) {
        JavaFacilityDecBook f;
        f = new JavaFacilityDecBook(name, concept, realiz);
        myFacilityList.add(f);
        myCurrentFacility = f;
    }

    @Override
    public void fxnAdd(String retType, String funcName) {
        JavaFunctionBook f;
        f = new JavaFunctionBook(funcName, retType, isRealization);
        myFunctionList.add(f);
        myCurrentFunction = f;
    }

    @Override
    public void fxnAddParameter(String parameter) {

        myCurrentFunction.parameterList.add(parameter);
    }

    @Override
    public String output() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

class JavaFunctionBook extends AbstractFunctionBook {

    JavaFunctionBook(String nameStr, String retStr, boolean isRealiz) {
        super(nameStr, retStr, isRealiz);
    }

    @Override
    public String getString() {
        StringBuilder functionBuilder = new StringBuilder();
        functionBuilder.append("public ").append(returnType).append(" ");
        functionBuilder.append(name).append("(");

        int incomingLength = functionBuilder.length();

        for (String parameter : parameterList) {
            if (functionBuilder.length() != incomingLength) {
                functionBuilder.append(",");
            }
            functionBuilder.append(parameter);
        }

        functionBuilder.append(")");
        if (isRealization) {
            functionBuilder.append(" {");
            for (String variable : varInitList) {
                functionBuilder.append(variable);
            }
            functionBuilder.append(allStmt);
            functionBuilder.append("}");
            return functionBuilder.toString();
        }
        return functionBuilder.append(";").toString();
    }
}

class JavaFacilityDecBook extends AbstractFacilityDecBook {

    JavaFacilityDecBook(String name, String concept, String realiz) {
        super(name, concept, realiz);
    }

    @Override
    public String getString() {

        StringBuilder facilityBuilder = new StringBuilder();

        // For facility declarations with >= 1 enhancements this variable
        // refers to the last parameter: new Array_Realiz(<original params>)
        StringBuilder originalInstantiation = new StringBuilder();

        List<String> allParameters = new LinkedList<String>(parameterList);
        facilityBuilder.append(concept).append(" ").append(name).append(" = ");

        if (enhancementList.isEmpty()) {
            facilityBuilder.append("new ").append(conceptRealization);
            facilityBuilder.append("(");
        }
        else if (enhancementList.size() == 1) {
            facilityBuilder.append("new ").append(
                    enhancementList.get(0).realization);
            facilityBuilder.append("(");

            allParameters.addAll(enhancementList.get(0).parameterList);

            originalInstantiation.append("new ").append(conceptRealization);
            originalInstantiation.append("(");

            int i = originalInstantiation.length();
            for (String parameter : parameterList) {
                if (originalInstantiation.length() != i) {
                    originalInstantiation.append(", ");
                }
                originalInstantiation.append(parameter);

            }
            originalInstantiation.append(")");
            allParameters.add(originalInstantiation.toString());
        }
        else {
            // iterate over facility enhancement list and for each one,
            // do like your doing right below this line.
            facilityBuilder.append(conceptRealization).append(".createProxy(");
        }

        int incomingLength = facilityBuilder.length();
        for (String parameter : allParameters) {
            if (facilityBuilder.length() != incomingLength) {
                facilityBuilder.append(", ");
            }
            facilityBuilder.append(parameter);
        }

        facilityBuilder.append(");");
        return facilityBuilder.toString();
    }
}