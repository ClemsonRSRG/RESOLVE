/**
 * AbstractFunctionExp.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.mathexpr;

import edu.clemson.cs.r2jt.typeandpopulate2.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>This is the abstract base class for all the mathematical function expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public abstract class AbstractFunctionExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's qualifier.</p> */
    protected final PosSymbol myQualifier;

    /** <p>The object's quantification (if any).</p> */
    protected SymbolTableEntry.Quantification myQuantification;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A helper constructor that allow us to store the location
     * of the created object directly in the this class.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qualifier A {@link PosSymbol} name object.
     */
    protected AbstractFunctionExp(Location l, PosSymbol qualifier) {
        super(l);
        myQualifier = qualifier;
        myQuantification = SymbolTableEntry.Quantification.NONE;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This class represents function <em>applications</em>. The type of a
     * function application is the type of the range of the function. Often
     * we'd like to think about the type of the <em>function itself</em>, not 
     * the type of the result of its application.  Unfortunately our AST does 
     * not consider that the 'function' part of a FunctionExp (as distinct from
     * its parameters) might be a first-class citizen with a type of its own.  
     * This method emulates retrieving the (not actually extant) first-class 
     * function part and guessing its type.  In this case, the guess is 
     * "conservative", in that we guess the smallest set that can't be 
     * contradicted by the available information.  For nodes without a true,
     * first-class function to consult (which, at the moment, is all of them), 
     * this means that for the formal parameter types, we'll guess the types of 
     * the actual parameters, and for the return type we'll guess 
     * <strong>Empty_Set</strong> (since we have no information about how the 
     * return value is used.)  This guarantees that the type we return will be
     * a subset of the actual type of the function the RESOLVE programmer 
     * intends (assuming she has called it correctly.)</p>
     *
     * @param g The type graph.
     *
     * @return A subset of the actual function type.
     */
    public MTFunction getConservativePreApplicationType(TypeGraph g) {
        List<Exp> params = this.getSubExpressions();
        List<MTType> subTypes = new LinkedList<>();

        for (Exp param : params) {
            subTypes.add(param.getMathType());
        }

        return new MTFunction(g, g.EMPTY_SET, subTypes);
    }

    //TODO : This does not work correctly for nested MTCartesians/TupleExps
    /**
     * <p>Syntactically, a function application may have multiple parameters.
     * Mathematically, we must be able to refer to the single mathematical 
     * object that represents "all the parameters."  This method returns that
     * object.</p>
     */
    /*public Exp getSoleParameter(TypeGraph g) {
    	return buildSoleParameter(g, getLocation(), getParameters());
    }
    
    public static final Exp buildSoleParameter(TypeGraph g, Location l,
    			List<Exp> params) {
    	
    	Exp result;
    	
    	switch (params.size()) {
    	case 0:
    		result = g.getNothingExp();
    		break;
    	case 1:
    		result = params.get(0);
    		break;
    	default:
    		List<MTType> componentTypes = new LinkedList<>();
    		TupleExp tuple = new TupleExp(l, params);
    		for (Exp param : params) {
    			componentTypes.add(param.getMathType());
    		}
    		MTType tupleType = MTFunction.buildParameterType(g, componentTypes);
    		
    		propogateTypes(tuple, tupleType);
    		
    		result = tuple;
    	}
    	
    	return result;
    }*/

    /**
     * <p>Returns the mathematical operator as a symbol.</p>
     *
     * @return A {link PosSymbol} object containing the operator.
     */
    public abstract PosSymbol getOperatorAsPosSymbol();

    /**
     * <p>Returns the mathematical operator as a string.</p>
     *
     * @return The operator as a string.
     */
    public abstract String getOperatorAsString();

    /**
     * <p>Returns the module qualifier for this expression.</p>
     *
     * @return A {@link PosSymbol} object containing the qualifier.
     */
    public PosSymbol getQualifier() {
        return myQualifier;
    }

    /**
     * <p>Returns the quantifier for this expression.</p>
     *
     * @return An {@link SymbolTableEntry.Quantification} object representing
     * the quantification for this expression.
     */
    public SymbolTableEntry.Quantification getQuantification() {
        return myQuantification;
    }

    /**
     * <p>Sets the quantification for this expression.</p>
     *
     * @param q The quantification for this expression.
     */
    public void setQuantification(SymbolTableEntry.Quantification q) {
        if (!q.equals(SymbolTableEntry.Quantification.NONE)) {
            throw new UnsupportedOperationException("The function "
                    + getOperatorAsString() + " does not support "
                    + "quantification.");
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This method sets <code>t</code> as the type for an
     * expression and it's subexpressions.</p>
     *
     * @param e The expression we wish to change the type.
     * @param t The new mathematical type.
     */
    /*private static final void propogateTypes(Exp e, MTType t) {
        e.setMathType(t);

        Iterator<Exp> subExpressions = e.getSubExpressions().iterator();
        Iterator<MTType> subTypes = t.getComponentTypes().iterator();

        //Note that e and t must have the same structure
        while (subExpressions.hasNext()) {
            propogateTypes(subExpressions.next(), subTypes.next());
        }
    }*/

}