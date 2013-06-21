/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.clemson.cs.r2jt.translation.supervising;

/**
 * Collects, stores, and passes-off info encountered during 
 * the TreeWalkerVisitor's traversals. 
 * 
 * <p>A <code>Supervisor</code> only stores the imports needed 
 * for the translation of a given module.</p>
 * 
 * <p>The rest of the time, a <code>Supervisor</code> simply collects 
 * as much info from non-serializable <code>TreeWalkerVisistor</code>
 * traversals as possible, then simply passes this information to
 * a more appropriate, specialized class (e.g. <code>Function</code>) 
 * that stores, sorts, and ultimately prints that particular 
 * specialized chunk of code out in its proper, correct form.</p>
 * 
 * <p>So you can think of <code>Supervisor</code> then as both liason
 * and 'bookkeeper'. Something that stands in the middle - passing off
 * the raw data from Translator <code>TreeWalkerVisitor</code> methods 
 * to more specialized classes that then give data back in its correct 
 * form so it can be placed in its final position on the page by the 
 * <code>Supervisor</code>.
 * 
 * @author Mark T 
 * @author Welch D
 */

public interface Supervisor {

    /**
     * <p>Adds an import to the file undergoing translation.</p> 
     * @param uses A fully-qualified import string.
     */
    public void addUses(String uses);

    // ===========================================================
    // Employee Function Methods
    // ===========================================================

    /**
     * <p>Creates a function called <code>name</code> of type
     * <code>type</code> and puts it into a list of other 
     * functions in the module.</p> 
     * @param type A function's return type/name. 
     * @param name A function name.
     */
    public void fxnAdd(String type, String name);

    /**
     * <p>Adds string <code>parameter</code> to the parameter 
     * list of the function it corresponds to.</p> 
     * @param parameter A parameter's type and name.
     */
    public void fxnAddParam(String parameter);

    /**
     * <p>Adds <code>varName</code> to its corresponding
     * function's variable list.</p> 
     * @param variable A variable's type and name.
     */
    public void fxnAddVarDecl(String variable);

    /**
     * <p>Appends <code>stmt</code> to a buffer in 
     * <code>Function</code> that, after repeated appends 
     * via the Walker, builds-up the final body of a function.</p> 
     * @param stmt A <code>stmt</code> string.
     */
    public void fxnAppendTo(String stmt);

    //TODO annotate me.
    public void fxnEnd();

    /**
     * <p>Appends a string <code>stmt</code>s to a global buffer 
     * that, after repeated appends, grows into the complete 
     * body of a given function.</p> 
     * 
     * @return A StringBuilder object ready for further formatting.
     */
    public StringBuilder output();
}
