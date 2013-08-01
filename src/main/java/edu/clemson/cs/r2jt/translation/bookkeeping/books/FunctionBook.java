package edu.clemson.cs.r2jt.translation.bookkeeping.books;

import java.util.ArrayList;

/**
 *
 * @author Welch D
 */
public abstract class FunctionBook {
	
	private String myName;
	private String myReturnType;
	private Boolean hasBody;

	private ArrayList<String> myParameterList;
	private ArrayList<String> myVarInitList;

	private StringBuilder myStmt;

    /**
     * <p>Constructs an <code>AbstractFunctionBook</code> object 
     * that provides a container for some function named 
     * <code>name</code> with return type <code>type</code>. 
     * The function's parameters, variables, and 'stmts' will be 
     * added using standard @link FunctionBook methods.</p>
     *
     * @param name The function's name.
     * @param returnType The function's return type.
     */
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
}
