package edu.clemson.cs.r2jt.absyn;

import java.util.Map;

import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.type.Type;

/**
 * <p>A <code>FunctionValueExp</code> represents </p>
 */
public class FunctionValueExp extends Exp {

    @Override
    public void accept(ResolveConceptualVisitor v) {
    // TODO Auto-generated method stub

    }

    @Override
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String asString(int indent, int increment) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Exp> getSubExpressions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSubExpression(int index, Exp e) {
    // TODO Auto-generated method stub

    }

    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        // TODO Auto-generated method stub
        return null;
    }

}
