/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.supervising;

/**
 * Provides a container for RESOLVE functions that allows users
 * to easily collect, add-to, and maintain translation relevant
 * information (i.e. function name, variable name/type, parameter 
 * name/type, etc).
 * 
 * <p>Functions in RESOLVE are reliably serialized using
 * <code>TreeWalkerVisitor</code> traversals. Thus, Each 
 * <code>Function</code> has a StringBuilder instance variable, 
 * <code>allstmt</code> which, throughout the various TreeWalker
 * traversals over the function's statements and expressions, gets 
 * automatically "filled in" with corresponding, correctly translated 
 * java,c,etc code. This eases the task of translation considerably 
 * since it allows one to store entire chunks of code 
 * (i.e. <code>EmployeeFunction</code>) in a self contained list for 
 * later ordering and output by the <code>Supervisor</code>.</p>
 * 
 * @author Mark T
 * @author Welch D
 */
public interface EmployeeFunction {

    // ===========================================================
    // Adder Methods
    // ===========================================================

    /**
     * <p>Adds a formed (type+name) string <code>p</code> to
     * <code>EmployeeFunction</code>.</p>
     */
    public void addParameter(String p);

    /**
     * <p>Adds a formed (type+name) string <code>v</code> to
     * <code>EmployeeFunction</code>.</p>
     */
    public void addVariable(String v);

    /**
     * <p>Appends <code>s</code> to the growing string of statements
     * in <code>EmployeeFunction</code>.</p>
     */
    public void appendToStmt(String s);

    public String getReturnType();

    public String getName();

    /**
     * <p>Returns an unformatted (no newlines or tabs) string 
     * representation of <code>Function</code>.</p>
     */
    public String getString();
}
