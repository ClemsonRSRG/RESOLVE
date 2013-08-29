package edu.clemson.cs.r2jt.translation.bookkeeping;

import java.util.List;

/**
 * Collects, stores, and passes-off info encountered during 
 * translation-related TreeWalkerVisitor traversals to specialized 
 * container classes. 
 * 
 * <p>A <code>Bookkeeper</code> itself only stores the imports needed 
 * for the translation of a given module.</p>
 * 
 * <p>The rest of the time, <code>Bookkeeper</code> simply collects 
 * info from <code>TreeWalkerVisistor</code> traversals and passes 
 * this info to more specialized <em>Book</em> classes that store, 
 * sort, and ultimately print that particular specialized chunk of 
 * code out in its correct form/order.</p>
 * 
 * @author Mark T 
 * @author Welch D
 */

public interface Bookkeeper {

    /**
     * <p>Adds an import to the file undergoing translation.</p>
     * @param uses A fully-qualified import string.
     */
    public void addUses(String uses);

    // -----------------------------------------------------------
    //   Facility methods
    // -----------------------------------------------------------

    public void facAdd(String name, String concept, String realiz);

    public void facAddParameter(String parameter);

    public void facAddEnhancement(String name, String realiz);

    public void facAddEnhancementParameter(String parameter);

    public boolean facEnhancementIsOpen();

    public void facEnhancementEnd();

    public void facEnd();

    // -----------------------------------------------------------
    //   Function methods
    // -----------------------------------------------------------

    /**
     * <p>Builds a <code>FunctionBook</code> object w/ name 
     * <code>name</code> and type <code>type</code>.</p>
     * @param type A function's return type. 
     * @param name A function's name.
     */
    public void fxnAdd(String type, String name);

    /**
     * <p>Adds <code>parameter</code> to its corresponding 
     * <code>FunctionBook</code>'s parameter list.</p>
     * @param parameter A parameter's type and name.
     */
    public void fxnAddParameter(String parameter);

    /**
     * <p>Adds <code>variable</code> to its corresponding
     * <code>FunctionBook</code>'s variable list.</p>
     * @param variable A variable's type and name.
     */
    public void fxnAddVariableDeclaration(String variable);

    /**
     * <p>Appends <code>stmt</code> to its corresponding
     * <code>FunctionBook</code>'s statement buffer.</p>
     * @param stmt A <code>stmt</code> string.
     */
    public void fxnAppendTo(String stmt);

    //TODO annotate me.
    public void fxnEnd();

    /**
     * <p>Returns a string representation of the module undergoing
     * translation into Java, C, etc.</p>
     */
    public String output();
}
