package edu.clemson.cs.r2jt.translation.bookkeeping;

import java.util.ArrayList;

/**
 * Collects, stores, and passes-off info encountered during 
 * translation-related TreeWalkerVisitor traversals to specialized 
 * container classes. 
 * 
 * <p>A <code>Bookkeeper</code> itself only stores the imports needed 
 * for the translation of a given module.</p>
 * 
 * <p>The rest of the time, <code>Bookkeeper</code> simply collects 
 * as much info from non-serializable <code>TreeWalkerVisistor</code>
 * traversals as possible, then passes this information to more
 * specialized <em>Book</em> classes that store, sort, and print 
 * that particular specialized chunk of code out in its correct 
 * form.</p>
 * 
 * <p>So you can think of <code>Bookkeeper</code> then as both liason
 * and supervisor. Something that stands in the middle - passing off
 * raw data from Translator <code>TreeWalkerVisitor</code> methods 
 * to specialized book-classes that then give data back in its full
 * form to be placed on the shelf by the <code>Bookkeeper</code>.
 * 
 * @author Mark T 
 * @author Welch D
 */

public interface Bookkeeper {

    /**
     * <p>Adds an import to the file undergoing translation.</p>
     * 
     * @param uses A fully-qualified import string.
     */
    public void addUses(String uses);

    /* FacilityDeclBook Methods */
    public void facAdd(String name, String concept, String realiz);

    public void facAddParam(String parameter);

    public void facAddEnhance(String name, String realiz);

    public void facAddEnhanceParam(String parameter);

    public boolean facEnhanceIsOpen();

    public void facEnhanceEnd();

    public void facEnd();

    /* End FacilityDeclBook Methods */

    /* FunctionBook Methods */

    /**
     * <p>Builds a <code>FunctionBook</code> object w/ name 
     * <code>name</code> and type <code>type</code>.</p>
     * 
     * @param type A function's return type. 
     * @param name A function's name.
     */
    public void fxnAdd(String type, String name);

    /**
     * <p>Adds <code>parameter</code> to its corresponding 
     * <code>FunctionBook</code>'s parameter list.</p>
     * 
     * @param parameter A parameter's type and name.
     */
    public void fxnAddParam(String parameter);

    /**
     * <p>Adds <code>variable</code> to its corresponding
     * <code>FunctionBook</code>'s variable list.</p>
     * 
     * @param variable A variable's type and name.
     */
    public void fxnAddVarDecl(String variable);

    /**
     * <p>Appends <code>stmt</code> to its corresponding
     * <code>FunctionBook</code>'s statement buffer.</p>
     * 
     * @param stmt A <code>stmt</code> string.
     */
    public void fxnAppendTo(String stmt);

    //TODO annotate me.
    public void fxnEnd();

    /* End FunctionBook methods */

    /**
     * <p>Returns a string representation of the module undergoing
     * translation into Java, C, etc.</p>
     */
    public String output();
}
