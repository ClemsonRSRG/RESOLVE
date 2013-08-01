package edu.clemson.cs.r2jt.translation.bookkeeping;

import edu.clemson.cs.r2jt.translation.bookkeeping.FacilityDeclBook.FacilityDeclEnhance;

import java.util.ArrayList;
import edu.clemson.cs.r2jt.translation.bookkeeping.Bookkeeper.*;
import edu.clemson.cs.r2jt.translation.bookkeeping.books.FunctionBook;
import java.util.Iterator;

/**
 *
 * @author 
 */
public abstract class AbstractBookkeeper implements Bookkeeper {

    FunctionBook myCurFunctionBook;
    FacilityDeclBook myCurFacilityDeclarationBook;
    /**
     * <p>Name of the module currently undergoing translation.</p>		
     */
    String myModuleName;

    /**
     * If we aren't translating a realization, then we don't need
     * bodies following function declarations as the files we are 
     * outputting will be class interfaces. Thus, this should only 
     * be false for Concepts and Enhancement declaration modules.
     */
    Boolean isRealization;

    ArrayList<FacilityDeclBook> facilityList;
    ArrayList<String> constructorList;
    ArrayList<String> importList;
    ArrayList<FunctionBook> functionList;

    /**
     * Construct a supervisor to manage Java modules undergoing 
     * translation.
     */
    public AbstractBookkeeper(String name, Boolean isRealiz) {
        moduleName = name;
        isRealization = isRealiz;

        importList = new ArrayList();
        functionList = new ArrayList();
        facilityList = new ArrayList();
    }

    /**
     * Stores packages needed by the module undergoing translation.
     */
    @Override
    public void addUses(String usesName) {
        importList.add(usesName);
    }

    /* FacilityDeclBook Methods */
    /*@Override
    public void facAdd(String name, String concept, String realiz) {
        FacilityDeclBook newFac = new FacilityDeclBook(name,concept,realiz);
        facilityList.add(newFac);
        currentFacility = newFac;
    }*/

    // -----------------------------------------------------------
    //   Facility book methods
    // -----------------------------------------------------------

    @Override
    public void facAddParam(String parameter) {
        currentFacility.parameterList.add(parameter);
    }

    @Override
    public void facAddEnhance(String name, String realiz) {
        try {
            FacilityDeclEnhance newEnhance;
            //The below is the correct syntax, it just...wrinkles my brain
            newEnhance = currentFacility.new FacilityDeclEnhance(name, realiz);
            currentFacility.enhanceList.add(newEnhance);
            currentFacility.currentEnhance = newEnhance;
        }
        catch (NullPointerException e) {

        }
    }

    @Override
    public boolean facEnhanceIsOpen() {
        return currentFacility.currentEnhance != null;
    }

    @Override
    public void facAddEnhanceParam(String parameter) {
        currentFacility.currentEnhance.parameterList.add(parameter);
    }

    @Override
    public void facEnhanceEnd() {
        currentFacility.currentEnhance = null;
    }

    @Override
    public void facEnd() {
        currentFacility = null;
    }

    // -----------------------------------------------------------
    //   Function book methods
    // -----------------------------------------------------------

    @Override
    public void fxnAddParam(String parName) {
        currentFunction.myParameterList.add(parName);
    }

    @Override
    public void fxnAddVarDecl(String varName) {
        currentFunction.myVarInitList.add(varName);
    }

    @Override
    public void fxnAppendTo(String stmt) {
        currentFunction.myStmt.append(stmt);
    }

    @Override
    public void fxnEnd() {
        currentFunction = null;
    }
}

/**
 * Provides a container for RESOLVE functions that allows users
 * to easily collect, add-to, and maintain translation relevant
 * information (i.e. function name, variable name/type, parameter 
 * name/type, etc).
 * 
 * <p>Functions in RESOLVE are reliably serialized using
 * <code>TreeWalkerVisitor</code> traversals. Thus, Each 
 * <code>FunctionBook</code> has a StringBuilder instance variable, 
 * <code>allstmt</code>, which, throughout the various TreeWalker
 * traversals over the function's statements and expressions, gets 
 * automatically "filled in" with corresponding, correctly translated 
 * Java, C, etc code. This eases the task of translation considerably 
 * since it allows one to store entire chunks of fully translated code 
 * within a <code>FunctionBook</code> object for later ordering 
 * and output by the <code>Bookkeeper</code>.</p>
 * 
 * @author Mark T
 * @author Welch D
 */
/*abstract class FunctionBook {

    protected String myName;
    protected String myReturnType;
    protected Boolean hasBody;

    protected ArrayList<String> myParameterList;
    protected ArrayList<String> myVarInitList;

    protected StringBuilder myStmt;


    FunctionBook(String returnType, String name, boolean hasBody) {
        myName = name;
        myReturnType = returnType;
        this.hasBody = hasBody;

        myParameterList = new ArrayList<String>();
        myVarInitList = new ArrayList<String>();
        myStmt = new StringBuilder();

    }

    public String getName() {
        return myName;
    }

    abstract String getString();
}*/

abstract class FacilityDeclBook {

    protected String myName;
    protected String myConcept;
    protected String myConceptRealiz;

    protected ArrayList<FacilityDeclEnhance> enhanceList;
    protected ArrayList<String> parameterList;
    protected FacilityDeclEnhance currentEnhance;

    FacilityDeclBook(String name, String concept, String realiz) {
        myName = name;
        myConcept = concept;
        myConceptRealiz = realiz;

        enhanceList = new ArrayList<FacilityDeclEnhance>();
        parameterList = new ArrayList<String>();
    }

    class FacilityDeclEnhance {

        protected String myName;
        protected String myRealiz;
        protected ArrayList<String> parameterList;

        FacilityDeclEnhance(String newEnhance, String realiz) {
            myName = newEnhance;
            myRealiz = realiz;
            parameterList = new ArrayList();
        }
    }

    abstract String getString();
}

class RecordBook {

}
