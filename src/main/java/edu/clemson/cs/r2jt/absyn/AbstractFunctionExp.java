package edu.clemson.cs.r2jt.absyn;

import java.util.Iterator;
import java.util.LinkedList;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.mathtype.MTFunction;
import edu.clemson.cs.r2jt.mathtype.MTType;
import edu.clemson.cs.r2jt.mathtype.SymbolTableEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public abstract class AbstractFunctionExp extends Exp {

    /**
     * <p>This class represents function <em>applications</em>.  The type of a 
     * function application is the type of the range of the function.  Often 
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
     */
    public MTFunction getConservativePreApplicationType(TypeGraph g) {
        List<Exp> params = getParameters();
        java.util.List<MTType> subTypes = new LinkedList<MTType>();

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
    			java.util.List<Exp> params) {
    	
    	Exp result;
    	
    	switch (params.size()) {
    	case 0:
    		result = g.getNothingExp();
    		break;
    	case 1:
    		result = params.get(0);
    		break;
    	default:
    		java.util.List<MTType> componentTypes = new LinkedList<MTType>();
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

    private static final void propogateTypes(Exp e, MTType t) {
        e.setMathType(t);

        Iterator<Exp> subExpressions = e.getSubExpressions().iterator();
        Iterator<MTType> subTypes = t.getComponentTypes().iterator();

        //Note that e and t must have the same structure
        while (subExpressions.hasNext()) {
            propogateTypes(subExpressions.next(), subTypes.next());
        }
    }

    public abstract String getOperatorAsString();

    public abstract PosSymbol getOperatorAsPosSymbol();

    public abstract PosSymbol getQualifier();

    public abstract Location getLocation();

    public abstract int getQuantification();

    public void setQuantification(SymbolTableEntry.Quantification q) {
        if (!q.equals(SymbolTableEntry.Quantification.NONE)) {
            throw new UnsupportedOperationException("The function "
                    + getOperatorAsString() + " does not support "
                    + "quantification.");
        }
    }

    public List<Exp> getParameters() {
        return this.getSubExpressions();
    }
}
