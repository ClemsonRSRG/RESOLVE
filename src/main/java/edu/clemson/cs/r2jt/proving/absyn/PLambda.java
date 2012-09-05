package edu.clemson.cs.r2jt.proving.absyn;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.analysis.MathExpTypeResolver;
import edu.clemson.cs.r2jt.proving.immutableadts.EmptyImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.SimpleImmutableList;
import edu.clemson.cs.r2jt.type.FunctionType;
import edu.clemson.cs.r2jt.type.Type;

public class PLambda extends PExp {

    private static final SimpleImmutableList<PExp> EMPTY_LIST =
            new EmptyImmutableList<PExp>();

    public final String variableName;
    private final PExp myBody;

    public PLambda(String variableName, Type variableType, PExp body,
            MathExpTypeResolver metr) {
        super(body.structureHash * 34, body.valueHash * 31
                + variableName.hashCode(), new FunctionType(variableType, body
                .getType()), metr);

        this.variableName = variableName;
        myBody = body;
    }

    public void accept(PExpVisitor v) {
        v.beginPExp(this);
        v.beginPLambda(this);
        v.endPLambda(this);
        v.endPExp(this);
    }

    @Override
    public SimpleImmutableList<PExp> getSubExpressions() {
        return EMPTY_LIST;
    }

    @Override
    public PExpSubexpressionIterator getSubExpressionIterator() {
        return EmptySubexpressionIterator.INSTANCE;
    }

    @Override
    public boolean isObviouslyTrue() {
        return myBody.isObviouslyTrue();
    }

    @Override
    protected void splitIntoConjuncts(List<PExp> accumulator) {
        accumulator.add(this);
    }

    @Override
    public PExp flipQuantifiers() {
        throw new UnsupportedOperationException("This method has not yet "
                + "been implemented.");
    }

    @Override
    protected void bindTo(PExp target, Map<PExp, PExp> accumulator)
            throws BindingException {

        //As a lambda expression, we can't be quantified and our body is
        //indivisible, so we only bind to identical things.
        if (!this.equals(target)) {
            throw BINDING_EXCEPTION;
        }
    }

    @Override
    public PExp substitute(Map<PExp, PExp> substitutions) {
        PExp retval;

        if (substitutions.containsKey(this)) {
            retval = substitutions.get(this);
        }
        else {
            retval = this;
        }

        return retval;
    }

    @Override
    public boolean containsName(String name) {
        return variableName.equals(name) || myBody.containsName(name);
    }

    @Override
    public Set<String> getSymbolNamesNoCache() {
        return myBody.getSymbolNames();
    }

    @Override
    public Set<PSymbol> getQuantifiedVariablesNoCache() {
        return myBody.getQuantifiedVariables();
    }

    @Override
    public List<PExp> getFunctionApplicationsNoCache() {
        return myBody.getFunctionApplications();
    }

    @Override
    public boolean containsExistential() {
        return myBody.containsExistential();
    }

    @Override
    public boolean isEquality() {
        return false;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
