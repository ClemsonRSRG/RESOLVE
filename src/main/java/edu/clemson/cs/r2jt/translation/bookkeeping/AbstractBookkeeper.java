/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.bookkeeping;

import edu.clemson.cs.r2jt.translation.bookkeeping.books.*;
import java.util.ArrayList;

/**
 *
 * @author 
 */
public abstract class AbstractBookkeeper implements Bookkeeper {

    private FunctionBook currentFunction;
    private FacilityDeclBook currentFacility;
    /**
     * Name of the module we are translating (e.g., 'Stack_Template,'
     * 'Int_Do_Nothing,' etc).
     */
    protected String moduleName;

    /**
     * If we aren't translating a realization, then we don't need
     * bodies following function declarations as the files we are 
     * outputting will be class interfaces. Thus, this should only 
     * be false for Concepts and Enhancement declaration modules.
     */
    protected Boolean isRealization;

    protected ArrayList<FacilityDeclBook> facilityList;
    protected ArrayList<String> constructorList;
    protected ArrayList<String> importList;
    protected ArrayList<FunctionBook> functionList;

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
    }

    @Override
    public void fxnAddVarDecl(String varName) {
        currentFunction.addVariable(varName);
    }

    @Override
    public void fxnAppendTo(String stmt) {
        currentFunction.appendToStmt(stmt);
    }

    @Override
    public void fxnEnd() {
        currentFunction = null;
    }
}
