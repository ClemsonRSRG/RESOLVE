package edu.clemson.cs.r2jt.translation.bookkeeping;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class AbstractBookkeeper implements Bookkeeper {

    /**
     * <p>Pointers to the various books currently being handled by the
     * Bookkeeper.</p>
     */
    protected AbstractFunctionBook myCurrentFunction;
    protected AbstractFacilityDecBook myCurrentFacility;

    /**
     * <p>Name of the module currently being managed by the Bookkeeper.</p>
     */
    protected String myModuleName;

    /**
     * <p>This flag indicates whether or not bodies will proceed declarations.
     * (i.e. functions, etc).</p>
     */
    protected Boolean isRealization;

    /**
     * <p>These lists can be thought of as the Bookkeeper's shelf - where all
     * completed and current books are stored.</p>
     */
    protected List<AbstractFacilityDecBook> myFacilities;
    protected List<AbstractFunctionBook> myFunctions;
    protected List<String> myConstructors;
    protected List<String> myImports;

    public AbstractBookkeeper(String moduleName, Boolean realization) {
        myModuleName = moduleName;
        isRealization = realization;

        myFunctions = new LinkedList<AbstractFunctionBook>();
        myFacilities = new LinkedList<AbstractFacilityDecBook>();
        myConstructors = new LinkedList<String>();
        myImports = new LinkedList<String>();
    }

    @Override
    public void addUses(String usesName) {
        myImports.add(usesName);
    }

    @Override
    public void addConstructor(String constructor) {
        myConstructors.add(constructor);
    }

    //-------------------------------------------------------------------
    //   FacilityBook methods
    //-------------------------------------------------------------------

    @Override
    public void facAddParameter(String parameter) {
        myCurrentFacility.myParameters.add(parameter);
    }

    @Override
    public void facAddEnhancement(String name, String realiz) {
        try {
            FacilityEnhancementBook enhancement =
                    new FacilityEnhancementBook(name, realiz);
            myCurrentFacility.myEnhancements.add(enhancement);
            myCurrentFacility.myCurEnhancement = enhancement;
        }
        catch (NullPointerException e) {

        }
    }

    @Override
    public boolean facEnhancementIsOpen() {
        return myCurrentFacility.myCurEnhancement != null;
    }

    @Override
    public void facAddEnhancementParameter(String parameter) {
        myCurrentFacility.myCurEnhancement.myParameters.add(parameter);
    }

    @Override
    public void facEnhancementEnd() {
        myCurrentFacility.myCurEnhancement = null;
    }

    @Override
    public void facEnd() {
        myCurrentFacility = null;
    }

    //-------------------------------------------------------------------
    //   FunctionBook methods
    //-------------------------------------------------------------------

    @Override
    public void fxnAddParameter(String parameter) {
        myCurrentFunction.myParameters.add(parameter);
    }

    @Override
    public void fxnAddVariableDeclaration(String variable) {
        myCurrentFunction.myVariableInitializations.add(variable);
    }

    @Override
    public void fxnAppendTo(String stmt) {
        myCurrentFunction.myStatementBlock.append(stmt);
    }

    @Override
    public boolean fxnIsOpen() {
        return myCurrentFunction != null;
    }

    @Override
    public void fxnEnd() {
        myCurrentFunction = null;
    }
}

abstract class AbstractFunctionBook {

    protected String myName;
    protected String myReturnType;

    protected List<String> myParameters;
    protected List<String> myVariableInitializations;

    protected StringBuilder myStatementBlock;
    protected boolean isRealization;

    public AbstractFunctionBook(String name, String type, boolean bodyFlag) {
        myName = name;
        myReturnType = type;
        isRealization = bodyFlag;

        myStatementBlock = new StringBuilder();
        myParameters = new LinkedList<String>();
        myVariableInitializations = new LinkedList<String>();
    }

    abstract String getString();
}

abstract class AbstractFacilityDecBook {

    protected String myName;
    protected String myConcept;
    protected String myConceptRealization;

    protected List<FacilityEnhancementBook> myEnhancements;
    protected List<String> myParameters;
    protected FacilityEnhancementBook myCurEnhancement;

    AbstractFacilityDecBook(String name, String concept, String realization) {
        myName = name;
        myConcept = concept;
        myConceptRealization = realization;

        myEnhancements = new LinkedList<FacilityEnhancementBook>();
        myParameters = new LinkedList<String>();
    }

    abstract String getString();
}

class FacilityEnhancementBook {

    protected String myName;
    protected String myRealization;
    protected ArrayList<String> myParameters;

    FacilityEnhancementBook(String name, String realization) {
        myName = name;
        myRealization = realization;
        myParameters = new ArrayList();
    }
}

class RecordBook {

}
