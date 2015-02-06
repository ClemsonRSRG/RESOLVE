package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A <code>MathTypeTheoremDeclAST</code> is a theorem that allows users to
 * statically establish relationships among <code>MTTypes</code> and other
 * classes/sets that are otherwise unable to be automatically inferred by
 * typechecking alone.</p>
 */
public class MathTypeTheoremAST extends DeclAST {

    private final List<MathVariableAST> myUniversalVars =
            new ArrayList<MathVariableAST>();
    private ExprAST myAssertion;

    public MathTypeTheoremAST(Token start, Token stop, Token name,
            List<MathVariableAST> universals, ExprAST assertion) {
        super(start, stop, name);
        myAssertion = assertion;
        myUniversalVars.addAll(universals);
    }

    public List<MathVariableAST> getUniversalVariables() {
        return myUniversalVars;
    }

    public ExprAST getAssertion() {
        return myAssertion;
    }
}