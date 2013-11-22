package edu.clemson.cs.r2jt.translation.bookkeeping;

import java.util.LinkedList;
import java.util.List;

public class JavaBookkeeper extends AbstractBookkeeper {

    public JavaBookkeeper(String moduleName, Boolean isRealiz) {
        super(moduleName, isRealiz);
    }

    @Override
    public void facAdd(String name, String concept, String realiz) {
        JavaFacilityDecBook f;
        f = new JavaFacilityDecBook(name, concept, realiz);
        myFacilities.add(f);
        myCurrentFacility = f;
    }

    @Override
    public void fxnAdd(String funcName, String retType) {
        JavaFunctionBook f;
        f = new JavaFunctionBook(funcName, retType, isRealization);
        myFunctions.add(f);
        myCurrentFunction = f;
    }

    @Override
    public void fxnAddParameter(String parameter) {
        myCurrentFunction.myParameters.add(parameter);
    }

    @Override
    public String output() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

class JavaFunctionBook extends AbstractFunctionBook {

    JavaFunctionBook(String name, String returns, boolean realization) {
        super(name, returns, realization);
    }

    @Override
    public String getString() {
        StringBuilder functionBuilder = new StringBuilder();
        if (isRealization) {
            functionBuilder.append("public ").append(myReturnType);
            functionBuilder.append(" ").append(myName);
        }
        else {
            functionBuilder.append(myReturnType).append(" ").append(myName);
        }
        functionBuilder.append("(");

        int incomingLength = functionBuilder.length();

        for (String parameter : myParameters) {
            if (functionBuilder.length() != incomingLength) {
                functionBuilder.append(", ");
            }
            functionBuilder.append(parameter);
        }

        functionBuilder.append(")");
        if (isRealization) {
            functionBuilder.append(" {");
            for (String variable : myVariableInitializations) {
                functionBuilder.append(variable);
            }
            functionBuilder.append(myStatementBlock);
            functionBuilder.append("}");
            return functionBuilder.toString();
        }
        return functionBuilder.append(";").toString();
    }
}

class JavaFacilityDecBook extends AbstractFacilityDecBook {

    JavaFacilityDecBook(String name, String concept, String realization) {
        super(name, concept, realization);
    }

    @Override
    public String getString() {

        StringBuilder facilityBuilder = new StringBuilder();

        // For facility declarations with >= 1 enhancements this variable
        // refers to the last parameter: new Array_Realiz(<original params>)
        StringBuilder originalInstantiation = new StringBuilder();

        List<String> allParameters = new LinkedList<String>(myParameters);
        facilityBuilder.append(myConcept).append(" ").append(myName).append(
                " = ");

        if (myEnhancements.isEmpty()) {
            facilityBuilder.append("new ").append(myConceptRealization);
            facilityBuilder.append("(");
        }
        else if (myEnhancements.size() == 1) {
            facilityBuilder.append("new ").append(
                    myEnhancements.get(0).myRealization);
            facilityBuilder.append("(");
        }
        else {
            facilityBuilder.append(myEnhancements.get(0).myRealization);
            facilityBuilder.append(".createProxy").append("(");
        }

        // Add parameters from any enhancements into our final
        // output list, "allParameters".
        for (int i = 0; i < myEnhancements.size(); i++) {
            allParameters.addAll(myEnhancements.get(i).myParameters);
        }

        // Don't forget to tack on the last piece which instantiates
        // the original baseFacility's realization (originalInstantiation)
        if (myEnhancements.size() > 0) {
            originalInstantiation.append("new ").append(myConceptRealization);
            originalInstantiation.append("(");

            int i = originalInstantiation.length();
            for (String parameter : myParameters) {
                if (originalInstantiation.length() != i) {
                    originalInstantiation.append(", ");
                }
                originalInstantiation.append(parameter);
            }
            originalInstantiation.append(")");
            allParameters.add(originalInstantiation.toString());
        }

        // Now we just print the parameters and we're done.
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
