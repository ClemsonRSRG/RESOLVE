package edu.clemson.cs.r2jt.proving.absyn;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.absyn.AltItemExp;
import edu.clemson.cs.r2jt.absyn.AlternativeExp;
import edu.clemson.cs.r2jt.analysis.MathExpTypeResolver;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.proving.immutableadts.EmptyImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.SimpleImmutableList;
import edu.clemson.cs.r2jt.type.Type;

public class PAlternatives extends PExp {

    private final List<Alternative> myAlternatives =
            new LinkedList<Alternative>();

    private final PExp myOtherwiseClauseResult;

    public PAlternatives(List<PExp> conditions, List<PExp> results,
            PExp otherwiseClauseResult, MathExpTypeResolver typer) {

        super(
                calculateStructureHash(conditions, results,
                        otherwiseClauseResult), calculateStructureHash(
                        conditions, results, otherwiseClauseResult),
                getResultType(results, otherwiseClauseResult), typer);

        sanityCheckConditions(conditions);

        if (conditions.size() != results.size()) {
            throw new IllegalArgumentException("conditions.size() must equal "
                    + "results.size().");
        }

        Iterator<PExp> conditionIter = conditions.iterator();
        Iterator<PExp> resultIter = conditions.iterator();

        while (conditionIter.hasNext()) {
            myAlternatives.add(new Alternative(conditionIter.next(), resultIter
                    .next()));
        }

        myOtherwiseClauseResult = otherwiseClauseResult;
    }

    public PAlternatives(AlternativeExp alternativeExp,
            MathExpTypeResolver typer) {

        this(getConditions(alternativeExp, typer), getResults(alternativeExp,
                typer), getOtherwiseClauseResult(alternativeExp, typer), typer);
    }

    public void accept(PExpVisitor v) {
        v.beginPExp(this);
        v.beginPAlternatives(this);

        boolean first = true;
        for (Alternative alt : myAlternatives) {
            if (!first) {
                v.fencepostPAlternatives(this);
            }

            alt.result.accept(v);
            alt.condition.accept(v);
        }

        myOtherwiseClauseResult.accept(v);

        v.endPAlternatives(this);
        v.endPExp(this);
    }

    private static List<PExp> getConditions(AlternativeExp alternativeExp,
            MathExpTypeResolver typer) {

        List<PExp> result = new LinkedList<PExp>();
        for (AltItemExp aie : alternativeExp.getAlternatives()) {
            if (aie.getTest() != null) {
                result.add(PExp.buildPExp(aie.getTest(), typer));
            }
        }

        return result;
    }

    private static List<PExp> getResults(AlternativeExp alternativeExp,
            MathExpTypeResolver typer) {

        List<PExp> result = new LinkedList<PExp>();
        for (AltItemExp aie : alternativeExp.getAlternatives()) {
            if (aie.getTest() != null) {
                result.add(PExp.buildPExp(aie.getAssignment(), typer));
            }
        }

        return result;
    }

    private static PExp getOtherwiseClauseResult(AlternativeExp alternativeExp,
            MathExpTypeResolver typer) {

        PExp workingOtherwiseClauseResult = null;

        for (AltItemExp aie : alternativeExp.getAlternatives()) {
            if (workingOtherwiseClauseResult != null) {
                throw new IllegalArgumentException("AlternativeExps with "
                        + "additional alternatives after the 'otherwise' "
                        + "clause are not accepted by the prover. \n\t"
                        + aie.getAssignment() + " appears in such a position.");
            }

            if (aie.getTest() == null) {
                workingOtherwiseClauseResult =
                        PExp.buildPExp(aie.getAssignment(), typer);
            }
        }

        return workingOtherwiseClauseResult;
    }

    private void sanityCheckConditions(List<PExp> conditions) {
        try {
            Type b = myTyper.getType("Boolean_Theory", "B", null, true);
            for (PExp condition : conditions) {
                if (!condition.typeMatches(b)) {
                    throw new IllegalArgumentException("AlternativeExps with "
                            + "non-boolean-typed conditions are not accepted "
                            + "by the prover. \n\t" + condition + " has type "
                            + condition.getType());
                }
            }
        }
        catch (TypeResolutionException e) {
            throw new RuntimeException("Prover couldn't get a handle on type "
                    + "B.");
        }
    }

