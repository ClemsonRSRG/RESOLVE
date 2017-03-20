/**
 * AbstractFunctionExp.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>This is the abstract base class for all the mathematical function expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public abstract class AbstractFunctionExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's qualifier.</p> */
    protected PosSymbol myQualifier;

    /** <p>The object's quantification (if any).</p> */
    protected SymbolTableEntry.Quantification myQuantification;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the location
     * of any objects created from a class that inherits from
     * {@code AbstractFunctionExp}.</p>
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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        AbstractFunctionExp that = (AbstractFunctionExp) o;

        if (myQualifier != null ? !myQualifier.equals(that.myQualifier)
                : that.myQualifier != null)
            return false;
        return myQuantification == that.myQuantification;
    }

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
    public final MTFunction getConservativePreApplicationType(TypeGraph g) {
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
     * @return A {@link PosSymbol} object containing the operator.
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
    public final PosSymbol getQualifier() {
        return myQualifier;
    }

    /**
     * <p>Returns the quantifier for this expression.</p>
     *
     * @return An {@link SymbolTableEntry.Quantification} object representing
     * the quantification for this expression.
     */
    public final SymbolTableEntry.Quantification getQuantification() {
        return myQuantification;
    }

    /**
     * <p>Returns the list of parameters in this expression.</p>
     *
     * @return A list of {@link Exp} containing all the parameters to this function.
     */
    public final List<Exp> getParameters() {
        return this.getSubExpressions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result =
                31 * result
                        + (myQualifier != null ? myQualifier.hashCode() : 0);
        result =
                31
                        * result
                        + (myQuantification != null ? myQuantification
                                .hashCode() : 0);
        return result;
    }

    /**
     * <p>Sets the qualifier for this expression.</p>
     *
     * @param qualifier The qualifier for this expression.
     */
    public final void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
    }

    /**
     * <p>Sets the quantification for this expression.</p>
     *
     * @param q The quantification type for this expression.
     */
    public final void setQuantification(SymbolTableEntry.Quantification q) {
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
     * <p>This method sets {@code t} as the type for an
     * expression and it's sub-expressions.</p>
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