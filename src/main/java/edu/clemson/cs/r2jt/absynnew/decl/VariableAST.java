package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.MathTypeAST;
import edu.clemson.cs.r2jt.absynnew.NamedTypeAST;
import org.antlr.v4.runtime.Token;

public class VariableAST extends DeclAST {

    private final NamedTypeAST myType;

    public VariableAST(Token start, Token stop, Token name, NamedTypeAST type) {
        super(start, stop, name);
        myType = type;
    }

    public NamedTypeAST getType() {
        return myType;
    }

    public static class MathVariableDeclAST extends DeclAST {

        private final MathTypeAST mySyntacticType;

        public MathVariableDeclAST(Token start, Token stop, Token name,
                MathTypeAST type) {
            super(start, stop, name);
            mySyntacticType = type;
        }

        public MathTypeAST getSyntaxType() {
            return mySyntacticType;
        }
    }
}