    private static int calculateStructureHash(List<PExp> conditions,
            List<PExp> results, PExp otherwiseClauseResult) {

        int hash = 0;

        Iterator<PExp> conditionIter = conditions.iterator();
        Iterator<PExp> resultIter = conditions.iterator();

        while (conditionIter.hasNext()) {
            hash *= 31;
            hash += conditionIter.next().structureHash;
            hash *= 34;
            hash += resultIter.next().structureHash;
        }

        return hash;
    }

    private static Type getResultType(List<PExp> results,
            PExp otherwiseClauseResult) {

        PExp prototypeResult = null;

        for (PExp curResult : results) {
            if (prototypeResult == null) {
                prototypeResult = curResult;
            }
            else {
                if (!curResult.typeMatches(prototypeResult)) {
                    throw new IllegalArgumentException("AlternativeExps with "
                            + "results of different types are not accepted by "
                            + "the prover. \n\t" + prototypeResult + " has "
                            + "type " + prototypeResult.getType() + ".\n\t"
                            + curResult + " has type " + curResult.getType()
                            + ".");
                }
            }
        }

        if (!otherwiseClauseResult.typeMatches(prototypeResult)) {
            throw new IllegalArgumentException("AlternativeExps with "
                    + "results of different types are not accepted by "
                    + "the prover. \n\t" + prototypeResult + " has " + "type "
                    + prototypeResult.getType() + ".\n\t"
                    + otherwiseClauseResult + " has type "
                    + otherwiseClauseResult.getType() + ".");
        }

        return prototypeResult.getType();
    }

    @Override
    public SimpleImmutableList<PExp> getSubExpressions() {
        return new EmptyImmutableList<PExp>();
    }

    @Override
    public PExpSubexpressionIterator getSubExpressionIterator() {
        return EmptySubexpressionIterator.INSTANCE;
    }

    @Override
    public boolean isObviouslyTrue() {
        boolean result = true;

        for (Alternative a : myAlternatives) {
            result &= a.result.isObviouslyTrue();
        }

        return result && myOtherwiseClauseResult.isObviouslyTrue();
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

        //For the moment, we only bind to identical things
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
        boolean result = false;

        for (Alternative a : myAlternatives) {
            result |=
                    a.condition.containsName(name)
                            || a.result.containsName(name);
        }

        return result || myOtherwiseClauseResult.containsName(name);
    }

    @Override
    public Set<String> getSymbolNamesNoCache() {
        Set<String> result = new HashSet<String>();

        for (Alternative a : myAlternatives) {
            result.addAll(a.condition.getSymbolNames());
            result.addAll(a.result.getSymbolNames());
        }

        result.addAll(myOtherwiseClauseResult.getSymbolNames());

        return result;
    }

    @Override
    public Set<PSymbol> getQuantifiedVariablesNoCache() {
        Set<PSymbol> result = new HashSet<PSymbol>();

        for (Alternative a : myAlternatives) {
            result.addAll(a.condition.getQuantifiedVariables());
            result.addAll(a.result.getQuantifiedVariables());
        }

        result.addAll(myOtherwiseClauseResult.getQuantifiedVariables());

        return result;
    }

    @Override
    public List<PExp> getFunctionApplicationsNoCache() {
        List<PExp> result = new LinkedList<PExp>();

        for (Alternative a : myAlternatives) {
            result.addAll(a.condition.getFunctionApplications());
            result.addAll(a.result.getFunctionApplications());
        }

        result.addAll(myOtherwiseClauseResult.getFunctionApplications());

        return result;
    }

    @Override
    public boolean containsExistential() {
        boolean result = false;

        for (Alternative a : myAlternatives) {
            result |= a.condition.containsExistential();
            result |= a.result.containsExistential();
        }

        return result || myOtherwiseClauseResult.containsExistential();
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

    private static class Alternative {

        public final PExp condition;
        public final PExp result;

        public Alternative(PExp condition, PExp result) {
            this.condition = condition;
            this.result = result;
        }
    }
}
