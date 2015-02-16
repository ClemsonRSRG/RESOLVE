package edu.clemson.cs.r2jt.typereasoning2;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.typeandpopulate2.MTProper;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;

public class TypeGraph {

    /**
     * <p>A set of non-thread-safe resources to be used during general type
     * reasoning. This really doesn't belong here, but anything that's reasoning
     * about types should already have access to a type graph, and only one type
     * graph is created per thread, so this is a convenient place to put it.</p>
     */
    public final PerThreadReasoningResources threadResources =
            new PerThreadReasoningResources();

    public final MTProper CLS = new MTProper(this, null, true, "MType");
    public final MTProper VOID = new MTProper(this, CLS, false, "Void");

    public boolean isSubtype(MTType subtype, MTType supertype) {
        return false;
    }

    public boolean isKnownToBeIn(ExprAST value, MTType expected) {
        return false;
    }

    public boolean isKnownToBeIn(MTType value, MTType expected) {
        return false;
    }
}