package edu.clemson.cs.r2jt.absynnew.stmt;

import edu.clemson.cs.r2jt.absynnew.expr.ProgOperationRefAST;

public class CallAST extends StmtAST {

    private final ProgOperationRefAST myOpReferenceExpr;

    public CallAST(ProgOperationRefAST expr) {
        super(expr.getStart(), expr.getStop());
        myOpReferenceExpr = expr;
    }

    public ProgOperationRefAST getWrappedOpReferenceExpr() {
        return myOpReferenceExpr;
    }
}
