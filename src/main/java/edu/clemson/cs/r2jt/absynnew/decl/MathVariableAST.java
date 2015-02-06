package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.MathTypeAST;
import org.antlr.v4.runtime.Token;

public class MathVariableAST extends DeclAST {

    private final MathTypeAST mySyntacticType;

    public MathVariableAST(Token start, Token stop, Token name, MathTypeAST type) {
        super(start, stop, name);
        mySyntacticType = type;
    }

    public MathTypeAST getSyntaxType() {
        return mySyntacticType;
    }
}