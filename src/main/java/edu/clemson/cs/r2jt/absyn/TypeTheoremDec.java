package edu.clemson.cs.r2jt.absyn;

import java.util.Iterator;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class TypeTheoremDec extends Dec {

    private PosSymbol myName;
    private List<MathVarDec> myUniversalVars = new List<MathVarDec>();
    private Exp myAssertion;

    public void addVarDecGroup(List<MathVarDec> vars) {
        Iterator<MathVarDec> iter = vars.iterator();
        while (iter.hasNext()) {
            myUniversalVars.add(iter.next());
        }
    }

    public void setName(PosSymbol name) {
        this.myName = name;
    }

    public void setAssertion(Exp assertion) {
        this.myAssertion = assertion;
    }

    public Exp getBindingCondition() {
        if (hasBindingCondition()) {
            return ((InfixExp) myAssertion).getLeft();
        }
        return null;
    }

    public Exp getBindingExpression() {
        if (hasBindingCondition()) {
            return ((InfixExp) myAssertion).getRight();
        }
        return myAssertion;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
    // don't need this with the new walker
    }

    @Override
    public String asString(int indent, int increment) {
        return "Type Theorem " + myName;
    }

    @Override
    public PosSymbol getName() {
        return myName;
    }

    public List<MathVarDec> getUniversalVars() {
        return myUniversalVars;
    }

    public Exp getAssertion() {
        return myAssertion;
    }

    public boolean hasBindingCondition() {
        return myAssertion instanceof InfixExp
                && ((InfixExp) myAssertion).getOperatorAsString().equals(
                        "implies");
    }
}
