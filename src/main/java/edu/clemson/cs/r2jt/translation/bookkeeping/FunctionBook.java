/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.bookkeeping.hidden;

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
public interface FunctionBook {

    //TODO : add @param information for all below
    /* FunctionBook Methods */

    /**
     * <p>Adds a formed (type+name) parameter string <code>p</code> to
     * an <code>FunctionBook</code> object.</p>
     */
    public void addParameter(String p);

    /**
     * <p>Adds a formed (type+name) variable string <code>v</code> to
     * an <code>FunctionBook</code> object.</p>
     */
    public void addVariable(String v);

    /**
     * <p>Appends <code>s</code> to <code>FunctionBook</code>'s
     * statement buffer.</p>
     */
    public void appendToStmt(String s);

    public String getReturnType();

    public String getName();

    /**
     * <p>Returns an unformatted (no newlines or tabs) string 
     * representation of a <code>FunctionBook</code> object.</p>
     */
    public String getString();
}
