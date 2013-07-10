/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.bookkeeping;

<<<<<<< HEAD
import edu.clemson.cs.r2jt.translation.bookkeeping.FacilityDeclBook.FacilityDeclEnhance;

import java.util.ArrayList;
import edu.clemson.cs.r2jt.translation.bookkeeping.Bookkeeper.*;
=======
import edu.clemson.cs.r2jt.translation.bookkeeping.books.*;
import java.util.ArrayList;
>>>>>>> intial version of type and call qualification -- expect copious amounts of bugs and unintended behavior

/**
 *
 * @author 
 */
public abstract class AbstractBookkeeper implements Bookkeeper {

<<<<<<< HEAD
    FunctionBook currentFunction;
    FacilityDeclBook currentFacility;
=======
    private FunctionBook currentFunction;
    private FacilityDeclBook currentFacility;
>>>>>>> intial version of type and call qualification -- expect copious amounts of bugs and unintended behavior
    /**
     * Name of the module we are translating (e.g., 'Stack_Template,'
     * 'Int_Do_Nothing,' etc).
     */
<<<<<<< HEAD
    String moduleName;
=======
    protected String moduleName;
>>>>>>> intial version of type and call qualification -- expect copious amounts of bugs and unintended behavior

    /**
     * If we aren't translating a realization, then we don't need
     * bodies following function declarations as the files we are 
     * outputting will be class interfaces. Thus, this should only 
     * be false for Concepts and Enhancement declaration modules.
     */
<<<<<<< HEAD
    Boolean isRealization;

    ArrayList<FacilityDeclBook> facilityList;
    ArrayList<String> constructorList;
    ArrayList<String> importList;
    ArrayList<FunctionBook> functionList;
=======
    protected Boolean isRealization;

    protected ArrayList<FacilityDeclBook> facilityList;
    protected ArrayList<String> constructorList;
    protected ArrayList<String> importList;
    protected ArrayList<FunctionBook> functionList;
>>>>>>> intial version of type and call qualification -- expect copious amounts of bugs and unintended behavior

    /**
     * Construct a supervisor to manage Java modules undergoing 
     * translation.
     */
    public AbstractBookkeeper(String name, Boolean isRealiz) {
        moduleName = name;
        isRealization = isRealiz;

        importList = new ArrayList();
        functionList = new ArrayList();
    }

    /**
     * Stores packages needed by the module undergoing translation.
     */
    @Override
    public void addUses(String usesName) {
        importList.add(usesName);
    }

    /* FacilityDeclBook Methods */
<<<<<<< HEAD
    /*@Override
    public void facAdd(String name, String concept, String realiz) {
        FacilityDeclBook newFac = new FacilityDeclBook(name,concept,realiz);
        facilityList.add(newFac);
        currentFacility = newFac;
    }*/

    @Override
    public void facAddParam(String parameter) {
        currentFacility.parameterList.add(parameter);
    }

    @Override
    public void facAddEnhance(String name, String realiz) {
        try{
            FacilityDeclEnhance newEnhance;
            //The below is the correct syntax, it just...wrinkles my brain
            newEnhance = currentFacility.new FacilityDeclEnhance(name, realiz);
            currentFacility.enhanceList.add(newEnhance);
            currentFacility.currentEnhance = newEnhance;
        } catch (NullPointerException e){
            
        }
    }

    @Override
    public void facAddEnhanceParam(String parameter) {
        currentFacility.currentEnhance.parameterList.add(parameter);
    }
    
    @Override
    public void facEnd(){
        currentFacility = null;
    }
    
    /* End FacilityDeclBook Methods */
    
    
    /* Abstract FunctionBook calls */
    @Override
    public void fxnAddParam(String parName) {
        currentFunction.parameterList.add(parName);
=======
    public void facAdd(String name, String concept, String realiz) {
    // TODO : This...
    }

    public void facAddParam(String parameter) {
        currentFacility.addParameter(parameter);
    }

    public void facAddEnhance(String name, String realiz) {
        currentFacility.addEnhancement(name, realiz);
    }

    public void facAddEnhanceParam(String parameter) {
        currentFacility.addEnhanceParameter(parameter);
    }

    /* End FacilityDeclBook Methods */
    /* Abstract FunctionBook calls */
    @Override
    public void fxnAddParam(String parName) {
        currentFunction.addParameter(parName);
>>>>>>> intial version of type and call qualification -- expect copious amounts of bugs and unintended behavior
    }

    @Override
    public void fxnAddVarDecl(String varName) {
<<<<<<< HEAD
        currentFunction.varInitList.add(varName);
=======
        currentFunction.addVariable(varName);
>>>>>>> intial version of type and call qualification -- expect copious amounts of bugs and unintended behavior
    }

    @Override
    public void fxnAppendTo(String stmt) {
<<<<<<< HEAD
        currentFunction.allStmt.append(stmt);
=======
        currentFunction.appendToStmt(stmt);
>>>>>>> intial version of type and call qualification -- expect copious amounts of bugs and unintended behavior
    }

    @Override
    public void fxnEnd() {
        currentFunction = null;
    }
<<<<<<< HEAD
    
    
    
    
}

/* Books! */
    
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
abstract class FunctionBook{
    protected String myName;
    protected String myReturnType;
    protected Boolean hasBody;

    protected ArrayList<String> parameterList;
    protected ArrayList<String> varInitList;
    protected StringBuilder allStmt;
    /**
     * <p>Constructs an <code>AbstractFunctionBook</code> object 
     * that provides a container for some function named 
     * <code>name</code> with return type <code>type</code>. 
     * The function's parameters, variables, and 'stmts' will be 
     * added using standard @link FunctionBook methods.</p>
     *
     * @param name The function's name.
     * @param type The function's return type.
     */
    FunctionBook(String name, String returnType, boolean hasBody){
        myName = name;
        myReturnType = returnType;
        this.hasBody = hasBody;

        parameterList = new ArrayList<String>();
        varInitList = new ArrayList<String>();
        allStmt = new StringBuilder();
    }

    abstract String getString();
}

abstract class FacilityDeclBook{
    protected String myName;
    protected String myConcept;
    protected String myConceptRealiz;

    protected ArrayList<FacilityDeclEnhance> enhanceList;
    protected ArrayList<String> parameterList;
    protected FacilityDeclEnhance currentEnhance;


    FacilityDeclBook(String name, String concept, String realiz){
        myName = name;
        myConcept = concept;
        myConceptRealiz = realiz;

        enhanceList = new ArrayList<FacilityDeclEnhance>();
        parameterList = new ArrayList<String>();
    }
    class FacilityDeclEnhance{
        protected String myName;
        protected String myRealiz;
        protected ArrayList<String> parameterList;

        FacilityDeclEnhance(String newEnhance, String realiz){
            myName = newEnhance;
            myRealiz = realiz;
        }
    }
    abstract String getString();
}

class RecordBook{

}
=======
}
>>>>>>> intial version of type and call qualification -- expect copious amounts of bugs and unintended behavior
