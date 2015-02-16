package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import org.antlr.v4.runtime.Token;


public class ModuleArgumentAST extends ResolveAST {

    private final ExprAST myArgumentExpr;

    private MTType myMathType;
    private PTType myProgramTypeValue;

    public ModuleArgumentAST(ExprAST expr, Token start, Token stop) {
        super(start, stop);
        myArgumentExpr = expr;
    }

    public ExprAST getArgumentExpr() {
        return myArgumentExpr;
    }

    public PTType getProgramTypeValue() {
        return myProgramTypeValue;
    }

    public MTType getMathType() {
        return myMathType;
    }

    public void setProgramTypeValue(PTType t) {
        myProgramTypeValue = t;
    }

    public void setMathType(MTType t) {
        myMathType = t;
    }

}
